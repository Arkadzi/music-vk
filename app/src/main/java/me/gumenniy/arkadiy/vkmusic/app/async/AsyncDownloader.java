package me.gumenniy.arkadiy.vkmusic.app.async;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import retrofit.http.Url;

/**
 * Created by Arkadiy on 10.04.2016.
 */
public class AsyncDownloader extends AsyncTask<Void, Void, Void> {
    private String url;

    public AsyncDownloader(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + Settings.CACHE_DIRECTORY);
            folder.mkdir();
            String file = String.format("%s/%d",
                    folder,
                    System.currentTimeMillis());
            Log.e("Async", file);
            URL url = new URL(this.url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int fileSize = urlConnection.getContentLength();
            BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[1024];
            int byteRead;
            int totalRead = 0;
            while ((byteRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, byteRead);
                totalRead += byteRead;
                Log.e("Async", totalRead + " of " + fileSize + " " + ((100 * totalRead) / fileSize) + " %%");
            }
            is.close();
            os.close();

        } catch (Exception e) {
            Log.e("Async", "exception " + e.toString());
        }
        Log.e("Async", "done");
        return null;
    }


}
