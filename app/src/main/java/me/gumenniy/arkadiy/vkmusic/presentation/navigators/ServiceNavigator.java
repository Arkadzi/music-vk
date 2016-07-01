package me.gumenniy.arkadiy.vkmusic.presentation.navigators;

import android.content.Intent;

import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;
import me.gumenniy.arkadiy.vkmusic.data.MusicService;

/**
 * Created by sebastian on 01.07.16.
 */
public class ServiceNavigator {
    public void startService(A) {
        Intent intent = new Intent(this, MusicService.class);
        startService(Settings.Notification.ACTION.END_FOREGROUND_ACTION);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }
}
