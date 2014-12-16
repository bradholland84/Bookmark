package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

/**
 * Created by Brad on 10/13/2014.
 */
public class bookDetailActivity extends ActionBarActivity implements ActionMode.Callback {
    private boolean editFlag = false;
    private ParseImageView coverPhoto;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView minutesReadTextView;
    private RatingBar ratingBar;
    private ParseFile cover_file;
    private String title;
    private String bookId;
    private String description;
    private double bookRating;
    private int minutes;
    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                minutes = book.getTotalMinutes();

                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                minutesReadTextView = (TextView) findViewById(R.id.tv_minutes);

                coverPhoto = (ParseImageView) findViewById(R.id.iv_cover_photo);
                ParseFile cover_file = book.getParseFile("coverPhoto");
                if (cover_file != null) {
                    coverPhoto.setParseFile(cover_file);
                    cover_file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            Log.v("pic", "getdata callback okay?");
                            coverPhoto.setVisibility(View.VISIBLE);
                        }
                    });
                }

                titleEditText.setText(title);
                descriptionEditText.setText(description);
                ratingBar.setRating((float)bookRating);
                minutesReadTextView.setText("Minutes read: " + Integer.toString(minutes));

                getSupportActionBar().setTitle(title);
            }

        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_detail_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_book:
                // do edit stuff function here
                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                mActionMode = this.startSupportActionMode(this);
                titleEditText.setFocusable(true);
                titleEditText.setFocusableInTouchMode(true);
                descriptionEditText.setFocusable(true);
                descriptionEditText.setFocusableInTouchMode(true);
                ratingBar.setFocusable(true);
                ratingBar.setFocusableInTouchMode(true);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.book_detail_contextual, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_book:
                ParseQuery<Book> query = ParseQuery.getQuery("Books");
                query.getInBackground(bookId, new GetCallback<Book>() {
                    @Override
                    public void done(Book book, ParseException e) {
                        book.put("title", titleEditText.getText().toString());
                        book.put("description", descriptionEditText.getText().toString());
                        book.put("rating", ratingBar.getRating());
                        book.saveInBackground();
                    }
                });
                mode.finish(); // Action picked, so close the CAB
                Toast.makeText(this, "Book Updated!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }
}
