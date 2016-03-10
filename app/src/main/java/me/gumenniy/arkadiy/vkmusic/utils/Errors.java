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
    }

    public static Integer get(String s) {
        for (String key:
             ERRORS.keySet()) {
            if (s.contains(key)) return ERRORS.get(key);
        }
        return null;
    }
}
