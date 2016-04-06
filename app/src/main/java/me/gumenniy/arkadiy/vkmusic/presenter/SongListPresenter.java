package me.gumenniy.arkadiy.vkmusic.presenter;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.AccessDeniedEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 07.03.2016.
 */

@Singleton
public class SongListPresenter extends BaseListPresenter<Song> {

    public final static String CURRENT_USER = "current";
    @NotNull
    private final EventBus eventBus;
    @NotNull
    private String userId;
    private boolean recommended;

    @Inject
    public SongListPresenter(VkApi api, UserSession user, @NotNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
    }

    @Override
    protected boolean handled(VKError error) {
        if (error.getErrorCode() == 15) {
            eventBus.post(new AccessDeniedEvent(userId));
        }
        return super.handled(error);
    }

    @Override
    @NotNull
    protected Call<VKResult<Song>> getApiCall(VkApi api, UserSession user) {
        String ownerId = (userId.equals(CURRENT_USER)) ? user.getClientId() : userId;
//        api.getRecommendedSongs(ownerId, getData().size(), 50, user.getToken());
        if (recommended) {
            return api.getRecommendedSongs(ownerId, getData().size(), 50, user.getToken());
        } else {
            return api.getSongs(ownerId, getData().size(), 50, user.getToken());
        }
    }

    @Override
    public void handleClick(int position) {
        Log.e("debug", "1");
        eventBus.post(new PlayQueueEvent(getData(), position));
    }

    public void setUserId(@NotNull String userId, boolean recommended) {
        if (!userId.equals(this.userId) || recommended != this.recommended) {
            this.userId = userId;
            this.recommended = recommended;
            reset();
        }
    }
}
