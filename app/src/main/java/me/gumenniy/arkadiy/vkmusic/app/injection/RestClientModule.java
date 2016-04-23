package me.gumenniy.arkadiy.vkmusic.app.injection;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.model.Album;
import me.gumenniy.arkadiy.vkmusic.model.Artwork;
import me.gumenniy.arkadiy.vkmusic.model.SongCache;
import me.gumenniy.arkadiy.vkmusic.rest.LastFMApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.adapter.AlbumAdapter;
import me.gumenniy.arkadiy.vkmusic.rest.model.adapter.ArtworkAdapter;
import me.gumenniy.arkadiy.vkmusic.rest.model.adapter.VKResultTypeAdapter;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 07.03.2016.
 */
@Module
public class RestClientModule {
    private MusicApplication app;
    public static String baseUrl = "https://api.vk.com/method/";

    public RestClientModule(MusicApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkClient() {
        OkHttpClient okClient = new OkHttpClient();
        okClient.setReadTimeout(10, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okClient.interceptors().add(interceptor);
        okClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                Log.e("response", response.toString());
                return response;
            }
        });

        return okClient;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okClient) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VKResult.class, new VKResultTypeAdapter())
                .registerTypeAdapter(Album.class, new AlbumAdapter())
                .registerTypeAdapter(Artwork.class, new ArtworkAdapter())
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    public VkApi provideVkApi(Retrofit retrofit) {
        return retrofit.create(VkApi.class);
    }


    @Provides
    @Singleton
    public LastFMApi provideLastFMApi(Retrofit retrofit) {
        return retrofit.create(LastFMApi.class);
    }

    @Provides
    @Singleton
    public SongCache provideSongCache() {
        return new SongCache(app);
    }

    @Provides
    @Singleton
    SharedPreferences providePreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    UserSession provideUserSession(SharedPreferences prefs) {
        return new UserSession(prefs, app);
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
