package me.gumenniy.arkadiy.vkmusic.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiy on 27.05.2016.
 */
public abstract class SQLiteStorage<T> implements Storage<T> {
    private DbHelper helper;

    public SQLiteStorage(DbHelper helper) {
        this.helper = helper;
    }

    public DbHelper getHelper() {
        return helper;
    }

    @Override
    public List<T> fetch() {
        List<T> result = new ArrayList<>();

        DbHelper helper = getHelper();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = getProjection();
        Cursor cursor = db.query(getTableName(), columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(construct(cursor));
            }
            cursor.close();
        }

        return result;
    }

    @Override
    public void save(T item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = getContentValues(item);

        db.insert(getTableName(), null, values);
    }

    @NonNull
    protected abstract ContentValues getContentValues(T item);

    protected abstract T construct(Cursor cursor);

    @NonNull
    protected abstract String getTableName();

    @NonNull
    protected abstract String[] getProjection();
}
