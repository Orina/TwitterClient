package me.elmira.simpletwitterclient.timeline;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.mvp.BasePresenter;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/22/17.
 */

public interface Contract {

    interface View extends BaseView<Presenter> {

        void addTweets(List<Tweet> tweets);

        void setTweets(List<Tweet> tweets);

        long getNextTweetId();

        void onTweetJustPosted(Tweet tweet);

        void onTweetRemotePosted(long tempId, Tweet tweet);

        void onLoadingFailure();

        //void onNewTweetPosted(Tweet tweet);
    }


    interface Presenter extends BasePresenter {

        void loadTweets(long sinceId, long maxId);

        void postNewTweet(String body);

        void destroy();
    }

}
