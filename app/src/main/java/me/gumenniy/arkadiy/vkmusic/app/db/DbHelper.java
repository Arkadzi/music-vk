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

    public static final String TABLE_SONG = "tableSong";
    public static final String SONG_ID = "song_id";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String TABLE_LYRICS = "tableLyrics";
    private static final String DB_NAME = "VkDatabase";
    private static final String KEY_ID = "_id";

    public static final String LYRICS_ID = "lyrics_id";
    public static final String LYRICS = "lyrics";


    public DbHelper(Context context) {
        super(context, context.getExternalCacheDir() + File.separator + DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("db", "onCreate()");
        db.execSQL("CREATE TABLE "
                + TABLE_SONG + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LYRICS);
            onCreate(db);
        }
    }

//
}
