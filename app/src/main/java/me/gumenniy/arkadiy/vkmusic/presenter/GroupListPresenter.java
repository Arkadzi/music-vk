package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    @NonNull
    private final EventBus eventBus;

    @Inject
    public GroupListPresenter(@NonNull VkApi api, UserSession user, @NonNull EventBus eventBus) {
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
    @NonNull
    protected Call<VKResult<Group>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
        return api.getGroups(user.getClientId(), getData().size(), 100, user.getToken());
    }


    @Override
    public void handleClick(int position) {
        Group item = getData().get(position);
        if (item.isAudioAvailable()) {
            BaseView<Group> view = getView();
            if (view != null)
                view.navigateBy(item);
        } else {
            showMessage("-201");
        }
    }
}
