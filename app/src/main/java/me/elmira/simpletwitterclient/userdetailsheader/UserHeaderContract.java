package me.elmira.simpletwitterclient.userdetailsheader;

import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/28/17.
 */

public interface UserHeaderContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showLoadingIndicator(boolean isLoading);

        void showUserHeader(User user);

        void onLoadingFailure();
    }

    interface Presenter {

        void loadUserDetails(long userId);
    }
}
