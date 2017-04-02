package me.elmira.simpletwitterclient.userdetailsheader;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.elmira.simpletwitterclient.BR;
import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.TwitterClientApp;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.source.TwitterRepository;

/**
 * Created by elmira on 3/28/17.
 */

public class UserHeaderFragment extends Fragment implements UserHeaderContract.View {

    public static final String PARAM_UID = "uid";
    private ViewDataBinding binding;

    private UserHeaderContract.Presenter mPresenter;
    private long userId;

    private ImageView iconView;
    private ImageView backgroundView;

    private LoadUserListener mListener;
    private static final int imageWidthPx = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.2);


    public interface LoadUserListener {
        void onUserLoaded(User user);
    }

    public UserHeaderFragment() {
    }

    public static UserHeaderFragment newInstance(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_UID, uid);

        UserHeaderFragment fragment = new UserHeaderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoadUserListener) {
            mListener = (LoadUserListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details_header, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);

        iconView = (ImageView) view.findViewById(R.id.ivIcon);
        iconView.getLayoutParams().width = imageWidthPx;

        backgroundView = (ImageView) view.findViewById(R.id.ivBackdrop);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterRepository repository = TwitterClientApp.getRepository();
        mPresenter = new UserHeaderPresenter(this, repository);

        userId = getArguments().getLong(PARAM_UID, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadUserDetails(userId);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showLoadingIndicator(boolean isLoading) {

    }

    @Override
    public void showUserHeader(User user) {
        binding.setVariable(BR.user, user);
        binding.executePendingBindings();

        if (user.getProfileImageUrl() != null) {
            Picasso.with(getActivity()).load(user.getProfileImageUrl())
                    .resize(imageWidthPx, 0)
                    .noPlaceholder()
                    .transform(new RoundedCornersTransformation(10, 5))
                    .into(iconView);
        }
        if (user.getBannerUrl() != null) {
            Picasso.with(getActivity())
                    .load(user.getBannerUrl())
                    .into(backgroundView);
        }

        if (mListener != null) {
            mListener.onUserLoaded(user);
        }
    }

    @Override
    public void onLoadingFailure() {

    }

    @Override
    public void setPresenter(UserHeaderContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
