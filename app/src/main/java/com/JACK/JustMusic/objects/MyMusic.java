package com.JACK.JustMusic.objects;

import android.content.Context;
import android.util.Log;

import com.JACK.JustMusic.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MyMusic {
    private final String TAG = "MyMusic";

    public final static String ITEM_KEY = "ITEM_KEY";
    public final static String TITLE_KEY = "TITLE_KEY";
    public final static String DESK_KEY = "DESK_KEY";
    public final static String IMAGE_KEY = "IMAGE_KEY";
    public final static String ID_KEY = "ID_KEY";
    public final static String CONTENT_KEY = "CONTENT_KEY";
    public final static String TRACKLIST_TYPE_KEY = "TRACKLIST_TYPE_KEY";

    public final static String CONTENT_TRACKLIST = "CONTENT_TRACKLIST";
    public final static String CONTENT_PLAYLISTS = "CONTENT_PLAYLISTS";
    public final static String CONTENT_ALBUMS = "CONTENT_ALBUMS";
    public final static String CONTENT_ARTISTS = "CONTENT_ARTISTS";

    public final static String TRACKLIST_TYPE_SPECIAL = "TRACKLIST_TYPE_SPECIAL";
    public final static String TRACKLIST_TYPE_ARTIST = "TRACKLIST_TYPE_ARTIST";
    public final static String TRACKLIST_TYPE_ALBUM = "TRACKLIST_TYPE_ALBUM";
    public final static String TRACKLIST_TYPE_PLAYLIST = "TRACKLIST_TYPE_PLAYLIST";

    public final static String ITEM_ALL_TRACKS = "ITEM_ALL_TRACKS";
    public final static String ITEM_ALBUMS = "ITEM_ALBUMS";
    public final static String ITEM_ARTISTS = "ITEM_ARTISTS";
    public final static String ITEM_PLAYLISTS = "ITEM_PLAYLISTS";

    private Context context;
    private ArrayList<HashMap<String, Object>> myMusicArray;

    public MyMusic( Context context) {
        this.context = context;
        loadInstance();
    }

    public ArrayList<HashMap<String, Object>> getMyMusicArray() {
        return myMusicArray;
    }

    private void initDefaultMyMusicArray() {
        myMusicArray = new ArrayList<>(4);

        addTracklistToMyMusic(
                ITEM_ALL_TRACKS,
                TRACKLIST_TYPE_SPECIAL,
                context.getResources().getString(R.string.my_music_item_all_tracks),
                context.getResources().getString(R.string.playlist_all_tracks_desc),
                R.drawable.yoda,
                0,
                CONTENT_TRACKLIST
        );
        myMusicArray.add(newMyMusicItem(
                ITEM_ALBUMS,
                context.getResources().getString(R.string.my_music_item_albums),
                R.drawable.yoda,
                CONTENT_ALBUMS
        ));
        myMusicArray.add(newMyMusicItem(
                ITEM_ARTISTS,
                context.getResources().getString(R.string.my_music_item_artists),
                R.drawable.yoda,
                CONTENT_ARTISTS
        ));
        myMusicArray.add(newMyMusicItem(
                ITEM_PLAYLISTS,
                context.getResources().getString(R.string.my_music_item_playlists),
                R.drawable.yoda,
                CONTENT_PLAYLISTS
        ));
    }

    public HashMap<String, Object> getMyMusicItem( int position){
        return myMusicArray.get(position);
    }

    private HashMap<String, Object> newMyMusicItem( String item, String title, int resImage, String contentType) {
        HashMap< String, Object>  hashMap = new HashMap<>(4);
        hashMap.put(ITEM_KEY, item);
        hashMap.put( TITLE_KEY, title);
        hashMap.put( IMAGE_KEY, resImage);
        hashMap.put( CONTENT_KEY, contentType);

        return hashMap;
    }

    public void addTracklistToMyMusic(String item, String type, String title, String desc, int resImage, long id, String contentType) {
        HashMap< String, Object>  hashMap = newMyMusicItem(
                item,
                title,
                resImage,
                contentType
        );
        hashMap.put(TRACKLIST_TYPE_KEY, type);
        hashMap.put( DESK_KEY, desc);
        hashMap.put( ID_KEY, id);

        myMusicArray.add(hashMap);
    }

    public void saveInstance(){
        Log.d(TAG, "saveInstance()");

        File suspend_f = new File(context.getFilesDir(), "myMusic.saved");

        FileOutputStream   fos  = null;
        ObjectOutputStream oos  = null;
        boolean            keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(myMusicArray);
        } catch (Exception e) {
            Log.e(TAG, "saveInstance() failed " +  e.getMessage());
            keep = false;
        } finally {
            try {
                if (oos != null)   oos.close();
                if (fos != null)   fos.close();
                if (!keep) suspend_f.delete();
            } catch (Exception e) {
                Log.e(TAG, "saveInstance() " +  e.getMessage());
            }
        }
    }

    private void loadInstance(){
        File suspend_f = new File(context.getFilesDir(), "myMusic.saved");

        ArrayList<HashMap<String, Object>> reconstructedClass = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);
            reconstructedClass = (ArrayList<HashMap<String, Object>>) is.readObject();
        } catch(Exception e) {
            Log.e(TAG, "loadInstance() " + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
            } catch (Exception e) {
                Log.e(TAG, "loadInstance() " + e.getMessage());
            }
        }

        if ( reconstructedClass != null) {
            Log.d(TAG, "loadInstance() is done");
            myMusicArray = reconstructedClass;
        }
        else {
            Log.d(TAG, "loadInstance() initDefaultMyMusicArray()");
            initDefaultMyMusicArray();
        }
    }
}