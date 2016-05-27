package me.gumenniy.arkadiy.vkmusic.app.db;

import me.gumenniy.arkadiy.vkmusic.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public interface StorageFactory {
    Storage<Lyrics> getLyricsStorage();
    Storage<Song> getSongStorage();
}
