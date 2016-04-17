package com.JACK.JustMusic;

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

import com.JACK.JustMusic.myUtil.MusicContentProvider;
import com.JACK.JustMusic.objects.MyMusic;

// TODO RecyclerView
// https://habrahabr.ru/post/237101/
public class TracklistFragment extends Fragment {
    private final String TAG = "Fragment Tracklist   ";

    private Cursor cursor;
    private String tracklistType;
    private Bundle args;

    private OnTrackSelectedListener onTrackSelectedListener;
    public interface OnTrackSelectedListener {
        void onTracklistSelected(Cursor cursor, int position, int time);
        void setTitleActionBar(String title);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onTrackSelectedListener = (OnTrackSelectedListener)activity;
        Log.d(TAG, " onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate");
        setRetainInstance(true);

        args = getArguments();
        String selection;
        String[] selectionArgs = {null};

        if ( !args.isEmpty()) {
            tracklistType = args.getString(MyMusic.TRACKLIST_TYPE_KEY);
            switch (tracklistType) {
                case MyMusic.TRACKLIST_TYPE_ARTIST :
                    String artistName = args.getString( MyMusic.TITLE_KEY);
                    onTrackSelectedListener.setTitleActionBar(artistName);
                    long artistID = args.getLong(MyMusic.ID_KEY);
                    selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                            MediaStore.Audio.Media.ARTIST_ID + "=?";
                    selectionArgs[0]= Long.toString(artistID);

                    cursor = MusicContentProvider.getCursorSelectionTracks(getContext(), selection, selectionArgs);
                    break;

                case MyMusic.TRACKLIST_TYPE_ALBUM :
                    String albumName = args.getString( MyMusic.TITLE_KEY);
                    onTrackSelectedListener.setTitleActionBar(albumName);
                    long albumID = args.getLong(MyMusic.ID_KEY);
                    selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                            MediaStore.Audio.Media.ALBUM_ID + "=?";
                    selectionArgs[0]= Long.toString(albumID);

                    cursor = MusicContentProvider.getCursorSelectionTracks(getContext(), selection, selectionArgs);
                    break;

                case MyMusic.TRACKLIST_TYPE_PLAYLIST :
                    String playlistName = args.getString( MyMusic.TITLE_KEY);
                    onTrackSelectedListener.setTitleActionBar(playlistName);
                    long playlistID = args.getLong(MyMusic.ID_KEY);
                    cursor = MusicContentProvider.getCursorTracksFromPlaylist( getContext(), playlistID);
                    break;

                case MyMusic.TRACKLIST_TYPE_SPECIAL :
                    String item = args.getString( MyMusic.ITEM_KEY);
                    String name = args.getString( MyMusic.TITLE_KEY);
                    onTrackSelectedListener.setTitleActionBar(name);

                    switch (item) {
                        case MyMusic.ITEM_ALL_TRACKS:
                            selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                            cursor = MusicContentProvider.getCursorSelectionTracks(getContext(), selection, null);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_with_listview, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        ListView lvTracklist = (ListView) rootView.findViewById(R.id.listView);
        lvTracklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onTrackSelectedListener.onTracklistSelected(cursor, position, 0);
            }
        });

        lvTracklist.setAdapter(new SimpleCursorAdapter(
                getContext(),
                R.layout.tracklist_list_item,
                cursor,
                new String[]{
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST
                },
                new int[]{
                        R.id.textSongTitle,
                        R.id.textSongArtist
                },
                0
        ));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, " onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(TAG, " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, " onResume");
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