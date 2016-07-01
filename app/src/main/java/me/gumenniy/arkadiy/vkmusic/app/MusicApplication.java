package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Application;
import android.content.Context;

import me.gumenniy.arkadiy.vkmusic.injection.ApplicationComponent;
import me.gumenniy.arkadiy.vkmusic.injection.ApplicationModule;
import me.gumenniy.arkadiy.vkmusic.injection.DaggerApplicationComponent;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class MusicApplication extends Application {

    private ApplicationComponent component;

    public static MusicApplication getApp(Context context) {
        return (MusicApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildAppComponent();
    }

    private void buildAppComponent() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
