package me.gumenniy.arkadiy.vkmusic.utils;

import java.util.HashMap;

import me.gumenniy.arkadiy.vkmusic.R;

/**
 * Created by Arkadiy on 09.03.2016.
 */
public class Errors {
    private static final HashMap<String, Integer> ERRORS = new HashMap<>();
    static {
        ERRORS.put("ExtCertPathValidatorException", R.string.check_time);
        ERRORS.put("201", R.string.user_audio_access_denied);
        ERRORS.put("-201", R.string.group_audio_access_denied);
    }

    public static Integer get(String s) {
        for (String key:
             ERRORS.keySet()) {
            if (s.contains(key)) return ERRORS.get(key);
        }
        return null;
    }
}
