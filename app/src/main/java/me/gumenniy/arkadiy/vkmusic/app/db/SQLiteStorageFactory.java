package me.gumenniy.arkadiy.vkmusic.app.db;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.model.Song;

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
