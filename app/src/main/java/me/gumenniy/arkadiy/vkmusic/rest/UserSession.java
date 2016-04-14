package me.gumenniy.arkadiy.vkmusic.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class UserSession {
    private final String TOKEN = "token";
    private final String CLIENT_ID = "client_id";
    private final Context context;

    private SharedPreferences prefs;
    private String token;
    private String clientId;

    public UserSession(SharedPreferences prefs, Context context) {
        this.prefs = prefs;
        this.token = prefs.getString(TOKEN, "");
        this.clientId = prefs.getString(CLIENT_ID, "");
        this.context = context;
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

    private void logout() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieSyncManager.createInstance(context);
            CookieManager.getInstance().removeAllCookie();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebViewDatabase.getInstance(context).clearUsernamePassword();
        }
    }
}
