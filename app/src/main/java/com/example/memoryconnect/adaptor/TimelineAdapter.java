package com.example.memoryconnect.adaptor;

// Manages the data in the UI for the RecyclerView

// Imports
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

    //media type - photo or music
    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_MUSIC = 1;

    public interface OnItemClickListener {
        void onItemClick(PhotoEntry event);
    }

    //adapter
    public TimelineAdapter(List<PhotoEntry> events, OnItemClickListener listener) {
        this.events = events != null ? events : new ArrayList<>(); // Ensure events is never null
        this.listener = listener;
    }

    //gets the view type
    @Override
    public int getItemViewType(int position) {

        //gets the event at the position
        PhotoEntry event = events.get(position);

        //returns the view type based on the event
        return (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty())
                ? VIEW_TYPE_MUSIC
                : VIEW_TYPE_PHOTO;
    }

    @NonNull
    @Override

    // Creates view dynamically based on type
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MUSIC) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.music_timeline_entry, parent, false); // Music layout
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.timeline_entry, parent, false); // Photo layout
        }
        return new TimelineViewHolder(view, viewType);
    }

    // Updates the events dynamically with Firebase data
    public void setEvents(List<PhotoEntry> newEvents) {


        if (newEvents == null || newEvents.isEmpty()) {


            Log.d("TimelineAdapter", "No new events to display.");


            return;
        }

        // Log the number of events being updated
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

    // Binds the ViewHolder to the data
    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        PhotoEntry event = events.get(position);

        // check for presence of both
        if ((event.getPhotoUrl() == null || event.getPhotoUrl().isEmpty()) &&


                (event.getYoutubeUrl() == null || event.getYoutubeUrl().isEmpty())) {



            Log.e("TimelineAdapter", "Both Photo URL and YouTube URL are missing for event: " + event.getId());
            return;
        }

        // Bind the view data
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // get youtube id
    private static String extractVideoId(String youtubeUrl) {

        String videoId = null;


        Pattern pattern = Pattern.compile("(?<=v=|/)([A-Za-z0-9_-]{11})");


        Matcher matcher = pattern.matcher(youtubeUrl);


        if (matcher.find()) {
            videoId = matcher.group(1);


        }
        return videoId;
    }

    // Timeline ViewHolder class manages and displays entries
    static class TimelineViewHolder extends RecyclerView.ViewHolder {

        // Variables for views
        private ImageView musicCover, photoCover;
        private TextView musicTitle, photoTitle;
        private MaterialButton playButton;

        public TimelineViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            // Initialize views based on the type
            if (viewType == VIEW_TYPE_MUSIC) {


                musicCover = itemView.findViewById(R.id.timelineMusicCover);
                musicTitle = itemView.findViewById(R.id.timelineMusicTitle);
                playButton = itemView.findViewById(R.id.timelinePlayButton);


            } else {


                photoCover = itemView.findViewById(R.id.timelineEventImage);
                photoTitle = itemView.findViewById(R.id.timelineEventTitle);


            }
        }

        // Bind method handles both music and photo entries
        public void bind(PhotoEntry event, OnItemClickListener listener) {
            if (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty()) {


                // Handle YouTube music entry
                musicTitle.setText(event.getTitle());


                String videoId = extractVideoId(event.getYoutubeUrl());


                if (videoId != null) {


                    String youtubeThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";

                    //load cover
                    Glide.with(itemView.getContext())
                            .load(youtubeThumbnailUrl)
                            .into(musicCover);
                }

                playButton.setOnClickListener(v -> {


                    String youtubeUrl = event.getYoutubeUrl();


                    if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
                        try {


                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                            intent.setPackage("com.google.android.youtube");
                            itemView.getContext().startActivity(intent);


                        } catch (Exception e) {


                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                            itemView.getContext().startActivity(intent);
                        }
                    } else {


                        Log.e("TimelineAdapter", "YouTube URL is null or empty");


                    }
                });

            } else if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
                //if not use regular photo netry
                photoTitle.setText(event.getTitle());
                Glide.with(itemView.getContext())
                        .load(event.getPhotoUrl())
                        .into(photoCover);
                itemView.setOnClickListener(v -> listener.onItemClick(event));
            }
        }
    }
}