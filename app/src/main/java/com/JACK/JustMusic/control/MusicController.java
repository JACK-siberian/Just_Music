package com.JACK.JustMusicWW.control;

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

import com.JACK.JustMusicWW.R;
import com.JACK.JustMusicWW.myUtil.MyUtil;
import com.JACK.JustMusicWW.objects.MyMusic;
import com.JACK.JustMusicWW.objects.Song;
import com.JACK.JustMusicWW.objects.Tracklist;
import com.JACK.JustMusicWW.PlayMusicService;
import com.JACK.JustMusicWW.RemoteMediaControlReceiver;

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

    public static final String WAKE_LOCK_MODE_PARTIAL = "PARTIAL_WAKE_LOCK";
    public static final String WAKE_LOCK_MODE_SCREEN_DIM = "SCREEN_DIM_WAKE_LOCK";
    public static final String WAKE_LOCK_MODE_SCREEN_BRIGHT = "SCREEN_BRIGHT_WAKE_LOCK";
    public static final String WAKE_LOCK_MODE_FULL = "FULL_WAKE_LOCK";

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
    public void onStopMusic(long pos) {
        streamMusicStatus = MUSIC_STOPPING;
        curTracklist.setLastTimePosition((int)pos);
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

        if ( key.equals(context.getString(R.string.setting_lp_wake_lock_mode_list))) {
            setWakeLockMode(prefs.getString(context.getString(R.string.setting_lp_wake_lock_mode_list), WAKE_LOCK_MODE_PARTIAL));
        }
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
        return !getLoopMode().equals(MusicController.LOOP_MODE_NONE) || curTracklist.hasTrack(curTracklist.getCurPosition() - 1);
    }

    public boolean hasNextTrack() {
        return !getLoopMode().equals(MusicController.LOOP_MODE_NONE) || curTracklist.hasTrack(curTracklist.getCurPosition() + 1);
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

    public void setWakeLockMode(String mode) {
        context.startService(new Intent(context, PlayMusicService.class)
                    .putExtra("ACTION", PlayMusicService.ACTION_CHANGE_TRACK)
                    .putExtra("MODE", mode)
        );
    }

    public void changePlaylist(Cursor cursor, int position, int time) {
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