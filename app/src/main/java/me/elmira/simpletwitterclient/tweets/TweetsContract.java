package me.elmira.simpletwitterclient.tweets;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/27/17.
 */

public interface TweetsContract {

    interface View extends BaseView<Presenter> {

        void addTweets(List<Tweet> tweets);

        void onLoadingFailure();

        boolean isActive();
    }

    interface Presenter {

        void loadTweets(long uid, long sinceId, long maxId);

        void destroy();
    }
}