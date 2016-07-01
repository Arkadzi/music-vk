package me.gumenniy.arkadiy.vkmusic.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.async.SupportLoader;
import me.gumenniy.arkadiy.vkmusic.data.db.StorageFactory;
import me.gumenniy.arkadiy.vkmusic.domain.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.domain.model.Song;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.event.SongLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.data.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.data.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;
import retrofit.Call;
import retrofit.Response;

public class LoadService extends IntentService {
    private static final String ACTION_LOAD = "me.gumenniy.arkadiy.vkmusic.app.action.LOAD";
    private static final String EXTRA_SONG = "me.gumenniy.arkadiy.vkmusic.app.extra.SONG";

    private final Handler handler = new Handler();
    @Inject
    EventBus eventBus;
    @Inject
    StorageFactory storageFactory;
    @Inject
    UserSession userSession;
    @Inject
    VkApi vkApi;
    @Inject
    SupportLoader supportLoader;

    private NotificationManager mNotifyManager;

    public LoadService() {
        super("LoadService");
    }

    public static void startActionLoad(Context context, Song song) {
        Intent intent = new Intent(context, LoadService.class);
        intent.setAction(ACTION_LOAD);
        intent.putExtra(EXTRA_SONG, song);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicApplication.getApp(this).getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                Song song = (Song) intent.getSerializableExtra(EXTRA_SONG);
                handleActionLoad(song);
            }
        }
    }

    private void handleActionLoad(Song song) {

        loadSong(song);
        handler.post(new Runnable() {
            @Override
            public void run() {
                eventBus.post(new SongLoadedEvent());
            }
        });
    }

    private void loadSong(Song song) {
        File folder = new File(Settings.CACHE_DIRECTORY);
        folder.mkdir();

        int totalRead = 0;
        URL url;
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        HttpURLConnection urlConnection = null;
        NotificationCompat.Builder builder = createBuilder(song);
        boolean isLoading = true;
        File file = null;
        String fileName = String.valueOf(System.currentTimeMillis());
        while (isLoading) {
            try {
                url = new URL(song.getUrl());
                showMessage(builder, R.string.downloadPreparing);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    int fileSize = urlConnection.getContentLength();
                    is = new BufferedInputStream(urlConnection.getInputStream());
                    if (file == null) {
                        file = new File(folder + File.separator + fileName);
                        addRecord(file.toString(), song);
                    }
                    os = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    int percent = -1;
                    while ((byteRead = is.read(buffer)) > 0) {
                        os.write(buffer, 0, byteRead);
                        totalRead += byteRead;
                        percent = updateProgress(builder, fileSize, totalRead, percent);
                    }
                    loadLyrics(song);
                    showMessage(builder, R.string.downloadComplete);
                    is.close();
                    os.close();

                }
                isLoading = false;
            } catch (MalformedURLException me) {
                showToast(getString(R.string.unableToLoad) + " " + song.getTitle());
                isLoading = false;
            } catch (SocketException se) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                showMessage(builder, R.string.errorOccured);
                isLoading = false;
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
            }
        }
    }

    private void loadLyrics(final Song song) {
        if (song.hasLyrics()) {
            try {
                Call<VKResult<Lyrics>> lyricsCall = vkApi.getLyrics(song.getLyricsId(), userSession.getToken());
                Response<VKResult<Lyrics>> lyricsResponse = lyricsCall.execute();
                VKResult<Lyrics> lyricsResult = lyricsResponse.body();

                if (lyricsResponse.isSuccess() && lyricsResult.isSuccessful()) {
                    final Lyrics lyrics = lyricsResult.getResponse();
                    storageFactory.getLyricsStorage().save(lyrics);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("lyrics", lyrics.getText());
                            supportLoader.addLyrics(song, lyrics.getText());
                        }
                    });
                }
            } catch (IOException e) {
            }
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
        Song localSong = new Song(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                file, song.getDuration(),
                song.getLyricsId(),
                song.getOwnerId());

        storageFactory.getSongStorage().save(localSong);
    }

    private NotificationCompat.Builder createBuilder(Song song) {
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return new NotificationCompat.Builder(this)
                .setContentTitle(String.format("%s - %s", song.getTitle(), song.getArtist()))
                .setSmallIcon(R.drawable.ic_save_white_36dp);
    }
}
