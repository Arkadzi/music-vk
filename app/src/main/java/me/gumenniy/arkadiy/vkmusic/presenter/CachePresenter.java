package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.LocalCache;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.model.SongCache;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.SongLoadedEvent;

/**
 * Created by Arkadiy on 22.04.2016.
 */
@Singleton
public class CachePresenter implements BasePresenter<SimpleBaseView<Song>>, LocalCache.OnDataLoadedListener<Song> {
    private final EventBus eventBus;
    private final SongCache cache;
    public final List<Song> data = new ArrayList<>();
    @Nullable
    private SimpleBaseView<Song> view;
    private State state;

    @Inject
    public CachePresenter(EventBus eventBus, SongCache cache) {
        this.eventBus = eventBus;
        this.cache = cache;
        reset();
        eventBus.register(this);
    }

    @Override
    public void bindView(@Nullable SimpleBaseView<Song> view) {
        this.view = view;
        if (view != null) {
            switch (state) {
                case STATE_FIRST_BIND:
                    startLoad();
                    break;
                case STATE_IDLE:
                    onLoadingStop(data);
                    break;
                case STATE_FIRST_LOAD:
                    view.showProgress(state);
                    break;
            }
        }
    }

    @Override
    public void handleClick(int position) {
        eventBus.post(new PlayQueueEvent(data, position, true));
    }

    private void startLoad() {
        state = State.STATE_FIRST_LOAD;
        if (view != null) {
            view.showProgress(state);
        }
        cache.loadCache(this);
    }

    @Override
    public void onDataLoaded(List<Song> data) {
        this.data.clear();
        this.data.addAll(data);
        onLoadingStop(data);
        state = State.STATE_IDLE;
    }

    private void onLoadingStop(List<Song> data) {
        if (view != null) {
            view.renderData(data);
            view.hideProgress();
        }
    }

    @Subscribe
    public void onSongLoadedEvent(SongLoadedEvent event) {
        reset();
        if (view != null) {
            startLoad();
        }
    }

    private void reset() {
        state = State.STATE_FIRST_BIND;
        data.clear();
    }
}
