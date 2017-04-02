package me.elmira.simpletwitterclient.followers;

import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/31/17.
 */

public class FollowersPresenter implements FollowersContract.Presenter {

    private TwitterRepository mTwitterRepository;
    private FollowersContract.View mView;

    public FollowersPresenter(TwitterRepository mTwitterRepository, FollowersContract.View mView) {
        this.mTwitterRepository = mTwitterRepository;
        this.mView = mView;
    }

    @Override
    public void loadFollowersNext(long userId, long cursor) {
        mTwitterRepository.loadFollowers(userId, cursor, new TwitterDataSource.LoadUserCursorCollectionCallback() {
            @Override
            public void onUsersLoaded(UserCursoredCollection collection) {
                if (mView != null && mView.isActive()) {
                    mView.onLoadUsersNext(collection);
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
    public void loadFollowersPrev(long userId, long cursor) {
         mTwitterRepository.loadFollowers(userId, cursor, new TwitterDataSource.LoadUserCursorCollectionCallback() {
            @Override
            public void onUsersLoaded(UserCursoredCollection collection) {
                if (mView != null && mView.isActive()) {
                    mView.onLoadUsersPrev(collection);
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
