package com.JACK.JustMusic.myUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

public class MusicContentProvider {
    public static Cursor getCursorSelectionTracks( Context context, String selection, String[] selectionArgs) {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                android.provider.MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };
        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );
        return  cursorLoader.loadInBackground();
    }

    public static Cursor getCursorTracksFromPlaylist( Context context, long playlistId) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(playlistId));

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                android.provider.MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                selection,
                null,
                null
        );
        return  cursorLoader.loadInBackground();
    }

    public static Cursor getCursorAllAlbums( Context context) {
        Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.ALBUM_ART
        };

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                null,
                null,
                MediaStore.Audio.Albums.ALBUM_KEY
        );
        return  cursorLoader.loadInBackground();
    }

    public static Cursor getCursorAllArtists( Context context) {
        Uri uri = android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST_KEY
        );
        return  cursorLoader.loadInBackground();
    }

    public static Cursor getCursorAllPlaylists( Context context) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };

        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                null,
                null,
                MediaStore.Audio.Playlists.DATE_MODIFIED
        );
        return  cursorLoader.loadInBackground();
    }

    // TODO
    /*
    http://android-helper.com.ua/mp3-files/

    Создание нового плейлиста:

    ContentValues cv = new ContentValues();
    cv.put(MediaStore.Audio.Playlists.NAME, "Новый плейлист");
    Uri uri = getContentResolver().insert(MediaStore.Audio.Playlists.getContentUri("external"), cv);

     */
}
