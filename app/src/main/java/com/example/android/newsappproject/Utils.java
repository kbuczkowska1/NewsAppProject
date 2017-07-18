package com.example.android.newsappproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * Created by Kasia on 2017-07-12.
 */

// Utility class with methods to help perform the HTTP request and parse the response.
public class Utils {

    // Tag for the log messages.
    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static final String NULL_STRING = "";
    private static String mAuthor;

    private Utils() {
    }

    public static List<News> fetchData(String queryUrl) {
        URL url = generateURL(queryUrl);
        String jsonResponse = NULL_STRING;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return parseJSON(jsonResponse, mAuthor);
    }

    private static URL generateURL(String queryString) {
        URL url = null;
        try {
            url = new URL(queryString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error url", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = NULL_STRING;

        if (url == null) return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream iStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                iStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(iStream);
            } else {
                Log.e(LOG_TAG, "Connection error: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem JSON result", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (iStream != null) iStream.close();
        }
        return jsonResponse;
    }

    //Convert the {@link InputStream} into a String which contains the whole JSON response from th server.
    private static String readFromStream(InputStream iStream) throws IOException {
        StringBuilder out = new StringBuilder();
        if (iStream != null) {
            InputStreamReader iStreamReader =
                    new InputStreamReader(iStream, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(iStreamReader);
            String line = buffer.readLine();
            while (line != null) {
                out.append(line);
                line = buffer.readLine();
            }
        }
        return out.toString();
    }

    private static Bitmap getBitmapFromUrl(String bitmapUrlString) {
        if (bitmapUrlString.isEmpty()) return null;
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        InputStream iStream = null;
        try {
            URL url = generateURL(bitmapUrlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                iStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(iStream);
            } else {
                Log.e(LOG_TAG, "Connection error: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting bitmap", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (iStream != null) {
                try {
                    iStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing input stream", e);
                }
            }
        }
        return bitmap;
    }

    private static List<News> parseJSON(String jsonString, String mAuthor) {
        if (TextUtils.isEmpty(jsonString)) return null;
        List<News> out = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray results = root.optJSONObject("response").optJSONArray("results");
            if (results == null) return null;
            for (int i = 0; i < results.length(); ++i) {
                JSONObject currentNews = results.getJSONObject(i);
                final String title = currentNews.optString("webTitle");
                final String webUrl = currentNews.optString("webUrl");
                final String sectionName = currentNews.optString("sectionName");
                final String date = currentNews.optString("webPublicationDate");
                JSONObject fields = currentNews.optJSONObject("fields");

                News tmpNews = new News(title, mAuthor, webUrl, sectionName, date);
                if (fields != null) {
                    tmpNews.setmAuthor(fields.optString("byline"));
                    tmpNews.setmTrailText(fields.optString("trailText"));
                    tmpNews.setmThumbnailBitmap(getBitmapFromUrl(fields.optString("thumbnail")));
                }
                out.add(tmpNews);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "problem parsing json", e);
        }
        return out;
    }
}