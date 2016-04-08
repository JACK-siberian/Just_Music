package com.JACK.JustMusicWW;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class NowPlayingFragment extends Fragment {
    private final String TAG = "Fragment NowPlaying   ";

    private OnNowPlayingFragmentListener onNowPlayingFragmentListener;
    public interface OnNowPlayingFragmentListener {
        void openPlayerFragment();
        void setTitleActionBar( String title);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onNowPlayingFragmentListener = (OnNowPlayingFragmentListener) activity;
        Log.d(TAG, " onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate");
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_now_playing_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_player :
                onNowPlayingFragmentListener.openPlayerFragment();
                return true;
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate( R.layout.fragment_with_listview, container, false);
        onNowPlayingFragmentListener.setTitleActionBar( getString(R.string.now_playing)
                + " [ " + 1 +" / " + 7 + " ]");
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        ListView lvTracklist = (ListView) rootView.findViewById(R.id.listView);
        lvTracklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        lvTracklist.setAdapter(null); // TODO
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, " onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, " onStop");
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