package me.elmira.simpletwitterclient.hometimeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TweetsLoader;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/22/17.
 */

public class TimelinePresenter implements TimelineContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Tweet>> {

    private static final String LOG_TAG = "TimelinePresenter";

    private TwitterRepository mTwitterRepository;
    private TimelineContract.View mView;

    private LoaderManager mLoaderManager;
    private TweetsLoader mLoader;

    private final static int TWEETS_QUERY = 1;

    public TimelinePresenter(@NonNull LoaderManager loaderManager, @NonNull TweetsLoader loader,
                             @NonNull TwitterRepository twitterRepository, @NonNull TimelineContract.View view) {
        mTwitterRepository = twitterRepository;
        mView = view;

        mLoaderManager = loaderManager;
        mLoader = loader;

        mView.setPresenter(this);
    }

    @Override
    public void loadTweets(long sinceId, long maxId) {
        Log.d(LOG_TAG, "loadUserTweets()");
        if (mView == null || !mView.isActive()) return;
        mView.setLoadingIndicator(true);

        mTwitterRepository.loadHomeTweets(sinceId, maxId, new TwitterDataSource.LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                //display loaded tweets on UI
                if (mView != null && mView.isActive()) {
                    mView.addTweets(tweets);
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onFailure() {
                if (mView != null && mView.isActive()) {
                    mView.onLoadingFailure();
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

    @Override
    public void start() {
        Log.d(LOG_TAG, "start()");
        if (mLoaderManager.getLoader(TWEETS_QUERY) == null) {
            mLoaderManager.initLoader(TWEETS_QUERY, null, this);
        }
    }

    @Override
    public Loader<List<Tweet>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader()");
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Tweet>> loader, List<Tweet> data) {
        Log.d(LOG_TAG, "onLoadFinished()");
        if (mView == null || !mView.isActive()) return;

        // tweets are loaded from memory cache or local data source
        mView.setTweets(data);

        //start loading tweets from remote data source, take into account the last loaded tweet id
        long sinceId = 0;
        if (data != null && data.size() > 0) {
            sinceId = data.get(0).getUid();
        }
        Log.d(LOG_TAG, "Loading remote tweets after memory cache/local data source has finished loading");
        loadTweets(sinceId, 0);
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        //do nothing
    }

    @Override
    public void destroy() {
        mView = null;
        mLoader.onReset();
        mLoaderManager.destroyLoader(TWEETS_QUERY);
    }
}