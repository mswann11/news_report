package com.example.android.newsreport;

import android.graphics.drawable.Drawable;

public class Story {
    private String title;
    private String date;
    private String author;
    private String url;
    private String section;
    private Drawable image;
    private String body;

    public Story(String titleInput, String dateInput, String authorInput, String urlInput,
                 String sectionInput, Drawable imageInput, String bodyInput) {
        title = titleInput;
        date = dateInput;
        author = authorInput;
        url = urlInput;
        section = sectionInput;
        image = imageInput;
        body = bodyInput;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() {
        return section;
    }

    public Drawable getImage() {
        return image;
    }

    public String getBody() {
        return body;
    }
}
