package com.example.android.book_listing_app;

/**
 * Created by iva on 6/6/17.
 */

public class Books {

    private String mBookTitle;

//    private String mSubtitle;

    private StringBuilder mBookAuthor;



    public Books(String title, StringBuilder author) {
        mBookTitle = title;
//        mSubtitle = subtitle;
        mBookAuthor = author;
    }

    public String getTitle() {
        return mBookTitle;
    }

//    public String getSubtitle() {
//        return mSubtitle;
//    }

    public StringBuilder getAuthor() {
        return mBookAuthor;
    }
}
