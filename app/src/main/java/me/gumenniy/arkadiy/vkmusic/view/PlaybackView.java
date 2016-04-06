package me.gumenniy.arkadiy.vkmusic.view;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public interface PlaybackView {
    void setQueue(List<Song> queue);

    void renderSong(int position, @NotNull Song song);

    void updatePlaybackButtonImage(boolean started);

    void setBufferProgress(int percent, int progress);

    void renderImage(Song song, String url);
}
