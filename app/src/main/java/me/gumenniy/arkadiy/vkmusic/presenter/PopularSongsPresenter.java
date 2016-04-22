package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.UpdateMyMusicEvent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKAddRemoveResult;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
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
    protected Call<VKResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
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
                UserSession user = getUser();
                Log.e("user", String.format("%s %s %s", song.getId(), user.getClientId(), user.getToken()));
                getVkApi().addSong(song.getId(), song.getOwnerId(), user.getToken()).enqueue(new Callback<VKAddRemoveResult>() {
                    @Override
                    public void onResponse(Response<VKAddRemoveResult> response, Retrofit retrofit) {
                        VKAddRemoveResult body = response.body();
                        if (response.isSuccess() && body.isSuccessful()) {
                            eventBus.post(new UpdateMyMusicEvent());
                            showMessage("added");
                        } else {
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
