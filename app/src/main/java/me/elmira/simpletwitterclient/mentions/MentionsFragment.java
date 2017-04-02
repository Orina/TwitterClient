package me.elmira.simpletwitterclient.mentions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import me.elmira.simpletwitterclient.util.NetworkUtil;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

/**
 * Created by elmira on 3/27/17.
 */

public class MentionsFragment extends Fragment implements MentionsContract.View {

    private static final String LOG_TAG = "MentionsFragment";
    private MentionsContract.Presenter mPresenter;

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private TweetsAdapter mTweetsAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private View emptyFeedLayout;

    private LoadingIndicatorListener mLoadingListener;

    public MentionsFragment() {

    }

    public static MentionsFragment newInstance() {
        return new MentionsFragment();
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
        return inflater.inflate(R.layout.fragment_mentions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMentionsRecyclerView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MentionsPresenter(TwitterClientApp.getRepository(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            mPresenter.loadMentions(mTweetsAdapter.getHighestId(), 0);
        }
        else {
            setLoadingIndicator(true);
            emptyFeedLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addTweets(List<Tweet> tweets) {
        mTweetsAdapter.addTweets(tweets);

        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }

        boolean noTweets = mTweetsAdapter.getItemCount() == 0;
        emptyFeedLayout.setVisibility(noTweets ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoadingFailure() {
        Toast.makeText(getActivity(), R.string.load_mentions_failed, Toast.LENGTH_SHORT).show();
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(MentionsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void setupMentionsRecyclerView(View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Log.d(LOG_TAG, "Loading tweets from SwipeRefresh layout");
                    mPresenter.loadMentions(mTweetsAdapter.getHighestId(), 0);
                }
                else {
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvMentions);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTweetsAdapter = new TweetsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mRecyclerView.setAdapter(mTweetsAdapter);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    Log.d(LOG_TAG, "Loading mentions from endless scrolling, page: " + page);
                    Log.d(LOG_TAG, mTweetsAdapter.toString());
                    mPresenter.loadMentions(0, mTweetsAdapter.getLowestId() - 1);
                }
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
        emptyFeedLayout = view.findViewById(R.id.emptyFeedLayout);
    }

    @Override
    public void setLoadingIndicator(boolean loading) {
        if (mLoadingListener != null) {
            mLoadingListener.setLoadingIndicator(loading);
        }
    }
}