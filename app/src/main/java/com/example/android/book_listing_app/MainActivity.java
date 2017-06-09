package com.example.android.book_listing_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final String BOOK_URL_BASE =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private static final String BOOK_URL_ANDROID =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    private BooksAdapter mAdapter;

    private EditText userInput;

    private TextView mEmptyView;

    private View loadingIndicator;

    private TextView welcomeText;

    private boolean isInternetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        final ListView booksListView = (ListView) findViewById(R.id.list_view);
        // Find a reference to the EditText in the layout
        userInput = (EditText) findViewById(R.id.search_bar);
        // Find a reference to the ImageButton for search in the layout
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        // Find a reference to the text that is shown when setEmptyView is called
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);
        // Find a reference to the Progress bar in the layout
        loadingIndicator = findViewById(R.id.progress_bar);
        // Find a reference to the temporary text
        welcomeText = (TextView) findViewById(R.id.temp_text_view);
        // Set the welcome message on that temporary text
        welcomeText.setText(R.string.books_android);

        isInternetConnected = checkInternetConnection();

        // Hide the ProgressBar
        loadingIndicator.setVisibility(View.GONE);

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.e(LOG_TAG, "Hide the keyboard.");

        // Create new adapter that takes an empty list of books as input
        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        // Set the adapter on the {@link ListView)
        // so the list can be populated in the user interface
        booksListView.setAdapter(mAdapter);

        BookListingAsyncTask task = new BookListingAsyncTask();

        // If there is a network connection, fetch data
        if (isInternetConnected) {
            // Start the AsyncTask to fetch the books data
            task.execute(BOOK_URL_ANDROID);
            welcomeText.setText(R.string.books_android);
        } else {
            welcomeText.setVisibility(View.GONE);
            // Otherwise, display error
            // First, hide loading indicator so error will be visible
            loadingIndicator.setVisibility(View.GONE);
            //Update empty state with no connection error message
            mEmptyView.setText(R.string.no_internet);
        }

        // Set a click listener to the ImageButton Search which sends query to the URL
        // base on the user input.
        searchButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide the keyboard after the Search button is clicked
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // If there is a network connection, fetch data
                if (isInternetConnected) {
                    String searchWord = userInput.getText().toString();
                    if (searchWord.isEmpty()) {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_enter_word),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Hide the empty state text
                        mEmptyView.setVisibility(View.GONE);
                        welcomeText.setVisibility(View.GONE);
                        // Start the AsyncTask to fetch the books data
                        BookListingAsyncTask task = new BookListingAsyncTask();
                        task.execute(BOOK_URL_BASE + searchWord);
                    }
                } else {
                    welcomeText.setVisibility(View.GONE);
                    // Otherwise, display error
                    // First, hide loading indicator so error will be visible
                    loadingIndicator.setVisibility(View.GONE);
                    //Update empty state with no connection error message
                    mEmptyView.setText(R.string.no_internet);
                }
            }
        });
    }

    /**
     * Check the Internet Connection.
     */
    private boolean checkInternetConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);

        // Get details on the currently active default network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            //Update empty state with no connection error message
            mEmptyView.setText(R.string.no_internet);
            return false;
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and
     * then update the UI with the list of books in the response.
     * <p>
     * We''ll only override two of the methods of AsyncTask doInBackground() and onPostExecute()
     * The doInBackground() runs on a background thread, so it can run long running code
     * (like network activity), without interfering with the responsiveness of the app.
     * The onPostExecute() is passed the result of the doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookListingAsyncTask extends AsyncTask<String, Void, List<Books>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Books}s as the result.
         *
         * @param urls
         * @return {@link Books}s as the result
         */
        @Override
        protected List<Books> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Books> result = QueryUtils.fetchBooksData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First clears out the adapter, to get get rid of books data from a previous
         * query to Google Books API. Then updates the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Books> books) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add to the adapter's
            // data set. This will trigger the ListView to update.
            if (books != null && !books.isEmpty()) {
                mAdapter.addAll(books);
            } else {
                mEmptyView.setText(R.string.no_data_found);
            }
        }
    }
}
