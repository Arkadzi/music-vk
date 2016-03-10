package me.gumenniy.arkadiy.vkmusic.presenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 10.03.2016.
 */

@Singleton
public class FriendListPresenter extends BaseListPresenter<Friend> {

    @Inject
    public FriendListPresenter(VkApi api, UserSession user) {
        super(api, user);
    }

    @Override
    protected Call<VKResult<Friend>> getApiCall(VkApi api, UserSession user) {
        return api.getFriends(user.getClientId(), getData().size(), 100);
    }

    @Override
    public void handleClick(int position) {
    }
}
