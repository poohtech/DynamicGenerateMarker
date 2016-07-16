package com.example.user.dynamicgeneratemarker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;


public class Utils {

    public static String FILE_PICTURE_URL = "http://chart.apis.google.com/chart?chst=d_map_spin&chld=";

    /**
     * Check Connectivity of network.
     */
    public static boolean isOnline(Context context) {
        try {
            if (context == null)
                return false;

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                if (cm.getActiveNetworkInfo() != null) {
                    return cm.getActiveNetworkInfo().isConnected();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
//			Log.error("Exception", e);
            return false;
        }

    }

    public static void verifyUserPath() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/DynamicMarker");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        dir = null;
    }
}
