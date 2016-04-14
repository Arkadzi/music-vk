package me.gumenniy.arkadiy.vkmusic.app;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Connection;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.app.async.PlayerExecutor;
import me.gumenniy.arkadiy.vkmusic.app.audio.ForegroundManager;
import me.gumenniy.arkadiy.vkmusic.app.audio.Player;
import me.gumenniy.arkadiy.vkmusic.model.Artwork;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.event.PlayQueueEvent;
import me.gumenniy.arkadiy.vkmusic.rest.LastFMApi;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public class MusicService extends Service implements Player,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {


    public static final String NONE = "none";
    private static final int BUFFER_LOOP_MAX_COUNT = 100;
    private static final int RESET = 1;
    private static final int LOAD = 2;
    private final IBinder musicBind = new MusicBinder();
    @Inject
    EventBus eventBus;
    @Inject
    LastFMApi artworkApi;

    //error calculating
    private int bufferLoopedCount;
    private int prevPercent;

    //music execution
    private MediaPlayer player;
    @NonNull
    private List<Song> queue;
    private Map<String, String> images;
    private int currentQueuePosition;
    @Nullable
    private PlayerListener playerListener;
    private boolean isPrepared;
    private boolean shouldStart;
    private int savedPosition;
    private PlayerExecutor playerExecutor;
    private Handler handler;
    private ForegroundManager foregroundManager;

    @Override
    public void onCreate() {
        super.onCreate();
        (MusicApplication.getApp(this)).getComponent().inject(this);
        eventBus.register(this);
        preparePlayerExecutor();
        setQueue(new ArrayList<Song>());
        images = new HashMap<>();
        foregroundManager = new ForegroundManager(this, images);
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
//        new AsyncDownloader(getCurrentSong().getUrl()).execute();

        resetPlayer(true);
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
        Log.e("player", "complete ");
        next();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("player", "error " + what + " " + extra);
        if (playerListener != null) {
            Song currentSong = getCurrentSong();
            if (currentSong != null)
                playerListener.onError(currentSong);
        }
        if (!(what == 1 && extra == -1004))
            resetPlayer(true);
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("play", String.format("percent = %d isPrepared = %b isPlaying = %b count = %d", percent, isPrepared(), (isPrepared() && isPlaying()), bufferLoopedCount));
        if (!isPrepared() && (prevPercent == percent && percent != 100)) {
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
        isPrepared = false;

        resetBufferCount();
        final Song playSong = getCurrentSong();
        if (playSong != null) {
            Log.e("play", "_____BEGIN_____" + playSong.getTitle());
            playerExecutor.postTask(RESET, new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("play", "_____RESET_____" + playSong.getTitle());
                        player.reset();
                        player.setDataSource(playSong.getUrl());
                        player.prepareAsync();
                        Log.e("play", "_____PREPARING_____" + playSong.getTitle());
                    } catch (Exception e) {
                        Log.e("play", "exception " + String.valueOf(e));
                    }
                }
            }, true, true);
            notifyBeginPreparing();
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

    @Nullable
    @Override
    public String loadImageUrl(@NonNull Song song) {
        String url = getImageUrl(song);
        Log.e("handler", song.getTitle() + "_________________" + url);
        if (url == null) {
            loadImageUrlAsync(song);
            return null;
        } else if (!url.equals(NONE)) {
            return url;
        }
        return null;
    }

    private void loadImageUrlAsync(final Song song) {
        playerExecutor.postTask(LOAD, new Runnable() {
            String url;

            @Override
            public void run() {
                try {
                    Log.e("handler", "_________BEGIN " + song.getTitle());
                    Call<Artwork> artworkCall = artworkApi.getArtwork2(Settings.LAST_FM_API_KEY, song.getArtist(), song.getTitle());
                    Response<Artwork> artworkResponse = artworkCall.execute();
                    Log.e("handler", "_________END " + song.getTitle());
                    if (artworkResponse.isSuccess()) {
                        Artwork artwork = artworkResponse.body();
                        url = artwork.getUri();
                    }
                } catch (NullPointerException npe) {
                    url = "";
                } catch (Exception e) {
                    Log.e("exception", song.getTitle() + " " + String.valueOf(e));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("handler", song.getTitle() + "_____LOADED " + url);
                        if (url != null) {
                            if (url.isEmpty()) {
                                putImageUri(song, NONE);
                            } else {
                                putImageUri(song, url);
                                notifyImageLoaded(song, url);
                            }
                        }
                    }
                });
            }
        }, false, false);
    }

    private void notifyImageLoaded(Song song, String url) {
        if (playerListener != null) {
            playerListener.onImageLoaded(song, url);
        }
    }

    private void resetPlayer(boolean resetPosition) {
        Log.e("debug", "resetPlayer()");
        if (isPrepared() && !resetPosition) {
            savedPosition = getCurrentSongPosition();
        } else {
            savedPosition = 0;
        }
        playSong();
        Log.e("debug", "resetPlayer() end");
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
        playerExecutor = new PlayerExecutor();
        playerExecutor.start();
        playerExecutor.prepareHandler();
    }

    private void quitPlayerExecutor() {
        playerExecutor.quit();
    }

    @Override
    public void start() {
        setShouldStart(true);
        if (isPrepared()) {
            player.start();
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

            resetPlayer(true);
        }
    }

    @Override
    public void prev() {
        if (!isQueueEmpty()) {
            currentQueuePosition = (--currentQueuePosition + getQueue().size()) % getQueue().size();

            resetPlayer(true);
        }
    }

    @Override
    public void setPlayerListener(@Nullable PlayerListener listener) {
        playerListener = listener;
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
        playSong();
    }

    @NonNull
    @Override
    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(@NonNull List<Song> queue) {
        this.queue = queue;
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

    @Nullable
    private String getImageUrl(Song song) {
        return images.get(song.getKey());
    }

    private void putImageUri(@NonNull Song song, @NonNull String url) {
        images.put(song.getKey(), url);
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
