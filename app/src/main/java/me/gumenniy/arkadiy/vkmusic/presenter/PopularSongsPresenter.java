package me.gumenniy.arkadiy.vkmusic.presenter;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 30.03.2016.
 */
@Singleton
public class PopularSongsPresenter extends BaseListPresenter<Song> implements MenuListener {
    private final EventBus eventBus;
    private int onlyEng;
    private int genreId;

    @Inject
    public PopularSongsPresenter(@NotNull VkApi api, @NotNull UserSession user, @NotNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
    }

    @NotNull
    @Override
    protected Call<VKResult<Song>> getApiCall(VkApi api, UserSession user) {
        if (genreId == 0) {
            return api.getPopularSongs(onlyEng, getData().size(), 3, user.getToken());
        } else {
            return api.getPopularSongsByGenre(onlyEng, genreId, getData().size(), 3, user.getToken());
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
        loadData(STATE_FIRST_LOAD);

    }

    @Override
    public void onMenuItemClicked(int id) {
        reset();
        genreId = id;
        loadData(STATE_FIRST_LOAD);
    }

    public boolean isOnlyForeign() {
        return onlyEng == 1;
    }
}
