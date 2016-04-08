package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.app.Player;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.view.PlaybackView;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public class PlaybackPresenter implements BasePresenter<PlaybackView>, Player.PlayerListener {

    public static final int PAUSE_PLAY = 0;
    public static final int PREV = -1;
    public static final int NEXT = 1;

    @Nullable
    private Player player;
    @Nullable
    private PlaybackView view;

    @Inject
    public PlaybackPresenter() {

    }

    public void handleClick(final int type) {
        if (player != null) {
            switch (type) {
                case PAUSE_PLAY:
                    if (player.isPrepared()) {
                        if (player.isPlaying()) {
                            player.pause();
                        } else {
                            player.start();
                        }
                    }
                    break;
                case PREV:
                    player.prev();
                    break;
                case NEXT:
                    player.next();
                    break;
            }
        }
    }

    public void onPageSelected(int position) {
        if (player != null) {
            player.playSong(position);
        }
    }

    @Override
    public void bindView(@Nullable PlaybackView view) {
        this.view = view;
        /*if (view == null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        } else if (view != null && !eventBus.isRegistered(this)) {
            eventBus.register(this);
        }*/
    }

    public void setPlayer(@Nullable Player player) {
        if (player == null) {
            if (this.player != null) {
                this.player.setPlayerListener(null);
            }
            this.player = null;
        } else {
            this.player = player;
            player.setPlayerListener(this);
            Song song = player.getCurrentSong();
            if (song != null && view != null) {
                view.setQueue(player.getQueue());
                view.renderSong(player.getCurrentQueuePosition(), song);
                view.updatePlaybackButtonImage(player.isPrepared() && player.isPlaying());
            }
        }
    }

    @Override
    public void onBeginPreparingSong(int position, Song song) {
        if (view != null) {
            view.renderSong(position, song);
        }
    }

    @Override
    public void onSongStarted() {
        if (view != null) {
            view.updatePlaybackButtonImage(true);
        }
    }

    @Override
    public void onSongPaused() {
        if (view != null) {
            view.updatePlaybackButtonImage(false);
        }
    }

    @Override
    public void onError(Song song) {
    }

    @Override
    public void onQueueChanged(@NotNull List<Song> queue) {
        if (view != null) {
            view.setQueue(queue);
//            view.setPosition(position);
        }
    }

    @Override
    public void onSongBuffering(int bufferedPercent, int progress) {
        if (view != null) {
            view.setBufferProgress(bufferedPercent, progress / 1000);
        }
    }

    @Override
    public void onImageLoaded(Song song, String url) {
        if (view != null) {
            view.renderImage(song, url);
        }
    }

    public void onProgressChanged(int progress) {
        if (player != null) {
            player.seekTo(progress * 1000);
        }
    }

    @Nullable
    public String askUrl(Song item) {
        if (player != null) {
            return player.loadImageUrl(item);
        }
        return null;
    }
}
