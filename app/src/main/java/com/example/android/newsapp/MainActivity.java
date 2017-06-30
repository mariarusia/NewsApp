package com.example.android.newsapp;

import android.app.LoaderManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    //just a loader id
    private static final int NEWS_LOADER_ID = 1;
    //the empty text view
    private TextView textView;
    //adapter for the list of news
    private NewsAdapter mAdapter;
    //the query which would later become a url
    private String mQuery;
    //network information and status
    private NetworkInfo networkInfo;
    private ConnectivityManager connMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("I have restarted", "restarted");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mQuery = sharedPrefs.getString(
                getString(R.string.default_topic_key),
                getString(R.string.default_topic));

        // Get a reference to the ConnectivityManager to check state of network connectivity
        connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);
        //set empty view if there are no books found
        textView = (TextView) findViewById(R.id.empty);
        newsListView.setEmptyView(textView);

        //news array, adapter to show it
        List<News> news = new ArrayList<News>();
        mAdapter = new NewsAdapter(this, news);

        //set adapter to the listView
        newsListView.setAdapter(mAdapter);

        //we do not need the spinner right at the start, only when we are fetching information
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(GONE);

        // Get details on the currently active default data network
        //networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
            mAdapter.clear();

            //Hide the "No books message" during the request
            textView.setVisibility(GONE);

            //show the spinner
            loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.VISIBLE);

            //start fetching
            //getLoaderManager().restartLoader(BOOKS_LOADER_ID, null, MainActivity.this);
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            mAdapter.clear();
            textView.setText("No Internet");
            //textView.setVisibility(View.VISIBLE);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // Find the current newsItem that was clicked on
                    News currentNews = mAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri newsUri = Uri.parse(currentNews.getUrl());

                    // Create a new intent to view the earthquake URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                } else {
                    // Otherwise, display error
                    // Update empty state with no connection error message
                    textView.setText(R.string.no_inet);
                }
            }
        });
    }

    @Override
    public void onResume() {
        Log.v("Resuming", "yes");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mQuery = sharedPrefs.getString(
                getString(R.string.default_topic_key),
                getString(R.string.default_topic));

        mAdapter.clear();
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);

        Log.v("afterResume", mQuery);
        super.onResume();

        }
        /*networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //getLoaderManager().destroyLoader(BOOKS_LOADER_ID);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
            //getLoaderManager().initLoader(0, null, this);
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            textView.setText(R.string.no_inet);
        }
    }
    */
    //overriding the Loader methods
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, mQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // hide the spinner
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(GONE);
        textView.setVisibility(View.VISIBLE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        } else {
            textView.setText(R.string.no_news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mQuery = sharedPrefs.getString(
                getString(R.string.default_topic_key),
                getString(R.string.default_topic));*/
    }

    //restoring the values and starting the search
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mQuery = sharedPrefs.getString(
                getString(R.string.default_topic_key),
                getString(R.string.default_topic));

        Log.v("restarted, Ive put", mQuery);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (mQuery.length() > 0) {
                //hide the No books message which appears
                //textView.setVisibility(GONE);
                getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
            } else {
                mQuery = "Germany";
            }
            //editText.setText(mQuery);
        } else {
            // Otherwise, display error
            // Update empty state with no connection error message
            //editText.setText(mQuery);
            textView.setText(R.string.no_inet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
