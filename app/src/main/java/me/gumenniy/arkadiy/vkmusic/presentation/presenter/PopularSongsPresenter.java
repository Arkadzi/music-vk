package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.domain.model.Song;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.StartLoadingEvent;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.UpdateMyMusicEvent;
import me.gumenniy.arkadiy.vkmusic.data.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.data.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKListResult;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 30.03.2016.
 */
@Singleton
public class PopularSongsPresenter extends BaseListPresenter<Song> implements MenuListener {
    private final EventBus eventBus;
    private int onlyEng;
    private int genreId;

    @Inject
    public PopularSongsPresenter(@NonNull VkApi api, @NonNull UserSession user, @NonNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
    }

    @NonNull
    @Override
    protected Call<VKListResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
        if (genreId == 0) {
            return api.getPopularSongs(onlyEng, getData().size(), 3, user.getToken());
        } else {
            return api.getPopularSongsByGenre(onlyEng, genreId, getData().size(), 3, user.getToken());
        }
    }

    @Override
    public void handleMenuClick(Settings.Menu which, Song song, int position) {
        switch (which) {
            case Add:
                showProgressDialog();
                UserSession user = getUser();
                getVkApi().addSong(song.getId(), song.getOwnerId(), user.getToken()).enqueue(new Callback<VKResult<Long>>() {
                    @Override
                    public void onResponse(Response<VKResult<Long>> response, Retrofit retrofit) {
                        dismissProgressDialog();
                        VKResult<Long> body = response.body();
                        if (response.isSuccess() && body.isSuccessful()) {
                            eventBus.post(new UpdateMyMusicEvent());
                            showMessage("added");
                        } else {
                            showMessage(body.getError().getErrorMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        dismissProgressDialog();
                        showMessage(String.valueOf(t));
                    }
                });
                break;
            case Load:
                eventBus.post(new StartLoadingEvent(song));
                break;
        }
    }


    @Override
    public void handleClick(int position) {
        eventBus.post(new PlayQueueEvent(getData(), position));
    }

    @Override
    public void onCheckableMenuItemClicked(boolean checked) {
        reset();
        onlyEng = checked ? 1 : 0;
        loadData(State.STATE_FIRST_LOAD);
    }

    @Override
    public void onMenuItemClicked(int id) {
        reset();
        genreId = id;
        loadData(State.STATE_FIRST_LOAD);
    }

    public boolean isOnlyForeign() {
        return onlyEng == 1;
    }
}
