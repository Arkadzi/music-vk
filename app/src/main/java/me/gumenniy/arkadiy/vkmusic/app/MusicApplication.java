package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Application;
import android.content.Context;

//import me.gumenniy.arkadiy.vkmusic.injection.DaggerRestComponent;
import me.gumenniy.arkadiy.vkmusic.injection.DaggerRestComponent;
import me.gumenniy.arkadiy.vkmusic.injection.RestClientModule;
import me.gumenniy.arkadiy.vkmusic.injection.RestComponent;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class MusicApplication extends Application {

    private RestComponent component;

    public static MusicApplication getApp(Context context) {
        return (MusicApplication) context.getApplicationContext();
    }

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

    public RestComponent getComponent() {
        return component;
    }
}
