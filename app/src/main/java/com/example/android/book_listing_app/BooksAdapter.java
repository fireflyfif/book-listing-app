package com.example.android.book_listing_app;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iva on 6/7/17.
 */

public class BooksAdapter extends ArrayAdapter<Books> {

    public BooksAdapter(Activity context, ArrayList<Books> books ) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Books currentBook = getItem(position);

        // Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        // Format the title of the current book in that TextView
        titleView.setText(currentBook.getTitle());

//        TextView subtitleView = (TextView) listItemView.findViewById(R.id.book_subtitle);
//        subtitleView.setText(currentBook.getSubtitle());

        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(currentBook.getAuthor());

        return listItemView;
    }
}
