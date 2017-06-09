package com.example.android.book_listing_app;

/**
 * Created by iva on 6/6/17.
 */

public class Books {

    /**
     * Title of the book
     */
    private String mBookTitle;

    /**
     * Authors of the book
     */
    private StringBuilder mBookAuthor;

    /**
     * Subtitle of the book
     */
    private String mSubtitle;

    /**
     * URL of the book
     */
    private String mUrl;

    /**
     * Create a new constructor for Books object.
     *
     * @param title    is the title of the book
     * @param author   is the names of the authors of the book
     * @param subtitle is the picture of the book
     * @param url      is the url of the book
     */
    public Books(String title, StringBuilder author, String subtitle, String url) {
        mBookTitle = title;
        mBookAuthor = author;
        mSubtitle = subtitle;
        mUrl = url;
    }

    /**
     * Get the title of the book
     */
    public String getTitle() {
        return mBookTitle;
    }

    /**
     * Get the names of the authors of the book
     */
    public StringBuilder getAuthor() {
        return mBookAuthor;
    }

    /**
     * Get the picture of the book
     */
    public String getSubtitle() {
        return mSubtitle;
    }

    /**
     * Get the url of the book
     */
    public String getUrl() {
        return mUrl;
    }
}
