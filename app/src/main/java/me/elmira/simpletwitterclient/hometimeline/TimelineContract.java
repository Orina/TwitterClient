package me.elmira.simpletwitterclient.hometimeline;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.mvp.BasePresenter;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/22/17.
 */

public interface TimelineContract {

    interface View extends BaseView<Presenter> {

        void addTweets(List<Tweet> tweets);

        void setTweets(List<Tweet> tweets);

        void onLoadingFailure();

        boolean isActive();

        void onTweetRemotePosted(Tweet tweet);

        void setLoadingIndicator(boolean loading);
    }

    interface Presenter extends BasePresenter {

        void loadTweets(long sinceId, long maxId);
    }
}