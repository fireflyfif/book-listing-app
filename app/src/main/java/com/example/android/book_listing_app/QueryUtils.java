package com.example.android.book_listing_app;

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
 * Helper methods related to requesting and receiving books data from Google Books API.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return list of {@link Books} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Books> extractFeaturesFromJson(String booksJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Books> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract "items" JSONArray associated with the key called "items"
            // which represents a list of information about the book
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            // For each book in the booksArray, create an {@link Books} object
            for (int i = 0; i < booksArray.length(); i++) {
                // Get a single book and position it within the list of books
                JSONObject currentBook = booksArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all information
                // for that book
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Extract the value from the key called "title"
                String title = volumeInfo.getString("title");

                /// Extract "authors" JSONArray associated with the key called "authors"
                // which may represents a list of authors of the book
                // TODO: Check if this is a good logic!
                JSONArray authorsArray;
                StringBuilder authors = new StringBuilder();
                if (volumeInfo.has("authors")) {
                    authorsArray = volumeInfo.getJSONArray("authors");
                    // Iterate the JSONArray and print the info of JSONObjects
                    for (int n = 0; n < authorsArray.length(); n++) {
                        authors.append(System.getProperty("line.separator"));
                        authors.append(authorsArray.getString(n));
                    }
                } else {
                    authors.append("No Author");
                }

                // Extract the value from the key called "subtitle"
                String subtitle = "";
                if (volumeInfo.has("subtitle")) {
                    subtitle = volumeInfo.getString("subtitle");
                }
                // This is probably unnecessary ???
                else {
                    subtitle = "";
                }

                // Extract the URL of the book
//                String url = null;
//                if (volumeInfo.has("infoLink")) {
//                    url = volumeInfo.getString("infoLink");
//                }

                // Create a new {@link Books} object with the title, subtitle and authors
                // from the JSON response.
                Books booksObject = new Books(title, authors, subtitle);

                // Add the new {@link Books} to the list of books
                books.add(booksObject);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the JSON list books", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Query the Google Books API and return a list of {@link Books} object.
     */
    public static List<Books> fetchBooksData (String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list {@link Books}
//        List<Books> booksList = extractFeaturesFromJson(jsonResponse);

        // Return the list of {@link Books}.
        return extractFeaturesFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error building the URL", e);
        }
        return url;
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
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            // If the request was successful (response code 200)
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response code of the object: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON result: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response
     * from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
        }
        return result.toString();
    }
}
