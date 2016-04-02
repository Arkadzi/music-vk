package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

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
public class SearchPresenter extends BaseListPresenter<Song> implements OnSearchSubmitListener, MenuListener {
    @NotNull
    private String query = "";
    private int onlyPerformer;

    private final EventBus eventBus;

    @Inject
    public SearchPresenter(@NotNull VkApi api, @NotNull UserSession user, @NotNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
        state = STATE_IDLE;
    }


    @NotNull
    @Override
    protected Call<VKResult<Song>> getApiCall(VkApi api, UserSession user) {
        return api.getSongsByQuery(query, onlyPerformer, getData().size(), 10, user.getToken());
    }

    @Override
    public void handleClick(int position) {
        eventBus.post(new PlayQueueEvent(getData(), position));
    }

    @Override
    public void onSearchSubmit(@NotNull String query) {
        Log.e("Search", "submit " + query);
        this.query = query;
        if (!query.isEmpty()) {
            reset();
            loadData(STATE_FIRST_LOAD);
        }

    }

    @Override
    public void onCheckableMenuItemClicked(boolean checked) {
        onlyPerformer = checked ? 1 : 0;
        if (!query.isEmpty()) {
            reset();
            loadData(STATE_FIRST_LOAD);
        }
    }

    @Override
    public void onMenuItemClicked(int id) {

    }

    public boolean isOnlyPerformer() {
        return onlyPerformer == 1;
    }
}
