package com.memy.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.memy.R;
import com.memy.pojo.StoryFeedData;
import com.memy.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryMediaFeedListAdapter extends RecyclerView.Adapter<StoryMediaFeedListAdapter.ViewHolder> {

    private List<StoryFeedData> storyFeedDataList = new ArrayList<>();
    private Context context;
    private Drawable likeNonSelect, likeSelect, commentNonSelect, commentSelect;

    public StoryMediaFeedListAdapter(Context ctx, List<StoryFeedData> list) {
        context = ctx;
        storyFeedDataList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list_adapter, parent, false);
        likeNonSelect = parent.getContext().getResources().getDrawable(R.drawable.ic_like_nonselct);
        likeSelect = parent.getContext().getResources().getDrawable(R.drawable.ic_like_selct);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryFeedData obj = storyFeedDataList.get(position);
        if (obj != null) {
            if ((obj != null) && (obj.getSelf_author() != null)) {
                holder.userNameTextView.setText((!TextUtils.isEmpty(obj.getSelf_author().getFirstname())) ? firstLetterCaps(obj.getSelf_author().getFirstname()) : "");
                holder.likeTextView.setCompoundDrawablesWithIntrinsicBounds(obj.getSelf_author().getLiked() ? likeSelect : likeNonSelect, null, null, null);
                //if (!TextUtils.isEmpty(url)) {
                Glide.with(context)
                        .load(obj.getSelf_author().getPhoto())
                        .centerCrop()
                        .placeholder(R.drawable.ic_profile_male)
                        .error(R.drawable.ic_profile_male)
                        .into(holder.profilePhotoImageView);
                //}
            }
            holder.titleTextView.setText((!TextUtils.isEmpty(obj.getTitle())) ? firstLetterCaps(obj.getTitle()) : "");
            holder.descTextView.setText((!TextUtils.isEmpty(obj.getContent())) ? firstLetterCaps(obj.getContent()) : "");
            holder.likeTextView.setText((obj.getLikes() != 0) ? String.valueOf(obj.getLikes()) : context.getString(R.string.like_label));
            holder.commentTextView.setText((obj.getComments() != 0) ? String.valueOf(obj.getComments()) : context.getString(R.string.comment_label));
            holder.titleTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            holder.postDateTextView.setText(Utils.getFormattedDate(obj.getCreatedOn()));

            MediaViewPager mediaViewPager = new MediaViewPager(context, obj.getFiles());
            holder.mediaViewPager.setAdapter(mediaViewPager);
            new TabLayoutMediator(holder.tabLayout, holder.mediaViewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            // tab.setText("Tab " + (position + 1));
                        }
                    }).attach();
            holder.tabLayout.setVisibility((obj.getFiles().size() > 1) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (storyFeedDataList != null) ? storyFeedDataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView userNameTextView, titleTextView, descTextView, likeTextView, commentTextView, shareTextView, postDateTextView;
        ViewPager2 mediaViewPager;
        TabLayout tabLayout;
        CircleImageView profilePhotoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descTextView = itemView.findViewById(R.id.descTextView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            shareTextView = itemView.findViewById(R.id.shareTextView);
            postDateTextView = itemView.findViewById(R.id.postDateTextView);
            mediaViewPager = itemView.findViewById(R.id.mediaViewPager);
            tabLayout = itemView.findViewById(R.id.tab_layout);
            profilePhotoImageView = itemView.findViewById(R.id.profilePhotoImageView);
        }
    }

    public void updateFeeds(List<StoryFeedData> list) {
        storyFeedDataList = list;
    }

    private String firstLetterCaps(String str) {
        if ((!TextUtils.isEmpty(str)) && (str.length() > 1)) {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }
        return "";
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
       /* MediaViewPager adapter = ((MediaViewPager)holder.mediaViewPager.getAdapter());
        int childCount = holder.mediaViewPager.getChildCount();
        for(int i =0; i < childCount;i++){
            View view = holder.mediaViewPager.getChildAt(i);
            adapter.playMediaPlayerView(view);
        }*/
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        MediaViewPager adapter = ((MediaViewPager) holder.mediaViewPager.getAdapter());
        int childCount = holder.mediaViewPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = holder.mediaViewPager.getChildAt(i);
            adapter.pauseMediaPlayerView(view);
        }
        super.onViewDetachedFromWindow(holder);
    }


    public void pauseMedia(RecyclerView.ViewHolder viewHolder) {
        if ((viewHolder != null) && (viewHolder.itemView != null)) {
            ViewPager2 viewPager2 = viewHolder.itemView.findViewById(R.id.mediaViewPager);
            if (viewPager2 != null) {
                MediaViewPager adapter = ((MediaViewPager) viewPager2.getAdapter());
                if (adapter != null) {
                    int childCount = viewPager2.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View view = viewPager2.getChildAt(i);
                        adapter.pauseMediaPlayerView(view);
                    }
                }
            }
        }
    }
}
