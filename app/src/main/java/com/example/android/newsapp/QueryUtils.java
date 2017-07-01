package com.example.android.newsapp;

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
 * Created by maria on 30.06.2017.
 * <p>
 * The class to parse the query, fo the request and so on.
 */

public final class QueryUtils {

    private static String test = "Putin";

    //add LOG TAG
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //parts of api query to create
    private static String base_url = "http://content.guardianapis.com/search?q=";
    private static String adding_part = "&api-key=test";
    private static String default_uri = "http://content.guardianapis.com/search?q=germany&api-key=test";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    static List<News> fetchNewsData(String query) {

        //if nothing is chosen, return the default
        if (query.isEmpty()) {
            //Log.v("query null", query);
            return extractFeatureFromJson(default_uri);
        } else {
            String requestUrl = createApiQuery(query);
            // Create URL object
            URL url = createUrl(requestUrl);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = null;

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            // Extract relevant fields from the JSON response and create a list of {@link News}s

            // Return the list of {@link News}s
            return extractFeatureFromJson(jsonResponse);
        }
    }

    //creating an api query
    private static String createApiQuery(String query) {

        String result_uri;

        if (query.isEmpty())
            result_uri = default_uri;
        else {
            result_uri = base_url.concat(query).concat(adding_part);
        }

        //Log.v("url_for_the_api", result_uri);
        return result_uri;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Handle redirect (response code 301)
            int httpResponse = urlConnection.getResponseCode();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /* Return a list of {@link News} objects that has been built up from
    * parsing the given JSON response.
    */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Log.v("url_we parse", newsJSON);
        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            //not to cause json exception in case nothing is found
            if (baseJsonResponse.has("response")) {

                // Extract the JSONObject associated with the key called "response",
                JSONObject response = baseJsonResponse.getJSONObject("response");

                //check if the "results" array exists
                if (response.has("results")) {

                    JSONArray results = response.getJSONArray("results");

                    // For each result create News object
                    for (int i = 0; i < results.length(); i++) {

                        // Get a single item at position i within the list of news
                        JSONObject currentNews = results.getJSONObject(i);

                        //get title of the news
                        String title = currentNews.getString("webTitle");

                        //get section
                        String section = currentNews.getString("sectionName");

                        //get date
                        String date = currentNews.getString("webPublicationDate");

                        //get url
                        String url = currentNews.getString("webUrl");

                        // Create a new {@link News object from the JSON response.
                        News newsItem = new News(title, date, section, url);

                        // Add the new {@link News} to the list of news.
                        news.add(newsItem);
                    }
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news
        return news;
    }

}
