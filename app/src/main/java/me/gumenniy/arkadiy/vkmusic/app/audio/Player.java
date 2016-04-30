package me.gumenniy.arkadiy.vkmusic.app.audio;



import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public interface Player {
    @NonNull
    List<Song> getQueue();
    int getCurrentQueuePosition();
    boolean isQueueEmpty();
    boolean isPrepared();
    boolean isPlaying();
    void start();
    void pause();
    void next();
    void prev();
    void setPlayerListener(@Nullable PlayerListener listener);
    void seekTo(int position);
    @Nullable
    Song getCurrentSong();
    void playSong(int position);
    @Nullable
    Bitmap getImageBitmap(@NonNull Song song);
    @Nullable
    String getImageUrl(@NonNull Song item);

    boolean isShouldStart();

    interface PlayerListener {
        void onBeginPreparingSong(int position, @NonNull Song song, boolean shouldStart);
        void onSongStarted();
        void onSongPaused();
        void onError(@NonNull Song song);
        void onQueueChanged(@NonNull List<Song> queue);
        void onSongBuffering(int bufferedPercent, int progress);
        void onImageLoaded(@NonNull Song song, @NonNull Bitmap bitmap);
        void onUrlLoaded(@NonNull Song song, @NonNull String url);
    }
}
