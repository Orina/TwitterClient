package me.elmira.simpletwitterclient.userdetailsheader;

import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/28/17.
 */

public class UserHeaderPresenter implements UserHeaderContract.Presenter {

    private UserHeaderContract.View mView;
    private TwitterRepository mRepository;

    public UserHeaderPresenter(UserHeaderContract.View mView, TwitterRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
        mView.setPresenter(this);
    }

    @Override
    public void loadUserDetails(long userId) {
        mView.showLoadingIndicator(true);

        mRepository.loadUser(userId, new TwitterDataSource.LoadUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (mView.isActive()) {
                    mView.showLoadingIndicator(false);
                    mView.showUserHeader(user);
                }
            }

            @Override
            public void onFailure() {
                if (mView.isActive()) {
                    mView.showLoadingIndicator(false);
                    mView.onLoadingFailure();
                }
            }
        });
    }
}
