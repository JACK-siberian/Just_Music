package com.JACK.JustMusicWW.objects;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Tracklist {
    private final String TAG = "Tracklist";

    private File filesDirPath;
    private ArrayList<Song> songs;
    private int curPosition;
    private int lastTimePosition;
    private ArrayList<Song> safeArray;

    public Tracklist( File filesDirPath) {
        this.filesDirPath = filesDirPath;
        loadInstance();
    }

    public boolean isAvailable() {
        return songs != null;
    }

    public boolean setTracklist(Cursor cursor, int position, int time) {
        ArrayList<Song> tempSongs = null;
        if ( cursor != null && cursor.moveToFirst()) {
            int count;
            if ( (count = cursor.getCount()) > 0) {
                tempSongs = new ArrayList<>(count);

                int idColumn = cursor
                        .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int titleColumn = cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationColumn = cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION);
                int dataStreamColumn = cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA);

                do {
                    long thisId = cursor.getLong(idColumn);
                    String thisTitle = cursor.getString(titleColumn);
                    String thisAlbum = cursor.getString(albumColumn);
                    String thisArtist = cursor.getString(artistColumn);
                    String dataStream = cursor.getString(dataStreamColumn);

                    long thisDur = cursor.getLong(durationColumn);

                    tempSongs.add(new Song(thisId, thisArtist, thisTitle, thisAlbum, thisDur,dataStream));

                } while (cursor.moveToNext());

                cursor.close();
            }
        }

        if ( tempSongs != null) {
            this.curPosition = position;
            this.lastTimePosition = time;
            songs = tempSongs;
            this.safeArray = null;
            saveTracklist();
            saveTimePosition();

            return true;
        }
        else
            return false;
    }

    public Song getCurTrack() {
        if ( !songs.isEmpty() )
            return getTrack(curPosition);
        else
            return null;
    }

    public void toTrack(int position) {
        if (isValidIndex(position)) {
            curPosition = position;
            lastTimePosition = 0;
        }
    }
    public void toPrevTrack() {
        if ( isValidIndex(curPosition - 1))
            curPosition--;
        else
            curPosition = songs.size() - 1;
        setLastTimePosition(0);
    }
    public void toNextTrack() {
        if ( isValidIndex(curPosition + 1))
            curPosition++;
        else
            curPosition = 0;

        setLastTimePosition(0);
    }

    public boolean hasTrack(int position) {
        return isValidIndex(position);
    }

    public void toStartTracklist() {
        curPosition = 0;
        lastTimePosition = 0;
    }

    public int getLastTimePosition() {
        return lastTimePosition;
    }
    public synchronized void setLastTimePosition(int lastTimePosition) {
        this.lastTimePosition = lastTimePosition;
    }

    public int getCurPosition() {
        return curPosition;
    }

    public void setShuffleMode( boolean mode) {
        if (mode) {
            // TODO вроде затратно делаю... когда в 0 позицию элемент вставляю
            safeArray = new ArrayList<>(songs);
            Song firstSong = getCurTrack();
            songs.remove(curPosition);
            Collections.shuffle(songs);
            songs.add(0, firstSong);
            curPosition = 0;
        }
        else if ( safeArray != null) {
            int safePosition = safeArray.indexOf( getCurTrack());
            songs = safeArray;
            curPosition = safePosition;
            safeArray = null;
        }
        saveTracklist();
    }
    public ArrayList<Uri> getUriArray() {
        if (songs != null) {
            ArrayList<Uri> uris = new ArrayList<>(songs.size());
            // TODO может по другому ??!!
            for (Song song : songs)
                uris.add(song.getUri());
            return uris;
        }
        else
            return null;
    }

    public void saveTimePosition() {
        Log.d(TAG, "saveTimePosition()");

        if ( songs != null && songs.size() > 0){
            File suspend_f = new File(filesDirPath, "time_position.saved");

            FileOutputStream fos  = null;
            ObjectOutputStream oos  = null;
            boolean            keep = true;


            try {
                fos = new FileOutputStream(suspend_f);
                oos = new ObjectOutputStream(fos);
                oos.writeInt(curPosition);
                oos.writeInt(lastTimePosition);

            } catch (Exception e) {
                Log.e(TAG, "saveTimePosition() failed; " +  e.getMessage());
                keep = false;
            } finally {
                try {
                    if (oos != null)   oos.close();
                    if (fos != null)   fos.close();
                    if (!keep)
                        suspend_f.delete();
                } catch (Exception e) {
                    Log.e(TAG, "saveTimePosition() " +  e.getMessage());
                }
            }
        }
    }
    public void saveTracklist() {
        Log.d(TAG, "saveTracklist()");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if ( songs != null && songs.size() > 0) {
                    File suspend_f = new File(filesDirPath, "tracklist.saved");

                    FileOutputStream fos  = null;
                    ObjectOutputStream oos  = null;
                    boolean            keep = true;


                    try {
                        fos = new FileOutputStream(suspend_f);
                        oos = new ObjectOutputStream(fos);

                        oos.writeObject(songs);
                        oos.writeObject(safeArray);

                    } catch (Exception e) {
                        Log.e(TAG, "saveTracklist() failed; " +  e.getMessage());
                        keep = false;
                    } finally {
                        try {
                            if (oos != null)   oos.close();
                            if (fos != null)   fos.close();
                            if (!keep)
                                suspend_f.delete();
                        } catch (Exception e) {
                            Log.e(TAG, "saveTracklist() " +  e.getMessage());
                        }
                    }
                }
                return null;
            }
        }.execute();
    }
    private void loadInstance() {
        Log.d(TAG, "loadTracklist()");

        File suspend_f = new File(filesDirPath, "tracklist.saved");

        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);

            songs = (ArrayList<Song>)is.readObject();
            safeArray = (ArrayList<Song>)is.readObject();

        } catch(Exception e) {
            Log.e(TAG, "loadTracklist() " + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
            } catch (Exception e) {
                Log.e(TAG, "loadTracklist() " + e.getMessage());
            }
        }

        Log.d(TAG, "loadTime_position()");
        suspend_f = new File(filesDirPath, "time_position.saved");

        fis = null;
        is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);

            curPosition = is.readInt();
            lastTimePosition = is.readInt();
        } catch(Exception e) {
            Log.e(TAG, "loadTime_position() " + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
            } catch (Exception e) {
                Log.e(TAG, "loadTime_position() " + e.getMessage());
            }
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < songs.size();
    }
    private Song getTrack(int index) {
        if ( isValidIndex(index) )
            return songs.get( index );
        else
            return null;
    }
}