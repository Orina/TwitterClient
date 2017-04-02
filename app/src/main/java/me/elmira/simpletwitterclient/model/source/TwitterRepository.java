package me.elmira.simpletwitterclient.model.source;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.UserCursoredCollection;

/**
 * Created by elmira on 3/22/17.
 */

public class TwitterRepository implements TwitterDataSource {

    private static final String LOG_TAG = "TwitterRepository";

    private TwitterDataSource mLocalDataSource;
    private TwitterDataSource mRemoteDataSource;

    public static TwitterRepository instance;
    private User mCurrentUser;

    private LinkedHashMap<Long, Tweet> mCachedTweets;

    private TwitterRepository(TwitterDataSource localDataSource, TwitterDataSource remoteDataSource) {
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;

        mCachedTweets = new LinkedHashMap<>();
    }

    public static TwitterRepository getInstance(TwitterDataSource localDataSource, TwitterDataSource remoteDataSource) {
        if (instance == null) {
            instance = new TwitterRepository(localDataSource, remoteDataSource);
        }
        return instance;
    }

    @Override
    public void loadUserTweets(final long uid, final long sinceId, final long maxId, final LoadTweetsCallback callback) {
        //loading tweets only from remote data source
        mRemoteDataSource.loadUserTweets(uid, sinceId, maxId, new LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                callback.onTweetsLoaded(tweets);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadHomeTweets(final long sinceId, long maxId, final LoadTweetsCallback callback) {
        //loading tweets only from remote data source
        mRemoteDataSource.loadHomeTweets(sinceId, maxId, new LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                addTweetsToCache(sinceId > 0, tweets);
                //save loaded tweets to memory cache
                callback.onTweetsLoaded(tweets);
                //save loaded tweets on local data source
                saveTweets(tweets);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });

        if (mCurrentUser == null) {
            loadCurrentUser(new LoadUserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    //do nothing
                }

                @Override
                public void onFailure() {
                    //do nothing
                }
            });
        }
    }

    @Override
    public Tweet loadTweet(@NonNull long tweetId) {
        return null;
    }

    @Override
    public List<Tweet> preloadTweets() {
        // pre-load tweets from local data source only
        List<Tweet> tweets = mLocalDataSource.preloadTweets();
        addTweetsToCache(false, tweets);
        return tweets;
    }

    public List<Tweet> getCachedTweets() {
        return mCachedTweets == null ? null : new ArrayList<>(mCachedTweets.values());
    }

    public boolean isCachedTweetsAvailable() {
        return mCachedTweets != null && mCachedTweets.size() > 0;
    }

    @Override
    public void saveTweets(List<Tweet> tweets) {
        mLocalDataSource.saveTweets(tweets);
    }

    @Override
    public void createTweet(final Tweet tweet, final CreateTweetCallback callback) {
        Log.d(LOG_TAG, "createTweet() " + tweet.toString());

        mRemoteDataSource.createTweet(tweet, new CreateTweetCallback() {
            @Override
            public void onTweetCreated(Tweet newTweet) {
                newTweet.setSync(true);
                Log.d(LOG_TAG, "onTweetCreated successfully. New tweet: " + newTweet.toString());

                callback.onTweetCreated(newTweet);

                mLocalDataSource.createTweet(newTweet, null);
                addTweetToCacheTop(newTweet);
            }

            @Override
            public void onFailure() {
                Log.e(LOG_TAG, "can't create new tweet");
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadCurrentUser(final LoadUserCallback callback) {
        Log.d(LOG_TAG, "load current user...");
        if (mCurrentUser != null) {
            Log.d(LOG_TAG, "current user is not null, loading it immediately");
            callback.onUserLoaded(mCurrentUser);
            return;
        }
        mRemoteDataSource.loadCurrentUser(new LoadUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                Log.d(LOG_TAG, "Current user is loaded successfully!!! uid:" + user.getUid());
                mCurrentUser = user;
                callback.onUserLoaded(user);
                mLocalDataSource.addCurrentUser(user);
            }

            @Override
            public void onFailure() {
                Log.e(LOG_TAG, "Can't load current user");
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadUser(long userId, final LoadUserCallback callback) {
        Log.d(LOG_TAG, "Loading user ... uid: " + userId);
        mRemoteDataSource.loadUser(userId, new LoadUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                Log.d(LOG_TAG, "User was successfully loaded: " + user.toString());
                callback.onUserLoaded(user);
            }

            @Override
            public void onFailure() {
                Log.e(LOG_TAG, "can't load user");
                callback.onFailure();
            }
        });
    }

    public void syncTweets() {
        //mLocalDataSource.
    }

    @Override
    public void getMentions(long sinceId, long maxId, final LoadTweetsCallback callback) {
        //loading mentions tweets only from remote data source
        mRemoteDataSource.getMentions(sinceId, maxId, new LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                callback.onTweetsLoaded(tweets);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void addCurrentUser(User user) {
        throw new UnsupportedOperationException("operation is not supported on repository object: user is created via local data source.");
    }

    @Override
    public void updateTweet(Tweet tweet, long tempId, UpdateTweetCallback callback) {
        throw new UnsupportedOperationException("operation is not supported on repository object: user is created via local data source.");
    }

    @Override
    public void loadFollowing(long userId, long cursor, final LoadUserCursorCollectionCallback callback) {
        mRemoteDataSource.loadFollowing(userId, cursor, new LoadUserCursorCollectionCallback() {
            @Override
            public void onUsersLoaded(UserCursoredCollection collection) {
                Log.d(LOG_TAG, "Loaded following: prev cursor: "+collection.getPreviousCursor()+", next cursor: "+collection.getNextCursor());
                callback.onUsersLoaded(collection);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadFollowers(long userId, long cursor, final LoadUserCursorCollectionCallback callback) {
        mRemoteDataSource.loadFollowers(userId, cursor, new LoadUserCursorCollectionCallback() {
            @Override
            public void onUsersLoaded(UserCursoredCollection collection) {
                callback.onUsersLoaded(collection);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void searchTweets(String query, long sinceId, long maxId, SearchTweetsCallback callback) {
        mRemoteDataSource.searchTweets(query, sinceId, maxId, callback);
    }

    @Override
    public void destroy() {
        mLocalDataSource.destroy();
        mRemoteDataSource.destroy();
    }

    private void addTweetToCacheTop(Tweet tweet) {
        if (tweet == null) return;
        LinkedHashMap newMap = new LinkedHashMap();
        newMap.put(tweet.getUid(), tweet);
        newMap.putAll(mCachedTweets);
        mCachedTweets = newMap;
    }

    private void addTweetsToCache(boolean begin, List<Tweet> tweets) {
        if (tweets == null || tweets.size() == 0) return;
        if (mCachedTweets == null || mCachedTweets.size() == 0) {
            begin = true;
        }
        if (begin) {
            LinkedHashMap newMap = new LinkedHashMap();
            for (Tweet tweet : tweets) {
                newMap.put(tweet.getUid(), tweet);
            }
            newMap.putAll(mCachedTweets);
            mCachedTweets = newMap;
        }
        else {
            for (Tweet tweet : tweets) {
                mCachedTweets.put(tweet.getUid(), tweet);
            }
        }
    }

    public void clearCache() {
        mCachedTweets.clear();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public long getCurrentUserId() {
        return mCurrentUser == null ? 0 : mCurrentUser.getUid();
    }
}