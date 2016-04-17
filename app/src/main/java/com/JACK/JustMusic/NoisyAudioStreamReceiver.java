package com.JACK.JustMusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.JACK.JustMusic.control.MusicController;

public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ( AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            Log.d("NoisyAudio_Receiver", " ACTION_AUDIO_BECOMING_NOISY: ");
            MusicController.getInstance(context.getApplicationContext()).playPauseMusic();
        }
    }
}