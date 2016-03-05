package me.gumenniy.arkadiy.vkmusic.rest.callback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.model.User;
import me.gumenniy.arkadiy.vkmusic.rest.RestClient;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class SongCallback extends Callback<Song> {
    public SongCallback(EventBus bus) {
        super(bus);
    }

//    @Override
//    public SongsFailedEvent getDataFailedEvent() {
//        return new SongsFailedEvent();
//    }
//
//    @Override
//    public SongsNotLoadedEvent getDataNotLoadedEvent() {
//        return new SongsNotLoadedEvent();
//    }

    @Override
    public SongsLoadedEvent getDataLoadedEvent(List<Song> data, boolean isLoading) {
        return new SongsLoadedEvent(data, isLoading);
    }

    @Override
    protected Call<VKResult<Song>> getCall(RestClient.VkApiInterface vkApi, User user, int offset) {
        return vkApi.getSongs(user.getId(), offset, 100, user.getToken());
    }
}
