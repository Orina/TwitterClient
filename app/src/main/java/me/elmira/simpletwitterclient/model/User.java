package me.elmira.simpletwitterclient.model;

import org.json.JSONException;
import org.json.JSONObject;

import me.elmira.simpletwitterclient.model.source.remote.JsonAttributes;

/**
 * Created by elmira on 3/22/17.
 */

public class User {

    private String name;
    private long uid;
    private String profileImageUrl;
    private String screenName;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString(JsonAttributes.User.NAME);
        user.uid = jsonObject.getLong(JsonAttributes.User.UID);
        user.profileImageUrl = jsonObject.getString(JsonAttributes.User.PROFILE_IMAGE_URL);
        if (user.profileImageUrl!=null && user.profileImageUrl.contains("_normal.")){
            user.profileImageUrl = user.profileImageUrl.replace("_normal.", ".");
        }
        user.screenName = jsonObject.getString(JsonAttributes.User.SCREEN_NAME);
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
                '}';
    }
}
