package me.gumenniy.arkadiy.vkmusic;

import android.app.Application;
import android.content.SharedPreferences;

import com.squareup.otto.Bus;

import me.gumenniy.arkadiy.vkmusic.pojo.User;
import me.gumenniy.arkadiy.vkmusic.rest.RestClient;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class MusicApplication extends Application {
    private RestClient client;

    private Bus mBus;
    @Override
    public void onCreate() {
        super.onCreate();
        mBus = new Bus();

        client = new RestClient(mBus);
        restoreUser();
        mBus.register(client);
    }

    public Bus getBus() {
        return mBus;
    }

    public RestClient getClient() {
        return client;
    }

    private void restoreUser() {
        SharedPreferences prefs = getSharedPreferences(Settings.PREFS, MODE_PRIVATE);
        String accessToken = prefs.getString(LoginActivity.TOKEN, "");
        String uid = prefs.getString(LoginActivity.UID, "");
        User user = new User(accessToken, uid);
        client.setUser(user);
    }
}
