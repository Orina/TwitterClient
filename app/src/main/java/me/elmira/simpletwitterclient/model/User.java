package me.elmira.simpletwitterclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import me.elmira.simpletwitterclient.model.source.remote.JsonAttributes;

/**
 * Created by elmira on 3/22/17.
 */

public class User implements Parcelable {

    private static DecimalFormat decimalFormatter = new DecimalFormat("#,###,###");

    private String name;
    private long uid;
    private String profileImageUrl;
    private String screenName;

    private int followingCount;
    private int followersCount;

    private String bannerUrl;

    private boolean following;
    private String description;

    public User() {
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString(JsonAttributes.User.NAME);
        user.uid = jsonObject.getLong(JsonAttributes.User.UID);
        user.profileImageUrl = jsonObject.getString(JsonAttributes.User.PROFILE_IMAGE_URL);
        if (user.profileImageUrl != null && user.profileImageUrl.contains("_normal.")) {
            user.profileImageUrl = user.profileImageUrl.replace("_normal.", ".");
        }
        user.screenName = jsonObject.getString(JsonAttributes.User.SCREEN_NAME);
        user.following = jsonObject.has(JsonAttributes.User.FOLLOWING) && jsonObject.getBoolean(JsonAttributes.User.FOLLOWING);

        user.followersCount = jsonObject.getInt(JsonAttributes.User.FOLLOWERS_COUNT);
        user.followingCount = jsonObject.getInt(JsonAttributes.User.FOLLOWING_COUNT);

        user.bannerUrl = jsonObject.has(JsonAttributes.User.BANNER_URL) ? jsonObject.getString(JsonAttributes.User.BANNER_URL) : null;
        user.description = jsonObject.has(JsonAttributes.User.DESCRIPTION) ? jsonObject.getString(JsonAttributes.User.DESCRIPTION) : null;

        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDisplayScreenName() {
        return "@" + screenName;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public boolean isFollowing() {
        return following;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedFollowingCount() {
        return decimalFormatter.format(followingCount);
    }

    public String getFormattedFollowersCount() {
        return decimalFormatter.format(followersCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return uid == user.uid;
    }

    @Override
    public int hashCode() {
        return (int) (uid ^ (uid >>> 32));
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid=" + uid +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", screenName='" + screenName + '\'' +
                ", followingCount=" + followingCount +
                ", followersCount=" + followersCount +
                ", bannerUrl='" + bannerUrl + '\'' +
                ", following=" + following +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.screenName);
        dest.writeInt(this.followingCount);
        dest.writeInt(this.followersCount);
        dest.writeString(this.bannerUrl);
        dest.writeByte(this.following ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.profileImageUrl = in.readString();
        this.screenName = in.readString();
        this.followingCount = in.readInt();
        this.followersCount = in.readInt();
        this.bannerUrl = in.readString();
        this.following = in.readByte() != 0;
        this.description = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
