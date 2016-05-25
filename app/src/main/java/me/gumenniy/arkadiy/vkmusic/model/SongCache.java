package me.gumenniy.arkadiy.vkmusic.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.db.DbHelper;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import retrofit.http.Url;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public class SongCache implements LocalCache<Song> {
    private final DbHelper helper;
    private CacheLoader loader;

    public SongCache(DbHelper helper) {
        this.helper = helper;
    }

    @Override
    public void loadCache(OnDataLoadedListener<Song> listener) {
        loader = new CacheLoader(listener);
        loader.execute();
    }

    class CacheLoader extends AsyncTask<Void, Void, List<Song>> {

        private OnDataLoadedListener<Song> listener;

        public CacheLoader(OnDataLoadedListener<Song> listener) {
            this.listener = listener;
        }

        @Override
        protected List<Song> doInBackground(final Void... params) {
            return helper.fetchSongs();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            if (listener != null) {
                listener.onDataLoaded(songs);
                listener = null;
            }
        }
    }
}
