package me.elmira.simpletwitterclient.model.source;

import android.support.annotation.NonNull;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;

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

    interface LoadCurrentUserCallback {
        void onUserLoaded(User user);

        void onFailure();
    }

    List<Tweet> preloadTweets();

    List<Tweet> loadTweets(long sinceId, long maxId);

    void loadTweets(long sinceId, long maxId, LoadTweetsCallback callback);

    Tweet loadTweet(@NonNull long tweetId);

    void saveTweets(List<Tweet> tweets);

    void createTweet(Tweet tweet, CreateTweetCallback callback);

    void updateTweet(Tweet tweet, long tempId, UpdateTweetCallback callback);

    void loadCurrentUser(LoadCurrentUserCallback callback);

    void addCurrentUser(User user);

    void destroy();
}