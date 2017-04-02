package me.elmira.simpletwitterclient.search;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/31/17.
 */

public interface SearchContract {

    interface View extends BaseView<Presenter> {
        void onSearchResult(List<Tweet> tweets);

        void onSearchFailure();

        void setLoadingIndicator(boolean loading);
    }

    interface Presenter {
        void searchTweets(String query, long sinceId, long maxId);

        void destroy();
    }
}
