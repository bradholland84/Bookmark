package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by Brad on 10/13/2014.
 */
public class bookDetailActivity extends ActionBarActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView minutesReadTextView;
    private Button saveBookButton;
    private RatingBar ratingBar;
    private String title;
    private String bookId;
    private String description;
    private double bookRating;
    private int minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_layout);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("id");
        Log.v("TAGID", bookId);

        ParseQuery<Book> query = ParseQuery.getQuery("Books");
        query.whereEqualTo("objectId", bookId);
        query.getFirstInBackground(new GetCallback<Book>() {
            @Override
            public void done(Book book, ParseException e) {
                title = book.getTitle();
                description = book.getDescription();
                bookRating = book.getRating();
                minutes = book.getWeeklyMinutes();

                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                minutesReadTextView = (TextView) findViewById(R.id.tv_minutes);

                titleEditText.setText(title);
                descriptionEditText.setText(description);
                ratingBar.setRating((float)bookRating);
                minutesReadTextView.setText("Minutes read: " + Integer.toString(minutes));


                android.support.v7.app.ActionBar ab = getSupportActionBar();
                ab.setTitle(title);
            }
        });








    }
}
