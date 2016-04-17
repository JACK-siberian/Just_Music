package com.JACK.JustMusic;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.JACK.JustMusic.control.MusicController;
import com.JACK.JustMusic.objects.MyMusic;

public class MyMusicActivity
        extends AppCompatActivity
        implements MyMusicFragment.OnMyMusicItemSelectedListener,
                PlaylistsFragment.PlaylistsFragmentListener,
                TracklistFragment.OnTrackSelectedListener,
                ArtistsFragment.ArtistsFragmentListener,
                AlbumsFragment.AlbumsFragmentListener {

    public final String TAG = MyMusicActivity.class.getSimpleName();

    private FragmentManager fragmentManager;
    private Toolbar myToolbar;

    //  AlbumsFragment.AlbumsFragmentListener
    @Override
    public void onAlbumSelected(long id, String name) {
        Bundle args = new Bundle();
        args.putString(MyMusic.TRACKLIST_TYPE_KEY, MyMusic.TRACKLIST_TYPE_ALBUM);
        args.putLong(MyMusic.ID_KEY, id);
        args.putString(MyMusic.TITLE_KEY, name);

        createTracklistFragment(args);
    }

    //  ArtistsFragment.ArtistsFragmentListener
    @Override
    public void onArtistSelected(long id, String name) {
        Bundle args = new Bundle();
        args.putString(MyMusic.TRACKLIST_TYPE_KEY, MyMusic.TRACKLIST_TYPE_ARTIST);
        args.putLong(MyMusic.ID_KEY, id);
        args.putString(MyMusic.TITLE_KEY, name);

        createTracklistFragment(args);
    }

    //  MyMusicFragment.OnMyMusicItemSelectedListener
    @Override
    public void requestArtists() {
        createArtistsFragment();
    }
    @Override
    public void requestAlbums() {
        createAlbumsFragment();
    }
    @Override
    public void requestPlaylists(){
        createPlaylistsFragment();
    }
    @Override
    public void requestTracklist( String item, String name, String type, long id){
        Bundle args = new Bundle();
        args.putString( MyMusic.TRACKLIST_TYPE_KEY, type);
        args.putString( MyMusic.ITEM_KEY, item);
        args.putString( MyMusic.TITLE_KEY, name);
        args.putLong(MyMusic.ID_KEY, id);
        createTracklistFragment(args);
    }

    // PlaylistsFragment.PlaylistsFragmentListener
    @Override
    public void requestTracklistFromPlaylists( String name, long id_playlist){
        Bundle args = new Bundle();
        args.putString( MyMusic.TRACKLIST_TYPE_KEY, MyMusic.TRACKLIST_TYPE_PLAYLIST);
        args.putString( MyMusic.TITLE_KEY, name);
        args.putLong( MyMusic.ID_KEY, id_playlist);

        createTracklistFragment(args);
    }

    //  TracklistFragment.OnTrackSelectedListener
    @Override
    public void onTracklistSelected(Cursor cursor, int position, int time) {
        MusicController.getInstance(getApplicationContext()).changeTracklist(cursor, position, time);
        startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
        finish();
    }

    // All fragments
    @Override
    public void setTitleActionBar(String title) {
        myToolbar.setTitle(title);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);
        myToolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //noinspection ResourceType
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        fragmentManager = getSupportFragmentManager();
        createMyMusicFragment();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void createArtistsFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ArtistsFragment fragmentArtists = new ArtistsFragment();
        ft.replace(R.id.containerFragment, fragmentArtists, "fragmentArtists");
        ft.addToBackStack(null);
        ft.commit();
    }
    private void createAlbumsFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        AlbumsFragment fragmentAlbums = new AlbumsFragment();
        ft.replace(R.id.containerFragment, fragmentAlbums, "fragmentAlbums");
        ft.addToBackStack(null);
        ft.commit();
    }
    private void createMyMusicFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        MyMusicFragment fragmentMyMusic = new MyMusicFragment();
        ft.add(R.id.containerFragment, fragmentMyMusic, "fragmentMyMusic");
        // ft.addToBackStack(null);
        ft.commit();
    }
    private void createPlaylistsFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        PlaylistsFragment fragmentPlaylists = new PlaylistsFragment();
        ft.replace( R.id.containerFragment, fragmentPlaylists, "fragmentPlaylists");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void createTracklistFragment( Bundle args) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        TracklistFragment fragmentTracklist = new TracklistFragment();
        fragmentTracklist.setArguments(args);
        ft.replace(R.id.containerFragment, fragmentTracklist, "fragmentTracklist");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Log.i(TAG, "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        finish();
    }
}