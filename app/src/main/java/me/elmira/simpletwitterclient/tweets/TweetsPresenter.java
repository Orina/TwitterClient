package me.elmira.simpletwitterclient.tweets;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/27/17.
 */

public class TweetsPresenter implements TweetsContract.Presenter {

    private static final String LOG_TAG = "TweetsPresenter";

    private TwitterRepository mTwitterRepository;
    private TweetsContract.View mView;


    public TweetsPresenter(@NonNull TwitterRepository twitterRepository, @NonNull TweetsContract.View view) {

        mTwitterRepository = twitterRepository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loadTweets(long uid, long sinceId, long maxId) {
        Log.d(LOG_TAG, "loadUserTweets()");

        mTwitterRepository.loadUserTweets(uid, sinceId, maxId, new TwitterDataSource.LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                //display loaded tweets on UI
                if (mView != null && mView.isActive()) {
                    mView.addTweets(tweets);
                }
            }

            @Override
            public void onFailure() {
                if (mView != null && mView.isActive()) {
                    mView.onLoadingFailure();
                }
            }
        });
    }

    @Override
    public void destroy() {
        mView = null;
    }
}