package com.memy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.memy.R;
import com.memy.pojo.FeedListFileObj;
import com.memy.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MediaViewPager extends RecyclerView.Adapter<MediaViewPager.Holder> {
    private Context context;
    private ArrayList<FeedListFileObj> mediaList = new ArrayList<>();
    public MediaViewPager(Context ctx, List<FeedListFileObj> list){
        context = ctx;
        mediaList = (ArrayList<FeedListFileObj>) list;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_view_pager_adapter,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        FeedListFileObj obj = mediaList.get(position);
        if((obj != null)) {
            String url = obj.getFile();
            if (!TextUtils.isEmpty(url)) {
                if (Utils.isImageFile(url)) {
                    holder.storyImageView.setVisibility(View.VISIBLE);
                    holder.videoPlayerView.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(url)
                            .centerCrop()
                            .placeholder(R.drawable.img_place_holder)
                            .error(R.drawable.img_place_holder)
                            .into(holder.storyImageView);

                } else if ((Utils.isVideoFile(url)) || ((Utils.isAudioFile(url)))) {
                    holder.videoPlayerView.setVisibility(View.VISIBLE);
                    holder.storyImageView.setVisibility(View.GONE);
                    ExoPlayer player = (ExoPlayer) holder.videoPlayerView.getPlayer();
                    if(player == null) {
                        player = new ExoPlayer.Builder(context).build();
                        MediaItem mediaItem = MediaItem.fromUri(url);
                        player.addMediaItem(mediaItem);
                        player.setPlayWhenReady(true);
                        holder.videoPlayerView.setPlayer(player);
                    }
                    holder.videoPlayerView.setShowNextButton(false);
                    holder.videoPlayerView.setShowPreviousButton(false);
                   // player.prepare();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mediaList != null) ? mediaList.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        AppCompatImageView storyImageView;
        StyledPlayerView videoPlayerView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            storyImageView = itemView.findViewById(R.id.storyImageView);
            videoPlayerView = itemView.findViewById(R.id.videoPlayerView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Holder holder) {
        super.onViewAttachedToWindow(holder);
        playMediaPlayer(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        super.onViewDetachedFromWindow(holder);
        pauseMediaPlayer(holder);
    }

    public void playMediaPlayer( Holder holder){
        if((holder != null) && (holder.videoPlayerView.getVisibility() == View.VISIBLE) && (holder.videoPlayerView.getPlayer() != null)) {
            holder.videoPlayerView.getPlayer().prepare();
        }
    }

    public void pauseMediaPlayer(Holder holder){
        if((holder != null) && (holder.videoPlayerView.getVisibility() == View.VISIBLE) && (holder.videoPlayerView.getPlayer() != null)) {
            holder.videoPlayerView.getPlayer().pause();
        }
    }

    public void pauseMediaPlayerView(View view){
        StyledPlayerView playerView = view.findViewById(R.id.videoPlayerView);
        if((playerView != null) && (playerView.getVisibility() == View.VISIBLE) && (playerView.getPlayer() != null)) {
            playerView.getPlayer().pause();
        }
    }
    public void playMediaPlayerView(View view){
        StyledPlayerView playerView = view.findViewById(R.id.videoPlayerView);
        if((playerView != null) && (playerView.getVisibility() == View.VISIBLE) && (playerView.getPlayer() != null)) {
           // playerView.getPlayer().prepare();
        }
    }
}
