package me.gumenniy.arkadiy.vkmusic.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.data.injection.DataModule;

/**
 * Created by sebastian on 01.07.16.
 */
@Module(includes = DataModule.class)
public class ApplicationModule {
    private MusicApplication app;

    public ApplicationModule(MusicApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app;
    }
}
