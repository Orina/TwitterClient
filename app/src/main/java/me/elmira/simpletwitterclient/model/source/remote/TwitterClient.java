package me.elmira.simpletwitterclient.model.source.remote;

import android.content.Context;
import android.content.Intent;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by elmira on 3/22/17.
 */

public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "VSe6xmRxLcaEa7P4QJ41VIH2R";       // Change this
    public static final String REST_CONSUMER_SECRET = "gsQf1MqNFV6zbxMTvvTZCzwv2REHqDvuPSBMTguR6TbQVpubkw"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://codepathtweets"; // Change this (here and in manifest)

    public TwitterClient(Context c) {
        super(c, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        requestIntentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;
    }

    public static final int DEFAULT_COUNT = 25;

    public void getHomeTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > 0) {
            params.put("max_id", String.valueOf(max_id));
        }
        params.put("include_entities", true);
        getClient().get(apiUrl, params, handler);
    }

    public void postTweet(String body, long tweetUid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body);
        if (tweetUid > 0) {
            params.put("in_reply_to_status_id", tweetUid);
        }
        getClient().post(apiUrl, params, handler);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    public void getMentions(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_COUNT);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(long uid, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (uid > 0) {
            params.put("user_id", uid);
        }
        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > 0) {
            params.put("max_id", String.valueOf(max_id));
        }
        params.put("include_entities", true);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        if (uid > 0) {
            params.put("user_id", uid);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowers(long uid, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        if (uid > 0) {
            params.put("user_id", uid);
        }
        params.put("cursor", cursor);
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowing(long uid, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        if (uid > 0) {
            params.put("user_id", uid);
        }
        params.put("cursor", cursor);
        getClient().get(apiUrl, params, handler);
    }

    public void searchTweets(String query, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("q", query);

        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > 0) {
            params.put("max_id", String.valueOf(max_id));
        }
        params.put("result_type", "mixed");
        getClient().get(apiUrl, params, handler);
    }
}