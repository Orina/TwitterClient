package me.elmira.simpletwitterclient.following;

import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.mvp.BaseView;

/**
 * Created by elmira on 3/29/17.
 */

public interface FollowingContract {

    interface View extends BaseView<Presenter> {

        void onLoadUsersNext(UserCursoredCollection collection);

        void onLoadUsersPrev(UserCursoredCollection collection);

        void onLoadingFailure();

        boolean isActive();
    }

    interface Presenter {

        void loadFollowingNext(long userId, long cursor);

        void loadFollowingPrev(long userId, long cursor);

        void destroy();
    }
}
