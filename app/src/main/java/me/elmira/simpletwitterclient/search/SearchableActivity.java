package me.elmira.simpletwitterclient.search;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.adapters.tweet.TweetsAdapter;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.util.NetworkUtil;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

/**
 * Created by elmira on 3/31/17.
 */

public class SearchableActivity extends AppCompatActivity implements TweetsAdapter.OnTweetClickListener,
        SearchContract.View {

    private static final String LOG_TAG = "SearchableActivity";

    private String query;
    private SearchContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private TweetsAdapter mTweetsAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private MenuItem loadingMenuItem;

    private View emptySearchLayout;
    private View loadingLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupToolbar();
        setupSearchRecyclerView();

        mPresenter = new SearchPresenter(TwitterClientApp.getRepository(), this);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        query = intent.getStringExtra(SearchManager.QUERY);
        Log.d(LOG_TAG, "do search: " + query);
        if (NetworkUtil.isNetworkAvailable(this)) {
            mPresenter.searchTweets(query, 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        loadingMenuItem = menu.findItem(R.id.action_loading);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        searchView.setIconified(false);

        if (query != null) {
            searchView.setQuery(query, false);
        }
        searchView.clearFocus();

        try {
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView mSearchCloseButton = (ImageView) searchField.get(searchView);
            if (mSearchCloseButton != null) {
                mSearchCloseButton.setEnabled(false);
                mSearchCloseButton.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error finding close button", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtil.isNetworkAvailable(this)) {
            showNetworkNotAvailable();
            setLoadingIndicator(true);
        }
    }

    @Override
    public void onTweetClick(Tweet tweet) {

    }

    @Override
    public void onIconClick(Tweet tweet, View iconView, View nameView) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.PARAM_USER, tweet.getUser());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair.create(iconView, "profileImage"), Pair.create(nameView, "profileName"));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onSearchResult(List<Tweet> tweets) {
        loadingLayout.setVisibility(View.INVISIBLE);
        mTweetsAdapter.addTweets(tweets);

        boolean noTweets = mTweetsAdapter.getItemCount() == 0;
        emptySearchLayout.setVisibility(noTweets ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSearchFailure() {
        loadingLayout.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.search_tweets_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSearchRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rvSearch);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTweetsAdapter = new TweetsAdapter();
        mTweetsAdapter.setListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mRecyclerView.setAdapter(mTweetsAdapter);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (NetworkUtil.isNetworkAvailable(getBaseContext())) {
                    Log.d(LOG_TAG, "Loading tweets from Endless scrolling, page: " + page);
                    Log.d(LOG_TAG, mTweetsAdapter.toString());
                    mPresenter.searchTweets(query, 0, mTweetsAdapter.getLowestId() - 1);
                }
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
        emptySearchLayout = findViewById(R.id.emptySearchLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
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