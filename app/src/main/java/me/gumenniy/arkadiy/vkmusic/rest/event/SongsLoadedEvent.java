package me.gumenniy.arkadiy.vkmusic.rest.event;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.pojo.Song;

/**
 * Created by Arkadiy on 20.02.2016.
 */
public class SongsLoadedEvent {
    public final List<Song> songs;
    public final boolean isLoading;

    public SongsLoadedEvent(List<Song> songs, boolean isLoading) {
        this.songs = songs;
        this.isLoading = isLoading;
    }
}
