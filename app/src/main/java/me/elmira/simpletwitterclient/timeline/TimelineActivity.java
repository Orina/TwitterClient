package me.elmira.simpletwitterclient.timeline;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.composetweet.ComposeDialogFragment;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TweetsLoader;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

public class TimelineActivity extends AppCompatActivity implements Contract.View, ComposeDialogFragment.PostTweetDialogListener {

    private static final String LOG_TAG = "TimelineActivity";

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private TweetsAdapter mTweetsAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private Contract.Presenter mPresenter;
    private View emptyFeedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupToolbar();
        setupFeedList();

        TwitterRepository repository = TwitterClientApp.getRepository();
        mPresenter = new Presenter(getSupportLoaderManager(),
                new TweetsLoader(this, repository),
                repository,
                this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showComposeDialog();
            }
        });
        emptyFeedLayout = findViewById(R.id.emptyFeedLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setupFeedList() {
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(LOG_TAG, "Loading tweets from SwipeRefresh layout");
                mPresenter.loadTweets(mTweetsAdapter.getHighestId(), 0);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.rvTweets);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTweetsAdapter = new TweetsAdapter();
        mRecyclerView.setAdapter(mTweetsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(LOG_TAG, "Loading tweets from Endless scrolling, page: " + page);
                Log.d(LOG_TAG, mTweetsAdapter.toString());
                mPresenter.loadTweets(0, mTweetsAdapter.getLowestId() - 1);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void addTweets(List<Tweet> tweets) {
        if (tweets != null && tweets.size() > 0) {
            Toast.makeText(this, R.string.load_tweets_success, Toast.LENGTH_SHORT).show();
        }

        mTweetsAdapter.addTweets(tweets);
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }

        boolean noTweets = mTweetsAdapter.getItemCount() == 0;
        emptyFeedLayout.setVisibility(noTweets ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setTweets(List<Tweet> tweets) {
        mEndlessScrollListener.resetState();
        mTweetsAdapter.setTweets(tweets);

        boolean noTweets = mTweetsAdapter.getItemCount() == 0;
        emptyFeedLayout.setVisibility(noTweets ? View.VISIBLE : View.INVISIBLE);

        mPresenter.loadTweets(mTweetsAdapter.getHighestId(), 0);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.mPresenter = presenter;
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
    public long getNextTweetId() {
        return mTweetsAdapter.getHighestId() + 1;
    }

    @Override
    public void onTweetRemotePosted(long tempId, Tweet tweet) {
        int position = mTweetsAdapter.onTweetRemotePosted(tempId, tweet);
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void onTweetJustPosted(Tweet tweet) {
        mTweetsAdapter.onTweetJustPosted(tweet);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onLoadingFailure() {
        Toast.makeText(this, R.string.load_tweets_failed, Toast.LENGTH_SHORT).show();
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }
    }
}