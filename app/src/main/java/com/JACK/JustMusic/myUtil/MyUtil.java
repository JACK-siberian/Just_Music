package com.JACK.JustMusic.myUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class MyUtil {
    public static String formatTime( long curTime) {
        if ( curTime >= 1000 * 60 * 60 )
            return String.format("%02d:%02d:%02d", curTime / 1000 / 3600, curTime / 1000 / 60 % 60, curTime / 1000 % 60);
        else
            return String.format("%02d:%02d",  curTime / 1000 / 60 % 60, curTime / 1000 % 60);
    }

    public static Bitmap getCoverArt ( Context context, Uri uriAudio) {
        // TODO setDataSource failed: status = 0x80000000
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
        myRetriever.setDataSource( context, uriAudio);
        byte[] artwork = myRetriever.getEmbeddedPicture();

        if (artwork != null)
            return BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
        else
            return null;
    }
}