package com.JACK.JustMusic;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.JACK.JustMusic.myUtil.AlphabetSimpleCursorAdapter;
import com.JACK.JustMusic.myUtil.MusicContentProvider;

public class AlbumsFragment extends Fragment {
    final String TAG = "Fragment Albums  ";

    private ListView lvAlbums;
    private Cursor cursor;
    private AlbumsFragmentListener albumsFragmentListener;

    public interface AlbumsFragmentListener {
        void onAlbumSelected(long id, String name);
        void setTitleActionBar(String title);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        albumsFragmentListener = (AlbumsFragmentListener) activity;
        Log.d(TAG, " onAttach");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        cursor = MusicContentProvider.getCursorAllAlbums(getContext());
        Log.i(TAG, " onCreate");
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_with_listview, container, false);
        initViews(rootView);

        lvAlbums.setAdapter(
                new AlphabetSimpleCursorAdapter(
                        getContext(),
                        MediaStore.Audio.Albums.ALBUM,
                        R.layout.albums_list_item,
                        cursor,
                        new String[]{
                                MediaStore.Audio.Albums.ALBUM,
                                MediaStore.Audio.Albums.NUMBER_OF_SONGS
                        },
                        new int[]{
                                R.id.textTitle,
                                R.id.textAlbumCountTracks
                        },
                        0
                )
        );
        return rootView;
    }

    private void initViews(View rootView) {
        lvAlbums = (ListView) rootView.findViewById( R.id.listView);
        lvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor temp_cursor = (Cursor) parent.getItemAtPosition(position);
                long album_id = temp_cursor.getLong(
                        temp_cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
                );
                String album_title = temp_cursor.getString(
                        temp_cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
                );
                albumsFragmentListener.onAlbumSelected(album_id, album_title);
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
        albumsFragmentListener.setTitleActionBar(getString(R.string.my_music_item_albums));
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