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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TwitterDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "TwitterClient.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ", ";

    private static final String NOT_NULL = " NOT NULL ";

    private static final String SQL_CREATE_TWEET_TABLE =
            "CREATE TABLE " + TwitterPersistenceContract.TweetEntry.TABLE_NAME + " (" +
                    TwitterPersistenceContract.TweetEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + " UNIQUE" + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_BODY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_DATE_CREATED + TEXT_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_SYNC + BOOLEAN_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE + BOOLEAN_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_FAVORITE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED + BOOLEAN_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.TweetEntry.COLUMN_NAME_RETWEETED_COUNT + INTEGER_TYPE + COMMA_SEP +
                    "FOREIGN KEY (" + TwitterPersistenceContract.TweetEntry.COLUMN_NAME_USER_ID + ") " +
                    "REFERENCES " + TwitterPersistenceContract.UserEntry.TABLE_NAME + " (" + TwitterPersistenceContract.TweetEntry._ID + "));";

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + TwitterPersistenceContract.UserEntry.TABLE_NAME + " (" +
                    TwitterPersistenceContract.UserEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TwitterPersistenceContract.UserEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + " UNIQUE" + COMMA_SEP +
                    TwitterPersistenceContract.UserEntry.COLUMN_NAME_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    TwitterPersistenceContract.UserEntry.COLUMN_NAME_SCREEN_NAME + TEXT_TYPE + COMMA_SEP +
                    TwitterPersistenceContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_URL + TEXT_TYPE +
                    " )";

    public TwitterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_TWEET_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
