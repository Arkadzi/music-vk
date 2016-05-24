package me.gumenniy.arkadiy.vkmusic.app.fragments;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public interface PlaybackView {
    void setQueue(@NonNull List<Song> queue);

    void renderSong(int position, @NonNull Song song);

    void updatePlaybackButtonImage(boolean started);

    void setBufferProgress(int percent, int progress);

    void renderImage(@NonNull Song song, @NonNull Bitmap bitmap);
    void renderImage(@NonNull Song song, @NonNull String url);

    void renderLyrics(Song song, String lyrics);
}
