package com.example.android.newsreport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;
    //private static String REQUEST_URL = "https://content.guardianapis.com/search?section=world|uk-news|us-news&show-tags=contributor&show-fields=headline,thumbnail&show-blocks=body&order-by=newest&api-key=7868c9a0-e952-4413-94ce-6a2f8520c683";
    private static String REQUEST_URL = "https://content.guardianapis.com/search?api-key=7868c9a0-e952-4413-94ce-6a2f8520c683&show-tags=contributor&show-fields=headline,thumbnail&show-blocks=body";
    private StoryAdapter adapter;
    private TextView emptyView;
    private ProgressBar progressBar;
    private ImageView guardianText;
    private LoaderManager loaderManager;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loaderManager.destroyLoader(LOADER_ID);
                Log.e(LOG_TAG, "destroyLoader called");
                loadData();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView storyListView = findViewById(R.id.list);
        adapter = new StoryAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(adapter);

        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Story currentStory = adapter.getItem(position);
                Uri storyUri = Uri.parse(currentStory.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);
                startActivity(websiteIntent);
            }
        });

        emptyView = findViewById(R.id.empty);
        storyListView.setEmptyView(emptyView);
        progressBar = findViewById(R.id.progress);
        guardianText = findViewById(R.id.guardian_text);
        loaderManager = getLoaderManager();
        loadData();
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "onCreateLoader called");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String topic = sharedPrefs.getString(
            getString(R.string.settings_topic_key),
            getString(R.string.settings_topic_default));
        String search = sharedPrefs.getString(
            getString(R.string.settings_search_key),
            getString(R.string.settings_search_default));
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("section", topic);
        if(!search.equals("")) {
            uriBuilder.appendQueryParameter("q", search);
        }
        uriBuilder.appendQueryParameter("order-by", orderBy);

        Log.e(LOG_TAG, uriBuilder.toString());
        return new StoryLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        Log.e(LOG_TAG, "onLoadFinished called");
        if (stories != null && !stories.isEmpty()) {
            adapter.addAll(stories);
        }
        emptyView.setText(R.string.no_stories);
        progressBar.setVisibility(View.GONE);
        guardianText.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        Log.e(LOG_TAG, "onLoaderReset called");
        adapter.clear();
    }

    public boolean checkNetworkConnection() {
        Log.e(LOG_TAG, "checkNetworkConnection called");
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void loadData() {
        emptyView.setText("");
        Log.e(LOG_TAG, "refresh called");
        progressBar.setVisibility(View.VISIBLE);
        guardianText.setVisibility(View.VISIBLE);
        if (checkNetworkConnection()) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            guardianText.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet);
        }
    }
}
