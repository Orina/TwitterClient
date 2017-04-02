package me.elmira.simpletwitterclient.mentions;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/29/17.
 */

public interface MentionsContract {

    interface View extends BaseView<Presenter> {
        void addTweets(List<Tweet> tweets);

        void onLoadingFailure();

        boolean isActive();

        void setLoadingIndicator(boolean loading);
    }

    interface Presenter {
        void loadMentions(long sinceId, long maxId);

        void destroy();
    }
}