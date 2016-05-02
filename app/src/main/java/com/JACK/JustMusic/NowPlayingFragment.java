package com.JACK.JustMusic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.JACK.JustMusic.myUtil.TracklistRecyclerViewAdapter;
import com.JACK.JustMusic.objects.Tracklist;

public class NowPlayingFragment extends Fragment {
    private final String TAG = "Fragment NowPlaying   ";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TracklistRecyclerViewAdapter tracklistRecyclerViewAdapter;
    private Context context;

    private OnNowPlayingFragmentListener onNowPlayingFragmentListener;
    public interface OnNowPlayingFragmentListener {
        void openPlayerFragment();
        void requestRefreshViews();
        void requestRefreshTracklistData();
        void setTitleActionBar( String title);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onNowPlayingFragmentListener = (OnNowPlayingFragmentListener) activity;
        context = activity;
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
        View rootView = inflater.inflate( R.layout.fragment_with_recylerview, container, false);
        initViews(rootView);
        onNowPlayingFragmentListener.requestRefreshTracklistData();
        onNowPlayingFragmentListener.requestRefreshViews();
        return rootView;
    }

    private void initViews(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void refreshTracklistAdapter(Tracklist tracklist) {
        Log.e(TAG,"refreshTracklistAdapter");
        tracklistRecyclerViewAdapter = new TracklistRecyclerViewAdapter(
                tracklist,
                context
        );
        if (recyclerView != null)
            recyclerView.setAdapter(tracklistRecyclerViewAdapter);
        else
            Log.e(TAG, "refreshTracklistAdapter()    recyclerView == null");
    }

    public void refreshView(int position) {
        Log.e(TAG,"refreshView");
        if ( recyclerView != null) {
            onNowPlayingFragmentListener.setTitleActionBar(
                            (position + 1) +
                            "/" +
                            tracklistRecyclerViewAdapter.getItemCount() +
                            "  " +
                            getString(R.string.now_playing)
            );

            linearLayoutManager.scrollToPosition(position);
            tracklistRecyclerViewAdapter.notifyDataSetChanged();
        }
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