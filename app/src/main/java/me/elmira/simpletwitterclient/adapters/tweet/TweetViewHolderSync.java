package me.elmira.simpletwitterclient.adapters.tweet;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.elmira.simpletwitterclient.BR;
import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.search.SearchableActivity;
import me.elmira.simpletwitterclient.userdetails.UserDetailsActivity;
import me.elmira.simpletwitterclient.viewutil.PatternEditableBuilder;

/**
 * Created by elmira on 3/23/17.
 */

public class TweetViewHolderSync extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    private ImageView imageView;
    private ImageView mediaImage;

    private TextView nameView;
    private TextView bodyView;

    int imageViewWidthPx;

    public TweetViewHolderSync(View itemView, int imageViewWidthPx) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
        this.imageViewWidthPx = imageViewWidthPx;
        imageView = (ImageView) itemView.findViewById(R.id.ivUser);
        mediaImage = (ImageView) itemView.findViewById(R.id.ivMedia);

        nameView = (TextView) itemView.findViewById(R.id.tvUserName);
        bodyView = (TextView) itemView.findViewById(R.id.tvTweetBody);

        imageView.getLayoutParams().width = imageViewWidthPx;
    }

    public void onBind(final Tweet tweet, final TweetsAdapter.OnTweetClickListener listener, final TweetsAdapter.OnTweetReplyListener replyListener) {
        binding.setVariable(BR.syncTweet, tweet);
        binding.executePendingBindings();

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorPrimary),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(binding.getRoot().getContext(), SearchableActivity.class);
                                intent.putExtra(SearchManager.QUERY, text);
                                binding.getRoot().getContext().startActivity(intent);
                            }
                        }).into(bodyView);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorPrimary),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(binding.getRoot().getContext(), UserDetailsActivity.class);
                                intent.putExtra(UserDetailsActivity.PARAM_USER, tweet.getUser());
                                binding.getRoot().getContext().startActivity(intent);
                            }
                        }).into(bodyView);

        if (tweet.getUser() != null && tweet.getUser().getProfileImageUrl() != null) {
            Picasso.with(imageView.getContext()).load(tweet.getUser().getProfileImageUrl())
                    .resize(imageViewWidthPx, 0)
                    .noPlaceholder()
                    .transform(new RoundedCornersTransformation(10, 5))
                    .into(imageView);
        }

        if (tweet.hasMedia() && tweet.getMedias().get(0).getMediaUrl() != null) {
            mediaImage.setVisibility(View.VISIBLE);
            Picasso.with(imageView.getContext()).load(tweet.getMedias().get(0).getMediaUrl())
                    .noPlaceholder()
                    .transform(new RoundedCornersTransformation(10, 5))
                    .into(mediaImage);
        }
        else {
            mediaImage.setVisibility(View.GONE);
        }

        if (listener != null) {
            View rootView = binding.getRoot();
            if (rootView != null) {
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onTweetClick(tweet);
                    }
                });
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIconClick(tweet, imageView, nameView);
                }
            });
        }
        if (replyListener != null) {
            binding.getRoot().findViewById(R.id.iconReply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyListener.onTweetReply(tweet);
                }
            });
        }
    }
}