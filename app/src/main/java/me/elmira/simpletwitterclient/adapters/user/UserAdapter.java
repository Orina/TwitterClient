package me.elmira.simpletwitterclient.adapters.user;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.UserCursoredCollection;

/**
 * Created by elmira on 3/29/17.
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> mUsers;
    private static final int imageWidthPx = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.2);

    private long nextCursor;
    private long previousCursor;

    private OnUserClickListener mListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false), imageWidthPx);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        ((UserViewHolder) holder).onBind(user, mListener);
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    public void setListener(OnUserClickListener mListener) {
        this.mListener = mListener;
    }

    public void addUsersToTail(UserCursoredCollection collection) {
        if (collection == null) return;

        if (collection.getUsers() == null || collection.getUsers().size() == 0) return;
        nextCursor = collection.getNextCursor();

        if (mUsers == null) {
            mUsers = new ArrayList<>();
            mUsers.addAll(collection.getUsers());
            notifyDataSetChanged();
        }
        else {
            int N = mUsers.size();
            for (int i = 0; i < collection.getUsers().size(); i++) {
                mUsers.add(collection.getUsers().get(i));
            }
            notifyItemRangeInserted(N, mUsers.size());
        }
    }

    public void addUsersToHead(UserCursoredCollection collection) {
        if (collection == null) return;
        previousCursor = collection.getPreviousCursor();

        if (collection.getUsers() == null || collection.getUsers().size() == 0) return;

        if (mUsers == null) {
            mUsers = new ArrayList<>();
            mUsers.addAll(collection.getUsers());
            notifyDataSetChanged();
        }
        else {
            List<User> newList = new ArrayList<>();
            for (int i = 0; i < collection.getUsers().size(); i++) {
                newList.add(collection.getUsers().get(i));
            }
            newList.addAll(mUsers);
            mUsers = newList;
            notifyItemRangeInserted(0, collection.getUsers().size());
        }
    }

    public long getNextCursor() {
        return nextCursor;
    }

    public long getPreviousCursor() {
        return previousCursor;
    }
}