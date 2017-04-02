package me.elmira.simpletwitterclient.userdetails;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.ref.WeakReference;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.followers.FollowersFragment;
import me.elmira.simpletwitterclient.following.FollowingFragment;
import me.elmira.simpletwitterclient.tweets.TweetsFragment;
import me.elmira.simpletwitterclient.util.SmartFragmentStatePagerAdapter;

/**
 * Created by elmira on 3/28/17.
 */

public class UserDetailsFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {

    private static final int TAB_COUNTS = 3;
    private static final int[] tabNames = new int[]{R.string.tab_tweets, R.string.tab_following, R.string.tab_followers};

    private WeakReference<Context> context;
    private WeakReference<UserDetailsTabListener> listener;

    private static final int TAB_POSITION_TWEETS = 0;
    private static final int TAB_POSITION_FOLLOWING = 1;
    private static final int TAB_POSITION_FOLLOWERS = 2;

    private long userId = -1;

    public interface UserDetailsTabListener {
        long getUserId();
    }

    public UserDetailsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = new WeakReference<Context>(context);

        if (context instanceof UserDetailsTabListener) {
            userId = ((UserDetailsTabListener) context).getUserId();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (context.get() != null) {
            return context.get().getString(tabNames[position]);
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == TAB_POSITION_TWEETS) {
            return TweetsFragment.newInstance(userId);
        }
        else if (position == TAB_POSITION_FOLLOWING) {
            return FollowingFragment.newInstance(userId);
        }
        else if (position == TAB_POSITION_FOLLOWERS) {
            return FollowersFragment.newInstance(userId);
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNTS;
    }
}
