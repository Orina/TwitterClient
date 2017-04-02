package me.elmira.simpletwitterclient.home;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.composetweet.ComposeDialogFragment;
import me.elmira.simpletwitterclient.hometimeline.TimelineFragment;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.replytweet.ReplyTweetFragment;
import me.elmira.simpletwitterclient.search.SearchableActivity;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.util.NetworkUtil;

public class HomeActivity extends AppCompatActivity implements HomeContract.View, ComposeDialogFragment.PostTweetDialogListener,
        ReplyTweetFragment.ReplyTweetDialogListener, LoadingIndicatorListener {

    private static final String LOG_TAG = "HomeActivity";
    public static String TAB_POSITION = "TAB_POSITION";

    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private HomeFragmentPagerAdapter mViewPagerAdapter;

    private HomeContract.Presenter mPresenter;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    private User mCurrentUser;
    private ImageView headerIconView;
    private TextView tvHeaderName;
    private TextView tvHeaderScreenName;

    private MenuItem loadingMenuItem;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar();
        setupNavigationDrawer();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.slidingTabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mPresenter = new HomePresenter(TwitterClientApp.getRepository(), this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtil.isNetworkAvailable(this)) {
            showNetworkNotAvailable();
            setLoadingIndicator(true);
        }
        mPresenter.loadCurrentUser();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION, 0));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        loadingMenuItem = menu.findItem(R.id.action_loading);
        searchMenuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        searchView.setIconifiedByDefault(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComposeDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeDialogFragment fragment = ComposeDialogFragment.newInstance();
        fragment.show(fragmentManager, "compose_fragment");
    }

    @Override
    public void onTweetPost(String inputText) {
        Log.d(LOG_TAG, "onTweetPost: " + inputText);
        mPresenter.postNewTweet(inputText);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onTweetPosted(Tweet tweet) {
        Log.d(LOG_TAG, "onTweetPosted() " + tweet.toString());
        if (mTabLayout.getSelectedTabPosition() == HomeFragmentPagerAdapter.TAB_POSITION_HOME) {

            TimelineFragment timelineFragment = (TimelineFragment)
                    mViewPagerAdapter.getRegisteredFragment(HomeFragmentPagerAdapter.TAB_POSITION_HOME);

            if (timelineFragment != null && timelineFragment.isAdded()) {
                timelineFragment.onTweetRemotePosted(tweet);
            }
        }
    }

    @Override
    public void onLoadingFailure() {
        Log.d(LOG_TAG, "onLoadingFailure()");
        Toast.makeText(this, R.string.network_not_available, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTweetReplied(Tweet tweet) {
        Log.d(LOG_TAG, "onTweetReplied() " + tweet.toString());
        mPresenter.createReplyTweet(tweet);
    }

    @Override
    public void onTweetReplyCreated(Tweet tweet) {
        Toast.makeText(this, R.string.reply_sent_successfully, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentUserLoaded(User user) {
        mCurrentUser = user;

        if (user.getBannerUrl() != null) {
            Picasso.with(this)
                    .load(user.getBannerUrl())
                    .into(headerIconView);
        }

        tvHeaderName.setText(user.getName());
        tvHeaderScreenName.setText(user.getDisplayScreenName());
    }

    @Override
    public void onCurrentUserLoadFailed() {

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigationDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.getDrawerArrowDrawable().mutate().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        mDrawer.addDrawerListener(mDrawerToggle);

        mNavView = (NavigationView) findViewById(R.id.navView);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (mCurrentUser == null) {
                    item.setChecked(true);
                    mDrawer.closeDrawers();
                    return true;
                }

                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        mDrawer.closeDrawer(Gravity.LEFT);
                        Intent intent = new Intent(getBaseContext(), UserDetailsActivity.class);
                        intent.putExtra(UserDetailsActivity.PARAM_USER, mCurrentUser);
                        startActivity(intent);
                        break;

                    case R.id.nav_timeline:
                        mViewPager.setCurrentItem(HomeFragmentPagerAdapter.TAB_POSITION_HOME);
                        break;

                    case R.id.nav_mentions:
                        mViewPager.setCurrentItem(HomeFragmentPagerAdapter.TAB_POSITION_MENTIONS);
                        break;
                }

                item.setChecked(true);
                mDrawer.closeDrawers();

                return true;
            }
        });

        View headerRoot = mNavView.getHeaderView(0);
        headerIconView = (ImageView) headerRoot.findViewById(R.id.ivDrawerHeader);
        tvHeaderName = (TextView) headerRoot.findViewById(R.id.tvUserName);
        tvHeaderScreenName = (TextView) headerRoot.findViewById(R.id.tvUserScreenName);
    }

    @Override
    public void setLoadingIndicator(boolean loading) {
        if (loadingMenuItem != null) {
            loadingMenuItem.setVisible(loading);
        }
    }

    private void showNetworkNotAvailable() {
        Toast.makeText(this, R.string.network_not_available, Toast.LENGTH_SHORT).show();
    }
}