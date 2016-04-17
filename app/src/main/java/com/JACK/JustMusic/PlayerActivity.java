package com.JACK.JustMusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.JACK.JustMusic.control.MusicController;
import com.JACK.JustMusic.myUtil.ImageCoversPagerAdapter;
import com.JACK.JustMusic.myUtil.MyUtil;
import com.JACK.JustMusic.myUtil.OnSongLongClickDialog;
import com.JACK.JustMusic.objects.MyMusic;
import com.JACK.JustMusic.objects.Song;

import java.util.ArrayList;

// TODO MultiPlaylist
// TODO по файлу тыкая и запускай плеер
// TODO  WAKE_LOCK, автовыход, автовоспроизведение at setting
// TODO плавное переключение песен( листаешь и пока не остановишься на X милисек, не переключать песню)
// TODO в моей музыке, любой плейлист, или исполнитель, или альбом, добавлять как ярлык в мою музыку
// TODO на экран блокировки упралвнеие?!  И в уведомление ( возможно опционально api 14 )

public class PlayerActivity extends AppCompatActivity
        implements MusicController.MusicPlayerListener,
        NowPlayingFragment.OnNowPlayingFragmentListener,
        PlayerFragment.OnPlayerFragmentListener,
        ImageCoversPagerAdapter.OnSongLongClickListener {
    private final String TAG = PlayerActivity.class.getSimpleName();

    private TextView textViewCurTime;
    private TextView textViewFullTime;
    private TextView textRunnerTime;
    private FrameLayout frameRunnerTime;
    private ImageButton buttonPrevTrack;
    private ImageButton buttonNextTrack;
    private ImageButton buttonPlayPause;
    private ImageButton buttonShuffleMode;
    private ImageButton buttonLopeMode;
    private SeekBar seekBarCurSongProgress;
    private boolean seekBarIsEnable = true;

    private Toolbar myToolbar;

    private FragmentManager fragmentManager;
    private PlayerFragment playerFragment;
    private NowPlayingFragment nowPlayingFragment;
    private DialogFragment  onSongLongClickDialog;

    private MusicController musicController;


    //  NowPlayingFragment.OnNowPlayingFragmentListener
    @Override
    public void openPlayerFragment() {
        replaceToPlayerFragment();
    }

    //  PlayerFragment.OnPlayerFragmentListener
    @Override
    public void openNowPlayerFragment() {
        replaceToNowPlayingFragment();
    }
    @Override
    public void requestRefreshViews() {
        musicController.refreshViews(false);
    }
    @Override
    public void setTitleActionBar(String title) {
        myToolbar.setTitle(title);
    }
    @Override
    public void changeTrack(int position) {
        musicController.goToTrack(position);
    }
    @Override
    public void requestRefreshCoversData() {
        musicController.refreshCoversData();
    }
    //********!

    //  MusicController.OnMusicPlayerListener
    @Override
    public void changeButtonToPlay() {
        //noinspection ResourceType
        buttonPlayPause.setImageResource(R.drawable.button_play);
    }
    @Override
    public void changeButtonToPause() {
        //noinspection ResourceType
        buttonPlayPause.setImageResource(R.drawable.button_pause);
    }
    @Override
    public void changeSeekBarDurationWithTime( long duration, String durStr) {
        seekBarCurSongProgress.setMax((int)duration);
        textViewFullTime.setText(durStr);
    }
    @Override
    public void changeSeekBarPositionWithTime(long position, String curTime) {
        if ( seekBarIsEnable) {
            seekBarCurSongProgress.setProgress((int)position);
        }
        textViewCurTime.setText(curTime);
    }
    @Override
    public void changeShuffleMode(boolean mode) {
        if (mode)
            //noinspection ResourceType
            buttonShuffleMode.setImageResource( R.drawable.media_shuffle_on);
        else
            //noinspection ResourceType
            buttonShuffleMode.setImageResource( R.drawable.media_shuffle_off);
    }
    @Override
    public void changeLoopMode( String mode) {
        switch (mode) {
            case MusicController.LOOP_MODE_ALL :
                //noinspection ResourceType
                buttonLopeMode.setImageResource( R.drawable.button_loop_all);
                break;
            case MusicController.LOOP_MODE_SINGLE :
                //noinspection ResourceType
                buttonLopeMode.setImageResource( R.drawable.button_loop_single);
                break;
            case MusicController.LOOP_MODE_NONE :
                //noinspection ResourceType
                buttonLopeMode.setImageResource( R.drawable.button_loop_none);
                break;
        }
    }
    @Override
    public void refreshViews(Song song, int position, boolean smoothScroll, long dur, long pos, String posStr, String durStr) {
        changeSeekBarDurationWithTime(dur, durStr);
        changeSeekBarPositionWithTime(pos, posStr);

        textViewCurTime.setVisibility(View.VISIBLE);
        textViewFullTime.setVisibility(View.VISIBLE);

        if ( playerFragment != null ) //&& playerFragment.isVisible()) //TODO
            playerFragment.refreshView(song, position, smoothScroll);

        seekBarCurSongProgress.setEnabled(true);
        buttonShuffleMode.setEnabled(true);
        buttonPrevTrack.setEnabled(true);
        buttonPlayPause.setEnabled(true);
        buttonNextTrack.setEnabled(true);
        buttonLopeMode.setEnabled(true);
    }
    @Override
    public void disableViews() {
        seekBarCurSongProgress.setEnabled(false);
        buttonShuffleMode.setEnabled(false);
        buttonPrevTrack.setEnabled(false);
        buttonPlayPause.setEnabled(false);
        buttonNextTrack.setEnabled(false);
        buttonLopeMode.setEnabled(false);
        changeSeekBarPositionWithTime(0, "00:00");
        changeSeekBarDurationWithTime(0, "00:00");
        textViewCurTime.setVisibility(View.INVISIBLE);
        textViewFullTime.setVisibility(View.INVISIBLE);

        if ( playerFragment != null )
            playerFragment.disableView();

        startActivity(new Intent(
                        getApplicationContext(),
                        MyMusicActivity.class)
        );
        Toast.makeText(
                getApplicationContext(),
                getString(R.string.curTracklistNull),
                Toast.LENGTH_LONG
        ).show();
        finish();
    }
    @Override
    public void setImageCoversData(ArrayList<Uri> imageCoversData) {
        if (playerFragment != null)
            playerFragment.refreshCoversAdapter(imageCoversData);
    }
    //********!
    //!****** OnSongLongClickListener
    @Override
    public void onLongClick(int position) {
        Song song = musicController.getTrack(position);

        onSongLongClickDialog = new OnSongLongClickDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("title", song.getArtist() + " - " + song.getTitle());

        onSongLongClickDialog.setArguments(bundle);
        onSongLongClickDialog.show(fragmentManager, "onSongLongClickDialog");
    }
    //********!

    private void replaceToPlayerFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        playerFragment = new PlayerFragment();
        ft.replace(R.id.containerFragment, playerFragment, "playerFragment");
        ft.commit();
        nowPlayingFragment = null;
        musicController.refreshCoversData();
    }
    private void replaceToNowPlayingFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        nowPlayingFragment = new NowPlayingFragment();
        ft.replace(R.id.containerFragment, nowPlayingFragment, "nowPlayingFragment");
        ft.commit();
        playerFragment = null;
    }
    private void onExitPressed(){
        if (musicController.isPlaying()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this);
            alertDialog.setMessage(getResources().getString(R.string.exit_dialog_title));

            alertDialog.setPositiveButton(
                    getResources().getString(R.string.exit_dialog_positive),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            musicController.playPauseMusic();
                            finish();
                        }
                    }
            );

            alertDialog.setNegativeButton(
                    getResources().getString(R.string.exit_dialog_negative),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
            alertDialog.setNeutralButton(
                    getResources().getString(R.string.exit_dialog_neutral),
                    null
            );
            alertDialog.show();
        }
        else
            finish();
    }

    @Override
    public void onBackPressed() {
        onExitPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId()) {
            case R.id.action_settings :
                startActivity( new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_my_music :
                Intent intent = new Intent(getApplicationContext(), MyMusicActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_exit :
                onExitPressed();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate()");

        musicController = MusicController.getInstance(getApplicationContext());

        setContentView(R.layout.activity_player);
        myToolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(myToolbar);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        playerFragment = new PlayerFragment();
        ft.replace(R.id.containerFragment, playerFragment, "playerFragment");
        ft.commit();

        initViews();
        musicController.notifyViewCreated();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initViews() {
        buttonPlayPause = (ImageButton) findViewById(R.id.buttonPlayPause);
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicController.playPauseMusic();
            }
        });

        buttonPrevTrack = (ImageButton) findViewById( R.id.buttonPrevTrack);
        buttonPrevTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicController.prevTrack();
            }
        });

        buttonNextTrack = (ImageButton) findViewById( R.id.buttonNextTrack);
        buttonNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicController.nextTrack();
            }
        });

        buttonShuffleMode = (ImageButton) findViewById( R.id.buttonShuffleMode);
        buttonShuffleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicController.changeShuffleMode(!musicController.getShuffleMode());
            }
        });

        buttonLopeMode = (ImageButton) findViewById( R.id.buttonLopeMode );
        buttonLopeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicController.changeLoopMode();
            }
        });

        seekBarCurSongProgress = (SeekBar) findViewById(R.id.seekBarCurSongProgress);
        seekBarCurSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textRunnerTime.setText(
                        MyUtil.formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarIsEnable = false;
                frameRunnerTime.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarIsEnable = true;
                frameRunnerTime.setVisibility(View.GONE);
                musicController.setTimePosition(seekBar.getProgress());
            }
        });

        textViewCurTime = (TextView) findViewById( R.id.textCurTime);
        textViewFullTime = (TextView) findViewById( R.id.textFullTime);
        textRunnerTime = (TextView) findViewById( R.id.textRunnerTime);
        frameRunnerTime = (FrameLayout) findViewById( R.id.frameRunnerTime);
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicController.setMusicPlayerListener(this);
        Log.d(TAG, "onStart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        musicController.refreshViews(true);
        Log.d(TAG, "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        musicController.unsetMusicPlayerListener();
        Log.d(TAG, "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() ");
        musicController.notifyViewDestroyed();
    }
}