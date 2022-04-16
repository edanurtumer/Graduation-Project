package com.proje.steganography;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static long time;
    private static Context ctx;

    public static String getTimeAgo(long time, Context ctx){
        GetTimeAgo.time = time;
        GetTimeAgo.ctx = ctx;

        if(time < 1000000000000L){

            time *= 1000;

        }

        long now = System.currentTimeMillis();
        if(time > now || time <= 0){

            return null;

        }

        final long diff = now - time;
        if(diff < MINUTE_MILLIS ){
            return "çevrimiçi";
        }else if(diff < 2 * MINUTE_MILLIS){
            return "bir dakika önce";
        }else if(diff < 50 * MINUTE_MILLIS){
            return diff / MINUTE_MILLIS + "dakika önce";
        }else if(diff < 90 * MINUTE_MILLIS){
            return "1 saat önce";
        }else if(diff < 24 * HOUR_MILLIS){
            return diff / HOUR_MILLIS + "saat önce";
        }else if(diff < 48 * HOUR_MILLIS){
            return "dün";
        }else{
            return diff / DAY_MILLIS + "gün önce";
        }


    }

}
