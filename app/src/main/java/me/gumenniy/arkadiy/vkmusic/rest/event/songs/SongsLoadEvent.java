package me.gumenniy.arkadiy.vkmusic.rest.event.songs;

import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadEvent;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class SongsLoadEvent extends DataLoadEvent {
    public SongsLoadEvent(boolean refresh) {
        super(refresh);
    }
}
