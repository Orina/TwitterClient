package me.elmira.simpletwitterclient.model.source.remote;

/**
 * Created by elmira on 3/22/17.
 */

public interface JsonAttributes {

    interface User {
        String NAME = "name";
        String UID = "id";
        String PROFILE_IMAGE_URL = "profile_image_url";
        String SCREEN_NAME = "screen_name";
        String FOLLOWERS_COUNT = "followers_count";
        String FOLLOWING_COUNT = "friends_count";
        String FOLLOWING = "following";
        String BANNER_URL = "profile_banner_url";
        String BACKGROUND_IMAGE_URL = "profile_background_image_url";
        String DESCRIPTION = "description";
    }

    interface Tweet {
        String BODY = "text";
        String UID = "id";
        String CREATED_AT = "created_at";
        String USER = "user";
        String FAVORITE = "favorited";
        String FAVORITE_COUNT = "favorite_count";
        String RETWEET_COUNT = "retweet_count";
        String RETWEETED = "retweeted";
        String MEDIA = "media";
    }

    interface UserCursorCollection {
        String NEXT_CURSOR = "next_cursor";
        String PREV_CURSOR = "previous_cursor";
        String USERS = "users";
    }

    String ENTITIES = "entities";
    String STATUSES = "statuses";
}
