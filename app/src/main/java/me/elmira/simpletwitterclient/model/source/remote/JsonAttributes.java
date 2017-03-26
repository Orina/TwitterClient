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
        String ENTITIES = "entities";
    }
}
