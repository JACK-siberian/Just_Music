package com.JACK.JustMusic.myUtil;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.JACK.JustMusic.R;
import com.JACK.JustMusic.objects.Song;
import com.JACK.JustMusic.objects.Tracklist;

public class TracklistRecyclerViewAdapter extends RecyclerView.Adapter<TracklistRecyclerViewAdapter.ViewHolder>{
    private Tracklist tracklist;
    private Context context;
    private OnTracklistAdapterListener onTracklistAdapterListener;

    public interface OnTracklistAdapterListener {
        void onLongClick(int position);
        void onClickTrack(int position);
    }

    public TracklistRecyclerViewAdapter(Tracklist tracklist, Context context) {
        this.tracklist = tracklist;
        this.context = context;
        this.onTracklistAdapterListener = (OnTracklistAdapterListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.now_playing_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == tracklist.getCurPosition()) {
            holder.ivStatus.setImageResource(android.R.drawable.ic_media_pause);
            holder.textSongArtist.setTextColor(context.getResources().getColor(R.color.colorMyTheme));
            holder.textSongTitle.setTextColor(context.getResources().getColor(R.color.colorMyTheme));
        }
        else {
            holder.ivStatus.setImageResource(android.R.drawable.ic_media_play);
            holder.textSongArtist.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.textSongTitle.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        Song track = tracklist.getTrack(position);
        holder.textSongTitle.setText(track.getTitle());
        holder.textSongArtist.setText(track.getArtist());
        holder.textSongDuration.setText(MyUtil.formatTime(track.getDuration()));
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("onLongClick", "" + position);
                onTracklistAdapterListener.onLongClick(position);
                return true;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTracklistAdapterListener.onClickTrack(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracklist == null ? 0 : tracklist.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textSongTitle;
        TextView textSongArtist;
        TextView textSongDuration;
        ImageView ivStatus;
        ViewHolder(View itemView) {
            super(itemView);
            textSongTitle = (TextView)itemView.findViewById(R.id.textSongTitle);
            textSongArtist = (TextView)itemView.findViewById(R.id.textSongArtist);
            textSongDuration = (TextView)itemView.findViewById(R.id.textSongDuration);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
            cardView = (CardView) itemView.findViewById(R.id.cv);
        }
    }
}