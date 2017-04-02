package me.elmira.simpletwitterclient.model.source;

import android.support.annotation.NonNull;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.UserCursoredCollection;

/**
 * Created by elmira on 3/22/17.
 */

public interface TwitterDataSource {

    interface LoadTweetsCallback {
        void onTweetsLoaded(List<Tweet> tweets);

        void onFailure();
    }

    interface CreateTweetCallback {
        void onTweetCreated(Tweet tweet);

        void onFailure();
    }

    interface UpdateTweetCallback {
        void onTweetUpdataed(Tweet tweet);

        void onFailure();
    }

    interface LoadUserCallback {
        void onUserLoaded(User user);

        void onFailure();
    }

    interface LoadUserCursorCollectionCallback {
        void onUsersLoaded(UserCursoredCollection collection);

        void onFailure();
    }

    interface SearchTweetsCallback {
        void onTweetsFound(List<Tweet> tweets);

        void onFailure();
    }

    List<Tweet> preloadTweets();

    void loadHomeTweets(long sinceId, long maxId, LoadTweetsCallback callback);

    void loadUserTweets(long uid, long sinceId, long maxId, LoadTweetsCallback callback);

    Tweet loadTweet(@NonNull long tweetId);

    void saveTweets(List<Tweet> tweets);

    void createTweet(Tweet tweet, CreateTweetCallback callback);

    void updateTweet(Tweet tweet, long tempId, UpdateTweetCallback callback);

    void loadCurrentUser(LoadUserCallback callback);

    void addCurrentUser(User user);

    void getMentions(long sinceId, long maxId, LoadTweetsCallback callback);

    void loadUser(long userId, LoadUserCallback callback);

    void loadFollowing(long userId, long cursor, LoadUserCursorCollectionCallback callback);

    void loadFollowers(long userId, long cursor, LoadUserCursorCollectionCallback callback);

    void searchTweets(String query, long sinceId, long maxId, SearchTweetsCallback callback);

    void destroy();
}