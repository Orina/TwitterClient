package me.elmira.simpletwitterclient.following;

import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/29/17.
 */

public class FollowingPresenter implements FollowingContract.Presenter {

    private static final String LOG_TAG = "FollowingPresenter";

    private TwitterRepository mTwitterRepository;
    private FollowingContract.View mView;

    public FollowingPresenter(TwitterRepository mTwitterRepository, FollowingContract.View mView) {
        this.mTwitterRepository = mTwitterRepository;
        this.mView = mView;

        this.mView.setPresenter(this);
    }

    @Override
    public void loadFollowingNext(long userId, long cursor) {
        mTwitterRepository.loadFollowing(userId, cursor, new TwitterDataSource.LoadUserCursorCollectionCallback() {
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
    public void loadFollowingPrev(long userId, long cursor) {

        mTwitterRepository.loadFollowing(userId, cursor, new TwitterDataSource.LoadUserCursorCollectionCallback() {
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