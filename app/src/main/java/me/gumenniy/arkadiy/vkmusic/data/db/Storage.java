package me.gumenniy.arkadiy.vkmusic.data.db;

import java.util.List;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public interface Storage<T> {
    List<T> fetch();
    void save(T item);
}
