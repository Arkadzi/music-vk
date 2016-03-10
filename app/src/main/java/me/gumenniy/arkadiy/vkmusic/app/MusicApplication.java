package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import me.gumenniy.arkadiy.vkmusic.app.injection.DaggerRestComponent;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestClientModule;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class MusicApplication extends Application {

    private RestComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        buildAppComponent();
    }

    private void buildAppComponent() {
        component = DaggerRestComponent.builder()
                .restClientModule(new RestClientModule(this))
                .build();
    }

    public static MusicApplication getApp(Context context) {
        return (MusicApplication) context.getApplicationContext();
    }

    public RestComponent getComponent() {
        return component;
    }
}
