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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;
    private static String REQUEST_URL = "https://content.guardianapis.com/search?api-key=7868c9a0-e952-4413-94ce-6a2f8520c683&show-tags=contributor&show-fields=headline,thumbnail&show-blocks=body";
    private StoryAdapter adapter;
    private LinearLayout emptyLayout;
    private TextView emptyTitleTextView;
    private TextView emptyInfoTextView;
    private ImageView emptyImageView;
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

        emptyLayout = findViewById(R.id.empty);
        emptyTitleTextView = findViewById(R.id.empty_title_text_view);
        emptyInfoTextView = findViewById(R.id.empty_info_text_view);
        emptyImageView = findViewById(R.id.empty_image_view);
        storyListView.setEmptyView(emptyLayout);
        progressBar = findViewById(R.id.progress);
        guardianText = findViewById(R.id.guardian_image_view);
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
        String fromDate = sharedPrefs.getString(
            getString(R.string.settings_from_date_key),
            getString(R.string.settings_from_date_default));
        String orderBy  = sharedPrefs.getString(
            getString(R.string.settings_order_by_key),
            getString(R.string.settings_order_by_default));
        String toDate = sharedPrefs.getString(
            getString(R.string.settings_to_date_key),
            getTodaysDate());
        Boolean switchValue = sharedPrefs.getBoolean(
            getString(R.string.settings_switch_key),
            Boolean.valueOf(getString(R.string.settings_switch_default)));

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("section", topic);
        if(!fromDate.equals("")) {
            uriBuilder.appendQueryParameter("from-date", fromDate);
        }
        if(switchValue){
            uriBuilder.appendQueryParameter("to-date", getTodaysDate());
        } else {
            uriBuilder.appendQueryParameter("to-date", toDate);
        }
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
        emptyTitleTextView.setText(R.string.no_stories_title);
        emptyInfoTextView.setText(R.string.no_stories_info);
        emptyImageView.setImageResource(R.drawable.ic_library_books);
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
        emptyTitleTextView.setText("");
        emptyInfoTextView.setText("");
        emptyImageView.setImageResource(0);
        Log.e(LOG_TAG, "refresh called");
        progressBar.setVisibility(View.VISIBLE);
        guardianText.setVisibility(View.VISIBLE);
        if (checkNetworkConnection()) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            guardianText.setVisibility(View.GONE);
            emptyTitleTextView.setText(R.string.no_internet_title);
            emptyInfoTextView.setText(R.string.no_internet_info);
            emptyImageView.setImageResource(R.drawable.ic_signal_wifi_off);
        }
    }

    public String getTodaysDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
