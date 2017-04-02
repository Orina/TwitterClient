package me.elmira.simpletwitterclient.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.elmira.simpletwitterclient.model.source.remote.JsonAttributes;

/**
 * Created by elmira on 3/29/17.
 */

public class UserCursoredCollection {

    long nextCursor;
    long previousCursor;

    List<User> users;

    public static UserCursoredCollection fromJSON(JSONObject jsonObject) throws JSONException {
        UserCursoredCollection cursoredCollection = new UserCursoredCollection();

        cursoredCollection.nextCursor = jsonObject.getLong(JsonAttributes.UserCursorCollection.NEXT_CURSOR);
        cursoredCollection.previousCursor = jsonObject.getLong(JsonAttributes.UserCursorCollection.PREV_CURSOR);

        JSONArray jsonArray = jsonObject.getJSONArray(JsonAttributes.UserCursorCollection.USERS);
        int N = jsonArray == null ? 0 : jsonArray.length();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            users.add(User.fromJSON(jsonArray.getJSONObject(i)));
        }

        cursoredCollection.users = users;
        return cursoredCollection;
    }

    public long getNextCursor() {
        return nextCursor;
    }

    public long getPreviousCursor() {
        return previousCursor;
    }

    public List<User> getUsers() {
        return users;
    }
}
