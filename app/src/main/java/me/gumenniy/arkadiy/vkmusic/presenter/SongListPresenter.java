package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.AccessDeniedEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.UpdateMyMusicEvent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKAddRemoveResult;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
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
    protected Call<VKResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
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
        Call<VKAddRemoveResult> vkAddRemoveResultCall = null;
        switch (which) {
            case Add:
                vkAddRemoveResultCall = getVkApi().addSong(song.getId(), song.getOwnerId(), user.getToken());
                break;
            case Delete:
                vkAddRemoveResultCall = getVkApi().deleteSong(song.getId(), song.getOwnerId(), user.getToken());
                showProgressDialog();
                break;
        }
        vkAddRemoveResultCall.enqueue(new Callback<VKAddRemoveResult>() {
            @Override
            public void onResponse(Response<VKAddRemoveResult> response, Retrofit retrofit) {
                dismissProgressDialog();
                VKAddRemoveResult body = response.body();
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
//                dismissProgressDialog();
                showMessage(String.valueOf(t));
            }
        });
    }

    private void handleNotUserSongMenu(int which, Song song) {
        switch (which) {
            case 0:
                UserSession user = getUser();
                getVkApi().addSong(song.getId(), song.getOwnerId(), user.getToken()).enqueue(new Callback<VKAddRemoveResult>() {
                    @Override
                    public void onResponse(Response<VKAddRemoveResult> response, Retrofit retrofit) {
                        VKAddRemoveResult body = response.body();
                        if (!response.isSuccess() || !body.isSuccessful()) {
                            showMessage(body.getError().getErrorMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        showMessage(String.valueOf(t));
                    }
                });
                break;
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
