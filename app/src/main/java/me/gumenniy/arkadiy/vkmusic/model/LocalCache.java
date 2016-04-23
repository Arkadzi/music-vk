package me.gumenniy.arkadiy.vkmusic.model;

import java.util.List;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public interface LocalCache<T> {

    void loadCache(OnDataLoadedListener<Song> listener);

    interface OnDataLoadedListener<T> {
        void onDataLoaded(List<T> data);
    }
}
