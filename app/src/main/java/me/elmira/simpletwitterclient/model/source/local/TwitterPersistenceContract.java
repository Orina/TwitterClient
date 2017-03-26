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

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract used for the db to save the tasks locally.
 */
public final class TwitterPersistenceContract {

    public static final String CONTENT_AUTHORITY = "me.elmira.simpletwitterclient.provider";

    private static final String CONTENT_SCHEME = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);

    private static final String SEPARATOR = "/";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TwitterPersistenceContract() {
    }

    /* Inner class that defines the table contents */
    public static final class TweetEntry implements BaseColumns {
        public static final String PATH = "tweet";

        public static final String TABLE_NAME = "tweetTable";

        public static final String COLUMN_NAME_ENTRY_ID = "uid";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_DATE_CREATED = "date_created";
        public static final String COLUMN_NAME_SYNC = "sync";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
        public static final String COLUMN_NAME_FAVORITE_COUNT = "favorite_count";
        public static final String COLUMN_NAME_RETWEETED = "retweeted";
        public static final String COLUMN_NAME_RETWEETED_COUNT = "retweeted_count";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH;

        public static String[] TWEETS_COLUMNS = new String[]{
                TweetEntry._ID,
                TweetEntry.COLUMN_NAME_ENTRY_ID,
                TweetEntry.COLUMN_NAME_BODY,
                TweetEntry.COLUMN_NAME_USER_ID,
                TweetEntry.COLUMN_NAME_DATE_CREATED,
                TweetEntry.COLUMN_NAME_SYNC,
                TweetEntry.COLUMN_NAME_FAVORITE,
                TweetEntry.COLUMN_NAME_FAVORITE_COUNT,
                TweetEntry.COLUMN_NAME_RETWEETED,
                TweetEntry.COLUMN_NAME_RETWEETED_COUNT
        };

        public static Uri buildUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class UserEntry implements BaseColumns {
        public static final String PATH = "user";

        public static final String TABLE_NAME = "userTable";

        public static final String COLUMN_NAME_ENTRY_ID = "uid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SCREEN_NAME = "screen_name";
        public static final String COLUMN_NAME_PROFILE_IMAGE_URL = "profile_image_url";

        public static String[] USER_COLUMNS = new String[]{
                UserEntry._ID,
                UserEntry.COLUMN_NAME_ENTRY_ID,
                UserEntry.COLUMN_NAME_NAME,
                UserEntry.COLUMN_NAME_SCREEN_NAME,
                UserEntry.COLUMN_NAME_PROFILE_IMAGE_URL
        };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static Uri buildUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH;

    }

}
