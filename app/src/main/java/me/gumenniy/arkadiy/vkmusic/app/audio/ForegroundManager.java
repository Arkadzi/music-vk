package me.gumenniy.arkadiy.vkmusic.app.audio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.presentation.activities.MainActivity;
import me.gumenniy.arkadiy.vkmusic.data.MusicService;
import me.gumenniy.arkadiy.vkmusic.app.async.SupportLoader;
import me.gumenniy.arkadiy.vkmusic.domain.model.Song;
import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;

public class ForegroundManager {
    private final Service c;
    private final SupportLoader imageUrls;
    private final int remoteSize;
    private final int expandedSize;
    private boolean isForeground;

    public ForegroundManager(@NonNull Service c, @NonNull SupportLoader images) {
        this.c = c;
        this.imageUrls = images;
        float density = c.getResources().getDisplayMetrics().density;
        remoteSize = (int) (density * 64);
        expandedSize = (int) (density * 128);
    }

    public void beginForeground(@NonNull Song song, boolean isPlaying) {
        if (!isForeground()) {
            c.startForeground(Settings.Notification.FOREGROUND_SERVICE, getNotification(song, isPlaying));
            isForeground = true;
        }
    }

    public void endForeground() {
        if (isForeground()) {
            c.stopForeground(true);
            isForeground = false;
        }
    }

    public void updateRemoteView(@NonNull Song song, boolean isPlaying) {
        if (isForeground()) {
            NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(Settings.Notification.FOREGROUND_SERVICE, getNotification(song, isPlaying));
        }
    }

    @NonNull
    public Notification getNotification(@NonNull Song song, boolean isPlaying) {
        Intent resultIntent = new Intent(c, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(c, 0, resultIntent, 0);

        PendingIntent prev = getPendingIntent(Settings.Notification.ACTION.PREV_ACTION);
        PendingIntent next = getPendingIntent(Settings.Notification.ACTION.NEXT_ACTION);
        PendingIntent stop = getPendingIntent(Settings.Notification.ACTION.STOP_SERVICE_ACTION);
        PendingIntent pp = getPendingIntent(Settings.Notification.ACTION.PAUSE_PLAY_ACTION);

        final RemoteViews remoteView = new RemoteViews(c.getPackageName(), R.layout.notification_widget);

        remoteView.setOnClickPendingIntent(R.id.notification_prev, prev);
        remoteView.setOnClickPendingIntent(R.id.notification_next, next);
        remoteView.setOnClickPendingIntent(R.id.notification_stop, stop);
        remoteView.setOnClickPendingIntent(R.id.notification_pp, pp);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setTicker(song.getTitle())
                .setContent(remoteView)
                .setContentIntent(resultPendingIntent);

        if (isPlaying) {
            remoteView.setImageViewResource(R.id.notification_pp, R.drawable.ic_pause_white_24dp);
        } else {
            remoteView.setImageViewResource(R.id.notification_pp, R.drawable.ic_play_arrow_white_24dp);
        }
        remoteView.setTextViewText(R.id.notification_artist, song.getArtist());
        remoteView.setTextViewText(R.id.notification_title, song.getTitle());
        final Notification notification = builder.build();

        String url = imageUrls.getLoadedImageUrl(song.getKey());
        Bitmap bitmap = imageUrls.getImageBitmap(song.getKey());
        setBitmap(remoteView, notification, url, bitmap, remoteSize);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
            final RemoteViews expandedView = new RemoteViews(c.getPackageName(), R.layout.notification_expanded);
            notification.bigContentView = expandedView;

            setBitmap(expandedView, notification, url, bitmap, expandedSize);

            expandedView.setTextViewText(R.id.notification_artist, song.getArtist());
            expandedView.setTextViewText(R.id.notification_title, song.getTitle());
            expandedView.setOnClickPendingIntent(R.id.notification_prev, prev);
            expandedView.setOnClickPendingIntent(R.id.notification_next, next);
            expandedView.setOnClickPendingIntent(R.id.notification_stop, stop);
            expandedView.setOnClickPendingIntent(R.id.notification_pp, pp);

            if (isPlaying) {
                expandedView.setImageViewResource(R.id.notification_pp, R.drawable.ic_pause_white_24dp);
            } else {
                expandedView.setImageViewResource(R.id.notification_pp, R.drawable.ic_play_arrow_white_24dp);
            }
        }
        return notification;
    }

    private void setBitmap(RemoteViews remoteViews, Notification notification, String url, Bitmap bitmap, int scaledBitmap) {
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.album_art, Bitmap.createScaledBitmap(bitmap, scaledBitmap, scaledBitmap, false));
        } else if (url != null) {
            Picasso.with(c)
                    .load(url)
                    .into(remoteViews, R.id.album_art, Settings.Notification.FOREGROUND_SERVICE, notification);
        } else {
            remoteViews.setImageViewResource(R.id.album_art, R.drawable.default_cover);
        }
    }

    @NonNull
    private PendingIntent getPendingIntent(@NonNull String action) {
        Intent intent = new Intent(c, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(c, 0, intent, 0);
    }

    public boolean isForeground() {
        return isForeground;
    }
}
