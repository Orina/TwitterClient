package me.elmira.simpletwitterclient.home;

import android.support.annotation.NonNull;
import android.util.Log;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/29/17.
 */

public class HomePresenter implements HomeContract.Presenter {

    private static final String LOG_TAG = "HomePresenter";

    private TwitterRepository mTwitterRepository;
    private HomeContract.View mView;

    public HomePresenter(@NonNull TwitterRepository twitterRepository, @NonNull HomeContract.View view) {
        mTwitterRepository = twitterRepository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void postNewTweet(String body) {
        if (mView!=null){
            mView.setLoadingIndicator(true);
        }
        final Tweet tweet = new Tweet();
        tweet.setBody(body);
        tweet.setUid(0);

        mTwitterRepository.createTweet(tweet, new TwitterDataSource.CreateTweetCallback() {
            @Override
            public void onTweetCreated(Tweet newTweet) {
                Log.d(LOG_TAG, "onTweetCreated() " + newTweet.toString());
                if (mView != null) {
                    //update tweet sync status and uid in UI
                    mView.onTweetPosted(newTweet);
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onFailure() {
                Log.d(LOG_TAG, "onFailure()");
                if (mView != null) {
                    mView.onLoadingFailure();
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

    @Override
    public void createReplyTweet(Tweet tweet) {
        mView.setLoadingIndicator(true);

        mTwitterRepository.createTweet(tweet, new TwitterDataSource.CreateTweetCallback() {
            @Override
            public void onTweetCreated(Tweet newTweet) {
                Log.d(LOG_TAG, "createReplyTweet() " + newTweet.toString());
                if (mView != null) {
                    mView.onTweetReplyCreated(newTweet);
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onFailure() {
                Log.d(LOG_TAG, "onFailure()");
                if (mView != null) {
                    mView.onLoadingFailure();
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

    @Override
    public void loadCurrentUser() {
        mView.setLoadingIndicator(true);

        mTwitterRepository.loadCurrentUser(new TwitterDataSource.LoadUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (mView != null) {
                    mView.onCurrentUserLoaded(user);
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onFailure() {
                if (mView != null) {
                    mView.onCurrentUserLoadFailed();
                    mView.setLoadingIndicator(false);
                }
            }
        });
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void start() {
        // do nothing
    }
}
