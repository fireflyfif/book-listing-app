package com.example.android.book_listing_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BOOK_URL_REQUEST =
            "https://www.googleapis.com/books/v1/volumes?q=art&maxResults=30";

    private BooksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView booksListView = (ListView) findViewById(R.id.list_view);

        // Create new adapter that takes an empty list of books as input
        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        booksListView.setAdapter(mAdapter);

        // Start the AsyncTask to fetch the books data
        BookListingAsyncTask task = new BookListingAsyncTask();
        task.execute(BOOK_URL_REQUEST);

    }

    private class BookListingAsyncTask extends AsyncTask<String, Void, List<Books>> {
        @Override
        protected List<Books> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length <1 || urls[0] == null) {
                return null;
            }

            List<Books> result = QueryUtils.fetchBooksData(urls[0]);
            return result;

        }

        @Override
        protected void onPostExecute(List<Books> data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valide list of {@link Book}s, then add to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}
