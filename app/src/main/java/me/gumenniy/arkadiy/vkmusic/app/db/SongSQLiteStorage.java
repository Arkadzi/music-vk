package me.gumenniy.arkadiy.vkmusic.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public class SongSQLiteStorage extends SQLiteStorage<Song> {

    public SongSQLiteStorage(DbHelper helper) {
        super(helper);
    }

    @NonNull
    @Override
    protected ContentValues getContentValues(Song item) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.SONG_ID, item.getId());
        values.put(DbHelper.TITLE, item.getTitle());
        values.put(DbHelper.ARTIST, item.getArtist());
        values.put(DbHelper.LYRICS_ID, item.getLyricsId());
        values.put(DbHelper.DURATION, item.getDuration());
        values.put(DbHelper.PATH, item.getUrl());

        return values;
    }

    @Override
    protected Song construct(Cursor cursor) {
        String songId = cursor.getString(0);
        String title = cursor.getString(1);
        String artist = cursor.getString(2);
        String lyricsId = cursor.getString(3);
        String path = cursor.getString(4);
        int duration = cursor.getInt(5);

        return new Song(songId, title, artist, path, duration, lyricsId, "");
    }

    @NonNull
    @Override
    protected String getTableName() {
        return DbHelper.TABLE_SONG;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[]{
                DbHelper.SONG_ID,
                DbHelper.TITLE,
                DbHelper.ARTIST,
                DbHelper.LYRICS_ID,
                DbHelper.PATH,
                DbHelper.DURATION
        };
    }

}
