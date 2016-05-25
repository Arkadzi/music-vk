package me.gumenniy.arkadiy.vkmusic.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 27.04.2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DB_NAME = "VkDatabase";
    private static final String TABLE_SONGS = "tableSong";
    private static final String KEY_ID = "_id";
    private static final String SONG_ID = "song_id";
    private static final String TITLE = "title";
    private static final String ARTIST = "artist";
    private static final String DURATION = "duration";
    private static final String PATH = "path";

    private static final String TABLE_LYRICS = "tableLyrics";
    private static final String LYRICS_ID = "lyrics_id";
    private static final String LYRICS = "lyrics";

    private static DbHelper helper;

    public DbHelper(Context context) {
        super(context, context.getExternalCacheDir() + File.separator + DB_NAME, null, DATABASE_VERSION);
    }

    public static DbHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DbHelper(context.getApplicationContext());
        }

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("db", "onCreate()");
        db.execSQL("CREATE TABLE "
                + TABLE_SONGS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SONG_ID + " TEXT, "
                + TITLE + " TEXT, "
                + ARTIST + " TEXT, "
                + LYRICS_ID + " TEXT, "
                + PATH + " TEXT, "
                + DURATION + " INTEGER)");
        db.execSQL("CREATE TABLE "
                + TABLE_LYRICS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LYRICS_ID + " TEXT, "
                + LYRICS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DATABASE_VERSION < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LYRICS);
            onCreate(db);
        }
    }

    public List<Lyrics> fetchLyrics() {
        List<Lyrics> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {LYRICS_ID, LYRICS};
        Cursor cursor = db.query(TABLE_LYRICS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String lyricsId = cursor.getString(0);
                String text = cursor.getString(1);
                Lyrics lyrics = new Lyrics(lyricsId, text);
                result.add(lyrics);
            }
            cursor.close();
        }

        return result;
    }

    public List<Song> fetchSongs() {
        List<Song> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {SONG_ID, TITLE, ARTIST, LYRICS_ID, PATH, DURATION};
        Cursor cursor = db.query(TABLE_SONGS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String songId = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String lyricsId = cursor.getString(3);
                String path = cursor.getString(4);
                int duration = cursor.getInt(5);
                Song song = new Song(songId, title, artist, path, duration, lyricsId, "");
                Log.e("songs", title + " " + path);
                result.add(song);
            }
            cursor.close();
        }

        return result;
    }

    public void saveSong(String path, Song song) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SONG_ID, song.getId());
        values.put(TITLE, song.getTitle());
        values.put(ARTIST, song.getArtist());
        values.put(LYRICS_ID, song.getLyricsId());
        values.put(DURATION, song.getDuration());
        values.put(PATH, path);

        db.insert(TABLE_SONGS, null, values);
    }

    public void saveLyrics(Lyrics lyrics) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LYRICS_ID, lyrics.getLyricsId());
        values.put(LYRICS, lyrics.getText());

        db.insert(TABLE_LYRICS, null, values);
    }
}
