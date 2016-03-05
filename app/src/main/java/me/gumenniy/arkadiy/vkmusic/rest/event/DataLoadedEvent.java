package me.gumenniy.arkadiy.vkmusic.rest.event;

import java.util.List;

/**
 * Created by Arkadiy on 20.02.2016.
 */
public class DataLoadedEvent<T> {
    public final List<T> data;
    public final boolean isLoading;

    public DataLoadedEvent(List<T> data, boolean isLoading) {
        this.data = data;
        this.isLoading = isLoading;
    }
}
