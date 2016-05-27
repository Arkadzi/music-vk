package me.gumenniy.arkadiy.vkmusic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

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

    @Nullable
    public static Bitmap bytesToBitmap(byte[] byteBitmap, int width, int height) {
        Bitmap result = null;
        if (byteBitmap != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            result = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
        }
        return result;
    }

}
