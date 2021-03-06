package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.domain.model.Song;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.AccessDeniedEvent;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.StartLoadingEvent;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.UpdateMyMusicEvent;
import me.gumenniy.arkadiy.vkmusic.data.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.data.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKListResult;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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
        eventBus.register(this);
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
    protected Call<VKListResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
        String ownerId = (userId.equals(CURRENT_USER)) ? user.getClientId() : userId;
        if (recommended) {
            return api.getRecommendedSongs(ownerId, getData().size(), 50, user.getToken());
        } else {
            return api.getSongs(ownerId, getData().size(), 50, user.getToken());
        }
    }

    @Override
    public void handleMenuClick(final Settings.Menu which, final Song song, final int position) {
        UserSession user = getUser();
        Call<VKResult<Long>> vkAddRemoveResultCall = null;
        switch (which) {
            case Add:
                vkAddRemoveResultCall = getVkApi().addSong(song.getId(), song.getOwnerId(), user.getToken());
                showProgressDialog();
                break;
            case Delete:
                vkAddRemoveResultCall = getVkApi().deleteSong(song.getId(), song.getOwnerId(), user.getToken());
                showProgressDialog();
                break;
            case Load:
                eventBus.post(new StartLoadingEvent(song));
                break;
        }

        if (vkAddRemoveResultCall != null) {
            vkAddRemoveResultCall.enqueue(new Callback<VKResult<Long>>() {
                @Override
                public void onResponse(Response<VKResult<Long>> response, Retrofit retrofit) {
                    dismissProgressDialog();
                    VKResult<Long> body = response.body();
                    if (!response.isSuccess() || !body.isSuccessful()) {
                        showMessage(body.getError().getErrorMessage());
                    } else if (which == Settings.Menu.Delete && song == getData().get(position)) {
                        getData().remove(position);
                        BaseView<Song> view = getView();
                        if (view != null) {
                            view.renderData(getData());
                        }
                    } else if (which == Settings.Menu.Add) {
                        showMessage("added");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dismissProgressDialog();
                    showMessage(String.valueOf(t));
                }
            });
        }
    }

    public boolean currentUserList() {
        return (userId.equals(CURRENT_USER) && !recommended);
    }

    @Override
    public void handleClick(int position) {
        eventBus.post(new PlayQueueEvent(getData(), position));
    }

    public void setUserId(@NonNull String userId, boolean recommended) {
        if (!userId.equals(this.userId) || recommended != this.recommended) {
            this.userId = userId;
            this.recommended = recommended;
            reset();
        }
    }

    @Subscribe
    public void onUpdateMyMusicEvent(UpdateMyMusicEvent event) {
        reset();
        if (getView() != null) {
            loadData(State.STATE_FIRST_LOAD);
        }
    }
}
