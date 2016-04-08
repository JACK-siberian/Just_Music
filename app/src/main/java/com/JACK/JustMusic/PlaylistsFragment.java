package com.JACK.JustMusicWW;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.JACK.JustMusicWW.myUtil.MusicContentProvider;

public class PlaylistsFragment extends Fragment {
    final String TAG = "Fragment Playlists  ";

    private ListView lvPlaylists;
    private Cursor cursor;
    private PlaylistsFragmentListener playlistsFragmentListener;

    public interface PlaylistsFragmentListener {
        void requestTracklistFromPlaylists( String name, long id_playlist);
        void setTitleActionBar(String title);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        playlistsFragmentListener = (PlaylistsFragmentListener) activity;
        Log.d(TAG, " onAttach");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cursor = MusicContentProvider.getCursorAllPlaylists(getContext());
        setRetainInstance(true);
        Log.d(TAG, " onCreate");
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_with_listview, container, false);
        initViews(rootView);

        lvPlaylists.setAdapter(new SimpleCursorAdapter(
                getContext(),
                R.layout.playlists_list_item,
                cursor,
                new String[]{
                        MediaStore.Audio.Playlists.NAME
                },
                new int[]{
                        R.id.textTitle,
                },
                0
        ));
        return rootView;
    }

    private void initViews(View rootView) {
        lvPlaylists = (ListView) rootView.findViewById( R.id.listView);
        lvPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor temp_cursor = (Cursor) parent.getItemAtPosition(position);
                long playlist_id = temp_cursor.getLong(
                        temp_cursor.getColumnIndex( MediaStore.Audio.Playlists._ID)
                );
                String name = temp_cursor.getString(
                        temp_cursor.getColumnIndex( MediaStore.Audio.Playlists.NAME)
                );
                playlistsFragmentListener.requestTracklistFromPlaylists(name, playlist_id);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d(TAG, " onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(TAG, " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        playlistsFragmentListener.setTitleActionBar(getString(R.string.my_music_item_playlists));
        Log.d(TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, " onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, " onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( cursor != null)
            cursor.close();
        Log.d(TAG, " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, " onDetach");
    }
}