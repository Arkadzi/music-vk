package me.gumenniy.arkadiy.vkmusic.utils;

import android.content.Context;
import android.os.Build;

import me.gumenniy.arkadiy.vkmusic.R;

/**
 * Created by Arkadiy on 01.04.2016.
 */
public class UtilMethods {
    public static int getColor(Context c, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return c.getColor(R.color.white);
        } else {
            return c.getResources().getColor(R.color.white, null);
        }
    }
}
