package me.gumenniy.arkadiy.vkmusic.app.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import me.gumenniy.arkadiy.vkmusic.model.Artwork;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.LastFMApi;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by Arkadiy on 25.04.2016.
 */
public class ImageLoader {
    public static final String NONE = "NONE";
    private static final int LOAD = 1010;
    private final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private final HashMap<String, String> urls = new HashMap<>();
    private final Set<String> imageless = new HashSet<>();
    private final Handler handler = new Handler();

    private LastFMApi lastFMApi;
    private OnImageLoadedListener listener;

    private LruCache<String, Bitmap> cache;
    private AsyncExecutor downloader;

    public ImageLoader(LastFMApi lastFMApi) {
        this.lastFMApi = lastFMApi;
        initCache();
        initDownloader();
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
    public String getImageUrl(String key) {
        String url = urls.get(key);
        if (NONE.equals(url)) return null;
        return url;
    }


    private Bitmap retrieveAudioStreamMetadata(final Song song) {
        retriever.setDataSource(song.getUrl(), new HashMap<String, String>());
        byte[] bitmap = retriever.getEmbeddedPicture();
        Bitmap result = null;
        if (bitmap != null) {
            Bitmap byteBitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
            result = Bitmap.createScaledBitmap(byteBitmap, 300, 300, false);
            byteBitmap.recycle();

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

    public void loadArtwork(final Song song, final boolean loadByUrl) {
        downloader.postTask(LOAD, new Runnable() {

            @Override
            public void run() {
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

        }, false, false);
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

        void onUrlLoaded(Song song, String url);
    }
}
