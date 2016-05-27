package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.app.async.AsyncExecutor;
import me.gumenniy.arkadiy.vkmusic.app.async.SupportLoader;
import me.gumenniy.arkadiy.vkmusic.app.audio.ForegroundManager;
import me.gumenniy.arkadiy.vkmusic.app.audio.Player;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.StartLoadingEvent;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public class MusicService extends Service implements Player,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, SupportLoader.OnImageLoadedListener {


    private static final int BUFFER_LOOP_MAX_COUNT = 100;
    private static final int RESET = 1;

    private final IBinder musicBind = new MusicBinder();
    private final List<Song> queue = new ArrayList<>();
    @Inject
    EventBus eventBus;
    @Inject
    SupportLoader supportLoader;

    @Nullable
    private PlayerListener playerListener;
    private AsyncExecutor playerExecutor;
    private Handler handler;
    private MediaPlayer player;
    private ForegroundManager foregroundManager;

    private int bufferLoopedCount;
    private int prevPercent;
    private int currentQueuePosition;
    private int savedPosition;

    private boolean isPrepared;
    private boolean shouldStart;
    private boolean localStoragePlayback;

    private Runnable songProgressUpdate = new Runnable() {
        @Override
        public void run() {
            notifySongProgress();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        (MusicApplication.getApp(this)).getComponent().inject(this);
        eventBus.register(this);
        preparePlayerExecutor();
        foregroundManager = new ForegroundManager(this, supportLoader);
        initMediaPlayer();
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        quitPlayerExecutor();
        releasePlayer();
        super.onDestroy();
    }

    @Subscribe
    public void onPlayQueueEvent(PlayQueueEvent event) {
        setQueue(event.queue);
        setShouldStart(true);
        setQueuePosition(event.position);
        if (playerListener != null) {
            playerListener.onQueueChanged(getQueue());
        }
        localStoragePlayback = event.localStorage;
        playCurrentSong(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        seekTo(savedPosition);
        if (isShouldStart()) {
            start();
        }
        loadAdditionalSongDataAsync(getCurrentSong());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (playerListener != null) {
            Song currentSong = getCurrentSong();
            if (currentSong != null)
                playerListener.onError(currentSong);
        }
        if (!(what == 1 && extra == -1004))
            playCurrentSong(true);
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (!isPrepared && (prevPercent == percent && percent != 100)) {
            bufferLoopedCount++;
            if (bufferLoopedCount > BUFFER_LOOP_MAX_COUNT) {
                playCurrentSong(false);
                return;
            }
        } else {
            resetBufferCount();
        }
        prevPercent = percent;

        notifySongBuffering(percent);
    }

    private void notifySongBuffering(int percent) {
        if (playerListener != null) {
            playerListener.onSongBuffering(percent, getCurrentSongPosition());
        }
    }

    private void reset(Song playSong) {
        try {
            player.reset();
            if (playSong == getCurrentSong()) {
                player.setDataSource(playSong.getUrl());
                player.prepareAsync();
            }
        } catch (Exception e) {
        }
    }

    private void notifyBeginPreparing() {
        Song currentSong = getCurrentSong();
        if (currentSong != null) {
            if (playerListener != null) {
                playerListener.onBeginPreparingSong(getCurrentQueuePosition(), currentSong, isShouldStart());
            }
            foregroundManager.updateRemoteView(currentSong, isShouldStart());
        }
    }

    private void loadAdditionalSongDataAsync(final Song song) {
        supportLoader.loadData(song, !localStoragePlayback);
    }

    @Override
    public void onImageLoaded(Song song, @Nullable Bitmap bitmap) {
        notifyImageLoaded(song, bitmap);
        Song currSong = getCurrentSong();
        if (song.equals(currSong)) {
            foregroundManager.updateRemoteView(song, isPlaying());
        }
    }

    @Override
    public void onLyricsLoaded(Song song, String lyrics) {
        notifyLyricsLoaded(song, lyrics);
    }

    private void notifyLyricsLoaded(Song song, String lyrics) {
        if (playerListener != null) {
            playerListener.onLyricsLoaded(song, lyrics);
        }
    }

    @Override
    public void onUrlLoaded(Song song, String url) {
        notifyUrlLoaded(song, url);
        Song currSong = getCurrentSong();
        if (song.equals(currSong)) {
            foregroundManager.updateRemoteView(song, isPlaying());
        }

    }

    private void notifyImageLoaded(Song song, Bitmap bitmap) {
        if (playerListener != null) {
            playerListener.onImageLoaded(song, bitmap);
        }
    }

    private void notifyUrlLoaded(Song song, String url) {
        if (playerListener != null) {
            playerListener.onUrlLoaded(song, url);
        }
    }



    private void playCurrentSong(boolean resetPosition) {
        stopUpdatingSongBuffering();
        if (isPrepared() && !resetPosition) {
            savedPosition = getCurrentSongPosition();
        } else {
            savedPosition = 0;
        }

        isPrepared = false;

        resetBufferCount();
        final Song playSong = getCurrentSong();
        if (playSong != null) {
            playerExecutor.postTask(RESET, new Runnable() {
                @Override
                public void run() {
                    reset(playSong);
                }
            }, true, true);
            notifyBeginPreparing();
        }
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

    private void preparePlayerExecutor() {
        handler = new Handler();
        playerExecutor = new AsyncExecutor("player");
        playerExecutor.start();
        playerExecutor.prepareHandler();
        supportLoader.setListener(this);
    }

    private void quitPlayerExecutor() {
        playerExecutor.quit();
        supportLoader.abandon();
    }

    private void startUpdatingSongBuffering() {
        stopUpdatingSongBuffering();
        if (localStoragePlayback) {
            songProgressUpdate.run();
        }
    }

    @Subscribe
    public void onStartLoadingEvent(StartLoadingEvent event) {
        LoadService.startActionLoad(this, event.song);
    }

    private void stopUpdatingSongBuffering() {
        handler.removeCallbacks(songProgressUpdate);
    }

    private void notifySongProgress() {
        int currentSongPosition = 0;
        if (playerListener != null && isPrepared()) {
            currentSongPosition = getCurrentSongPosition();
            playerListener.onSongBuffering(0, currentSongPosition);
        }
        int delayMillis = 1000 - (currentSongPosition % 1000);

        handler.postDelayed(songProgressUpdate, delayMillis);
    }

    @Override
    public void start() {
        setShouldStart(true);
        if (isPrepared()) {
            player.start();
            startUpdatingSongBuffering();
        }
        if (playerListener != null) {
            playerListener.onSongStarted();
        }
        Song currentSong = getCurrentSong();
        if (currentSong != null)
            foregroundManager.updateRemoteView(currentSong, true);
    }

    @Override
    public void pause() {
        setShouldStart(false);
        if (isPrepared()) {
            player.pause();
            stopUpdatingSongBuffering();
        }
        if (playerListener != null) {
            playerListener.onSongPaused();
        }
        Song currentSong = getCurrentSong();
        if (currentSong != null)
            foregroundManager.updateRemoteView(currentSong, false);
    }

    @Override
    public void next() {
        if (!isQueueEmpty()) {
            currentQueuePosition = (++currentQueuePosition) % getQueue().size();

            playCurrentSong(true);
        }
    }

    @Override
    public void prev() {
        if (!isQueueEmpty()) {
            currentQueuePosition = (--currentQueuePosition + getQueue().size()) % getQueue().size();

            playCurrentSong(true);
        }
    }

    @Override
    public void setPlayerListener(@Nullable PlayerListener listener) {
        playerListener = listener;
        if (listener == null) {
            stopUpdatingSongBuffering();
        } else if (isPlaying()) {
            startUpdatingSongBuffering();
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public boolean isPlaying() {
        return isPrepared() ? player.isPlaying() : isShouldStart();
    }

    @Override
    public void seekTo(int position) {
        if (isPrepared()) {
            player.seekTo(position);
        }
    }

    private int getCurrentSongPosition() {
        return player.getCurrentPosition();
    }

    private void resetBufferCount() {
        bufferLoopedCount = 0;
    }

    @Nullable
    @Override
    public Song getCurrentSong() {
        try {
            return queue.get(currentQueuePosition);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void playSong(int position) {
        setQueuePosition(position);
        playCurrentSong(true);
    }

    @Nullable
    @Override
    public Bitmap getImageBitmap(@NonNull Song song) {
        return supportLoader.getImageBitmap(song.getKey());
    }

    @Nullable
    @Override
    public String getImageUrl(@NonNull Song song) {
        return supportLoader.getLoadedImageUrl(song.getKey());
    }

    @Override
    public String getLyrics(Song song) {
        return supportLoader.getLoadedLyrics(song.getLyricsId());
    }

    @NonNull
    @Override
    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(@NonNull List<Song> queue) {
        this.queue.clear();
        this.queue.addAll(queue);
    }

    @Override
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public void setQueuePosition(int position) {
        this.currentQueuePosition = position;
    }

    @Override
    public int getCurrentQueuePosition() {
        return currentQueuePosition;
    }

    @Override
    public boolean isShouldStart() {
        return shouldStart;
    }

    public void setShouldStart(boolean shouldStart) {
        this.shouldStart = shouldStart;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return super.onStartCommand(null, flags, startId);
        }
        Log.e("action", intent.getAction());
        switch (intent.getAction()) {
            case Settings.Notification.ACTION.PREV_ACTION:
                prev();
                break;
            case Settings.Notification.ACTION.NEXT_ACTION:
                next();
                break;
            case Settings.Notification.ACTION.PAUSE_PLAY_ACTION:
                if (isPlaying()) {
                    pause();
                } else {
                    start();
                }
                break;
            case Settings.Notification.ACTION.STOP_SERVICE_ACTION:
                stopSelf();
                break;
            case Settings.Notification.ACTION.BEGIN_FOREGROUND_ACTION:
                Song currentSong = getCurrentSong();
                if (currentSong != null) {
                    foregroundManager.beginForeground(currentSong, isPlaying());
                }
                break;
            case Settings.Notification.ACTION.END_FOREGROUND_ACTION:
                foregroundManager.endForeground();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
