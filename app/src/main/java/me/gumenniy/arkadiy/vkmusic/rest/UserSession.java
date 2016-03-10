package me.gumenniy.arkadiy.vkmusic.rest;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class UserSession {
    private final String TOKEN = "token";
    private final String CLIENT_ID = "client_id";

    private SharedPreferences prefs;
    private String token;
    private String clientId;

    public UserSession(SharedPreferences prefs) {
        this.prefs = prefs;
        this.token = prefs.getString(TOKEN, "");
        this.clientId = prefs.getString(CLIENT_ID, "");
    }

    public String getToken() {
        return token;
    }

    public String getClientId() {
        return clientId;
    }

    public void update(String userId, String token) {

        SharedPreferences.Editor editor = prefs.edit();
        this.token = token;
        this.clientId = userId;
        editor.putString(TOKEN, token);
        editor.putString(CLIENT_ID, clientId);
        editor.apply();
    }
}
