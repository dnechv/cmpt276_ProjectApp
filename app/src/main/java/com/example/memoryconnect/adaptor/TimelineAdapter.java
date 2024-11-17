package com.example.memoryconnect.adaptor;


//manages the data in the UI for the recycler view



//imports
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


    //variable for the media type
    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_MUSIC = 1;


    //placeholders
    List<PhotoEntry> hardcodedEntries = new ArrayList<>();



    public interface OnItemClickListener {
        void onItemClick(PhotoEntry event);
    }

    public TimelineAdapter(List<PhotoEntry> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;


        //placehoders - TODO  Pull data fromfirebase
        events.add(new PhotoEntry("1", "Test 1", "https://images.unsplash.com/photo-1503614472-8c93d56e92ce", "patientId1", System.currentTimeMillis()));


        events.add(new PhotoEntry("2", "Test 2", "https://images.unsplash.com/photo-1533094692971-5f4c56ec1339", "patientId2", System.currentTimeMillis()));

        events.add(new PhotoEntry("2", "YouTube Test", "patientId2", "2024-11-16", null, "https://www.youtube.com/watch?v=dQw4w9WgXcQ", System.currentTimeMillis()));


        events.add(new PhotoEntry("4", "Test 4", "https://images.unsplash.com/photo-1454111079122-eb76eb2d5754", "patientId4", System.currentTimeMillis()));

    }

    // method determines the view type for the entry
    @Override
    public int getItemViewType(int position) {
        PhotoEntry event = events.get(position);
        return (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty())
                ? VIEW_TYPE_MUSIC
                : VIEW_TYPE_PHOTO;
    }

    @NonNull
    @Override

    //creates view -> determines the type dynamically
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MUSIC) {
            view = LayoutInflater.from(parent.getContext())

                    //use music xml layout
                    .inflate(R.layout.music_timeline_entry, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())

                    //use regular xml layout
                    .inflate(R.layout.timeline_entry, parent, false);
        }
        return new TimelineViewHolder(view, viewType);
    }

    //binds the view holder to the data
    public void setEvents(List<PhotoEntry> newEvents) {
        if (newEvents == null || newEvents.isEmpty()) {


            Log.d("TimelineAdapter", "No new events to display.");
            return;
        }

        Log.d("TimelineAdapter", "Updating events with " + newEvents.size() + " new entries.");

        // Use DiffUtil to calculate the differences and update the RecyclerView efficiently
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return events.size();
            }

            @Override
            public int getNewListSize() {
                return newEvents.size();
            }


            //check if items are the same
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // Assume PhotoEntry has a unique ID to compare
                return events.get(oldItemPosition).getId().equals(newEvents.get(newItemPosition).getId());
            }


            //check if contents are the same
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                return events.get(oldItemPosition).equals(newEvents.get(newItemPosition));
            }
        });

        this.events.clear();
        this.events.addAll(newEvents);

        diffResult.dispatchUpdatesTo(this); // Efficiently update only changed items
    }


    ///binds the view holder to the data -> loads the image with glide
    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {


        //get the photo from the list
        PhotoEntry event = events.get(position);

        // bind the view data
        holder.bind(event, listener);

        //load the image fro the URL
        if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {




            Log.d("TimelineAdapter", "Loading image from URL: " + event.getPhotoUrl());

            //load image with glide
            Glide.with(holder.itemView.getContext())

                    //pass the photo URL
                    .load(event.getPhotoUrl())


                    //set the image to the view
                    .into(holder.photoCover);
        } else {




            Log.e("TimelineAdapter", "Photo URL is empty or null");



        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    //extracts youtube music id
    private static String extractVideoId(String youtubeUrl) {

        String videoId = null;

        Pattern pattern = Pattern.compile("(?<=v=|/)([A-Za-z0-9_-]{11})");

        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {

            videoId = matcher.group(1);

        }
        return videoId;
    }


    //timeline view holder class -manges and displays entries
    static class TimelineViewHolder extends RecyclerView.ViewHolder {


        //variables
        private ImageView musicCover, photoCover;
        private TextView musicTitle, photoTitle;
        private MaterialButton playButton;



        public TimelineViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            //initialize views based on the type
            if (viewType == VIEW_TYPE_MUSIC) {


                musicCover = itemView.findViewById(R.id.timelineMusicCover);
                musicTitle = itemView.findViewById(R.id.timelineMusicTitle);
                playButton = itemView.findViewById(R.id.timelinePlayButton);


            } else {


                photoCover = itemView.findViewById(R.id.timelineEventImage);
                photoTitle = itemView.findViewById(R.id.timelineEventTitle);
            }
        }


        //bind method -> handles both music and photos entries
        //dynamically selects which to bind based on the media type
        //usses glide for image loading


        public void bind(PhotoEntry event, OnItemClickListener listener) {
            if (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty()) {
                // Handle YouTube music entry
                musicTitle.setText(event.getTitle());

                // Extract the video ID from the YouTube URL
                String videoId = extractVideoId(event.getYoutubeUrl());
                if (videoId != null) {
                    // Load YouTube thumbnail
                    String youtubeThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                    Glide.with(itemView.getContext())
                            .load(youtubeThumbnailUrl) // Load thumbnail from YouTube
                            .into(musicCover);
                }

                playButton.setOnClickListener(v -> {
                    String youtubeUrl = event.getYoutubeUrl();

                    if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
                        try {


                            //autoplay
                            Uri uriWithAutoplay = Uri.parse(youtubeUrl).buildUpon()
                                    .appendQueryParameter("autoplay", "1")
                                    .build();


                           //open youtube aopp for music
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));

                            intent.setPackage("com.google.android.youtube");

                            itemView.getContext().startActivity(intent);
                        } catch (Exception e) {
                            //backup - open in browser
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));

                            itemView.getContext().startActivity(intent);
                        }
                        listener.onItemClick(event);
                    } else {
                        Log.e("TimelineAdapter", "YouTube URL is null or empty");
                    }
                });

            } else {


                //regular photpo em
                photoTitle.setText(event.getTitle());

                Log.d("TimelineAdapter", "Loading image from URL: " + event.getPhotoUrl());

                Glide.with(itemView.getContext())
                        .load("https://images.unsplash.com/photo-1503614472-8c93d56e92ce")
                        .into(photoCover);

                itemView.setOnClickListener(v -> listener.onItemClick(event));
            }
        }
    }
}