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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memoryconnect.R;
import com.example.memoryconnect.data_model.TimelineEntry;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//this is almost app1 adapter logic
//two xmls -> inflates based on the link type

public class PatientTimelineAdapter extends RecyclerView.Adapter<PatientTimelineAdapter.TimelineViewHolder> {

    private List<TimelineEntry> timelineEntries;

    //constructor
    public PatientTimelineAdapter(List<TimelineEntry> timelineEntries) {
        this.timelineEntries = timelineEntries;
    }

    //view types to differentiate adapter views
    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_YOUTUBE = 1;

    @Override
    public int getItemViewType(int position) {
        TimelineEntry entry = timelineEntries.get(position);

        //check view time
        if (entry.getYoutubeLink() != null && !entry.getYoutubeLink().isEmpty()) {
            return VIEW_TYPE_YOUTUBE;
        } else {
            return VIEW_TYPE_PHOTO;
        }
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        //inflate layout based on view type
        if (viewType == VIEW_TYPE_YOUTUBE) {


            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_entry_timeline, parent, false);
        } else {


            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_entry, parent, false);
        }

        return new TimelineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineEntry entry = timelineEntries.get(position);

        //set view data based on view type
        if (holder.viewType == VIEW_TYPE_YOUTUBE) {


            //set song name
            holder.songNameView.setText(entry.getSongName());

            //get video id
            String videoId = extractVideoId(entry.getYoutubeLink());
            if (videoId != null) {

                //load image from youtube
                String youtubeThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";

                //load image with glide
                Glide.with(holder.itemView.getContext())
                        .load(youtubeThumbnailUrl)
                        .into(holder.timelineImage);

                //open youtube
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getYoutubeLink()));
                    intent.setPackage("com.google.android.youtube");
                    holder.itemView.getContext().startActivity(intent);
                });
            }


            //if photo
        } else if (holder.viewType == VIEW_TYPE_PHOTO) {

            //set title + description
            holder.timelineTitle.setText(entry.getTitle());
            holder.photoDescriptionView.setText(entry.getPhotoDescription());


            //do photo stuff with glide
            if (entry.getPhotoUrl() != null && !entry.getPhotoUrl().isEmpty()) {



                Glide.with(holder.itemView.getContext())
                        .load(entry.getPhotoUrl())
                        .into(holder.timelineImage);
            }


        }
    }

    @Override
    public int getItemCount() {
        return timelineEntries.size();
    }

    //viewholder to hold views
    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        private TextView timelineTitle;
        private ImageView timelineImage;
        private int viewType;
        private TextView songNameView;
        private TextView photoDescriptionView;

        public TimelineViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            // views are assigned based on types

            if (viewType == VIEW_TYPE_YOUTUBE) {


                songNameView = itemView.findViewById(R.id.timelineMusicTitle);


                timelineImage = itemView.findViewById(R.id.timelineMusicCover);

                if (songNameView == null || timelineImage == null) {
                    Log.e("TimelineAdapter", "YouTube layout views not found");
                }


            } else {
                timelineTitle = itemView.findViewById(R.id.timelineEventTitle);
                timelineImage = itemView.findViewById(R.id.timelineEventImage);
                photoDescriptionView = itemView.findViewById(R.id.patientTimelineFragmentTitle);

                if (timelineTitle == null || timelineImage == null || photoDescriptionView == null) {
                    Log.e("TimelineAdapter", "Photo layout views not found");
                }
            }
        }
    }

    //extract video id from youtube link
    private static String extractVideoId(String youtubeUrl) {
        String videoId = null;
        Pattern pattern = Pattern.compile("(?<=v=|/)([A-Za-z0-9_-]{11})");
        Matcher matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }
}