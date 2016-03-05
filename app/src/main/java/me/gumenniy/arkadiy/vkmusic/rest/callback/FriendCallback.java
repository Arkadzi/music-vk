package me.gumenniy.arkadiy.vkmusic.rest.callback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.model.User;
import me.gumenniy.arkadiy.vkmusic.rest.RestClient;
import me.gumenniy.arkadiy.vkmusic.rest.event.friends.FriendsLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class FriendCallback extends Callback<Friend> {
    public FriendCallback(EventBus bus) {
        super(bus);
    }

//    @Override
//    public FriendsFailedEvent getDataFailedEvent() {
//        return new FriendsFailedEvent();
//    }
//
//    @Override
//    public FriendsNotLoadedEvent getDataNotLoadedEvent() {
//        return new FriendsNotLoadedEvent();
//    }

    @Override
    public FriendsLoadedEvent getDataLoadedEvent(List<Friend> data, boolean isLoading) {
        return new FriendsLoadedEvent(data, isLoading);
    }

    @Override
    protected Call<VKResult<Friend>> getCall(RestClient.VkApiInterface vkApi, User user, int offset) {
        return vkApi.getFriends(user.getId(), offset, 100);
    }
}
