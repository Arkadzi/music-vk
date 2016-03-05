package me.gumenniy.arkadiy.vkmusic.rest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.model.User;
import me.gumenniy.arkadiy.vkmusic.rest.callback.FriendCallback;
import me.gumenniy.arkadiy.vkmusic.rest.callback.SongCallback;
import me.gumenniy.arkadiy.vkmusic.rest.event.friends.FriendsLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.friends.FriendsLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadedEvent;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class RestClient {
    private static OkHttpClient okClient;

    static {
        okClient = new OkHttpClient();
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
    }

    private final String baseUrl = "https://api.vk.com/method/";
    private List<Song> songs;
    private EventBus bus;
    private VkApiInterface vkApi;
    private User user;
    private SongCallback songCallback;
    private final FriendCallback friendCallback;

    public RestClient(EventBus bus) {
        Retrofit client = getClient();
        vkApi = client.create(VkApiInterface.class);
        this.bus = bus;
        songCallback = new SongCallback(this.bus);
        friendCallback = new FriendCallback(this.bus);
    }

    @NonNull
    private Retrofit getClient() {
        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(VKResponse.class, new VKResponseTypeAdapter())
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Subscribe
    public void onSongsLoad(SongsLoadEvent event) {
        songCallback.onLoadSongs(vkApi, user, event.refresh);
    }

    @Subscribe
    public void onFriendsLoad(FriendsLoadEvent event) {
        friendCallback.onLoadSongs(vkApi, user, event.refresh);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public interface VkApiInterface {
        @GET("audio.get?v=5.45&need_user=0")
        Call<VKResult<Song>> getSongs(
                @Query("owner_id") String ownerId,
                @Query("offset") int offset,
                @Query("count") int count,
                @Query("access_token") String token);

        @GET("friends.get?v=5.45&order=name&fields=domain,photo_50")
        Call<VKResult<Friend>> getFriends(
                @Query("user_id") String userId,
                @Query("offset") int offset,
                @Query("count") int count);
    }
}
