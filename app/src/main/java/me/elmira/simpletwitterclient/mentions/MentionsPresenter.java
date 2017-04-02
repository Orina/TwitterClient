package me.elmira.simpletwitterclient.mentions;

import java.util.List;

import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/29/17.
 */

public class MentionsPresenter implements MentionsContract.Presenter {

    private TwitterRepository repository;
    private MentionsContract.View mView;

    public MentionsPresenter(TwitterRepository repository, MentionsContract.View mView) {
        this.repository = repository;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void loadMentions(long sinceId, long maxId) {
        if (mView == null || !mView.isActive()) return;
        mView.setLoadingIndicator(true);

        repository.getMentions(sinceId, maxId, new TwitterDataSource.LoadTweetsCallback() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets) {
                //display loaded tweets on UI
                if (mView != null && mView.isActive()) {
                    mView.setLoadingIndicator(false);
                    mView.addTweets(tweets);
                }
            }

            @Override
            public void onFailure() {
                if (mView != null && mView.isActive()) {
                    mView.setLoadingIndicator(false);
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