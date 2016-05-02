package com.JACK.JustMusic.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.JACK.JustMusic.PlayMusicService;
import com.JACK.JustMusic.R;
import com.JACK.JustMusic.RemoteMediaControlReceiver;
import com.JACK.JustMusic.myUtil.MyUtil;
import com.JACK.JustMusic.objects.MyMusic;
import com.JACK.JustMusic.objects.Song;
import com.JACK.JustMusic.objects.Tracklist;

import java.util.ArrayList;

public class MusicController
        implements PlayMusicService.ServiceListener,
                    SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = "MusicController";

    private final String MUSIC_PLAYING = "MUSIC_PLAYING";
    private final String MUSIC_PAUSING = "MUSIC_PAUSING";
    private final String MUSIC_STOPPING = "MUSIC_STOPPING";

    public static final String LOOP_MODE_NONE = "LOOP_MODE_NONE";
    public static final String LOOP_MODE_SINGLE = "LOOP_MODE_SINGLE";
    public static final String LOOP_MODE_ALL = "LOOP_MODE_ALL";

    private static MusicController instance;

    private MusicPlayerListener musicPlayerListener;
    private Context context;
    private SharedPreferences prefs;
    private ComponentName mediaButtonReceiver;
    private AudioManager audioManager;

    private Tracklist curTracklist;
    private MyMusic myMusic;

    private String streamMusicStatus = MUSIC_STOPPING;
    private boolean playerViewIsAlive = false;
    private boolean playerIsAvailable;
    /*
    //TODO mounted SD
     if (!Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
      return;
    }
     */

    private MusicController( Context context) {
        Log.d(TAG, "MusicController init");
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        PlayMusicService.setServiceListener(this);
        curTracklist = new Tracklist(context.getFilesDir());
        myMusic = new MyMusic(context);
        playerIsAvailable = curTracklist.isAvailable();

        mediaButtonReceiver = new ComponentName(
                context.getPackageName(),
                RemoteMediaControlReceiver.class.getName()
        );
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //noinspection deprecation
        audioManager.registerMediaButtonEventReceiver(mediaButtonReceiver);  // TODO read about deprecated and newReceiver
    }

    public static MusicController getInstance(Context context) {
        if ( instance == null)
            instance = new MusicController(context);
        return instance;
    }

    public void release() {
        Log.d(TAG, "MusicController release");
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        curTracklist.saveTimePosition();
        curTracklist = null;

        myMusic.saveInstance();
        myMusic = null;

        //noinspection deprecation
        //audioManager.unregisterMediaButtonEventReceiver(mediaButtonReceiver); // TODO test without unregister
        instance = null;
    }

    public interface MusicPlayerListener {
        void changeButtonToPlay();
        void changeButtonToPause();
        void changeSeekBarDurationWithTime( long duration, String durStr);
        void changeSeekBarPositionWithTime( long position, String curTime);
        void changeShuffleMode( boolean mode);
        void changeLoopMode( String mode);
        void refreshViews(Song song, int position, boolean smoothScroll, long dur, long pos, String posStr, String durStr);
        void disableViews();
        void setImageCoversData(ArrayList<Uri> imageCoversData);
        void setTracklistData(Tracklist tracklist);
    }

    public void setMusicPlayerListener(MusicPlayerListener musicPlayerListener) {
        this.musicPlayerListener = musicPlayerListener;
        refreshCoversData();
    }

    public void unsetMusicPlayerListener() {
        this.musicPlayerListener = null;
    }

    // PlayMusicService.ServiceListener
    @Override
    public void onCompletionTrack() {
        if (getLoopMode().equals(LOOP_MODE_SINGLE)) {
            curTracklist.setLastTimePosition(0);
            if ( isPlaying() ) {
                context.startService(new Intent(context, PlayMusicService.class)
                        .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
            }
            return;
        }

        if ( hasNextTrack()) {
            curTracklist.toNextTrack();
            if ( isPlaying() ) {
                context.startService(new Intent(context, PlayMusicService.class)
                        .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
            }

        }
        else {
            context.startService(new Intent(context, PlayMusicService.class)
                    .putExtra("ACTION", PlayMusicService.ACTION_PAUSE));
            curTracklist.toStartTracklist();
            Toast.makeText(
                    context,
                    context.getString(R.string.playlistEnd),
                    Toast.LENGTH_LONG
            ).show();
        }
        if ( musicPlayerListener != null) {
            refreshViews(true);
        }
    }
    @Override
    public void onPlayMusic() {
        streamMusicStatus = MUSIC_PLAYING;
        if ( musicPlayerListener != null)
            musicPlayerListener.changeButtonToPause();
    }
    @Override
    public void onPauseMusic() {
        streamMusicStatus = MUSIC_PAUSING;
        if ( musicPlayerListener != null)
            musicPlayerListener.changeButtonToPlay();
    }
    @Override
    public void onStopMusic() {
        streamMusicStatus = MUSIC_STOPPING;
        if ( canReleased() )
            release();
    }
    @Override
    public void onPlayProgressUpdate( long pos) {
        curTracklist.setLastTimePosition((int)pos);
        if ( musicPlayerListener != null)
            musicPlayerListener.changeSeekBarPositionWithTime(pos, MyUtil.formatTime(pos));
    }
    //*****!
    //  SharedPreferences.OnSharedPreferenceChangeListener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(context.getString(R.string.setting_cb_shuffle_mode)))
            setShuffleMode(sharedPreferences.getBoolean(
                            context.getString(R.string.setting_cb_shuffle_mode),
                            false)
            );
    }
    //*****!

    public void refreshViews(boolean anim_mode) {
        if ( musicPlayerListener != null ) {
            if (playerIsAvailable) {
                Song curTrack = curTracklist.getCurTrack();
                long trackDur = curTrack.getDuration();
                int trackPos = curTracklist.getLastTimePosition();
                musicPlayerListener.refreshViews(
                        curTrack,
                        curTracklist.getCurPosition(),
                        anim_mode,
                        trackDur,
                        trackPos,
                        MyUtil.formatTime(trackPos),
                        MyUtil.formatTime(trackDur)
                );

                if (isPlaying())
                    musicPlayerListener.changeButtonToPause();
                else
                    musicPlayerListener.changeButtonToPlay();
                musicPlayerListener.changeShuffleMode(getShuffleMode());
                musicPlayerListener.changeLoopMode(getLoopMode());
            } else
                musicPlayerListener.disableViews();
        }
    }

    public void refreshCoversData() {
        if (musicPlayerListener != null && playerIsAvailable)
            musicPlayerListener.setImageCoversData(curTracklist.getUriArray());
        else
            ;//musicPlayerListener.setImageCoversData(null);
        refreshViews(false);
    }

    public void refreshTracklistData() {
        if (musicPlayerListener != null && playerIsAvailable)
            musicPlayerListener.setTracklistData(curTracklist);
        else
            ;//musicPlayerListener.setImageCoversData(null);
        refreshViews(false);
    }

    public void notifyViewCreated() {
        playerViewIsAlive = true;
    }

    public void notifyViewDestroyed() {
        playerViewIsAlive = false;
        if ( canReleased() )
            release();
    }

    public void playPauseMusic() {
        if (playerIsAvailable) {
            if (isPlaying())
                context.startService(new Intent(context, PlayMusicService.class)
                        .putExtra("ACTION", PlayMusicService.ACTION_PAUSE));
            else {
                context.startService(new Intent(context, PlayMusicService.class)
                        .putExtra("ACTION", PlayMusicService.ACTION_PLAY));
            }
        }
    }

    public boolean hasPrevTrack() {
        return (!getLoopMode().equals(MusicController.LOOP_MODE_NONE) && (playerIsAvailable = curTracklist.isAvailable()))
                || curTracklist.hasTrack(curTracklist.getCurPosition() - 1);
    }

    public boolean hasNextTrack() {
        return (!getLoopMode().equals(MusicController.LOOP_MODE_NONE) && (playerIsAvailable = curTracklist.isAvailable()))
                || curTracklist.hasTrack(curTracklist.getCurPosition() + 1);
    }

    public void prevTrack() {
        if (playerIsAvailable) {
            if (curTracklist.getLastTimePosition() <= 14000) {
                if (hasPrevTrack()) {
                    curTracklist.toPrevTrack();
                    if (isPlaying())
                        context.startService(new Intent(context, PlayMusicService.class)
                                .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
                    if (musicPlayerListener != null) {
                        refreshViews(true);
                    }
                } else {
                    Toast.makeText(
                            context,
                            context.getString(R.string.playlistEnd),
                            Toast.LENGTH_LONG
                    ).show();
                }
            } else {
                setTimePosition(0);
            }
        }
    }

    public void nextTrack() {
        if (playerIsAvailable) {
            if (hasNextTrack()) {
                curTracklist.toNextTrack();
                if (isPlaying()) {
                    context.startService(new Intent(context, PlayMusicService.class)
                            .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
                }
                if (musicPlayerListener != null) {
                    refreshViews(true);
                }
            } else
                Toast.makeText(
                        context,
                        context.getString(R.string.playlistEnd),
                        Toast.LENGTH_LONG
                ).show();
        }
    }

    public void goToTrack(int position) {
        curTracklist.toTrack(position);
        if (isPlaying()) {
            context.startService(new Intent(context, PlayMusicService.class)
                    .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
        }
        if (musicPlayerListener != null) {
            refreshViews(true);
        }
    }
    public void onTrackClicked(int position) {
        if (position == curTracklist.getCurPosition())
            playPauseMusic();
        else {
            curTracklist.toTrack(position);
            context.startService(new Intent(context, PlayMusicService.class)
                    .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
            if (musicPlayerListener != null) {
                refreshViews(true);
            }
        }
    }

    public void changeShuffleMode(boolean mode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.setting_cb_shuffle_mode), mode);
        editor.apply();
    }
    private void setShuffleMode(boolean mode) {
        if (playerIsAvailable)
            curTracklist.setShuffleMode(mode);
        if (musicPlayerListener != null) {
            refreshCoversData();
            refreshViews(false);
            musicPlayerListener.changeShuffleMode(getShuffleMode());
        }
    }
    public boolean getShuffleMode() {
        return prefs.getBoolean(context.getString(R.string.setting_cb_shuffle_mode), false);
    }

    public Song getTrack(int position) {
        return curTracklist.getTrack(position);
    }
    public Song getCurTrack() {
        return curTracklist.getCurTrack();
    }
    public int getPosition() {
        return curTracklist.getLastTimePosition();
    }

    public void setTimePosition(int position) {
        if (playerIsAvailable) {
            curTracklist.setLastTimePosition(position);
            if (musicPlayerListener != null)
                musicPlayerListener.changeSeekBarPositionWithTime(
                        position,
                        MyUtil.formatTime(position)
                );
            if (isPlaying())
                context.startService(new Intent(context, PlayMusicService.class)
                        .putExtra("ACTION", PlayMusicService.ACTION_SET_POSITION)
                );
        }
    }

    public void changeLoopMode() {
        if (playerIsAvailable) {
            switch (getLoopMode()) {
                case LOOP_MODE_NONE :
                    setLoopMode(LOOP_MODE_ALL);
                    break;
                case LOOP_MODE_ALL :
                    setLoopMode(LOOP_MODE_SINGLE);
                    break;
                case LOOP_MODE_SINGLE :
                    setLoopMode(LOOP_MODE_NONE);
                    break;
            }
        }
    }
    public void setLoopMode(String mode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.setting_lp_loop_mode_list), mode);
        editor.apply();

        if (musicPlayerListener != null) {
            musicPlayerListener.changeLoopMode(mode);
        }
    }
    public String getLoopMode() {
        return prefs.getString(context.getString(R.string.setting_lp_loop_mode_list), LOOP_MODE_NONE);
    }

    public void deleteTrackFromTracklist(int position) {
        if (position == curTracklist.getCurPosition()) {
            boolean isPlaying = isPlaying();
            boolean hasRealNextTrack = curTracklist.hasNextRealTrack();

            curTracklist.deleteTrack(position);

            if (hasRealNextTrack) {
                if (isPlaying)
                    context.startService(new Intent(context, PlayMusicService.class)
                            .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
            } else {
                if (hasNextTrack())
                    context.startService(new Intent(context, PlayMusicService.class)
                            .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK));
                else {
                    if (isPlaying)
                        context.startService(new Intent(context, PlayMusicService.class)
                                .putExtra("ACTION", PlayMusicService.ACTION_PAUSE));
                    nextTrack();
                }

            }
            curTracklist.setLastTimePosition(0);
        }
        else
            curTracklist.deleteTrack(position);

        if (playerIsAvailable = curTracklist.isAvailable()) {
            refreshCoversData();
            //refreshTracklistData();
        } else
            refreshViews(false);
    }

    public void changeTracklist(Cursor cursor, int position, int time) {
        if ( curTracklist == null)
            curTracklist = new Tracklist(context.getFilesDir());

        if ( curTracklist.setTracklist(cursor, position, time)) {
            changeShuffleMode(false);
            playerIsAvailable = true;
            if (musicPlayerListener != null) {
                refreshCoversData();
            }
            refreshViews(false);
            context.startService(new Intent(context, PlayMusicService.class)
                    .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK)
            );
        }
        else {
            playerIsAvailable = false;
            curTracklist = null;
            refreshViews(false);
        }
    }

    public MyMusic getMyMusic() {
        return myMusic;
    }

    public boolean isPlaying() {
        return streamMusicStatus.equals(MUSIC_PLAYING);
    }

    private boolean canReleased() {
        return streamMusicStatus.equals(MUSIC_STOPPING) && !playerViewIsAlive;
    }
}