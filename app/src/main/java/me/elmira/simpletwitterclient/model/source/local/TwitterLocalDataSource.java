/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.elmira.simpletwitterclient.model.source.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class TwitterLocalDataSource implements TwitterDataSource {

    private static final String LOG_TAG = "TwitterLocalData";

    private static TwitterLocalDataSource INSTANCE;

    private ContentResolver mContentResolver;
    private HashMap<Long, User> hashUsers;

    private HandlerThread mHandlerThread;
    private Handler backgroundHandler;
    private Handler uiHandler;

    // Prevent direct instantiation.
    private TwitterLocalDataSource(@NonNull ContentResolver contentResolver) {
        checkNotNull(contentResolver);
        mContentResolver = contentResolver;

        hashUsers = new HashMap<>();

        mHandlerThread = new HandlerThread("Twitter Handler Thread", Process.THREAD_PRIORITY_LESS_FAVORABLE);
        mHandlerThread.start();

        backgroundHandler = new Handler(mHandlerThread.getLooper());
        uiHandler = new Handler(Looper.myLooper());
    }

    public static TwitterLocalDataSource getInstance(@NonNull ContentResolver contentResolver) {
        if (INSTANCE == null) {
            INSTANCE = new TwitterLocalDataSource(contentResolver);
        }
        return INSTANCE;
    }

    @Override
    public Tweet loadTweet(@NonNull long tweetId) {
        return null;
    }

    @Override
    public List<Tweet> loadTweets(long sinceId, long maxId) {
        throw new UnsupportedOperationException("operation is not supported on local data source");
    }

    @Override
    public void loadTweets(long sinceId, long maxId, LoadTweetsCallback callback) {
        throw new UnsupportedOperationException("operation is not supported on local data source");
    }

    @Override
    public List<Tweet> preloadTweets() {
        Log.d(LOG_TAG, "preloadTweets()");
        preloadAllUsers();

        Cursor cursor = null;
        try {
            List<Tweet> list = new ArrayList<>();
            cursor = mContentResolver.query(TwitterPersistenceContract.TweetEntry.CONTENT_URI,
                    TwitterPersistenceContract.TweetEntry.TWEETS_COLUMNS,
                    null,
                    null,
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID + " desc");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(parseTweet(cursor));
                }
                while (cursor.moveToNext());
            }
            return list;
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
    }

    @Override
    public void saveTweets(final List<Tweet> tweets) {
        if (tweets == null || tweets.size() == 0) return;
        // save tweets on background thread
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Tweet tweet : tweets) {
                    createTweetEntry(tweet);
                    if (!hashUsers.containsKey(tweet.getUser().getUid())) {
                        hashUsers.put(tweet.getUser().getUid(), tweet.getUser());
                    }
                }
            }
        });
    }

    @Override
    public void createTweet(final Tweet tweet, final CreateTweetCallback callback) {
        if (tweet == null) {
            if (callback != null) callback.onFailure();
            return;
        }
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                createTweetEntry(tweet);
                if (callback != null) callback.onTweetCreated(tweet);
            }
        });
    }

    @Override
    public void updateTweet(final Tweet tweet, final long tempId, final UpdateTweetCallback callback) {
        if (tweet == null) {
            if (callback != null) callback.onFailure();
            return;
        }
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                updateTweetEntry(tweet, tempId);
                if (callback != null) callback.onTweetUpdataed(tweet);
            }
        });
    }

    private void createTweetEntry(Tweet tweet) {
        if (tweet == null) return;

        try {
            createUserEntry(tweet.getUser());

            ContentValues contentValues = new ContentValues();

            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID, tweet.getUid());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_BODY, tweet.getBody());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_DATE_CREATED, tweet.getCreatedAt());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_SYNC, tweet.isSync() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_USER_ID, tweet.getUser() == null ? 0 : tweet.getUser().getUid());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE, tweet.isFavorite() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE_COUNT, tweet.getFavoriteCount());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED, tweet.isRetweeted() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED_COUNT, tweet.getRetweetedCount());

            mContentResolver.insert(TwitterPersistenceContract.TweetEntry.CONTENT_URI, contentValues);

        } catch (Throwable ex) {
            Log.e(LOG_TAG, "can'not insert tweet: " + tweet.toString());
            Log.e(LOG_TAG, ex.toString());
        }
    }

    private void createUserEntry(User user) {
        if (user == null || hashUsers.containsKey(user.getUid())) {
            return;
        }
        try {
            hashUsers.put(user.getUid(), user);

            ContentValues userValues = new ContentValues();
            userValues.put(TwitterPersistenceContract.UserEntry.COLUMN_NAME_ENTRY_ID, user.getUid());
            userValues.put(TwitterPersistenceContract.UserEntry.COLUMN_NAME_NAME, user.getName());
            userValues.put(TwitterPersistenceContract.UserEntry.COLUMN_NAME_SCREEN_NAME, user.getScreenName());
            userValues.put(TwitterPersistenceContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_URL, user.getProfileImageUrl());

            mContentResolver.insert(TwitterPersistenceContract.UserEntry.CONTENT_URI, userValues);

        } catch (Throwable ex) {
            Log.e(LOG_TAG, "can'not insert user: " + user.toString());
            Log.e(LOG_TAG, ex.toString());
        }
    }

    private void updateTweetEntry(Tweet tweet, long tempUid) {
        if (tweet == null) return;

        try {
            createUserEntry(tweet.getUser());

            ContentValues contentValues = new ContentValues();

            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID, tweet.getUid());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_BODY, tweet.getBody());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_DATE_CREATED, tweet.getCreatedAt());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_SYNC, tweet.isSync() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_USER_ID, tweet.getUser().getUid());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE, tweet.isFavorite() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE_COUNT, tweet.getFavoriteCount());
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED, tweet.isRetweeted() ? 1 : 0);
            contentValues.put(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED_COUNT, tweet.getRetweetedCount());

            mContentResolver.update(TwitterPersistenceContract.TweetEntry.CONTENT_URI, contentValues,
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID + "=?",
                    new String[]{"" + tempUid});

        } catch (Throwable ex) {
            Log.e(LOG_TAG, "can'not update tweet: " + tweet.toString());
            Log.e(LOG_TAG, ex.toString());
        }
    }

    @Override
    public void addCurrentUser(final User user) {
        if (user == null || hashUsers.containsKey(user.getUid())) return;
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                createUserEntry(user);
            }
        });
    }

    @Override
    public void destroy() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
    }

    @Override
    public void loadCurrentUser(LoadCurrentUserCallback callback) {
        throw new UnsupportedOperationException("operation is not supported on local data source");
    }

    private void preloadAllUsers() {
        Log.d(LOG_TAG, "Pre-loading all users from local data source into hash map");
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(TwitterPersistenceContract.UserEntry.CONTENT_URI,
                    TwitterPersistenceContract.UserEntry.USER_COLUMNS, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                hashUsers.clear();
                do {
                    User user = parseUser(cursor);
                    hashUsers.put(user.getUid(), user);
                }
                while (cursor.moveToNext());
            }
        } finally {
            Log.d(LOG_TAG, "hash users size: " + hashUsers.size());
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
    }

    private Tweet parseTweet(Cursor cursor) {
        Tweet tweet = new Tweet();
        tweet.setUid(cursor.getLong(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID)));
        tweet.setBody(cursor.getString(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_BODY)));
        tweet.setCreatedAt(cursor.getString(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_DATE_CREATED)));
        tweet.setSync(cursor.getInt(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_SYNC)) == 1);
        tweet.setFavorite(cursor.getInt(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE)) == 1);
        tweet.setRetweeted(cursor.getInt(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED)) == 1);
        tweet.setFavoriteCount(cursor.getInt(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE_COUNT)));
        tweet.setRetweetedCount(cursor.getInt(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED_COUNT)));

        long userId = cursor.getLong(cursor.getColumnIndex(TwitterPersistenceContract.TweetEntry.COLUMN_NAME_USER_ID));
        tweet.setUser(hashUsers.get(userId));
        return tweet;
    }

    private User parseUser(Cursor cursor) {
        User user = new User();
        user.setUid(cursor.getLong(cursor.getColumnIndex(TwitterPersistenceContract.UserEntry.COLUMN_NAME_ENTRY_ID)));
        user.setName(cursor.getString(cursor.getColumnIndex(TwitterPersistenceContract.UserEntry.COLUMN_NAME_NAME)));
        user.setScreenName(cursor.getString(cursor.getColumnIndex(TwitterPersistenceContract.UserEntry.COLUMN_NAME_SCREEN_NAME)));
        user.setProfileImageUrl(cursor.getString(cursor.getColumnIndex(TwitterPersistenceContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_URL)));
        return user;
    }
}