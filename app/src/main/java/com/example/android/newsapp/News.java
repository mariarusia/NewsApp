package com.example.android.newsapp;

/**
 * Created by maria on 30.06.2017.
 */

public class News {

    private final String mDate;
    private final String mTitle;
    private final String mSection;
    private final String mUrl;

    //constructor. create News object with date, title and section
    public News(String title, String date, String section, String url) {
        mTitle = title;
        mDate = date;
        mSection = section;
        mUrl = url;
    }

    //get date
    public String getDate() {
        return mDate;
    }

    //get title
    public String getTitle() {
        return mTitle;
    }

    //get section
    public String getSection() {
        return mSection;
    }

    //get url
    public String getUrl() {
        return mUrl;
    }
}
