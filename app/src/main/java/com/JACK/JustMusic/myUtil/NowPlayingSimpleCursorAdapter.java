package com.JACK.JustMusicWW.myUtil;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class NowPlayingSimpleCursorAdapter extends SimpleCursorAdapter {


    public NowPlayingSimpleCursorAdapter(Context context, String columnIndex,
                                         int layout, Cursor cursor,
                                         String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);

    }
}