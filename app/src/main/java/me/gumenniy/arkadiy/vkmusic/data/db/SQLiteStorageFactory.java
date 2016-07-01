package me.gumenniy.arkadiy.vkmusic.data.db;

import me.gumenniy.arkadiy.vkmusic.domain.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.domain.model.Song;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public class SQLiteStorageFactory implements StorageFactory {

    private final DbHelper helper;

    public SQLiteStorageFactory(DbHelper helper) {
        this.helper = helper;
    }

    @Override
    public SQLiteStorage<Lyrics> getLyricsStorage() {
        return new LyricsSQLiteStorage(helper);
    }

    @Override
    public SQLiteStorage<Song> getSongStorage() {
        return new SongSQLiteStorage(helper);
    }
}
