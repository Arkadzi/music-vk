package me.gumenniy.arkadiy.vkmusic.presenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    @NotNull
    protected Call<VKResult<Friend>> getApiCall(VkApi api, UserSession user) {
        return api.getFriends(user.getClientId(), getData().size(), 100);
    }

    @Override
    public void handleClick(int position) {
        Friend item = getData().get(position);
        if (item.isAudioAvailable()) {
            BaseView<Friend> view = getView();
            view.navigateBy(item);
        } else {
            showError("201");
        }
    }
}
