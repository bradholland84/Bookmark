package com.android.bradholland.bookmark;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Brad on 10/10/2014.
 */
public class AddBookActivity extends ActionBarActivity {

    private Book book;
    private ParseImageView coverPhoto;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button saveBookButton;
    private RatingBar ratingBar;
    private Spinner minutesSpinner;
    private int minutes;
    private float bookRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_book_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       book = new Book();


        coverPhoto = (ParseImageView) findViewById(R.id.iv_cover_photo);
        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment cameraFragment = new CameraFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.add_book_layout, cameraFragment);
                transaction.addToBackStack("NewBookFragment");
                transaction.commit();
            }
        });

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

        ratingBar = (RatingBar) findViewById(R.id.rb_ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                bookRating = rating;

            }
        });

        minutesSpinner = (Spinner) findViewById(R.id.spn_minutes_spinner);
        final Integer[] items = new Integer[] {0, 5, 10, 20, 30, 40, 50, 60, 75, 90, 105, 125, 150, 180, 210, 250, 285, 300, 350, 400, 450, 500, 600, 700, 850, 1000};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, items);
        minutesSpinner.setAdapter(adapter);

        minutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minutes = items[parent.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveBookButton = (Button) findViewById(R.id.btn_save_book);
        saveBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook(book);
            }
        });


    }


    private void addBook (Book book) {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        Log.v("TAGG", "minutes ====== > " + minutes);


        // Set the book's data

        book.setTitle(title);
        book.setDescription(description);
        book.setUser(ParseUser.getCurrentUser());
        book.setRating(bookRating);
        book.setWeeklyMinutes(minutes);
        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        book.setACL(new ParseACL(ParseUser.getCurrentUser()));

        // Save the book
        book.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
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

    public Book getCurrentBook() {
        return book;
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = getCurrentBook().getPhotoFile();
        if (photoFile != null) {
            Log.v("pic", "file exists");
            coverPhoto.setParseFile(photoFile);
            coverPhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //do stuff here?
                }
            });
        } else {
            Log.v("pic", "file doesn't exist yet");
        }
    }

}
