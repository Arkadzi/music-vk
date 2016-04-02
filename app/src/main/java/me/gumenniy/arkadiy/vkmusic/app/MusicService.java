package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public class MusicService extends Service implements Player,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {


    private static final int BUFFER_LOOP_MAX_COUNT = 35;
    private final IBinder musicBind = new MusicBinder();
    @Inject
    EventBus eventBus;
    boolean firstTime = true;
    private int bufferLoopedCount;
    private int prevPercent;
    private MediaPlayer player;
    @NotNull
    private List<Song> queue;
    private int currentPosition;
    private boolean isPrepared;
    @Nullable
    private PlayerListener playerListener;
    private boolean shouldStart;
    private int savedPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        (MusicApplication.getApp(this)).getComponent().inject(this);
        eventBus.register(this);
        setQueue(new ArrayList<Song>());
        initMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        releasePlayer();
    }

    private void initMediaPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
        player.reset();
    }

    private void releasePlayer() {
        player.reset();
        player.release();
        player = null;
    }

    @Subscribe
    public void onPlayQueueEvent(PlayQueueEvent event) {
        setShouldStart(true);
        setQueue(event.queue);
        setPosition(event.position);
        if (playerListener != null) {
            playerListener.onQueueChanged(getQueue(), getCurrentPosition());
        }

//        resetPlayer(true);
//        new AsyncTask<Song, Void, Void>() {
//            @Override
//            protected Void doInBackground(Song... params) {
//                try {
//                    String file = String.format("%s/%s.mp3",
//                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
//                            params[0].getTitle());
//                    URL url = new URL(params[0].getUrl());
//                    URLConnection urlConnection = url.openConnection();
//                    urlConnection.connect();
//                    int fileSize = urlConnection.getContentLength();
//                    BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream());
//                    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//                    byte[] buffer = new byte[1024];
//                    int byteRead;
//                    int totalRead = 0;
//                    while ((byteRead = is.read(buffer)) > 0) {
//                        os.write(buffer, 0, byteRead);
//                        totalRead += byteRead;
//                        Log.e("Async", totalRead + " of " + fileSize + " " + ((100 * totalRead) / fileSize) + " %%");
//                    }
//                    is.close();
//                    os.close();
//
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file))));
//
//
//                } catch (Exception e) {
//                    Log.e("Async", "exception " + e.toString());
//                }
//                Log.e("Async", "over");
//                return null;
//            }
//        }.execute(getCurrentSong());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        seekTo(savedPosition);
        if (isShouldStart()) {
            start();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (playerListener != null) {
            playerListener.onError(getCurrentSong());
        }
        return false;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void start() {
        if (isPrepared()) {
            setShouldStart(true);

            player.start();
            if (playerListener != null) {
                playerListener.onSongStarted();
            }
        }
    }

    @Override
    public void pause() {
        if (isPrepared()) {
            setShouldStart(false);

            player.pause();
            if (playerListener != null) {
                playerListener.onSongPaused();
            }
        }
    }

    @Override
    public void next() {
        if (!isQueueEmpty()) {
            currentPosition = (++currentPosition) % getQueue().size();

            resetPlayer(true);
        }
    }

    @Override
    public void prev() {
        if (!isQueueEmpty()) {
            currentPosition = (--currentPosition + getQueue().size()) % getQueue().size();

            resetPlayer(true);
        }
    }

    @Override
    public void setPlayerListener(@Nullable PlayerListener listener) {
        playerListener = listener;
    }

    @Override
    public void seekTo(int position) {
        if (isPrepared()) {
            player.seekTo(position);
        }
    }

    public void setPosition(int position) {
        this.currentPosition = position;
    }

    @Override
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    @Override
    @Nullable
    public Song getCurrentSong() {
        try {
            return queue.get(currentPosition);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("MusicService", String.format("percent = %d isPrepared = %b isPlaying = %b count = %d", percent, isPrepared(), (isPrepared() && isPlaying()), bufferLoopedCount));
        if ((prevPercent == percent && percent != 100)) {
            bufferLoopedCount++;
            if (bufferLoopedCount > BUFFER_LOOP_MAX_COUNT) {
                resetPlayer(false);
                return;
            }
        } else {
            resetBufferCount();
        }
        prevPercent = percent;

        if (playerListener != null) {
            playerListener.onSongBuffering(percent, player.getCurrentPosition());
        }
    }

    public void playSong() {
        Log.e("debug", "playSong()");
        isPrepared = false;
        resetBufferCount();
        final Song playSong = getCurrentSong();
        if (playSong != null) {
            try {
                player.setDataSource(playSong.getUrl());
                player.prepareAsync();
            } catch (Exception e) {
                Log.e("MusicService", String.valueOf(e));
            }
            if (playerListener != null) {
                playerListener.onBeginPreparingSong(getCurrentSong());
            }
        }
        Log.e("debug", "playSong() end");
    }

    private void resetPlayer(boolean resetPosition) {
        Log.e("debug", "resetPlayer()");
        if (isPrepared() && !resetPosition) {
            savedPosition = getCurrentPosition();
        } else {
            savedPosition = 0;
        }
//        if (firstTime || isPrepared()) {
        player.reset();
//            firstTime = false;
//        } else {
//            actionCancel();
//        }
        playSong();
        Log.e("debug", "resetPlayer() end");
    }

    private void actionCancel() {
        Log.e("debug", "actionCancel()");
        try {
            player.setDataSource("");
//            player.stop();
        } catch (Exception e) {
            Log.e("debug", "actionCancel(): mp.stop() exception");
            player.reset();
        }
        Log.e("debug", "actionCancel() end");

    }

    private void resetBufferCount() {
        bufferLoopedCount = 0;
    }

    @NotNull
    @Override
    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(@NotNull List<Song> queue) {
        this.queue = queue;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public boolean isShouldStart() {
        return shouldStart;
    }

    public void setShouldStart(boolean shouldStart) {
        this.shouldStart = shouldStart;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
