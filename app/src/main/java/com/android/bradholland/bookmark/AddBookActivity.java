package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Brad on 10/10/2014.
 */
public class AddBookActivity extends ActionBarActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button saveBookButton;
    private RatingBar ratingBar;
    private String title;
    private String description;
    private float bookRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_book_layout);

        Intent intent = getIntent();
       // title = intent.getParcelableExtra()

        titleEditText = (EditText) findViewById(R.id.et_title);
        descriptionEditText = (EditText) findViewById(R.id.et_description);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePostButtonState();
            }
        });

        saveBookButton = (Button) findViewById(R.id.btn_save_book);
        saveBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                bookRating = rating;
            }
        });

    }


    private void addBook () {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();



        // Create a post.
       Book book = new Book();

        // Set the book's title and description

        book.setTitle(title);
        book.setDescription(description);
        book.setUser(ParseUser.getCurrentUser());
        book.setRating(bookRating);
        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        book.setACL(new ParseACL(ParseUser.getCurrentUser()));

        // Save the book
        book.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
                // doListQuery();
            }
        });
    }

    private String getTitleEditTextText () {
        return titleEditText.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getTitleEditTextText().length();
        boolean enabled = length > 0;
        saveBookButton.setEnabled(enabled);
    }


}
