package me.elmira.simpletwitterclient.followers;

import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/31/17.
 */

public interface FollowersContract {

    interface View extends BaseView<Presenter> {

        void onLoadUsersNext(UserCursoredCollection collection);

        void onLoadUsersPrev(UserCursoredCollection collection);

        void onLoadingFailure();

        boolean isActive();
    }

    interface Presenter {

        void loadFollowersNext(long userId, long cursor);

        void loadFollowersPrev(long userId, long cursor);

        void destroy();
    }
}
