package com.simurg.infoboard.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.simurg.infoboard.log.FileLogger;

public class NetworkUtils {
    public static boolean isNetworkConnected(Context context) {
        FileLogger.log("isNetworkConnected", "NetworkCheck called");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // Получаем информацию о подключении
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
