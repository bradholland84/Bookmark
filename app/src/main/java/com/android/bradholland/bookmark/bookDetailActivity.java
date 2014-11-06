package com.android.bradholland.bookmark;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by Brad on 10/13/2014.
 */
public class bookDetailActivity extends Activity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button saveBookButton;
    private RatingBar ratingBar;
    private String title;
    private String bookId;
    private String description;
    private double bookRating;

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
                Log.v("TAGID", title);
                description = book.getDescription();
                Log.v("TAGID", description);
                bookRating = book.getRating();
                Log.v("TAGID", String.valueOf(bookRating));

                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);

                titleEditText.setText(title);
                descriptionEditText.setText(description);
                ratingBar.setRating((float)bookRating);

                ActionBar ab = getActionBar();
                ab.setTitle(title);
            }
        });








    }
}
