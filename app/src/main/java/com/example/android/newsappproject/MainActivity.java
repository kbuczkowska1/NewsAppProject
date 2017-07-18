package com.example.android.newsappproject;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String SEARCH_QUERY_KEY = "query";
    private static final int LOADER_ID = 1;
    private TextView mEmptyStateView;
    private ProgressBar mProgressBar;
    private NewsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateView = (TextView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Create a new adapter that takes an empty list of news as input.
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        ListView newsListView = (ListView) findViewById(R.id.list);
        newsListView.setAdapter(mAdapter);
        newsListView.setEmptyView(mEmptyStateView);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        mAdapter.getItem(position).getmUrl()
                ));
                if (browserIntent.resolveActivity(view.getContext().getPackageManager()) != null)
                    view.getContext().startActivity(browserIntent);
            }
        });

        if (getIntent() != null) handleIntent(getIntent());
    }

    //helpers
    private void handleIntent(Intent intent) {
        final String queryAction = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = new Bundle();
            bundle.putString(SEARCH_QUERY_KEY, query);
            lookupNews(bundle);
        } else if (Intent.ACTION_MAIN.equals(queryAction)) {
            lookupNews(null);
        }
    }

    //lookup
    private void lookupNews(Bundle bundle) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            mProgressBar.setVisibility(View.VISIBLE);

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, bundle, this);
        } else {
            mEmptyStateView.setText(getString(R.string.no_internet_connection));
        }
    }

    // Create Uri
    private String createUri(Bundle bundle) {
        String queryString;
        if (bundle != null) queryString = bundle.getString(SEARCH_QUERY_KEY);
        else queryString = "null";


        final String QUERY_URL = "http://content.guardianapis.com/search";


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", queryString);

        final boolean byline = sharedPrefs.getBoolean(getString(R.string.byline_key), true);
        final boolean trailText = sharedPrefs.getBoolean(getString(R.string.trailText_key), true);
        final boolean thumbnails = sharedPrefs.getBoolean(getString(R.string.thumbnail_key), true);
        StringBuilder fieldsBuilder = new StringBuilder();
        if (byline) fieldsBuilder.append("byline" + ",");
        if (trailText) fieldsBuilder.append("trailText" + ",");
        if (thumbnails) fieldsBuilder.append("thumbnail" + ",");
        if (fieldsBuilder.length() > 0) {
            fieldsBuilder.deleteCharAt(fieldsBuilder.length() - 1);
            //seems simpler and less hassle than checking
            uriBuilder.appendQueryParameter("show-fields", fieldsBuilder.toString());
        }
        uriBuilder.appendQueryParameter("api-key", "test");
        return uriBuilder.toString();

    }


    //menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, createUri(args));
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        // Hide progress spinner because the data has been loaded.
        mProgressBar.setVisibility(View.GONE);

        // Clear the adapter of previous news data.
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else if (data != null) mEmptyStateView.setText(getString(R.string.no_results));
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //intents
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
}