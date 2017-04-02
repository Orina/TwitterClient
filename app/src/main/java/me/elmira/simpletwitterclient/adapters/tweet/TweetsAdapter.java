package me.elmira.simpletwitterclient.adapters.tweet;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.Tweet;

/**
 * Created by elmira on 3/22/17.
 */

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "TweetsAdapter";
    private static final int imageWidthPx = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.18);

    private List<Tweet> mTweetsList;

    private static final int SYNC_ITEM_TYPE = 0;
    private static final int NOT_SYNC_ITEM_TYPE = 1;

    private long lowestId;
    private long highestId;

    private OnTweetClickListener mListener;
    private OnTweetReplyListener mReplyListener;

    public interface OnTweetClickListener {
        void onTweetClick(Tweet tweet);

        void onIconClick(Tweet tweet, View iconView, View nameView);
    }

    public interface OnTweetReplyListener {
        void onTweetReply(Tweet tweet);
    }

    public void setListener(OnTweetClickListener mListener) {
        this.mListener = mListener;
    }

    public void setReplyListener(OnTweetReplyListener mReplyListener) {
        this.mReplyListener = mReplyListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SYNC_ITEM_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_sync, parent, false);
            return new TweetViewHolderSync(v, imageWidthPx);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_not_sync, parent, false);
            return new TweetViewHolderNotSync(v, imageWidthPx);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = mTweetsList.get(position);
        if (holder instanceof TweetViewHolderSync) {
            ((TweetViewHolderSync) holder).onBind(tweet, mListener, mReplyListener);
        }
        else if (holder instanceof TweetViewHolderNotSync) {
            ((TweetViewHolderNotSync) holder).onBind(tweet);
        }
    }

    @Override
    public int getItemCount() {
        return mTweetsList == null ? 0 : mTweetsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTweetsList.get(position).isSync() ? SYNC_ITEM_TYPE : NOT_SYNC_ITEM_TYPE;
    }

    public void setTweets(List<Tweet> newTweets) {
        if (newTweets == null || newTweets.size() == 0) {
            mTweetsList = new ArrayList<>();
            lowestId = highestId = 0;
        }
        else {
            int N = newTweets.size();
            highestId = newTweets.get(0).getUid();
            lowestId = newTweets.get(N - 1).getUid();
            mTweetsList = new ArrayList<>(newTweets);
        }
        notifyDataSetChanged();
    }

    public void addTweets(List<Tweet> newTweets) {
        if (newTweets == null || newTweets.size() == 0) {
            Log.d(LOG_TAG, "adding new tweets: empty list");
            return;
        }
        int N = newTweets.size();
        long newHighestId = newTweets.get(0).getUid();
        long newLowestId = newTweets.get(N - 1).getUid();

        Log.d(LOG_TAG, "***START adding new tweets*** N: " + N + ", newHighestId: " + newHighestId + ", newLowestId: " + newLowestId
                + ", highestId: " + highestId + ", lowestId: " + lowestId);

        if (mTweetsList == null || mTweetsList.size() == 0) {
            mTweetsList = new ArrayList<>(newTweets);
            lowestId = newLowestId;
            highestId = newHighestId;
            notifyDataSetChanged();
        }
        // add new tweets to the beginning of current list
        else if (newLowestId > highestId) {
            List<Tweet> list = new ArrayList<>(newTweets);
            list.addAll(mTweetsList);
            mTweetsList = list;
            highestId = newHighestId;
            notifyItemRangeInserted(0, newTweets.size());
        }
        // add tweets to the end of current list
        else if (lowestId > newHighestId) {
            int oldCount = mTweetsList.size();
            mTweetsList.addAll(newTweets);
            lowestId = newLowestId;
            notifyItemRangeInserted(oldCount, newTweets.size());
        }
        Log.d(LOG_TAG, "***AFTER adding new tweets*** highestId: " + highestId + ", lowestId: " + lowestId);
    }

    public long getLowestId() {
        return lowestId;
    }

    public long getHighestId() {
        return highestId;
    }

    public void onTweetRemotePosted(Tweet tweet) {
        addTweetToPosition(0, tweet);
        updateSearchIds();
    }

    private void updateSearchIds() {
        if (mTweetsList == null || mTweetsList.size() == 0) return;
        int N = mTweetsList.size();

        highestId = Math.max(highestId, mTweetsList.get(0).getUid());
        lowestId = Math.min(lowestId, mTweetsList.get(N - 1).getUid());
    }

    private void addTweetToPosition(int position, Tweet tweet) {
        if (position == -1) {
            addTweetToListTop(tweet);
        }
        else {
            mTweetsList.set(position, tweet);
            notifyItemChanged(position);
        }
    }

    private void addTweetToListTop(Tweet tweet) {
        if (mTweetsList != null) {
            mTweetsList.add(0, tweet);
        }
        else {
            mTweetsList = new ArrayList<>();
            mTweetsList.add(tweet);
        }
        notifyItemInserted(0);
    }

    @Override
    public String toString() {
        return "TweetsAdapter{" +
                "size: " + getItemCount() +
                ", lowestId=" + lowestId +
                ", highestId=" + highestId +
                '}';
    }
}