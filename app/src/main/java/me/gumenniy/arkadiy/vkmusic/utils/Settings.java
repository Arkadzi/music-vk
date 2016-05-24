package me.gumenniy.arkadiy.vkmusic.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public interface Settings {
    int CLIENT_ID = 5315866;
    String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    String LAST_FM_API_KEY = "fe92647a6609aea759d311e37abac103";
    String CACHE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + ".vkmusic";
    String CONNECTIONS_FILE = CACHE_DIRECTORY + File.separator + "connections.txt";
    String PREFS = "prefs";

    interface Notification {

        int FOREGROUND_SERVICE = 110011;
        int LOAD_SERVICE = 111010;

        interface ACTION {
            String PREV_ACTION = "me.gumenniy.arkadiy.vkmusic.action.prev";
            String NEXT_ACTION = "me.gumenniy.arkadiy.vkmusic.action.next";
            String PAUSE_PLAY_ACTION = "me.gumenniy.arkadiy.vkmusic.action.pause_play";
            String STOP_SERVICE_ACTION = "me.gumenniy.arkadiy.vkmusic.action.stop";
            String BEGIN_FOREGROUND_ACTION = "me.gumenniy.arkadiy.vkmusic.action.begin";
            String END_FOREGROUND_ACTION = "me.gumenniy.arkadiy.vkmusic.action.end";
        }
    }

    enum Menu {Add, Delete, Load}
}
