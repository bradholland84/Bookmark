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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;

/**
 * Created by Brad on 10/13/2014.
 */
public class BookDetailActivityParallax extends ActionBarActivity implements ActionMode.Callback, ObservableScrollViewCallbacks {
    private ParseImageView coverPhoto;
    private EditText et_description;
    private TextView recentLogsHeader;
    private RatingBar ratingBar;
    private String title;
    private String bookId;
    private String description;
    private Intent intent;
    private double bookRating;
    private int minutes;
    private Book mBook;
    private FloatingActionButton fab;
    private LinearLayout loglayout;
    private com.android.bradholland.bookmark.Log log;
    ActionMode mActionMode;


    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_parallax_layout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        bookId = intent.getStringExtra("id");
        Log.v("TAGID", bookId);

        et_description = (EditText)findViewById(R.id.et_description);
        mImageView = findViewById(R.id.iv_cover_photo);
        mToolbarView = findViewById(R.id.support_toolbar);
        recentLogsHeader = (TextView)findViewById(R.id.tv_log_header);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        ParseQuery<Book> query = ParseQuery.getQuery("Books");
        query.fromLocalDatastore();

        query.whereEqualTo("objectId", bookId);
        query.getFirstInBackground(new GetCallback<Book>() {
            @Override
            public void done(Book book, ParseException e) {
                book.pinInBackground();
                mBook = book;
                title = book.getTitle();
                description = book.getDescription();
                bookRating = book.getRating();

                et_description.setText(description);
                getSupportActionBar().setTitle(title);

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
                    coverPhoto.setImageResource(R.drawable.default_cover);
                    Log.v("pic", "file NULL, doesn't exist yet");
                }
            }
        });


        ParseQuery<com.android.bradholland.bookmark.Log> logQuery = ParseQuery.getQuery("Logs");
        logQuery.whereEqualTo("bookId", bookId);
        logQuery.setLimit(10);
        logQuery.orderByDescending("timeStamp");
        logQuery.findInBackground(new FindCallback<com.android.bradholland.bookmark.Log>() {
            @Override
            public void done(List<com.android.bradholland.bookmark.Log> logs, ParseException e) {
                int size = logs.size();
                loglayout = (LinearLayout)findViewById(R.id.ll_logs_preview);
                for (int i = 0; i < size; i++) {
                    com.android.bradholland.bookmark.Log mLog = logs.get(i);
                    View v = getLayoutInflater().inflate(R.layout.log_item, loglayout, false);

                    TextView timeStamp = (TextView) v.findViewById(R.id.tv_timestamp);
                    TextView minutes = (TextView) v.findViewById(R.id.tv_minutesRead);
                    TextView notes = (TextView) v.findViewById(R.id.tv_notes);

                    DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                            .appendMonthOfYearShortText()
                            .appendDayOfMonth(1)
                            .toFormatter();
                    timeStamp.setText(mLog.getTimeStamp().toString(fmt));
                    minutes.setText("" + mLog.getMinutesRead() + " Minutes ");
                    if (mLog.getNotes().equals("")) {
                        mLog.setNotes("Blank note");
                    }
                    notes.setText(mLog.getNotes());
                    loglayout.addView(v, i);

                }
            }
        });


        // Code to add the floatingActionButton to this activity, creates new reading dialogs
        fab = (FloatingActionButton) findViewById(R.id.btn_add_log);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaterialDialog();
            }
        });

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
        ViewHelper.setTranslationY(et_description, scrollY / 2);
        ViewHelper.setTranslationY(loglayout, scrollY / 2);
        ViewHelper.setTranslationY(recentLogsHeader, scrollY / 2);
        if (scrollY < 0) {
            fab.show();
        }
        else if (scrollY > 0) {
            fab.hide();
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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

                et_description = (EditText) findViewById(R.id.et_description);
                //ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                mActionMode = this.startSupportActionMode(this);
                et_description.setFocusable(true);
                et_description.setFocusableInTouchMode(true);
                et_description.setTextColor(getResources().getColor(R.color.accent));
                //ratingBar.setIsIndicator(false);
                //ratingBar.setFocusableInTouchMode(true);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        fab.hide();
        inflater.inflate(R.menu.book_detail_contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        et_description.setTextColor(getResources().getColor(R.color.white));
        fab.show();
        mActionMode = null;
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
                        //TODO: Fix edit functionality

                        book.put("description", et_description.getText().toString());
                       // book.put("rating", ratingBar.getRating());
                        book.saveEventually();

                    }
                });
                //TODO: Fix edit functionality

                et_description = (EditText) findViewById(R.id.et_description);
                ratingBar = (RatingBar) findViewById(R.id.rating_view_detail);
                et_description.setFocusable(false);
                et_description.setFocusableInTouchMode(false);
                et_description.setTextColor(getResources().getColor(R.color.white));
                //ratingBar.setIsIndicator(true);
                //ratingBar.setFocusableInTouchMode(false);
                fab.show();
                mode.finish(); // Action picked, so close the CAB
                Toast.makeText(this, "Book Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_delete_book:
                deleteConfirmationDialog();
            default:
                return false;
        }
    }

    public void showMaterialDialog() {
        new MaterialDialog.Builder(this)
                .title("New log entry")
                .customView(R.layout.dialog_new_log, false)
                .positiveText("save")
                .negativeText("cancel")
                .negativeColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View v = dialog.getCustomView();

                        EditText hours = (EditText) v.findViewById(R.id.et_hours);
                        EditText minutes = (EditText) v.findViewById(R.id.et_minutes);
                        EditText notes = (EditText) v.findViewById(R.id.et_notes);

                        if (hours.getText().toString().equals("")) {
                            hours.setText("0");

                        }
                        if (minutes.getText().toString().equals("")) {
                            minutes.setText("0");
                        }

                        log = new com.android.bradholland.bookmark.Log();
                        log.setMinutesRead(
                                (Integer.parseInt(hours.getText().toString()) * 60)
                                        + (Integer.parseInt(minutes.getText().toString())));
                        log.setNotes(notes.getText().toString());
                        log.setTimeStamp(new DateTime());
                        log.setBookId(bookId);
                        log.put("parent", mBook);
                        log.put("createdBy", ParseUser.getCurrentUser());
                        log.saveEventually();
                        Toast.makeText(getBaseContext(), "Log saved", Toast.LENGTH_SHORT).show();
                    }

                    public void onNegative(MaterialDialog dialog) {
                        //Nothing to do
                    }
                })
                .show();
    }

    public void deleteConfirmationDialog() {
        new MaterialDialog.Builder(this)
                .title("Are you sure you want to delete this book?")
                .content("This action cannot be undone.")
                .positiveText("Delete")
                .negativeText("Cancel")
                .negativeColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.v("context menu", "Book should be deleted!");
                        ParseQuery<Book> deleteQuery = ParseQuery.getQuery("Books");
                        deleteQuery.fromLocalDatastore();
                        deleteQuery.getInBackground(bookId, new GetCallback<Book>() {
                            @Override
                            public void done(Book book, ParseException e) {
                                book.unpinInBackground();
                                book.deleteInBackground();
                                Toast.makeText(getBaseContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                                if (intent != null) {
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        });
                    }
                })
                .show();
    }

}
