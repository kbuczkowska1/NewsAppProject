package com.example.android.newsappproject;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Kasia on 2017-07-13.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private final String mUrl;

    public NewsLoader(Context context, String Url) {
        super(context);
        mUrl = Url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) return null;
        return Utils.fetchData(mUrl);
    }
}