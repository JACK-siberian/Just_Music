package com.JACK.JustMusicWW.objects;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.JACK.JustMusicWW.control.MusicController;

import java.io.IOException;

public  class MusicPlayer {
    private final static String TAG = MusicPlayer.class.getSimpleName();

    private MediaPlayer mediaPlayer;
    Context context;

    public MusicPlayer(Context context, MediaPlayer.OnCompletionListener list) {
        mediaPlayer = new MediaPlayer();
        this.context = context;
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(list);
    }

    public void prepareTrack( Uri trackUri) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource( context, trackUri);
            mediaPlayer.prepare();
        }
        catch ( IllegalArgumentException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            Log.e(TAG, "Can't getUri: " + trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playMusic() {
        if ( mediaPlayer != null)
                mediaPlayer.start();
    }

    public void pauseMusic() {
        if ( mediaPlayer != null)
            mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void setPosition ( int position ) {
        if ( mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getPosition () {
        if ( mediaPlayer != null )
            return mediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    public void setLoopMode(boolean loopMode) {
        mediaPlayer.setLooping(loopMode);
    }
    public void setWakeLock(String mode) {
        //mediaPlayer.setScreenOnWhilePlaying(true);
        switch (mode) {
            case MusicController.WAKE_LOCK_MODE_FULL :
                mediaPlayer.setWakeMode( context, PowerManager.FULL_WAKE_LOCK);
                break;
            case MusicController.WAKE_LOCK_MODE_SCREEN_BRIGHT :
                mediaPlayer.setWakeMode( context, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
                break;
            case MusicController.WAKE_LOCK_MODE_SCREEN_DIM :
                mediaPlayer.setWakeMode( context, PowerManager.SCREEN_DIM_WAKE_LOCK);
                break;
            case MusicController.WAKE_LOCK_MODE_PARTIAL :
                mediaPlayer.setWakeMode( context, PowerManager.PARTIAL_WAKE_LOCK);
                break;
        }
        /*
        Flag Value	             CPU	Screen	    Keyboard
        PARTIAL_WAKE_LOCK	    On*	    Off	        Off
        SCREEN_DIM_WAKE_LOCK	On	    Dim	         Off
        SCREEN_BRIGHT_WAKE_LOCK	On	    Bright	    Off
        FULL_WAKE_LOCK	        On	    Bright	    Bright
         */
    }

    public int getDuration ( ) {
        if ( mediaPlayer != null)
            return mediaPlayer.getDuration();
        else
            return 0;
    }

    public void releaseMusicPlayer() {
        if ( mediaPlayer != null ) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}