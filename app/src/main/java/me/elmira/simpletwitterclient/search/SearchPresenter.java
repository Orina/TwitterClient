package me.elmira.simpletwitterclient.search;

import android.util.Log;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/31/17.
 */

public class SearchPresenter implements SearchContract.Presenter {

    private static final String LOG_TAG = "SearchPresenter";

    private TwitterRepository mTwitterRepository;
    private SearchContract.View mView;

    public SearchPresenter(TwitterRepository mTwitterRepository, SearchContract.View mView) {
        this.mTwitterRepository = mTwitterRepository;
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void searchTweets(String query, long sinceId, long maxId) {
        Log.d(LOG_TAG, "Start search ... query: " + query);
        if (mView == null) return;

        mView.setLoadingIndicator(true);

        mTwitterRepository.searchTweets(query, sinceId, maxId, new TwitterDataSource.SearchTweetsCallback() {
            @Override
            public void onTweetsFound(List<Tweet> tweets) {
                Log.d(LOG_TAG, "Tweets were found: " + (tweets == null ? 0 : tweets.size()));
                if (mView != null) {
                    mView.setLoadingIndicator(false);
                    mView.onSearchResult(tweets);
                }
            }

            @Override
            public void onFailure() {
                Log.e(LOG_TAG, "can't search tweets");
                if (mView != null) {
                    mView.setLoadingIndicator(false);
                    mView.onSearchFailure();
                }
            }
        });
    }

    @Override
    public void destroy() {
        mView = null;
    }
}
