package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

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
    @NonNull
    private final EventBus eventBus;
    @NonNull
    private String userId;
    private boolean recommended;

    @Inject
    public SongListPresenter(@NonNull VkApi api, UserSession user, @NonNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
        userId = "";
    }

    @Override
    protected boolean handled(@NonNull VKError error) {
        if (error.getErrorCode() == 15) {
            eventBus.post(new AccessDeniedEvent(userId));
        }
        return super.handled(error);
    }

    @Override
    @NonNull
    protected Call<VKResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
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

    public void setUserId(@NonNull String userId, boolean recommended) {
        if (!userId.equals(this.userId) || recommended != this.recommended) {
            this.userId = userId;
            this.recommended = recommended;
            reset();
        }
    }
}
