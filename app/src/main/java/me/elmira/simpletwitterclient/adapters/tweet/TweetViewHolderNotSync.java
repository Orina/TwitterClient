package me.elmira.simpletwitterclient.adapters.tweet;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.elmira.simpletwitterclient.BR;
import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.Tweet;

/**
 * Created by elmira on 3/23/17.
 */

public class TweetViewHolderNotSync extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;
    private ImageView imageView;
    int imageViewWidthPx;

    public TweetViewHolderNotSync(View itemView, int imageViewWidthPx) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
        this.imageViewWidthPx = imageViewWidthPx;
        imageView = (ImageView) itemView.findViewById(R.id.ivUser);
        imageView.getLayoutParams().width = imageViewWidthPx;
    }

    public void onBind(final Tweet tweet) {
        binding.setVariable(BR.tweet, tweet);
        binding.executePendingBindings();

        if (tweet.getUser() != null && tweet.getUser().getProfileImageUrl() != null) {

            Picasso.with(imageView.getContext()).load(tweet.getUser().getProfileImageUrl())
                    .resize(imageViewWidthPx, 0)
                    .noPlaceholder()
                    .transform(new RoundedCornersTransformation(10, 5))
                    .into(imageView);
        }
    }
}