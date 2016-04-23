package me.gumenniy.arkadiy.vkmusic.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

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

import retrofit.http.Url;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public class SongCache implements LocalCache<Song> {
    private final String connectionName = "connections.txt";
    private final String folderName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
    private final Context context;
    private CacheLoader loader;

    public SongCache(Context context) {
        this.context = context;
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
            final List<Song> result = new ArrayList<>();

            final Map<String, String[]> connections = getConnections();
            final Set<String> keySet = connections.keySet();

            File folder = new File(folderName);
            folder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String fileName = pathname.getName();
                    if (!pathname.isDirectory() && keySet.contains(fileName)) {
                        String title = connections.get(fileName)[0];
                        String artist = connections.get(fileName)[1];
                        int duration = 0;
                        try {
                            duration  = Integer.parseInt(connections.get(fileName)[2]);
                        } catch (Exception e) {}
                        String url = Uri.fromFile(pathname).toString();
                        result.add(new Song(Song.LOCAL_ID, title, artist, url, duration, Song.LOCAL_ID));
                    }
                    return false;
                }
            });

            return result;
        }

        private Map<String, String[]> getConnections() {
            Map<String, String[]> connections = new HashMap<>();
            File connectionFile = new File(String.format("%s%s%s", folderName, File.separator, connectionName));
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(connectionFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] pair = line.split("\\|");
                    if (pair.length == 4) {
                        connections.put(pair[0], Arrays.copyOfRange(pair, 1, pair.length));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return connections;
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
