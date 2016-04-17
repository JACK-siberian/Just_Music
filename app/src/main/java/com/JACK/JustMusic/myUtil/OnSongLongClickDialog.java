package com.JACK.JustMusic.myUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.JACK.JustMusic.control.MusicController;

public class OnSongLongClickDialog extends DialogFragment implements DialogInterface.OnClickListener {
    final String TAG = "OnSongLongClickDialog";

    private Context context;
    private int position;
    private String title;

    private MusicController musicController;


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicController = MusicController.getInstance(getContext());

        position = getArguments().getInt("position");
        title = getArguments().getString("title");

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(new String[]{"Delete from playback queue", "Delete from SD-Card"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0 :
                                musicController.deleteTrackFromTracklist(position);
                                break;
                            case 1 :
                                Toast.makeText(getContext(), "not available", Toast.LENGTH_LONG).show();//TODo
                                break;
                            default:
                                Log.e(TAG, "which action" + which);
                                break;
                        }
                    }
                })
                .setCancelable(false);

        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}