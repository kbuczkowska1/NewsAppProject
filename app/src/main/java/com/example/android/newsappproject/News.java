package com.example.android.newsappproject;

import android.graphics.Bitmap;

/**
 * Created by Kasia on 2017-07-12.
 */

//An {@link News} object contains information related to a single news
class News {

    // Author of post.
    private String mAuthor;
    // Section of news.
    private final String mSection;
    // Title of tne news.
    private final String mTitle;
    // Text.
    private String mTrailText;
    // Date of post.
    private final String mDate;
    // URL.
    private final String mUrl;
    // Image.
    private Bitmap mThumbnail = null;

    //Create a new {@link News} object.
    News(String title, String mAuthor, String Url, String section, String date) {
        this.mAuthor = mAuthor;
        mSection = section;
        mTitle = title;
        mDate = date;
        mUrl = Url;
    }

    // Getter methods to return.
    String getmAuthor() {
        return mAuthor;
    }

    // Setter methods.
    void setmAuthor(String author) {
        mAuthor = author;
    }

    // Getter methods to return.
    String getmTitle() {
        return mTitle;
    }

    // Getter methods to return.
    String getmSection() {
        return mSection;
    }

    // Getter methods to return.
    String getmTrailText() {
        return mTrailText;
    }

    // Setter methods.
    void setmTrailText(String trailText) {
        mTrailText = trailText;
    }

    // Getter methods to return.
    String getmDate() {
        return mDate;
    }

    // Getter methods to return.
    String getmUrl() {
        return mUrl;
    }

    // Getter methods to return.
    Bitmap getmThumbnailBitmap() {
        return mThumbnail;
    }

    // Setter methods.
    void setmThumbnailBitmap(Bitmap thumbnailBitmap) {
        mThumbnail = thumbnailBitmap;
    }
}