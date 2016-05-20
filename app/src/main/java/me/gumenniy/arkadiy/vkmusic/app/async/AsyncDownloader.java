package me.gumenniy.arkadiy.vkmusic.app.async;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import me.gumenniy.arkadiy.vkmusic.utils.Settings;

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
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + Settings.CACHE_DIRECTORY);
        folder.mkdir();
        String file = String.format("%s/%d",
                folder,
                System.currentTimeMillis());
        Log.e("Async", file);
        URL url = null;
        try {
            url = new URL(this.url);
        } catch (MalformedURLException e) {
        }
        load(file, url);
        Log.e("Async", "done");
        return null;
    }

    private void load(String file, URL url) {
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        int totalRead = 0;
        boolean isLoading = true;
        while (isLoading) {
            Log.e("Async", "start " + totalRead);
            try {
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int fileSize = urlConnection.getContentLength();
                is = new BufferedInputStream(urlConnection.getInputStream());
                os = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[1024];
                int byteRead;
                is.skip(totalRead);
                while ((byteRead = is.read(buffer)) > 0) {
                    os.write(buffer, 0, byteRead);
                    totalRead += byteRead;
                    Log.e("Async", totalRead + " of " + fileSize + " " + ((100 * totalRead) / fileSize) + " %%");
                }
                isLoading = false;
            } catch (SocketException e) {
                Log.e("Async", "exception " + e.toString());
                Log.e("Async", "exception " + e.toString());
            } catch (IOException e) {
                isLoading = false;
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
