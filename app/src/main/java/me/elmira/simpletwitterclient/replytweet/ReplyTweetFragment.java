package me.elmira.simpletwitterclient.replytweet;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.Tweet;

/**
 * Created by elmira on 3/30/17.
 */

public class ReplyTweetFragment extends DialogFragment {

    public static final String PARAM_TWEET = "tweet";
    private static final int POST_CHARS_COUNT = 140;

    private EditText bodyEditText;
    private Button postButton;
    private TextView remainingTextView;
    private TextView titleTextView;

    private Tweet tweet;

    public interface ReplyTweetDialogListener {
        void onTweetReplied(Tweet tweet);
    }

    public ReplyTweetFragment() {
    }

    public static ReplyTweetFragment newInstance(Tweet tweet) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_TWEET, tweet);

        ReplyTweetFragment fragment = new ReplyTweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tweet = getArguments().getParcelable(PARAM_TWEET);

        postButton = (Button) view.findViewById(R.id.btnTweet);
        remainingTextView = (TextView) view.findViewById(R.id.tvRemainingCount);
        bodyEditText = (EditText) view.findViewById(R.id.edBody);
        titleTextView = (TextView) view.findViewById(R.id.tvTitle);


        titleTextView.setText(getResources().getString(R.string.reply_to, tweet.getUser().getName()));
        bodyEditText.setText(tweet.getUser().getDisplayScreenName());

        remainingTextView.setText("" + (POST_CHARS_COUNT - bodyEditText.getText().length()));

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyTweet();
            }
        });
        postButton.setEnabled(false);

        bodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int remaining = POST_CHARS_COUNT - s.length();
                remainingTextView.setText("" + remaining);
                remainingTextView.setTextColor(getResources().getColor((remaining >= 0 ? R.color.light_gray_text : R.color.colorAccent)));
                postButton.setEnabled(s.length() > 0 && s.length() <= POST_CHARS_COUNT);
            }
        });

        Dialog dialog = getDialog();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        bodyEditText.setSelection(bodyEditText.length());
    }

    public void replyTweet() {
        tweet.setBody(bodyEditText.getText().toString());
        ((ReplyTweetDialogListener) getActivity()).onTweetReplied(tweet);
        dismiss();
    }
}
