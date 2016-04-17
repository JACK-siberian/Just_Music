package com.JACK.JustMusic.myUtil;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;

public class AlphabetSimpleCursorAdapter extends SimpleCursorAdapter
        implements SectionIndexer {

    AlphabetIndexer mAlphabetIndexer;

    public AlphabetSimpleCursorAdapter(Context context, String columnIndex,
                                       int layout, Cursor cursor,
                                       String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);

        mAlphabetIndexer = new AlphabetIndexer(cursor,
                cursor.getColumnIndex( columnIndex),
                "0123456789ABCDEFGHIJKLMNOPQRTSUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ");
        mAlphabetIndexer.setCursor(cursor);
    }

    @Override
    public Object[] getSections() {
        return mAlphabetIndexer.getSections();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mAlphabetIndexer.getPositionForSection(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return mAlphabetIndexer.getSectionForPosition(position);
    }
}