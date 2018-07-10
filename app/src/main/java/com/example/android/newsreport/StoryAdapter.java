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

        TextView title = listItemView.findViewById(R.id.title);
        title.setText(currentStory.getTitle());

        TextView author = listItemView.findViewById(R.id.author);
        if (currentStory.getAuthor() != null) {
            author.setText(currentStory.getAuthor());
        } else {
            author.setVisibility(View.GONE);
        }

        TextView date = listItemView.findViewById(R.id.date);
        date.setText(currentStory.getDate());

        TextView section = listItemView.findViewById(R.id.section);
        section.setTextColor(ContextCompat.getColor(getContext(), R.color.background));
        String category = currentStory.getSection();

        switch (category) {
            case "Science":
                section.setBackgroundResource(R.color.science);
                break;
            case "Books":
                section.setBackgroundResource(R.color.books);
                break;
            case "US news":
                section.setBackgroundResource(R.color.us);
                break;
            case "Business":
                section.setBackgroundResource(R.color.business);
                break;
            case "World news":
                section.setBackgroundResource(R.color.world);
                break;
            case "Environment":
                section.setBackgroundResource(R.color.environment);
                break;
            case "Politics":
                section.setBackgroundResource(R.color.politics);
                break;
            case "Society":
                section.setBackgroundResource(R.color.society);
                break;
            case "Music":
                section.setBackgroundResource(R.color.music);
                break;
            default:
                section.setBackgroundResource(R.color.background);
                section.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
        }

        section.setText(currentStory.getSection());
        ImageView thumbnail = listItemView.findViewById(R.id.thumbnail);
        thumbnail.setImageDrawable(currentStory.getImage());

        TextView body = listItemView.findViewById(R.id.body);
        body.setText(currentStory.getBody());

        return listItemView;
    }
}
