package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Application;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import me.gumenniy.arkadiy.vkmusic.model.User;
import me.gumenniy.arkadiy.vkmusic.rest.RestClient;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class MusicApplication extends Application {
    private RestClient client;

    private EventBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = EventBus.getDefault();

        client = new RestClient(bus);
        restoreUser();
        bus.register(client);
    }

    public EventBus getBus() {
        return bus;
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
