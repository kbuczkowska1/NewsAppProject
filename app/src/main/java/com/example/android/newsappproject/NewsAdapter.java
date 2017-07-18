package com.example.android.newsappproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Kasia on 2017-07-12.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    // Tag for the log messages.
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    // Constructs a new {@link NewsAdapter}
    NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    // View lookup cache.
    private static class ViewHolder {
        TextView mAuthor;
        TextView mSection;
        TextView mTitle;
        TextView mTrailText;
        TextView mDate;
        ImageView mThumbnail;
    }

    //Returns a list item view that displays information about the news at the given position
    //in the list of news.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            holder = new ViewHolder();
            holder.mAuthor = (TextView) convertView.findViewById(R.id.author);
            holder.mSection = (TextView) convertView.findViewById(R.id.section);
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mTrailText = (TextView) convertView.findViewById(R.id.trail_text);
            holder.mDate = (TextView) convertView.findViewById(R.id.date);
            holder.mThumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        News currentNews = getItem(position);

        if (!currentNews.getmAuthor().isEmpty())
            holder.mAuthor.setText(currentNews.getmAuthor());
        if (!currentNews.getmSection().isEmpty())
            holder.mSection.setText(currentNews.getmSection());
        if (!currentNews.getmTitle().isEmpty())
            holder.mTitle.setText(currentNews.getmTitle());
        if (!currentNews.getmTrailText().isEmpty())
            holder.mTrailText.setText(currentNews.getmTrailText());
        if (currentNews.getmThumbnailBitmap() != null) {
            holder.mThumbnail.setImageBitmap(currentNews.getmThumbnailBitmap());
        }
        if (!currentNews.getmDate().isEmpty()) {
            Date parsedDate = parseDate(currentNews.getmDate());
            if (parsedDate != null) holder.mDate.setText(formatDate(parsedDate));
        }
        return convertView;
    }

    // Format data.
    private String formatDate(Date dateObj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(dateObj);
    }

    private Date parseDate(String strDate) {
        if (strDate == null) return null;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = parser.parse(strDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing date", e);
        }
        return date;
    }

}