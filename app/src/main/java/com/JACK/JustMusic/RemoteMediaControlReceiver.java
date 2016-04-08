package com.JACK.JustMusicWW;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.JACK.JustMusicWW.control.MusicController;

public class RemoteMediaControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ( Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.d("RemoteMediaReceiver", " MEDIA !! code: " + event.getKeyCode());

            MusicController musicController = MusicController.getInstance(context.getApplicationContext());
            if ( event.getAction() == KeyEvent.ACTION_UP) {
                if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
                    musicController.prevTrack();
                } else if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
                    musicController.nextTrack();
                } else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {
                    musicController.playPauseMusic();
                } else if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                    musicController.playPauseMusic();
                }
                // TODO media control

            /*else if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
                playPauseMusic();
            }*/
            }
        }
    }
}