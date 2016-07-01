package me.gumenniy.arkadiy.vkmusic.injection;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.async.SupportLoader;
import me.gumenniy.arkadiy.vkmusic.data.db.DbHelper;
import me.gumenniy.arkadiy.vkmusic.data.db.SQLiteStorageFactory;
import me.gumenniy.arkadiy.vkmusic.data.db.StorageFactory;
import me.gumenniy.arkadiy.vkmusic.domain.model.Album;
import me.gumenniy.arkadiy.vkmusic.domain.model.Artwork;
import me.gumenniy.arkadiy.vkmusic.domain.model.SongCache;
import me.gumenniy.arkadiy.vkmusic.data.rest.LastFMApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.adapter.AlbumAdapter;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.adapter.ArtworkAdapter;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.adapter.VKResultTypeAdapter;
import me.gumenniy.arkadiy.vkmusic.data.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.data.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKListResult;
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
        okClient.setCache(new Cache(app.getCacheDir(), Integer.MAX_VALUE));
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
    public StorageFactory provideStorageFactory(DbHelper helper) {
        return new SQLiteStorageFactory(helper);
    }

    @Provides
    @Singleton
    public Picasso providePicasso(OkHttpClient client) {
        return new Picasso.Builder(app)
//                .downloader(new OkHttpDownloader(client))
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Provides
    @Singleton
    public SupportLoader provideImageLoader(LastFMApi lastFMApi, VkApi vkApi,  UserSession userSession, StorageFactory factory) {
        return new SupportLoader(lastFMApi, vkApi, app, userSession, factory);
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okClient) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VKListResult.class, new VKResultTypeAdapter())
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
    public DbHelper provideDBHelper() {
        return new DbHelper(app);
    }

    @Provides
    @Singleton
    public SongCache provideSongCache(StorageFactory factory) {
        return new SongCache(factory);
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
