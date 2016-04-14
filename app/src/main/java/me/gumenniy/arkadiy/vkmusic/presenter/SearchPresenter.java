package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

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
public class SearchPresenter extends BaseListPresenter<Song> implements OnSearchSubmitListener {
    @NonNull
    private String query = "";
    private int onlyPerformer;

    private final EventBus eventBus;

    @Inject
    public SearchPresenter(@NonNull VkApi api, @NonNull UserSession user, @NonNull EventBus eventBus) {
        super(api, user);
        this.eventBus = eventBus;
        state = STATE_IDLE;
    }


    @NonNull
    @Override
    protected Call<VKResult<Song>> getApiCall(@NonNull VkApi api, @NonNull UserSession user) {
        return api.getSongsByQuery(query, onlyPerformer, getData().size(), 10, user.getToken());
    }

    @Override
    public void handleClick(int position) {
        eventBus.post(new PlayQueueEvent(getData(), position));
    }

    @Override
    public void onSearchSubmit(@NonNull String query, boolean onlyPerformer) {
        this.query = query;
        this.onlyPerformer = onlyPerformer ? 1 : 0;

        if (!query.isEmpty()) {
            reset();
            loadData(STATE_FIRST_LOAD);
        }

    }

    public boolean isOnlyPerformer() {
        return onlyPerformer == 1;
    }

    @Override
    protected void loadData(int state) {
        if (!query.isEmpty()) {
            super.loadData(state);
        }
    }

    @Override
    public void refresh() {
        if (!query.isEmpty()) {
            super.refresh();
        } else {
            onLoadingStop();
        }
    }

    @NonNull
    public String getQuery() {
        return query;
    }
}
