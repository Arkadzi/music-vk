package me.gumenniy.arkadiy.vkmusic.rest.event;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class DataLoadEvent {
    public boolean refresh;

    public DataLoadEvent(boolean refresh) {
        this.refresh = refresh;
    }
}
