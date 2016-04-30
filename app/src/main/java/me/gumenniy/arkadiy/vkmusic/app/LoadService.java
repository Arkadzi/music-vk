package me.gumenniy.arkadiy.vkmusic.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.SongLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

public class LoadService extends IntentService {
    private static final String ACTION_LOAD = "me.gumenniy.arkadiy.vkmusic.app.action.LOAD";
    private static final String EXTRA_URL = "me.gumenniy.arkadiy.vkmusic.app.extra.URL";
    private static final String EXTRA_TITLE = "me.gumenniy.arkadiy.vkmusic.app.extra.TITLE";
    private static final String EXTRA_ARTIST = "me.gumenniy.arkadiy.vkmusic.app.extra.ARTIST";
    private static final String EXTRA_DURATION = "me.gumenniy.arkadiy.vkmusic.app.extra.DURATION";
    private final Handler handler = new Handler();
    private NotificationManager mNotifyManager;
    @Inject
    EventBus eventBus;

    public LoadService() {
        super("LoadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicApplication.getApp(this).getComponent().inject(this);
    }

    public static void startActionLoad(Context context, Song song) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD);
        intent.putExtra(EXTRA_URL, song.getUrl());
        intent.putExtra(EXTRA_TITLE, song.getTitle());
        intent.putExtra(EXTRA_ARTIST, song.getArtist());
        intent.putExtra(EXTRA_DURATION, song.getDuration());
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                final String title = intent.getStringExtra(EXTRA_TITLE);
                final String artist = intent.getStringExtra(EXTRA_ARTIST);
                final int duration = intent.getIntExtra(EXTRA_DURATION, 0);

                Song song = new Song("", title, artist, url, duration, "");
                handleActionLoad(song);
            }
        }
    }

    private void handleActionLoad(Song song) {

        File folder = new File(Settings.CACHE_DIRECTORY);
        folder.mkdir();

        URL url;
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        HttpURLConnection urlConnection = null;
        NotificationCompat.Builder builder = createBuilder(song);
        try {
            url = new URL(song.getUrl());
            showMessage(builder, R.string.downloadPreparing);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String fileName = String.valueOf(System.currentTimeMillis());
                File file = new File(folder + File.separator + fileName);
                Log.e("Async", String.valueOf(file));
                int fileSize = urlConnection.getContentLength();
                is = new BufferedInputStream(urlConnection.getInputStream());
                os = new BufferedOutputStream(new FileOutputStream(file));
                addRecord(fileName, song);
                byte[] buffer = new byte[1024];
                int byteRead;
                int totalRead = 0;
                int percent = -1;
                while ((byteRead = is.read(buffer)) > 0) {
                    os.write(buffer, 0, byteRead);
                    totalRead += byteRead;
                    percent = updateProgress(builder, fileSize, totalRead, percent);
                }
                Log.e("Async", "loaded ");
                showMessage(builder, R.string.downloadComplete);
                is.close();
                os.close();

            }
        } catch (MalformedURLException me) {
            showToast(getString(R.string.unableToLoad) + " " + song.getTitle());
            Log.e("Async", "exception " + String.valueOf(me));
        } catch (IOException e) {
            showMessage(builder, R.string.errorOccured);
            Log.e("Async", "exception " + String.valueOf(e));
        } finally {
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
            } catch (IOException ignored) {
            }

            if (urlConnection != null)
                urlConnection.disconnect();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    eventBus.post(new SongLoadedEvent());
                }
            });
        }
    }

    private void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoadService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private int updateProgress(NotificationCompat.Builder builder, int max, int current, int prevPercent) {
        int percent = (int) (((float) current / max) * 100);
        if (percent != prevPercent) {
            builder.setContentText(getString(R.string.downloadInProgress));
            builder.setProgress(100, percent, false);
            mNotifyManager.notify(Settings.Notification.LOAD_SERVICE, builder.build());
        }
        return percent;
    }

    private void showMessage(NotificationCompat.Builder builder, int message) {
        builder.setProgress(0, 0, false);
        builder.setContentText(getString(message));
        mNotifyManager.notify(Settings.Notification.LOAD_SERVICE, builder.build());
    }

    private void addRecord(String file, Song song) throws IOException {
        File connectionFile = new File(Settings.CONNECTIONS_FILE);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(connectionFile, true));
            String string = getAppendedString(file, song);
            writer.write(string);
            writer.newLine();
            writer.flush();
            Log.e("Async", "connection done " + connectionFile);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private String getAppendedString(String file, Song song) {
        return file +
                "|" +
                song.getTitle() +
                "|" +
                song.getArtist() +
                "|" +
                String.valueOf(song.getDuration());
    }

    private NotificationCompat.Builder createBuilder(Song song) {
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return new NotificationCompat.Builder(this)
                .setContentTitle(String.format("%s - %s", song.getTitle(), song.getArtist()))
//                .setContentText(getString(R.string.downloadInProgress))
                .setSmallIcon(R.drawable.ic_save_white_36dp);
    }
}
