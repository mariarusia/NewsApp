package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by maria on 30.06.2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Loads a list of newss by using an AsyncTask to perform the
     * network request to the given URL.
     */
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getName();
    /**
     * Query
     */

    private String testQuery = "germany";

    private String mQuery;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param query   to load data from
     */
    public NewsLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {

        if (mQuery == null | mQuery.trim().length() == 0) {
            Log.v("the query is", "null");
            return QueryUtils.fetchNewsData(testQuery);
        }
        // Perform the network request, parse the response, and extract a list of books.
        Log.v("the query in loader", mQuery);
        List<News> news = QueryUtils.fetchNewsData(mQuery);
        return news;
    }
}
