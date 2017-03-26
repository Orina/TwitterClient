package me.elmira.simpletwitterclient.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.Date;
import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TweetsLoader;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/22/17.
 */

public class Presenter implements Contract.Presenter,
        LoaderManager.LoaderCallbacks<List<Tweet>> {

    private static final String LOG_TAG = "Presenter";

    private TwitterRepository mTwitterRepository;
    private Contract.View mView;

    private LoaderManager mLoaderManager;
    private TweetsLoader mLoader;

    private final static int TWEETS_QUERY = 1;

    public Presenter(@NonNull LoaderManager loaderManager, @NonNull TweetsLoader loader,
                     @NonNull TwitterRepository twitterRepository, @NonNull Contract.View view) {

        mTwitterRepository = twitterRepository;
        mView = view;

        mLoaderManager = loaderManager;
        mLoader = loader;

        mView.setPresenter(this);
    }

    @Override
    public void loadTweets(long sinceId, long maxId) {
        Log.d(LOG_TAG, "loadTweets()");

        mTwitterRepository.loadTweets(sinceId, maxId, new TwitterDataSource.LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                //display loaded tweets on UI
                mView.addTweets(tweets);
            }

            @Override
            public void onFailure() {
                mView.onLoadingFailure();
            }
        });
    }

    @Override
    public void start() {
        Log.d(LOG_TAG, "start()");
        if (mLoaderManager.getLoader(TWEETS_QUERY) == null) {
            mLoaderManager.initLoader(TWEETS_QUERY, null, this);
        }
        else {
            mLoaderManager.restartLoader(TWEETS_QUERY, null, this);
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
        // tweets are from memory cache or local data source
        mView.setTweets(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        //do nothing
    }

    @Override
    public void postNewTweet(String body) {
        final long nextTweetId = mView.getNextTweetId();

        Tweet tweet = new Tweet();
        tweet.setBody(body);
        tweet.setUid(nextTweetId);
        tweet.setSync(false);
        tweet.setCreatedAt(new Date());
        tweet.setUser(mTwitterRepository.getCurrentUser());

        mView.onTweetJustPosted(tweet);

        mTwitterRepository.createTweet(tweet, new TwitterDataSource.CreateTweetCallback() {
            @Override
            public void onTweetCreated(Tweet newTweet) {
                //update tweet sync status and uid in UI
                mView.onTweetRemotePosted(nextTweetId, newTweet);
            }

            @Override
            public void onFailure() {
                mView.onLoadingFailure();
            }
        });
    }

    @Override
    public void destroy() {
        mLoader.onReset();
        mLoaderManager.destroyLoader(TWEETS_QUERY);
    }
}
