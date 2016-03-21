package me.gumenniy.arkadiy.vkmusic.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.model.Group;
import me.gumenniy.arkadiy.vkmusic.presenter.event.AccessDeniedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 10.03.2016.
 */

@Singleton
public class GroupListPresenter extends BaseListPresenter<Group> {

    @NotNull
    private final EventBus eventBus;

    @Inject
    public GroupListPresenter(VkApi api, UserSession user, @NotNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }


    @Subscribe
    public void onSuccessDenied(AccessDeniedEvent event) {
        for (Group group : getData()) {
            String groupId = String.format("-%s", group.getId());
            if (groupId.equals(event.id)) {
                group.setIsAudioAvailable(false);
            }
        }
    }

    @Override
    @NotNull
    protected Call<VKResult<Group>> getApiCall(VkApi api, UserSession user) {
        return api.getGroups(user.getClientId(), getData().size(), 100, user.getToken());
    }


    @Override
    public void handleClick(int position) {
        Group item = getData().get(position);
        if (item.isAudioAvailable()) {
            BaseView<Group> view = getView();
            view.navigateBy(item);
        } else {
            showError("-201");
        }
    }
}
