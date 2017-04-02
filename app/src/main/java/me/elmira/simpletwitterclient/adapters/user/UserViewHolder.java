package me.elmira.simpletwitterclient.adapters.user;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.User;

/**
 * Created by elmira on 3/29/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    private ImageView imageView;
    private ImageView followingImage;

    int imageViewWidthPx;

    public UserViewHolder(View itemView, int imageViewWidthPx) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);

        this.imageViewWidthPx = imageViewWidthPx;
        followingImage = (ImageView) itemView.findViewById(R.id.ivFollowing);

        imageView = (ImageView) itemView.findViewById(R.id.ivUser);
        imageView.getLayoutParams().width = imageViewWidthPx;
    }

    public void onBind(final User user, final UserAdapter.OnUserClickListener listener) {
        binding.setVariable(BR.user, user);
        binding.executePendingBindings();

        if (user.getProfileImageUrl() != null) {
            Picasso.with(imageView.getContext()).load(user.getProfileImageUrl())
                    .resize(imageViewWidthPx, 0)
                    .noPlaceholder()
                    .transform(new RoundedCornersTransformation(10, 5))
                    .into(imageView);
        }
        if (user.isFollowing()) {
            followingImage.getDrawable().mutate().setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
            followingImage.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.following_icon_bg));
        }
        else {
            followingImage.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.not_following_icon_bg));
            followingImage.getDrawable().mutate().setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
        if (listener != null) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUserClick(user);
                }
            });
        }
    }
}
