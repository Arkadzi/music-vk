package me.gumenniy.arkadiy.vkmusic.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import me.gumenniy.arkadiy.vkmusic.domain.model.Lyrics;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public class LyricsSQLiteStorage extends SQLiteStorage<Lyrics> {

    public LyricsSQLiteStorage(DbHelper helper) {
        super(helper);
    }

    @NonNull
    @Override
    protected ContentValues getContentValues(Lyrics item) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.LYRICS_ID, item.getLyricsId());
        values.put(DbHelper.LYRICS, item.getText());

        return values;
    }

    @Override
    protected Lyrics construct(Cursor cursor) {
        String lyricsId = cursor.getString(0);
        String text = cursor.getString(1);

        return new Lyrics(lyricsId, text);
    }

    @NonNull
    @Override
    protected String getTableName() {
        return DbHelper.TABLE_LYRICS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[]{
                DbHelper.LYRICS_ID,
                DbHelper.LYRICS
        };
    }


}
