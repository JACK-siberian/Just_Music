package com.JACK.JustMusicWW;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.JACK.JustMusicWW.control.MusicController;
import com.JACK.JustMusicWW.objects.MyMusic;

import java.util.HashMap;

public class MyMusicFragment extends Fragment {
    final String TAG = "Fragment MyMusic  ";

    private GridView gvMyMusic;

    private OnMyMusicItemSelectedListener onGridItemSelectedListener;
    public interface OnMyMusicItemSelectedListener {
        void requestArtists();
        void requestAlbums();
        void requestPlaylists();
        void requestTracklist( String selection, String name, String type, long id_playlist);
        void setTitleActionBar(String title);
    }

    class NewViewBinder implements SimpleAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation)
        {
            if (view instanceof ImageView)
            {
                ImageView iv = (ImageView) view;
                iv.setImageResource((int)data);
                return true;
            }
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onGridItemSelectedListener = (OnMyMusicItemSelectedListener) activity;
        Log.d(TAG, " onAttach");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, " onCreate");
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_my_music, container, false);
        initViews(rootView);

        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                MusicController.getInstance(getActivity().getApplicationContext()).getMyMusic().getMyMusicArray(),
                R.layout.mymusic_grid_item,
                new String[]{
                        MyMusic.IMAGE_KEY,
                        MyMusic.TITLE_KEY
                },
                new int[]{
                        R.id.image_grid_item,
                        R.id.text_grid_item_title
                }
        );
        adapter.setViewBinder(new NewViewBinder());
        gvMyMusic.setAdapter(adapter);

        return rootView;
    }

    private void initViews(View rootView) {
        gvMyMusic = (GridView) rootView.findViewById( R.id.gvMyMusic);
        gvMyMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((String) getItem(position).get(MyMusic.CONTENT_KEY)) {
                    case MyMusic.CONTENT_TRACKLIST:
                        onGridItemSelectedListener.requestTracklist(
                                (String) getItem(position).get(MyMusic.ITEM_KEY),
                                (String) getItem(position).get(MyMusic.TITLE_KEY),
                                (String) getItem(position).get(MyMusic.TRACKLIST_TYPE_KEY),
                                (Long) getItem(position).get(MyMusic.ID_KEY)
                        );
                        break;
                    case MyMusic.CONTENT_PLAYLISTS:
                        onGridItemSelectedListener.requestPlaylists();
                        break;
                    case MyMusic.CONTENT_ARTISTS:
                        onGridItemSelectedListener.requestArtists();
                        break;
                    case MyMusic.CONTENT_ALBUMS:
                        onGridItemSelectedListener.requestAlbums();
                        break;
                }
            }
        });
    }

    private HashMap<String, Object> getItem(int position){
        return MusicController.getInstance(getActivity().getApplicationContext()).getMyMusic().getMyMusicItem(position);
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
        onGridItemSelectedListener.setTitleActionBar(getString(R.string.my_music));
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
        Log.d(TAG, " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, " onDetach");
    }
}