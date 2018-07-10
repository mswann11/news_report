package com.example.android.newsreport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class StoryLoader extends AsyncTaskLoader<List<Story>> {
    private String url;
    private static final String LOG_TAG = StoryLoader.class.getName();
    private Context context;

    public StoryLoader(Context contextInput, String urlInput){
        super(contextInput);
        context = contextInput;
        url = urlInput;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground(){
        if(url == null){
            return null;
        }

        return QueryUtils.fetchStories(context, url);
    }
}
