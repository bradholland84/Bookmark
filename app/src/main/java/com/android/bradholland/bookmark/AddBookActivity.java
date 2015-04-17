package com.android.bradholland.bookmark;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView coverPhotoHint;
    private boolean photoTaken;
    private boolean okToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_book_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        book = new Book();

        coverPhoto = (ParseImageView) findViewById(R.id.iv_cover_photo);
        coverPhoto.setPlaceholder(getResources().getDrawable(R.drawable.camerabuttonicon));
        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverPhoto.setEnabled(false);
                titleEditText.setEnabled(false);
                descriptionEditText.setEnabled(false);
                FragmentManager fragmentManager = getFragmentManager();
                Fragment cameraFragment = new CameraFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.add_book_layout, cameraFragment);
                transaction.addToBackStack("Camera Fragment");
                transaction.commit();
            }
        });

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setParseCoverPhoto();
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

    }


    private void addBook (Book book) {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        book.setTitle(title);
        book.setDescription(description);
        book.setUser(ParseUser.getCurrentUser());

        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        book.setACL(new ParseACL(ParseUser.getCurrentUser()));
        // Save the book
        book.pinInBackground();
        if (!isNetworkAvailable()) {
            book.saveEventually();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            book.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private String getTitleEditTextText () {
        return titleEditText.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getTitleEditTextText().length();
        if (length > 0 && photoTaken) {
            okToSave = true;
        }
    }

    public Book getCurrentBook() {
        return book;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("pic", "OnResume called");
        coverPhoto.setEnabled(true);
        titleEditText.setEnabled(true);
        descriptionEditText.setEnabled(true);
        getSupportActionBar().show();
        setParseCoverPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        getFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_book_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_book:
                if (okToSave) {
                    addBook(book);
                } else {
                    Toast.makeText(getBaseContext(), "Please fill out all of the fields & " +
                            "choose a cover photo", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setParseCoverPhoto() {
        final ParseFile photoFile = getCurrentBook().getPhotoFile();
        if (photoFile != null) {
            photoTaken = true;
            updatePostButtonState();
            Log.v("pic", "file exists");
            coverPhoto.setEnabled(true);
            titleEditText.setEnabled(true);
            descriptionEditText.setEnabled(true);
            coverPhoto.setParseFile(photoFile);
            coverPhoto.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    coverPhoto.setVisibility(View.VISIBLE);
                    coverPhotoHint = (TextView)findViewById(R.id.tv_cover_photo_hint);
                    coverPhotoHint.setText("Looks good! Touch to choose again.");
                }
            });
        } else {
            Log.v("pic", "file NULL, doesn't exist yet");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
