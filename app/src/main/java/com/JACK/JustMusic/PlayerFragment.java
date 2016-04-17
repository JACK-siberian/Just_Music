package com.JACK.JustMusic;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.JACK.JustMusic.myUtil.ImageCoversPagerAdapter;
import com.JACK.JustMusic.objects.Song;

import java.util.ArrayList;

public class PlayerFragment extends Fragment {
    final String TAG = "Fragment Player   ";

    private TextView textArtist;
    private TextView textTitle;
    private TextView textAlbum;
    private ViewPager pagerImageCovers;
    private ImageCoversPagerAdapter imageCoversPagerAdapter;
    private boolean changedByUser = true;

    private OnPlayerFragmentListener onPlayerFragmentListener;

    public interface OnPlayerFragmentListener {
        void openNowPlayerFragment();
        void requestRefreshViews();
        void requestRefreshCoversData();
        void setTitleActionBar( String title);
        void changeTrack(int position);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("JustMusic");
        onPlayerFragmentListener = (OnPlayerFragmentListener) activity;
        Log.d(TAG, " onAttach");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, " onCreate");
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_player_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_now_playing :
                onPlayerFragmentListener.openNowPlayerFragment();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        onPlayerFragmentListener.setTitleActionBar(getString(R.string.app_full_name));
        initViews(rootView);
        onPlayerFragmentListener.requestRefreshViews();
        return rootView;
    }

    private void initViews(View rootView) {
        textArtist = (TextView) rootView.findViewById(R.id.textArtist);
        textTitle = (TextView) rootView.findViewById(R.id.textTitle);
        textAlbum = (TextView) rootView.findViewById(R.id.textAlbum);
        pagerImageCovers = (ViewPager) rootView.findViewById(R.id.pagerImageCovers);
        pagerImageCovers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (changedByUser)
                    onPlayerFragmentListener.changeTrack(position);
                changedByUser = true;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void refreshCoversAdapter(ArrayList<Uri> covers) {
        Log.e(TAG,"refreshCoversAdapter");
        imageCoversPagerAdapter = new ImageCoversPagerAdapter(
                getContext(),
                covers
        );
        if (pagerImageCovers != null)
            pagerImageCovers.setAdapter(imageCoversPagerAdapter);
    }

    public void refreshView(Song curSong, int position, boolean smoothScroll) {
        Log.e(TAG,"refreshView");
        if ( pagerImageCovers != null) {
            textArtist.setText(curSong.getArtist());
            textTitle.setText(curSong.getTitle());
            textAlbum.setText(curSong.getAlbum());

            if (pagerImageCovers.getCurrentItem() != position) {
                changedByUser = false;
                pagerImageCovers.setCurrentItem(position, smoothScroll);
            }
        }
    }

    public void disableView() {
        textTitle.setText(getString(R.string.curTracklistNull));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, " onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        onPlayerFragmentListener.requestRefreshCoversData();
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