package com.example.android.newsreport;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<Story> fetchStories(Context context, String requestURL) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createURL(requestURL);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return extractFeaturesFromJson(context, jsonResponse);
    }

    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the story JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Story> extractFeaturesFromJson(Context context, String storyJSON) {
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }

        List<Story> stories = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(storyJSON);
            JSONObject response = root.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                String date = formatDate(context, result.getString("webPublicationDate"));
                String url = result.getString("webUrl");

                JSONArray tagsArray = result.getJSONArray("tags");
                String author = null;
                if (tagsArray.length() > 0) {
                    JSONObject firstTag = tagsArray.getJSONObject(0);
                    author = firstTag.getString("webTitle");
                    for (int j = 1; j < tagsArray.length(); j++) {
                        JSONObject tag = tagsArray.getJSONObject(j);
                        author += ", " + tag.getString("webTitle");
                    }
                }

                String section = result.getString("sectionName");

                JSONObject fields = result.getJSONObject("fields");
                Drawable image;
                if(fields.has("thumbnail")) {
                    String imageUrl = fields.getString("thumbnail");
                    image = createDrawable(imageUrl);
                } else{
                    image = context.getResources().getDrawable(R.drawable.guardian_default);
                }
                String title = fields.getString("headline");

                JSONObject blocks = result.getJSONObject("blocks");
                JSONArray bodyArray = blocks.getJSONArray("body");
                String body = bodyArray.getJSONObject(0).getString("bodyTextSummary");

                Story story = new Story(title, date, author, url, section, image, body);
                stories.add(story);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return stories;
    }

    private static Drawable createDrawable(String imageUrl) {
        InputStream inputStream = null;
        try {
            inputStream = (InputStream) new URL(imageUrl).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable image = null;
        if (inputStream != null) {
            image = Drawable.createFromStream(inputStream, "your source link");
        }
        return image;
    }

    private static String formatDate(Context context, String date) {
        String year = date.substring(0, 4);
        String month = getMonth(context, date.substring(5, 7));
        String day = date.substring(8, 10);
        return month + " " + day + ", " + year;
    }

    private static String getMonth(Context context, String month) {
        String monthText;
        switch (month) {
            case "01":
                monthText = context.getResources().getString(R.string.january);
                break;
            case "02":
                monthText = context.getResources().getString(R.string.february);
                break;
            case "03":
                monthText = context.getResources().getString(R.string.march);
                break;
            case "04":
                monthText = context.getResources().getString(R.string.april);
                break;
            case "05":
                monthText = context.getResources().getString(R.string.may);
                break;
            case "06":
                monthText = context.getResources().getString(R.string.june);
                break;
            case "07":
                monthText = context.getResources().getString(R.string.july);
                break;
            case "08":
                monthText = context.getResources().getString(R.string.august);
                break;
            case "09":
                monthText = context.getResources().getString(R.string.september);
                break;
            case "10":
                monthText = context.getResources().getString(R.string.october);
                break;
            case "11":
                monthText = context.getResources().getString(R.string.november);
                break;
            default:
                monthText = context.getResources().getString(R.string.december);
                break;
        }
        return monthText;
    }
}
