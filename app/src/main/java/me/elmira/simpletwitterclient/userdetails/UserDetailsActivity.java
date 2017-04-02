package me.elmira.simpletwitterclient.userdetails;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.userdetailsheader.UserHeaderFragment;

public class UserDetailsActivity extends AppCompatActivity implements UserDetailsFragmentPagerAdapter.UserDetailsTabListener, UserHeaderFragment.LoadUserListener {

    public static final String PARAM_USER = "user";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUser = getIntent().getParcelableExtra(PARAM_USER);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new UserDetailsFragmentPagerAdapter(getSupportFragmentManager(), this));

        mTabLayout = (TabLayout) findViewById(R.id.slidingTabs);
        mTabLayout.setupWithViewPager(mViewPager);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mUser != null ? mUser.getName() + "" : "");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white_transparent));

        if (savedInstanceState == null) {
            UserHeaderFragment headerFragment = UserHeaderFragment.newInstance(getUserId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flUserHeader, headerFragment, "headerTag")
                    .commit();
        }
    }

    @Override
    public long getUserId() {
        return mUser != null ? mUser.getUid() : 0;
    }

    @Override
    public void onUserLoaded(User user) {
        this.mUser = user;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
