package me.elmira.simpletwitterclient.oauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import me.elmira.simpletwitterclient.R;
import me.elmira.simpletwitterclient.model.source.remote.TwitterClient;
import me.elmira.simpletwitterclient.timeline.TimelineActivity;

/**
 * Created by elmira on 3/22/17.
 */

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        Log.d(LOG_TAG, "onLoginSuccess()");
        Toast.makeText(this, R.string.login_to_twitter_success, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
        //finish();
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
        Log.e(LOG_TAG, "onLoginFailure" + e.toString());
        Toast.makeText(this, R.string.login_to_twitter_failed, Toast.LENGTH_SHORT).show();
        //finish();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        Log.d(LOG_TAG, "loginToRest");
        getClient().connect();
    }
}