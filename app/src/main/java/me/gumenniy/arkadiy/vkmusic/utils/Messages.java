package me.gumenniy.arkadiy.vkmusic.utils;

import android.support.annotation.Nullable;

import java.util.HashMap;

import me.gumenniy.arkadiy.vkmusic.R;

/**
 * Created by Arkadiy on 09.03.2016.
 */
public class Messages {
    private static final HashMap<String, Integer> MESSAGES = new HashMap<>();
    static {
        MESSAGES.put("ExtCertPathValidatorException", R.string.check_time);
        MESSAGES.put("201", R.string.user_audio_access_denied);
        MESSAGES.put("-201", R.string.group_audio_access_denied);
        MESSAGES.put("added", R.string.songAdded);
    }

    @Nullable
    public static Integer get(String s) {
        for (String key: MESSAGES.keySet()) {
            if (s.contains(key)) return MESSAGES.get(key);
        }
        return null;
    }
}
