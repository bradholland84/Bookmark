package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by Brad on 10/13/2014.
 */
public class bookDetailActivity extends ActionBarActivity implements ActionMode.Callback {
    private boolean editFlag = false;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView minutesReadTextView;
    private RatingBar ratingBar;
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

                titleEditText.setFocusable(false);
                descriptionEditText.setFocusable(false);
                ratingBar.setFocusable(false);





                android.support.v7.app.ActionBar ab = getSupportActionBar();
                ab.setTitle(title);
            }

        });
/*
        final Button editBookButton = (Button) findViewById(R.id.btn_edit_book);
        editBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when the user chooses to edit the book
                editFlag = true;
                editBookButton.setText("Save Changes");
                titleEditText.setFocusable(true);
                titleEditText.setEnabled(false);
                descriptionEditText.setFocusable(true);
                descriptionEditText.setEnabled(false);
                ratingBar.setFocusable(true);
                ratingBar.setEnabled(false);

                final Button SaveChanges = (Button)bookDetailActivity.this.findViewById(R.id.btn_edit_book);
                SaveChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update characteristics of the book
                        ParseQuery<Book> query = ParseQuery.getQuery("Books");
                        query.getInBackground(bookId, new GetCallback<Book>() {
                            @Override
                            public void done(Book book, ParseException e) {
                                book.put("title", titleEditText.getText().toString());
                                book.put("description", descriptionEditText.getText().toString());
                                book.put("rating", ratingBar.getNumStars());
                                SaveChanges.setText("Edit Book");
                            }
                        });
                    }
                });

            }
        });
*/

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
                //TODO fix this
                mActionMode = startSupportActionMode(SupportActionMode.Callback);
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
                Toast.makeText(this, "Book Updated!", Toast.LENGTH_SHORT).show();
                mode.finish(); // Action picked, so close the CAB
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
