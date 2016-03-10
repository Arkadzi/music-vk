package me.gumenniy.arkadiy.vkmusic.presenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Group;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 10.03.2016.
 */

@Singleton
public class GroupListPresenter extends BaseListPresenter<Group> {

    @Inject
    public GroupListPresenter(VkApi api, UserSession user) {
        super(api, user);
    }

    @Override
    protected Call<VKResult<Group>> getApiCall(VkApi api, UserSession user) {
        return api.getGroups(user.getClientId(), getData().size(), 100, user.getToken());
    }

    @Override
    public void handleClick(int position) {

    }
}
