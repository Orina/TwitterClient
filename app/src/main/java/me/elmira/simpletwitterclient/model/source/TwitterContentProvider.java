package me.elmira.simpletwitterclient.model.source;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.elmira.simpletwitterclient.model.source.local.TwitterPersistenceContract;
import me.elmira.simpletwitterclient.model.source.local.TwitterDbHelper;

/**
 * Created by elmira on 3/22/17.
 */

public class TwitterContentProvider extends ContentProvider {

    private static final int TWEET = 100;
    private static final int TWEET_ID = 101;
    private static final int USER = 200;
    private static final int USER_ID = 202;

    private TwitterDbHelper mTwitterDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mTwitterDbHelper = new TwitterDbHelper(getContext());
        return true;
    }

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = TwitterPersistenceContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, TwitterPersistenceContract.TweetEntry.PATH, TWEET);
        matcher.addURI(content, TwitterPersistenceContract.TweetEntry.PATH + "/#", TWEET_ID);
        matcher.addURI(content, TwitterPersistenceContract.UserEntry.PATH, USER);
        matcher.addURI(content, TwitterPersistenceContract.UserEntry.PATH + "/#", USER_ID);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mTwitterDbHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TWEET:
                retCursor = db.query(TwitterPersistenceContract.TweetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TWEET_ID:
                long id = ContentUris.parseId(uri);
                retCursor = db.query(TwitterPersistenceContract.TweetEntry.TABLE_NAME,
                        projection,
                        TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID + "=?",
                        new String[]{"" + id},
                        null,
                        null,
                        sortOrder);
                break;
            case USER:
                retCursor = db.query(TwitterPersistenceContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER_ID:
                id = ContentUris.parseId(uri);
                retCursor = db.query(TwitterPersistenceContract.UserEntry.TABLE_NAME,
                        projection,
                        TwitterPersistenceContract.UserEntry.COLUMN_NAME_ENTRY_ID + "=?",
                        new String[]{"" + id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        ;
        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        //retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mTwitterDbHelper.getWritableDatabase();
        long id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case TWEET:
                id = db.insert(TwitterPersistenceContract.TweetEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TwitterPersistenceContract.TweetEntry.buildUriWith(id);
                }
                else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case USER:
                id = db.insert(TwitterPersistenceContract.UserEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TwitterPersistenceContract.UserEntry.buildUriWith(id);
                }
                else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has changed.
        //getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mTwitterDbHelper.getWritableDatabase();
        int rows = 0;

        switch (sUriMatcher.match(uri)) {
            case TWEET:
                rows = db.update(TwitterPersistenceContract.TweetEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("delete is not supported");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TWEET:
                return TwitterPersistenceContract.TweetEntry.CONTENT_TYPE;
            case TWEET_ID:
                return TwitterPersistenceContract.TweetEntry.CONTENT_ITEM_TYPE;
            case USER:
                return TwitterPersistenceContract.UserEntry.CONTENT_TYPE;
            case USER_ID:
                return TwitterPersistenceContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
