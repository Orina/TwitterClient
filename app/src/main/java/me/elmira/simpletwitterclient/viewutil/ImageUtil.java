package me.elmira.simpletwitterclient.viewutil;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by elmira on 3/24/17.
 */

public class ImageUtil {

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
