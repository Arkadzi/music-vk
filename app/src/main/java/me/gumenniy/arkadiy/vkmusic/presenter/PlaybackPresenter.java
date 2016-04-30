package me.gumenniy.arkadiy.vkmusic.presenter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.List;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.app.audio.Player;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.app.fragments.PlaybackView;

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
    private boolean shouldUpdateProgress = true;

    @Inject
    public PlaybackPresenter() {

    }

    public void handleClick(final int type) {
        if (player != null) {
            switch (type) {
                case PAUSE_PLAY:
                        if (player.isPlaying()) {
                            player.pause();
                        } else {
                            player.start();
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
                view.updatePlaybackButtonImage(player.isPlaying());
            }
        }
    }

    @Override
    public void onBeginPreparingSong(int position, @NonNull Song song, boolean shouldStart) {
        if (view != null) {
            view.renderSong(position, song);
            view.updatePlaybackButtonImage(shouldStart);
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
    public void onError(@NonNull Song song) {
    }

    @Override
    public void onQueueChanged(@NonNull List<Song> queue) {
        if (view != null) {
            view.setQueue(queue);
//            view.setPosition(position);
        }
    }

    @Override
    public void onSongBuffering(int bufferedPercent, int progress) {
        if (view != null && shouldUpdateProgress) {
            view.setBufferProgress(bufferedPercent, progress / 1000);
        }
    }

    @Override
    public void onImageLoaded(@NonNull Song song, @NonNull Bitmap bitmap) {
        if (view != null) {
            view.renderImage(song, bitmap);
        }
    }

    @Override
    public void onUrlLoaded(@NonNull Song song, @NonNull String url) {
        if (view != null) {
            view.renderImage(song, url);
        }
    }

    public void onProgressChanged(int progress) {
        if (player != null) {
            player.seekTo(progress * 1000);
        }
        shouldUpdateProgress = true;
    }

    @Nullable
    public Bitmap askBitmap(Song item) {
        if (player != null) {
            return player.getImageBitmap(item);
        }
        return null;
    }

    public void onStartTracking() {
        shouldUpdateProgress = false;
    }

    public String askUrl(Song item) {
        if (player != null) {
            return player.getImageUrl(item);
        }
        return null;
    }
}
