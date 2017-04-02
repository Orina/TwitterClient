package me.elmira.simpletwitterclient.home;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.hometimeline.TimelineFragment;
import me.elmira.simpletwitterclient.mentions.MentionsFragment;

/**
 * Created by elmira on 3/27/17.
 */

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int TABS_COUNT = 2;
    private int[] tabNames = new int[]{R.string.tab_home, R.string.tab_mentions};
    private int[] imageResId = new int[]{R.drawable.ic_home_white_24dp, R.drawable.ic_comment_white_24dp};

    public static final int TAB_POSITION_HOME = 0;
    public static final int TAB_POSITION_MENTIONS = 1;

    private WeakReference<Context> context;

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public HomeFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = new WeakReference<Context>(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == TAB_POSITION_HOME) {
            return TimelineFragment.newInstance();
        }
        else if (position == TAB_POSITION_MENTIONS) {
            return MentionsFragment.newInstance();
        }

        else return null;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (context.get() != null) {
            Drawable image = ContextCompat.getDrawable(context.get(), imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            image.mutate().setColorFilter(ContextCompat.getColor(context.get(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            SpannableString sb = new SpannableString("  " + context.get().getString(tabNames[position]));
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
        return null;
    }

    @Override
    public int getCount() {
        return TABS_COUNT;
    }
}