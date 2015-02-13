package com.android.bradholland.bookmark;

import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.r0adkll.slidr.Slidr;

import org.joda.time.DateTime;
import org.json.JSONArray;

/**
 * Created by Brad on 10/13/2014.
 */
public class bookDetailActivity extends ActionBarActivity implements ActionMode.Callback, LogDialogFragment.LogDialogListener {
    private boolean editFlag = false;
    private ParseImageView coverPhoto;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView minutesReadTextView;
    private RatingBar ratingBar;
    private Button toggleReading;
    private String title;
    private String bookId;
    private String description;
    private double bookRating;
    private int minutes;
    private DateTime sessionStart;
    private Clock mClock;
    private Book mBook;
    private DateTime mBookMonthDateTime;
    private DateTime mBookWeekDateTime;
    private JSONArray monthMinutesHistory;
    private JSONArray weekMinutesHistory;
    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_layout);
        Slidr.attach(this);
        mClock = new Clock();
        editFlag = false;


        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("id");
        Log.v("TAGID", bookId);


        ParseQuery<Book> query = ParseQuery.getQuery("Books");
        query.fromLocalDatastore();
        query.whereEqualTo("objectId", bookId);
        query.getFirstInBackground(new GetCallback<Book>() {
            @Override
            public void done(Book book, ParseException e) {
                mBook = book;
                mBookMonthDateTime = new DateTime(mBook.getMonthDate());
                mBookWeekDateTime = new DateTime(mBook.getWeekDate());
                Log.v("time", "current month time is =" + mBookMonthDateTime.toString());
                title = book.getTitle();
                description = book.getDescription();
                bookRating = book.getRating();
                minutes = book.getTotalMinutes();
                monthMinutesHistory = book.getMonthMinutesHistory();
                weekMinutesHistory = book.getWeekMinutesHistory();

                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                ratingBar.setIsIndicator(true);
                minutesReadTextView = (TextView) findViewById(R.id.tv_minutes);

                coverPhoto = (ParseImageView) findViewById(R.id.iv_cover_photo);
                final ParseFile photoFile = book.getPhotoFile();
                if (photoFile != null) {
                    Log.v("pic", "file exists");
                    coverPhoto.setParseFile(photoFile);
                    coverPhoto.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            coverPhoto.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    Log.v("pic", "file NULL, doesn't exist yet");
                }
                titleEditText.setText(title);
                descriptionEditText.setText(description);
                ratingBar.setRating((float) bookRating);
                minutesReadTextView.setText("Minutes read: " + Integer.toString(minutes));

                getSupportActionBar().setTitle(title);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_add_log);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: dialogue to create new log goes here
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
                ratingBar.setIsIndicator(false);
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
                query.fromLocalDatastore();
                query.getInBackground(bookId, new GetCallback<Book>() {
                    @Override
                    public void done(Book book, ParseException e) {
                        book.put("title", titleEditText.getText().toString());
                        book.put("description", descriptionEditText.getText().toString());
                        book.put("rating", ratingBar.getRating());
                        book.saveEventually();
                    }
                });

                titleEditText = (EditText) findViewById(R.id.et_title);
                descriptionEditText = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                titleEditText.setFocusable(false);
                titleEditText.setFocusableInTouchMode(false);
                descriptionEditText.setFocusable(false);
                descriptionEditText.setFocusableInTouchMode(false);
                ratingBar.setIsIndicator(true);
                ratingBar.setFocusableInTouchMode(false);
                editFlag = true;
                mode.finish(); // Action picked, so close the CAB
                Toast.makeText(this, "Book Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    public void showLogDialog() {
        DialogFragment dialog = new LogDialogFragment();
        dialog.show(getSupportFragmentManager(), "LogDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        //TODO: user pressed positive button...
    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        //TODO: user pressed negative button...
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
