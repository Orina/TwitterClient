package me.elmira.simpletwitterclient.model.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.elmira.simpletwitterclient.model.Tweet;
import me.elmira.simpletwitterclient.model.User;
import me.elmira.simpletwitterclient.model.UserCursoredCollection;
import me.elmira.simpletwitterclient.model.source.TwitterDataSource;

/**
 * Created by elmira on 3/23/17.
 */

public class TwitterRemoteDataSource implements TwitterDataSource {
    private static final String LOG_TAG = "TwitterRemoteData";

    private static TwitterRemoteDataSource instance = null;

    private TwitterClient mTwitterClient;

    private TwitterRemoteDataSource(Context context) {
        mTwitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
    }

    public static TwitterRemoteDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new TwitterRemoteDataSource(context);
        }
        return instance;
    }

    @Override
    public void loadHomeTweets(long sinceId, long maxId, final LoadTweetsCallback callback) {
        try {
            Log.d(LOG_TAG, "start loading tweets: sinceId: " + sinceId + ", maxId: " + maxId);
            Log.d(LOG_TAG, "access token: " + mTwitterClient.isAuthenticated());

            mTwitterClient.getHomeTimeline(sinceId, maxId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    try {
                        Log.d(LOG_TAG, "Success! Total tweets count: " + jsonArray.length());
                        Log.d(LOG_TAG, "JSON:" + jsonArray.toString());
                        int N = jsonArray.length();
                        List<Tweet> tweets = new ArrayList<Tweet>();
                        for (int i = 0; i < N; i++) {
                            Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                            tweets.add(tweet);
                        }
                        callback.onTweetsLoaded(tweets);
                    } catch (JSONException ex) {
                        Log.e(LOG_TAG, ex.toString());
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(LOG_TAG, "onFailure(): Throwable");
                    callback.onFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(LOG_TAG, "onFailure(): JSONObject");
                    Log.e(LOG_TAG, throwable.toString());
                    callback.onFailure();
                }
            });
        } catch (Throwable ex) {
            Log.e(LOG_TAG, "Catch throwable" + ex.toString());
            callback.onFailure();
        }
    }

    @Override
    public void loadUserTweets(long uid, long sinceId, long maxId, final LoadTweetsCallback callback) {
        try {
            Log.d(LOG_TAG, "start loading tweets: sinceId: " + sinceId + ", maxId: " + maxId);
            Log.d(LOG_TAG, "access token: " + mTwitterClient.isAuthenticated());

            mTwitterClient.getUserTimeline(uid, sinceId, maxId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    try {
                        Log.d(LOG_TAG, "Success! Total tweets count: " + jsonArray.length());
                        Log.d(LOG_TAG, "JSON:" + jsonArray.toString());
                        int N = jsonArray.length();
                        List<Tweet> tweets = new ArrayList<Tweet>();
                        for (int i = 0; i < N; i++) {
                            Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                            tweets.add(tweet);
                        }
                        callback.onTweetsLoaded(tweets);
                    } catch (JSONException ex) {
                        Log.e(LOG_TAG, ex.toString());
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(LOG_TAG, "onFailure(): Throwable");
                    callback.onFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(LOG_TAG, "onFailure(): JSONObject");
                    Log.e(LOG_TAG, throwable.toString());
                    callback.onFailure();
                }
            });
        } catch (Throwable ex) {
            Log.e(LOG_TAG, "Catch throwable" + ex.toString());
            callback.onFailure();
        }
    }

    @Override
    public void createTweet(Tweet tweet, final CreateTweetCallback callback) {

        mTwitterClient.postTweet(tweet.getBody(), tweet.getUid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    callback.onTweetCreated(tweet);
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable ex, JSONObject errorResponse) {
                Log.e(LOG_TAG, ex.toString());
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadCurrentUser(final LoadUserCallback callback) {
        Log.d(LOG_TAG, "loading current user...");
        mTwitterClient.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(LOG_TAG, "Current user JsonObject: " + response);
                    User user = User.fromJSON(response);
                    callback.onUserLoaded(user);
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "Can't load current user: " + ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't load current user: " + throwable);
                callback.onFailure();
            }
        });
    }

    @Override
    public void getMentions(long sinceId, long maxId, final LoadTweetsCallback callback) {
        Log.d(LOG_TAG, "loading mentions....");
        mTwitterClient.getMentions(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                try {
                    Log.d(LOG_TAG, "Success! Total mentions count: " + jsonArray.length());
                    Log.d(LOG_TAG, "JSON:" + jsonArray.toString());
                    int N = jsonArray.length();
                    List<Tweet> tweets = new ArrayList<Tweet>();
                    for (int i = 0; i < N; i++) {
                        Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                        tweets.add(tweet);
                    }
                    callback.onTweetsLoaded(tweets);
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't get mentions: " + throwable);
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadUser(long userId, final LoadUserCallback callback) {
        Log.d(LOG_TAG, "loading user...id: " + userId);
        mTwitterClient.getUserInfo(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(LOG_TAG, "Loaded user JsonObject: " + response);
                    User user = User.fromJSON(response);
                    callback.onUserLoaded(user);
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "Can't load user: " + ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't load user: " + throwable);
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadFollowing(long userId, long cursor, final LoadUserCursorCollectionCallback callback) {
        Log.d(LOG_TAG, "Loading following ... uid: " + userId + ", cursor: " + cursor);
        mTwitterClient.getFollowing(userId, cursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    callback.onUsersLoaded(UserCursoredCollection.fromJSON(response));
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "Can't load following: " + ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable ex, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't load following: " + ex.toString());
                callback.onFailure();
            }
        });
    }

    @Override
    public void loadFollowers(long userId, long cursor, final LoadUserCursorCollectionCallback callback) {
        Log.d(LOG_TAG, "Loading followers ... uid: " + userId + ", cursor: " + cursor);
        mTwitterClient.getFollowers(userId, cursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    callback.onUsersLoaded(UserCursoredCollection.fromJSON(response));
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "Can't load followers: " + ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable ex, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't load followers: " + ex.toString());
                callback.onFailure();
            }
        });
    }

    @Override
    public void searchTweets(String query, long sinceId, long maxId, final SearchTweetsCallback callback) {
        Log.d(LOG_TAG, "Searching tweets....query: " + query);
        mTwitterClient.searchTweets(query, sinceId, maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray(JsonAttributes.STATUSES);
                    int N = jsonArray.length();
                    List<Tweet> tweets = new ArrayList<Tweet>();
                    for (int i = 0; i < N; i++) {
                        Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                        tweets.add(tweet);
                    }
                    callback.onTweetsFound(tweets);
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "Can't search tweets: " + ex.toString());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable ex, JSONObject errorResponse) {
                Log.e(LOG_TAG, "Can't search tweets: " + ex.toString());
                callback.onFailure();
            }
        });
    }

    @Override
    public void destroy() {
        mTwitterClient = null;
    }

    @Override
    public Tweet loadTweet(@NonNull long tweetId) {
        throw new UnsupportedOperationException("operation is not supported on remote data source");
    }

    @Override
    public List<Tweet> preloadTweets() {
        throw new UnsupportedOperationException("operation is not supported on remote data source");
    }

    @Override
    public void saveTweets(List<Tweet> tweets) {
        throw new UnsupportedOperationException("operation is not supported on remote data source");
    }


    @Override
    public void addCurrentUser(User user) {
        throw new UnsupportedOperationException("operation is not supported on remote data source");
    }

    @Override
    public void updateTweet(Tweet tweet, long tempId, UpdateTweetCallback callback) {
        throw new UnsupportedOperationException("operation is not supported on remote data source");
    }
}
