package me.gumenniy.arkadiy.vkmusic.data.db;

import me.gumenniy.arkadiy.vkmusic.domain.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.domain.model.Song;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public interface StorageFactory {
    Storage<Lyrics> getLyricsStorage();
    Storage<Song> getSongStorage();
}
