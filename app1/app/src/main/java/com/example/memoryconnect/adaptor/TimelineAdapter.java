package com.example.memoryconnect.adaptor;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memoryconnect.R;
import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.youtube_view;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private final List<PhotoEntry> events;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PhotoEntry event);
    }

    public TimelineAdapter(List<PhotoEntry> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_entry, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        PhotoEntry event = events.get(position);
        holder.bind(event, listener);

        // Load Photo URL if available
        if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
            holder.eventImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(event.getPhotoUrl())
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setVisibility(View.GONE);
        }

        // Load YouTube Thumbnail if available
        if (event.getYoutubeUrl() != null && !event.getYoutubeUrl().isEmpty()) {
            holder.youtubeThumbnail.setVisibility(View.VISIBLE);
            String youtubeThumbnailUrl = "https://img.youtube.com/vi/" + extractYouTubeId(event.getYoutubeUrl()) + "/0.jpg";
            Glide.with(holder.itemView.getContext())
                    .load(youtubeThumbnailUrl)
                    .into(holder.youtubeThumbnail);

            holder.youtubeThumbnail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getYoutubeUrl()));
                holder.itemView.getContext().startActivity(intent);
            });
        } else {
            holder.youtubeThumbnail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<PhotoEntry> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {

        private final ImageView eventImage;
        private final ImageView youtubeThumbnail;
        private final TextView eventTitle;
        private final TextView eventDate;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.timelineEventImage);
            youtubeThumbnail = itemView.findViewById(R.id.timelineYouTubeThumbnail);
            eventTitle = itemView.findViewById(R.id.timelineEventTitle);
            eventDate = itemView.findViewById(R.id.timelineEventDate);
        }

        public void bind(PhotoEntry event, OnItemClickListener listener) {
            eventTitle.setText(event.getTitle());
            eventDate.setText(new Date(event.getTimeWhenPhotoAdded()).toString()); // Format as needed
            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }

    private String extractYouTubeId(String youtubeUrl) {
        String regex = "^(?:https?://)?(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeUrl);
        return matcher.find() ? matcher.group(1) : "";
    }
}

