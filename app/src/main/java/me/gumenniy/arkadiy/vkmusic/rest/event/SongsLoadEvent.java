package me.gumenniy.arkadiy.vkmusic.rest.event;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class SongsLoadEvent {
    public boolean refresh;

    public SongsLoadEvent(boolean refresh) {
        this.refresh = refresh;
    }
}
