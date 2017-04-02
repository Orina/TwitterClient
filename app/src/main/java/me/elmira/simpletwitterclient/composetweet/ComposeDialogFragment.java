package me.elmira.simpletwitterclient.composetweet;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.elmira.simpletwitterclient.R;

public class ComposeDialogFragment extends DialogFragment {

    public static final String LOG_TAG = "ComposeDialog";

    EditText bodyEditText;
    Button postButton;
    TextView remainingTextView;

    private static final int POST_CHARS_COUNT = 140;

    public interface PostTweetDialogListener {
        void onTweetPost(String inputText);
    }

    public ComposeDialogFragment() {

    }

    public static ComposeDialogFragment newInstance() {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle bundle = new Bundle();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postButton = (Button) view.findViewById(R.id.btnTweet);
        remainingTextView = (TextView) view.findViewById(R.id.tvRemainingCount);
        bodyEditText = (EditText) view.findViewById(R.id.edBody);

        remainingTextView.setText("" + POST_CHARS_COUNT);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet();
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
                remainingTextView.setTextColor(ContextCompat.getColor(getContext(), (remaining >= 0 ? R.color.light_gray_text : R.color.colorAccent)));
                postButton.setEnabled(s.length() > 0 && s.length() <= POST_CHARS_COUNT);
            }
        });

        Dialog dialog = getDialog();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void composeTweet() {
        ((PostTweetDialogListener) getActivity()).onTweetPost(bodyEditText.getText().toString());
        dismiss();
    }
}