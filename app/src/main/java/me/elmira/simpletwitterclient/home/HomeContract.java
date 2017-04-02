package me.elmira.simpletwitterclient.home;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.mvp.BasePresenter;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/29/17.
 */

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void onTweetPosted(Tweet tweet);

        void onLoadingFailure();

        void onTweetReplyCreated(Tweet tweet);

        void onCurrentUserLoaded(User user);

        void onCurrentUserLoadFailed();

        void setLoadingIndicator(boolean loading);
    }

    interface Presenter extends BasePresenter {

        void postNewTweet(String body);

        void createReplyTweet(Tweet tweet);

        void loadCurrentUser();
    }
}
