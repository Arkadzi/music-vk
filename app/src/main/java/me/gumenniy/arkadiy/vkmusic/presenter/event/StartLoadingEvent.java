package me.gumenniy.arkadiy.vkmusic.presenter.event;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 28.04.2016.
 */
public class StartLoadingEvent {
    public final Song song;

    public StartLoadingEvent(Song song) {
        this.song = song;
    }
}
