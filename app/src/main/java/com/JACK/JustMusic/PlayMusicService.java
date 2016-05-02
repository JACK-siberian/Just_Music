package com.JACK.JustMusic;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.JACK.JustMusic.control.MusicController;
import com.JACK.JustMusic.objects.MusicPlayer;
import com.JACK.JustMusic.objects.Song;

public class PlayMusicService extends Service {
    private final String TAG = PlayMusicService.class.getSimpleName();

    public final static String ACTION_PLAY = "ACTION_PLAY";
    public final static String ACTION_PAUSE = "ACTION_PAUSE";
    public final static String ACTION_CHANGE_TRACK = "ACTION_CHANGE_TRACK";
    public final static String ACTION_SET_POSITION = "ACTION_SET_POSITION";

    private MusicController musicController;

    private static ServiceListener serviceListener;
    private int curMusicVolume = 0;
    private MusicPlayer musicPlayer;
    private AudioManager audioManager;
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private Handler handler;

    private Song curTrack;

    private int temp = 1; // TODO


    public interface ServiceListener {
        void onCompletionTrack();
        void onPlayMusic();
        void onPauseMusic();
        void onStopMusic();
        void onPlayProgressUpdate(long pos);
    }

    public static void setServiceListener( ServiceListener listener) {
        serviceListener = listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        musicController = MusicController.getInstance(getApplicationContext());
        handler = new Handler();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        musicPlayer = new MusicPlayer(
                PlayMusicService.this.getApplicationContext(),
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        serviceListener.onCompletionTrack();
                        musicPlayer.setPosition(0);
                    }
                }
        );
        noisyAudioStreamReceiver= new NoisyAudioStreamReceiver();
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch ( focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK : {
                        curMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if (curMusicVolume > 1)
                            audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    1,
                                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                            );
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT : {
                        curMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        pauseMusic();
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_GAIN : {
                        audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                curMusicVolume,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                        );
                        playMusic();
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS:
                        if ( musicPlayer != null && musicPlayer.isPlaying())
                            pauseMusic();
                        stopSelf();
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() " + intent.getStringExtra("ACTION"));
        if ((flags & START_FLAG_RETRY) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " Flag_RETRY");
        if ((flags & START_FLAG_REDELIVERY) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_FLAG_REDELIVERY"); ///
        if ((flags & START_CONTINUATION_MASK) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_CONTINUATION_MASK");
        if ((flags & START_NOT_STICKY) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_NOT_STICKY");
        if ((flags & START_REDELIVER_INTENT) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_REDELIVER_INTENT");
        if ((flags & START_STICKY) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_STICKY"); ///
        if ((flags & START_STICKY_COMPATIBILITY) == 0)
            Log.i(TAG, "onStartCommand FLAG: " + " START_STICKY_COMPATIBILITY");  ///

        switch ( intent.getStringExtra("ACTION")) {
            case ACTION_PLAY :
                curTrack = musicController.getCurTrack();
                prepareTrack( curTrack.getUri(), musicController.getPosition());
                playMusic();
                break;

            case ACTION_PAUSE :
                stopMusic();
                break;

            case ACTION_CHANGE_TRACK :
                if (musicPlayer != null && musicPlayer.isPlaying())
                    pauseMusic();
                curTrack = musicController.getCurTrack();
                if (curTrack != null) {
                    prepareTrack(curTrack.getUri(), 0);
                    playMusic();
                }
                else
                    Log.e(TAG, "try prepare null track on -> case ACTION_CHANGE_TRACK :");
                break;

            case ACTION_SET_POSITION :
                if (musicPlayer != null)
                    musicPlayer.setPosition(musicController.getPosition());
                break;

            default :
                Log.e(TAG, "onStartCommand()" + " INVALID_ACTION !!!");
                break;
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        serviceListener.onStopMusic();
    }


    private void playMusic() {
        int result = audioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            musicPlayer.playMusic();
            startPlayProgressUpdater();
            sendNotification();
            serviceListener.onPlayMusic();
            registerReceiver(
                    noisyAudioStreamReceiver,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            );
            Log.d(TAG, "PLAY   cur:" + temp++);
        }
    }

    private void pauseMusic() {
        handler.removeCallbacks(playProgressUpdater);
        if ( musicPlayer != null) {
            musicPlayer.pauseMusic();
            unregisterReceiver(noisyAudioStreamReceiver);
            Log.d(TAG, "PAUSE   cur:" + temp++);
        }
        serviceListener.onPauseMusic();

    }

    private void stopMusic() {
        pauseMusic();
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        closeNotification();
        musicPlayer.releaseMusicPlayer();
        musicPlayer = null;
        stopSelf();
    }

    private void prepareTrack( Uri songUri, int position) {
        if ( musicPlayer != null ) {
            musicPlayer.prepareTrack(songUri);
            musicPlayer.setPosition(position);
        }
        else
            Log.e(TAG, "prepareTrack" + " musicPlayer null");
    }

    private void sendNotification() { // TODO notification with button for andr 4+
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon( android.R.drawable.ic_media_play)  // TODO change icon
                .setTicker(curTrack.getArtist()
                                + " - "
                                + curTrack.getTitle()
                )
                .setWhen( System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle( curTrack.getArtist() )
                .setContentText( curTrack.getTitle());

        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);
        //notificationManager.notify(1, mBuilder.build());
        startForeground(1, mBuilder.build());
    }

    private void closeNotification() {
        stopForeground(true);
    }

    private void startPlayProgressUpdater( ) {
        if (musicPlayer != null && musicPlayer.isPlaying())
            handler.postDelayed(playProgressUpdater, 500);
    }

    private Runnable playProgressUpdater = new Runnable() {
        public void run() {
            if ( musicPlayer != null) {
                serviceListener.onPlayProgressUpdate((long)musicPlayer.getPosition());
                startPlayProgressUpdater();
            } else {
                Log.e(TAG, "startPlayProgressUpdater" + " musicPlayer null");
            }
        }
    };
}