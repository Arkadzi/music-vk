package me.gumenniy.arkadiy.vkmusic.rest.event.songs;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadedEvent;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class SongsLoadedEvent extends DataLoadedEvent<Song> {
    public SongsLoadedEvent(List<Song> data, boolean isLoading) {
        super(data, isLoading);
    }
}
