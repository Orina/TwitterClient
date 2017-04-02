package me.elmira.simpletwitterclient.tweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.util.NetworkUtil;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

/**
 * Created by elmira on 3/27/17.
 */

public class TweetsFragment extends Fragment implements TweetsContract.View, TweetsAdapter.OnTweetClickListener {

    private static final String LOG_TAG = "TweetsFragment";
    public static final String PARAM_UID = "uid";

    private TweetsContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private TweetsAdapter mTweetsAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private View emptyFeedLayout;

    private long uid;

    public static TweetsFragment newInstance(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_UID, uid);

        TweetsFragment fragment = new TweetsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setPresenter(TweetsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweets, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTweetsRecyclerView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        uid = getArguments().getLong(PARAM_UID, 0);
        mPresenter = new TweetsPresenter(TwitterClientApp.getRepository(), this);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume()");
        super.onResume();
        mPresenter.loadTweets(uid, mTweetsAdapter.getHighestId(), 0);
    }

    @Override
    public void addTweets(List<Tweet> tweets) {
        mTweetsAdapter.addTweets(tweets);

        boolean noTweets = mTweetsAdapter.getItemCount() == 0;
        emptyFeedLayout.setVisibility(noTweets ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onLoadingFailure() {
        Toast.makeText(getActivity(), R.string.load_tweets_failed, Toast.LENGTH_SHORT).show();
    }

    private void setupTweetsRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvTweets);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTweetsAdapter = new TweetsAdapter();
        mTweetsAdapter.setListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mRecyclerView.setAdapter(mTweetsAdapter);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    Log.d(LOG_TAG, "Loading tweets from Endless scrolling, page: " + page);
                    Log.d(LOG_TAG, mTweetsAdapter.toString());
                    mPresenter.loadTweets(uid, 0, mTweetsAdapter.getLowestId() - 1);
                }
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
        emptyFeedLayout = view.findViewById(R.id.emptyFeedLayout);
    }

    @Override
    public void onTweetClick(Tweet tweet) {
        //todo show tweet details page
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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView()");
        super.onDestroyView();
        mPresenter.destroy();
        mPresenter = null;
    }
}
