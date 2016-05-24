package me.gumenniy.arkadiy.vkmusic.app.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import me.gumenniy.arkadiy.vkmusic.model.Artwork;
import me.gumenniy.arkadiy.vkmusic.model.Lyrics;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.LastFMApi;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by Arkadiy on 25.04.2016.
 */
public class SupportLoader {
    public static final String NONE = "NONE";
    private static final int LOAD = 1010;
    private final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private final HashMap<String, String> urls = new HashMap<>();
    private final HashMap<String, String> lyricsList = new HashMap<>();
    private final Set<String> imageless = new HashSet<>();
    private final Handler handler = new Handler();
    private final int size;
    private final UserSession userSession;

    private LastFMApi lastFMApi;
    private VkApi vkApi;
    private OnImageLoadedListener listener;

    private LruCache<String, Bitmap> cache;
    private AsyncExecutor downloader;

    public SupportLoader(LastFMApi lastFMApi, VkApi vkApi, Context context, UserSession userSession) {
        this.lastFMApi = lastFMApi;

        initCache();
        initDownloader();
        this.vkApi = vkApi;
        this.userSession = userSession;
        size = context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void abandon() {
        setListener(null);
        downloader.quit();
    }

    public void setListener(OnImageLoadedListener listener) {
        this.listener = listener;
    }

    private void initDownloader() {
        downloader = new AsyncExecutor("image");
        downloader.start();
        downloader.prepareHandler();
    }

    private void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void putImageBitmap(String key, Bitmap b) {
        if (getImageBitmap(key) == null)
            cache.put(key, b);
    }

    @Nullable
    public Bitmap getImageBitmap(String key) {
        return cache.get(key);
    }

    @Nullable
    public String getLoadedImageUrl(String key) {
        String url = urls.get(key);
        if (NONE.equals(url)) return null;
        return url;
    }

    public String getLoadedLyrics(String key) {
        String lyrics = lyricsList.get(key);
        if (lyrics == null) lyrics = "";
        return lyrics;
    }

    private Bitmap retrieveAudioStreamMetadata(final Song song) {
        retriever.setDataSource(song.getUrl(), new HashMap<String, String>());
        byte[] byteBitmap = retriever.getEmbeddedPicture();
        Bitmap result = null;
        if (byteBitmap != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
            options.inSampleSize = calculateInSampleSize(options, size, size);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            result = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
            if (result != null) {
                Log.e("bitmap", String.format("%d %d", result.getWidth(), result.getHeight()));
            }
        }
        return result;
    }

    private String getArtworkUrl(Song song) {
        String url = urls.get(song.getKey());
        if (url == null) {
            try {
                Call<Artwork> artworkCall = lastFMApi.getArtwork2(Settings.LAST_FM_API_KEY, song.getArtist(), song.getTitle());
                Response<Artwork> artworkResponse = artworkCall.execute();
                if (artworkResponse.isSuccess()) {
                    Artwork artwork = artworkResponse.body();
                    url = artwork.getUri();
                    if (url != null && url.isEmpty()) {
                        url = NONE;
                    }
                }
            } catch (NullPointerException e) {
                url = NONE;
            } catch (Exception e) {
                url = null;
            }
        }
        return url;
    }

    public void loadData(final Song song, final boolean loadByUrl) {
        downloader.postTask(LOAD, new Runnable() {

            @Override
            public void run() {
                loadLyrics(song);
                loadArtwork(loadByUrl, song);
            }

        }, false, false);
    }

    private void loadArtwork(boolean loadByUrl, Song song) {
        if (loadByUrl) {
            String url = getArtworkUrl(song);
            postLoadedUrl(song, url);
        } else if (!imageless.contains(song.getKey())) {
            Bitmap bitmap = getImageBitmap(song.getKey());
            Log.e("notification", "" + (bitmap == null));
            if (bitmap == null) {
                bitmap = retrieveAudioStreamMetadata(song);
            }
            postLoadedImage(song, bitmap);
        }
    }

    private void loadLyrics(Song song) {
        String lyrics = lyricsList.get(song.getKey());
        if (lyrics == null && song.hasLyrics()) {
            try {
                Call<VKResult<Lyrics>> lyricsCall = vkApi.getLyrics(song.getLyricsId(), userSession.getToken());
                Response<VKResult<Lyrics>> lyricsResponse = lyricsCall.execute();
                VKResult<Lyrics> lyricsResult = lyricsResponse.body();
                if (lyricsResponse.isSuccess() && lyricsResult.isSuccessful()) {
                    lyrics = lyricsResult.getResponse().getText();
                } else {
                    lyrics = "";
                }
            } catch (IOException e) {
                lyrics = "";
            }
            postLoadedLyrics(song, lyrics);
        }
    }

    private void postLoadedLyrics(final Song song, final String lyrics) {
        Log.e("lyrics", lyrics);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!lyrics.isEmpty()) {
                    lyricsList.put(song.getKey(), lyrics);
                }
                listener.onLyricsLoaded(song, lyrics);
            }
        });
    }

    private void postLoadedUrl(final Song song, @Nullable final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (url != null) {
                    urls.put(song.getKey(), url);
                    if (!url.equals(NONE)) {
                        listener.onUrlLoaded(song, url);
                    }
                }
            }
        });
    }

    private void postLoadedImage(final Song song, @Nullable final Bitmap bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    putImageBitmap(song.getKey(), bitmap);
                    listener.onImageLoaded(song, bitmap);
                } else {
                    imageless.add(song.getKey());
                }
            }
        });
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(Song song, Bitmap bitmap);

        void onLyricsLoaded(Song song, String lyrics);

        void onUrlLoaded(Song song, String url);
    }
}
