package me.elmira.simpletwitterclient.model;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.elmira.simpletwitterclient.model.source.remote.JsonAttributes;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;

/**
 * Created by elmira on 3/22/17.
 */

public class Tweet {

    private static String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    private static SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);

    static {
        sf.setLenient(true);
    }

    private String body;
    private long uid;
    private String createdAt;
    private User user;
    private boolean sync;
    private boolean favorite;
    private int favoriteCount;
    private boolean retweeted;
    private int retweetedCount;

    public Tweet() {
    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString(JsonAttributes.Tweet.BODY);
        tweet.uid = jsonObject.getLong(JsonAttributes.Tweet.UID);
        tweet.createdAt = jsonObject.getString(JsonAttributes.Tweet.CREATED_AT);
        tweet.favorite = jsonObject.has(JsonAttributes.Tweet.FAVORITE) && jsonObject.getBoolean(JsonAttributes.Tweet.FAVORITE);
        tweet.favoriteCount = jsonObject.has(JsonAttributes.Tweet.FAVORITE_COUNT) ? jsonObject.getInt(JsonAttributes.Tweet.FAVORITE_COUNT) : 0;
        tweet.retweeted = jsonObject.has(JsonAttributes.Tweet.RETWEETED) && jsonObject.getBoolean(JsonAttributes.Tweet.RETWEETED);
        tweet.retweetedCount = jsonObject.has(JsonAttributes.Tweet.RETWEET_COUNT) ? jsonObject.getInt(JsonAttributes.Tweet.RETWEET_COUNT) : 0;

        JSONObject media = jsonObject.has(JsonAttributes.Tweet.ENTITIES) ?
                (jsonObject.has(JsonAttributes.Tweet.MEDIA) ? jsonObject.getJSONObject(JsonAttributes.Tweet.MEDIA) : null)
                : null;
        if (media != null) {
            Log.d("Tweet", "JSON MEDIA: " + media.toString());
        }

        tweet.user = User.fromJSON(jsonObject.getJSONObject(JsonAttributes.Tweet.USER));
        tweet.sync = true;
        return tweet;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public boolean isSync() {
        return sync;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(Date date) {
        this.createdAt = sf.format(date);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public int getRetweetedCount() {
        return retweetedCount;
    }

    public void setRetweetedCount(int retweetedCount) {
        this.retweetedCount = retweetedCount;
    }

    public String getRelativeTimeAgo() {

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(),
                    0, FORMAT_ABBREV_ALL).toString().replace(". ago", "");
        } catch (Exception e) {
            //ignore
        }
        return relativeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (body != null ? !body.equals(tweet.body) : tweet.body != null) return false;
        return user != null ? user.equals(tweet.user) : tweet.user == null;

    }

    @Override
    public int hashCode() {
        int result = body != null ? body.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "body='" + body + '\'' +
                ", uid=" + uid +
                ", createdAt='" + createdAt + '\'' +
                ", sync=" + sync +
                ", favorite=" + favorite +
                ", favoriteCount=" + favoriteCount +
                ", retweeted=" + retweeted +
                ", retweetedCount=" + retweetedCount +
                ", user=" + (user == null ? "empty" : user.toString()) +
                '}';
    }
}