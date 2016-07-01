package me.gumenniy.arkadiy.vkmusic.domain.model;

import android.os.AsyncTask;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.data.db.StorageFactory;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public class SongCache implements LocalCache<Song> {
    private final StorageFactory storageFactory;
    private CacheLoader loader;

    public SongCache(StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
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
            return storageFactory.getSongStorage().fetch();
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
