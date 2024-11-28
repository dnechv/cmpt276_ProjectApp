package com.example.memoryconnect.adaptor;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memoryconnect.R;
import com.example.memoryconnect.local_database.PhotoEntry;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<PhotoEntry> events;
    private OnItemClickListener listener;

    // Media type - photo or music
    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_MUSIC = 1;

    public interface OnItemClickListener {
        void onItemClick(PhotoEntry event);
    }

    // Constructor
    public TimelineAdapter(List<PhotoEntry> events, OnItemClickListener listener) {
        this.events = events != null ? events : new ArrayList<>(); // Ensure events is never null
        this.listener = listener;
    }

    //determines view type
    @Override
    public int getItemViewType(int position) {
        PhotoEntry event = events.get(position);

        //get view type
        return (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty())
                ? VIEW_TYPE_MUSIC
                : VIEW_TYPE_PHOTO;
    }

    @NonNull
    @Override


    //inflate layout based on view type
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        if (viewType == VIEW_TYPE_MUSIC) {
            view = LayoutInflater.from(parent.getContext())

                    //music
                    .inflate(R.layout.music_timeline_entry, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())


                    //photo
                    .inflate(R.layout.timeline_entry, parent, false);
        }
        return new TimelineViewHolder(view, viewType);
    }

    //update dynamically based on new events
    public void setEvents(List<PhotoEntry> newEvents) {
        if (newEvents == null || newEvents.isEmpty()) {
            Log.d("TimelineAdapter", "No new events to display.");
            return;
        }

        //deubgging
        Log.d("timeline adapter", "events " + newEvents.size() + " new entries.");

        // Calculate the difference between the old and new events
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return events.size();
            }

            @Override
            public int getNewListSize() {
                return newEvents.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                String oldId = events.get(oldItemPosition).getId();
                String newId = newEvents.get(newItemPosition).getId();
                return oldId != null && newId != null && oldId.equals(newId);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                PhotoEntry oldItem = events.get(oldItemPosition);
                PhotoEntry newItem = newEvents.get(newItemPosition);
                return oldItem != null && newItem != null && oldItem.equals(newItem);
            }
        });




        this.events.clear();
        this.events.addAll(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }



    // Binds the view holder to the data
    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        PhotoEntry event = events.get(position);

        //check view type
        if (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty()) {



            // Bind YouTube data


            //song name

            //set musix title
            holder.musicTitle.setText(event.getSongName());

            //get youtube video id
            String videoId = extractVideoId(event.getYoutubeUrl());




            if (videoId != null) {



                String youtubeThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                Glide.with(holder.itemView.getContext())
                        .load(youtubeThumbnailUrl)
                        .into(holder.musicCover);
            }

            holder.playButton.setOnClickListener(v -> {
                String youtubeUrl = event.getYoutubeUrl();
                if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                    intent.setPackage("com.google.android.youtube");
                    holder.itemView.getContext().startActivity(intent);
                }
            });

        } else if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {


            //bind photo data



            //get title
            holder.photoTitle.setText(event.getTitle());


            //get description
            holder.photoDescription.setText(event.getPhotoDescription());

            //set visibility for description
            holder.photoDescription.setVisibility( View.VISIBLE);



            Glide.with(holder.itemView.getContext())
                    .load(event.getPhotoUrl())
                    .into(holder.photoCover);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    //extract video id from youtube
    private static String extractVideoId(String youtubeUrl) {
        String videoId = null;
        Pattern pattern = Pattern.compile("(?<=v=|/)([A-Za-z0-9_-]{11})");
        Matcher matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    //timeline views
    static class TimelineViewHolder extends RecyclerView.ViewHolder {

        // views
        private ImageView musicCover, photoCover;
        private TextView musicTitle, photoTitle, photoDescription;
        private MaterialButton playButton;

        public TimelineViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            //create view based on media type
            if (viewType == VIEW_TYPE_MUSIC) {
                musicCover = itemView.findViewById(R.id.timelineMusicCover);
                musicTitle = itemView.findViewById(R.id.timelineMusicTitle);
                playButton = itemView.findViewById(R.id.timelinePlayButton);

            } else {
                photoCover = itemView.findViewById(R.id.timelineEventImage);
                photoTitle = itemView.findViewById(R.id.timelineEventTitle);
                photoDescription = itemView.findViewById(R.id.photoDescriptionText);
            }
        }

        //bind to create views
        public void bind(PhotoEntry event, OnItemClickListener listener) {
            if (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty()) {
                // YouTube Binding


                musicTitle.setText(event.getSongName());
            } else if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {


                // Photo Binding


                photoDescription.setText(event.getPhotoDescription());
            }
        }
    }
}