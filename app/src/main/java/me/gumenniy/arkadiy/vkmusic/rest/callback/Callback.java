package me.gumenniy.arkadiy.vkmusic.rest.callback;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.User;
import me.gumenniy.arkadiy.vkmusic.rest.RestClient;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataFailedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataNotLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataStartLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.TokenRequiredEvent;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResponse;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public abstract class Callback<T> {
    private final EventBus bus;
    private List<T> data;
    private boolean isLoading;
    private int totalCount;

    public Callback(EventBus bus) {
        this.bus = bus;
    }

    public void onLoadSongs(RestClient.VkApiInterface vkApi, User user, boolean refresh) {
        Log.e("RestClient", "onLoadSongs() " + refresh + " " + user.getId() + " " + user.getToken());
        if (refresh) {
            data = null;
            totalCount = 0;
        }
        if (data == null || totalCount > data.size()) {
            int offset = (data == null) ? 0 : data.size();
            Call<VKResult<T>> call = getCall(vkApi, user, offset);
            isLoading = true;
            bus.post(new DataStartLoadEvent());
            call.enqueue(new retrofit.Callback<VKResult<T>>() {
                @Override
                public void onResponse(retrofit.Response<VKResult<T>> response, Retrofit retrofit) {
                    isLoading = false;
                    if (response.isSuccess()) {
                        VKError error = response.body().getError();
                        VKResponse audioResponse = response.body().getResponse();
                        if (audioResponse != null) {
                            if (data == null) data = new ArrayList<>();
                            List<T> newItems = response.body().getResponse().getItems();
                            totalCount = response.body().getResponse().getCount();
                            Log.e("VKResponse", data.size() + " " + newItems);
                            List<T> items = new ArrayList<>((newItems.size() + data.size()) * 2);
                            items.addAll(data);
                            items.addAll(newItems);
                            data = items;
                            bus.postSticky(getDataLoadedEvent(data, isLoading));

                        } else {
                            bus.post(new TokenRequiredEvent());
                        }
                    } else {
                        bus.post(new DataNotLoadedEvent());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("failure", t.getMessage());
                    isLoading = false;
                    bus.post(new DataFailedEvent());
                }
            });
        } else {
            bus.post(getDataLoadedEvent(data, isLoading));
        }
    }


    public abstract Object getDataLoadedEvent(List<T> data, boolean isLoading);

    protected abstract Call<VKResult<T>> getCall(RestClient.VkApiInterface vkApi, User user, int offset);
}
