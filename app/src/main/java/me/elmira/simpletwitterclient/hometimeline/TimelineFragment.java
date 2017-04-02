package me.elmira.simpletwitterclient.hometimeline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.adapters.tweet.TweetsAdapter;
import me.elmira.simpletwitterclient.home.LoadingIndicatorListener;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.source.TweetsLoader;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;
import me.elmira.simpletwitterclient.replytweet.ReplyTweetFragment;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.util.NetworkUtil;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

public class TimelineFragment extends Fragment implements TimelineContract.View,
        TweetsAdapter.OnTweetClickListener, TweetsAdapter.OnTweetReplyListener {

    private static final String LOG_TAG = "TimelineFragment";

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private TweetsAdapter mTweetsAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private TimelineContract.Presenter mPresenter;
    private View emptyFeedLayout;

    private LoadingIndicatorListener mLoadingListener;

    public TimelineFragment() {
    }

    public static TimelineFragment newInstance() {
        return new TimelineFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoadingIndicatorListener) {
            mLoadingListener = (LoadingIndicatorListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFeedList(view);
        emptyFeedLayout = view.findViewById(R.id.emptyFeedLayout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterRepository repository = TwitterClientApp.getRepository();
        mPresenter = new TimelinePresenter(getActivity().getSupportLoaderManager(),
                new TweetsLoader(getActivity(), repository),
                repository,
                this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.destroy();
    }

    private void setupFeedList(View root) {
        mSwipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Log.d(LOG_TAG, "Loading tweets from SwipeRefresh layout");
                    mPresenter.loadTweets(mTweetsAdapter.getHighestId(), 0);
                }
                else {
                    showNetworkNotAvailable();
                    mSwipeContainer.setRefreshing(false);
                    setLoadingIndicator(true);
                }
            }
        });

        mRecyclerView = (RecyclerView) root.findViewById(R.id.rvTweets);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTweetsAdapter = new TweetsAdapter();
        mTweetsAdapter.setListener(this);

        mTweetsAdapter.setReplyListener(this);
        mRecyclerView.setAdapter(mTweetsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Log.d(LOG_TAG, "Loading tweets from Endless scrolling, page: " + page);
                    Log.d(LOG_TAG, mTweetsAdapter.toString());
                    mPresenter.loadTweets(0, mTweetsAdapter.getLowestId() - 1);
                }
                else {
                    setLoadingIndicator(true);
                }
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void addTweets(List<Tweet> tweets) {
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
    }

    @Override
    public void setPresenter(TimelineContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void showNetworkNotAvailable() {
        Toast.makeText(getContext(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingFailure() {
        Toast.makeText(getContext(), R.string.load_tweets_failed, Toast.LENGTH_SHORT).show();
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onTweetRemotePosted(Tweet tweet) {
        mTweetsAdapter.onTweetRemotePosted(tweet);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onTweetClick(Tweet tweet) {

    }

    @Override
    public void onIconClick(Tweet tweet, View iconView, View nameView) {
        Intent intent = new Intent(this.getContext(), UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.PARAM_USER, tweet.getUser());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this.getActivity(),
                Pair.create(iconView, "profileImage"), Pair.create(nameView, "profileName"));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onTweetReply(Tweet tweet) {
        ReplyTweetFragment fragment = ReplyTweetFragment.newInstance(tweet);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.show(fragmentManager, "reply_tweet");
    }

    @Override
    public void setLoadingIndicator(boolean loading) {
        if (mLoadingListener != null) {
            mLoadingListener.setLoadingIndicator(loading);
        }
    }
}