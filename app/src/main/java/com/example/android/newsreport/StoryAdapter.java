package com.example.android.newsreport;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Activity context, ArrayList<Story> storyArrayList) {
        super(context, 0, storyArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_item, parent, false);
        }

        Story currentStory = getItem(position);

        TextView titleNewsTextView = listItemView.findViewById(R.id.title_text_view);
        titleNewsTextView.setText(currentStory.getTitle());

        TextView authorNewsTextView = listItemView.findViewById(R.id.author_text_view);
        if (currentStory.getAuthor() != null) {
            authorNewsTextView.setText(currentStory.getAuthor());
        } else {
            authorNewsTextView.setVisibility(View.GONE);
        }

        TextView dateNewsTextView = listItemView.findViewById(R.id.date_text_view);
        dateNewsTextView.setText(currentStory.getDate());

        TextView sectionNewsTextView = listItemView.findViewById(R.id.section_text_view);
        sectionNewsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.background));
        String category = currentStory.getSection();

        switch (category) {
            case "Science":
                sectionNewsTextView.setBackgroundResource(R.color.science);
                break;
            case "Books":
                sectionNewsTextView.setBackgroundResource(R.color.books);
                break;
            case "US news":
                sectionNewsTextView.setBackgroundResource(R.color.us);
                break;
            case "Business":
                sectionNewsTextView.setBackgroundResource(R.color.business);
                break;
            case "World news":
                sectionNewsTextView.setBackgroundResource(R.color.world);
                break;
            case "Environment":
                sectionNewsTextView.setBackgroundResource(R.color.environment);
                break;
            case "Politics":
                sectionNewsTextView.setBackgroundResource(R.color.politics);
                break;
            case "Society":
                sectionNewsTextView.setBackgroundResource(R.color.society);
                break;
            case "Music":
                sectionNewsTextView.setBackgroundResource(R.color.music);
                break;
            default:
                sectionNewsTextView.setBackgroundResource(R.color.background);
                sectionNewsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
        }

        sectionNewsTextView.setText(currentStory.getSection());
        ImageView thumbnailNewsImageView = listItemView.findViewById(R.id.thumbnail_image_view);
        thumbnailNewsImageView.setImageDrawable(currentStory.getImage());

        TextView bodyNewsTextView = listItemView.findViewById(R.id.body_text_view);
        bodyNewsTextView.setText(currentStory.getBody());

        return listItemView;
    }
}
