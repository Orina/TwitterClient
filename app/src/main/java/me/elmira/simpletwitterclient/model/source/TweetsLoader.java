package me.elmira.simpletwitterclient.model.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;

/**
 * Created by elmira on 3/23/17.
 */

public class TweetsLoader extends AsyncTaskLoader<List<Tweet>> {

    private static final String LOG_TAG = "TweetsLoader";

    private TwitterRepository mTwitterRepository;

    public TweetsLoader(Context context, @NonNull TwitterRepository twitterRepository) {
        super(context);
        mTwitterRepository = twitterRepository;
    }

    @Override
    public List<Tweet> loadInBackground() {
        Log.d(LOG_TAG, "*** loadInBackground() *** " + toString());
        return mTwitterRepository.preloadTweets();
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "*** onStartLoading() *** ");

        if (mTwitterRepository.isCachedTweetsAvailable()) {
            Log.d(LOG_TAG, "deliver tweets from memory cache");
            deliverResult(mTwitterRepository.getCachedTweets());
        }
        else {
            Log.d(LOG_TAG, "deliver tweets from local data source");
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Tweet> data) {
        Log.d(LOG_TAG, "*** deliverResult() *** ");
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        Log.d(LOG_TAG, "*** onStopLoading() *** ");
        cancelLoad();
    }

    @Override
    public void onReset() {
        Log.d(LOG_TAG, "*** onReset() *** ");
        this.onStopLoading();
    }
}