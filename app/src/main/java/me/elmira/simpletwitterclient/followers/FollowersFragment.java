package me.elmira.simpletwitterclient.followers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.adapters.user.UserAdapter;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.util.NetworkUtil;
import me.elmira.simpletwitterclient.viewutil.EndlessRecyclerViewScrollListener;
import me.elmira.simpletwitterclient.viewutil.ItemOffsetDecoration;

/**
 * Created by elmira on 3/28/17.
 */

public class FollowersFragment extends Fragment implements FollowersContract.View, UserAdapter.OnUserClickListener {

    public static final String PARAM_UID = "uid";
    private static final String LOG_TAG = "FollowersFragment";

    private RecyclerView mRecyclerView;
    private UserAdapter mUsersAdapter;

    private FollowersContract.Presenter mPresenter;

    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private SwipeRefreshLayout mSwipeContainer;

    private long uid;

    public FollowersFragment() {
    }

    public static FollowersFragment newInstance(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_UID, uid);

        FollowersFragment fragment = new FollowersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = getArguments().getLong(PARAM_UID, 0);
        mPresenter = new FollowersPresenter(TwitterClientApp.getRepository(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadFollowersNext(uid, -1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.destroy();
        mPresenter = null;
    }

    @Override
    public void onLoadUsersNext(UserCursoredCollection collection) {
        mUsersAdapter.addUsersToTail(collection);
    }

    @Override
    public void onLoadUsersPrev(UserCursoredCollection collection) {
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
        }
        mUsersAdapter.addUsersToHead(collection);
    }

    @Override
    public void onLoadingFailure() {
        Toast.makeText(getContext(), R.string.load_users_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(FollowersContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this.getContext(), UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.PARAM_USER, user);
        startActivity(intent);
    }

    private void setupRecyclerView(View root) {
        mUsersAdapter = new UserAdapter();
        mUsersAdapter.setListener(this);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.rvUsers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mUsersAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        mSwipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Log.d(LOG_TAG, "Loading users from SwipeRefresh layout");
                    mPresenter.loadFollowersPrev(uid, mUsersAdapter.getPreviousCursor());
                }
                else {
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    Log.d(LOG_TAG, "Loading users from endless scrolling, page: " + page);
                    mPresenter.loadFollowersNext(uid, mUsersAdapter.getNextCursor());
                }
            }
        };
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }
}