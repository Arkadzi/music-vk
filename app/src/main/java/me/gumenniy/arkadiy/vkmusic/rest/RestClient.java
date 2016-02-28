package me.gumenniy.arkadiy.vkmusic.rest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.gumenniy.arkadiy.vkmusic.pojo.Friend;
import me.gumenniy.arkadiy.vkmusic.pojo.VKResult;
import me.gumenniy.arkadiy.vkmusic.pojo.Song;
import me.gumenniy.arkadiy.vkmusic.pojo.User;
import me.gumenniy.arkadiy.vkmusic.pojo.VKResponse;
import me.gumenniy.arkadiy.vkmusic.pojo.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataFailedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataNotLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.TokenRequiredEvent;
import retrofit.Call;
import retrofit.Callback;
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
    private Bus mBus;
    private VkApiInterface vkApi;
    private User user;
    private boolean isLoading;

    public RestClient(Bus bus) {
        Retrofit client = getClient();
        vkApi = client.create(VkApiInterface.class);
        mBus = bus;
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
    public void onLoadSongs(final DataLoadEvent event) {
        Log.e("RestClient", "onLoadSongs() " + event.refresh + " " + user.getId() + " " + user.getToken());
        if (event.refresh) songs = null;
        int offset = (songs == null) ? 0 : songs.size();
        Call<VKResult<Song>> call = vkApi.getSongs(user.getId(), offset, 100, user.getToken());
        isLoading = true;
        call.enqueue(new Callback<VKResult<Song>>() {
            @Override
            public void onResponse(retrofit.Response<VKResult<Song>> response, Retrofit retrofit) {
                isLoading = false;
                if (response.isSuccess()) {
                    VKError error = response.body().getError();
                    VKResponse audioResponse = response.body().getResponse();
                    if (audioResponse != null) {
                        if (songs == null) songs = new ArrayList<>();

                        List<Song> newItems = response.body().getResponse().getItems();
                        Log.e("VKResponse", songs.size() + " " + newItems);
                        List<Song> items = new ArrayList<>((newItems.size() + songs.size()) * 2);
                        items.addAll(songs);
                        items.addAll(newItems);
                        songs = items;
                        mBus.post(new SongsLoadedEvent(songs, isLoading));

                    } else {
                        mBus.post(new TokenRequiredEvent());
                    }
                } else {
                    mBus.post(new DataNotLoadedEvent());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("failure", t.getMessage());
                isLoading = false;
                mBus.post(new DataFailedEvent());
            }
        });
    }


    @Produce
    public SongsLoadedEvent getLoadedSongs() {
        return new SongsLoadedEvent(songs, isLoading);
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

        @GET("audio.get?v=5.45&order=name&fields=domain,photo_50")
        Call<VKResult<Friend>> getFriends(
                @Query("user_id") String userId,
                @Query("offset") int offset,
                @Query("count") int count);
    }
}
