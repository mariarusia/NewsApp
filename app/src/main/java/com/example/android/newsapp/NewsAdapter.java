package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by maria on 30.06.2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news    is the list of news, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about news article
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the News at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_view);
        // Display title
        titleView.setText(currentNews.getTitle());

        TextView dateView = (TextView) listItemView.findViewById(R.id.date_view);

        String full_date = currentNews.getDate();
        String[] parts = full_date.split("T");

        // Display the date
        dateView.setText(parts[0]);

        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_view);
        // Display the secttion
        sectionView.setText(currentNews.getSection());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
