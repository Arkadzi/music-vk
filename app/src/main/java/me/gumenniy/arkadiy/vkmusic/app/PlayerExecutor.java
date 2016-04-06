package me.gumenniy.arkadiy.vkmusic.app;

import android.os.HandlerThread;

/**
 * Created by Arkadiy on 06.04.2016.
 */
public class PlayerExecutor extends HandlerThread {
    public PlayerExecutor(String name) {
        super(name);
    }

}
