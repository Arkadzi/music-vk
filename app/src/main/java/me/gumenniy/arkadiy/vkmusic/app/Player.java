package me.gumenniy.arkadiy.vkmusic.app;


import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public interface Player {
    List<Song> getQueue();
    int getCurrentQueuePosition();
    boolean isQueueEmpty();
    boolean isPrepared();
    boolean isPlaying();
    void start();
    void pause();
    void next();
    void prev();
    void setPlayerListener(PlayerListener listener);
    void seekTo(int position);
    Song getCurrentSong();
    String loadImageUrl(Song song);

    interface PlayerListener {
        void onBeginPreparingSong(int position, Song song);
        void onSongStarted();
        void onSongPaused();
        void onError(Song song);
        void onQueueChanged(@NotNull List<Song> queue);
        void onSongBuffering(int bufferedPercent, int progress);
        void onImageLoaded(Song song, String url);
    }
}
