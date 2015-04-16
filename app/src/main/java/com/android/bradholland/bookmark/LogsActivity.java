package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Created by Brad on 2/20/2015.
 */
public class LogsActivity extends ActionBarActivity {

    private String bookId;
    private String logId;
    private ListView logsListView;
    private ParseQueryAdapter<Log> logsQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_logs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("id");

        ParseQueryAdapter.QueryFactory<com.android.bradholland.bookmark.Log> logfactory =
                new ParseQueryAdapter.QueryFactory<com.android.bradholland.bookmark.Log>() {
                    public ParseQuery<Log> create() {
                        ParseQuery logQuery = new ParseQuery("Logs");
                        logQuery.whereEqualTo("bookId", bookId);
                        logQuery.orderByDescending("timeStamp");
                        return logQuery;
                    }
                };
        adapt(logfactory);

        logsListView = (ListView) findViewById(R.id.logs_listview);
        logsListView.setAdapter(logsQueryAdapter);
        registerForContextMenu(logsListView);

        logsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.logs_listview) {
            ListView logs_listview = (ListView) view;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Log selectedLog = (Log) logs_listview.getItemAtPosition(info.position);
            logId = selectedLog.getObjectId();

            android.util.Log.v("context menu", "this log's parse object id is = " + selectedLog.getObjectId());

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.loglist_item_overflow, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String s = String.valueOf(info.id);
        android.util.Log.v("context menu", s + "is this books' position");

        switch (item.getItemId()) {

            case R.id.delete_log:
                deleteConfirmationDialog(logId);

            default: return false;
        }
    }

    public void deleteConfirmationDialog(final String logId) {
        new MaterialDialog.Builder(this)
                .title("Are you sure you want to delete this log?")
                .content("This action cannot be undone.")
                .positiveText("Delete")
                .negativeText("Cancel")
                .negativeColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        android.util.Log.v("context menu", "Log should be deleted!");
                        ParseQuery<Log> deleteQuery = ParseQuery.getQuery("Logs");
                        //deleteQuery.fromLocalDatastore();
                        deleteQuery.getInBackground(logId, new GetCallback<Log>() {
                            @Override
                            public void done(Log log, ParseException e) {
                                //log.unpinInBackground();
                                log.deleteInBackground();
                                Toast.makeText(getBaseContext(), "Log deleted", Toast.LENGTH_SHORT).show();
                                doListQuery();
                            }
                        });
                    }
                })
                .show();
    }

    public void doListQuery() {
        logsQueryAdapter.loadObjects();
    }

    public void adapt(ParseQueryAdapter.QueryFactory logFactory) {
        logsQueryAdapter = new ParseQueryAdapter<com.android.bradholland.bookmark.Log>(this, logFactory) {
            @Override
            public View getItemView(com.android.bradholland.bookmark.Log mLog, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.log_item, null);
                }
                TextView timeStamp = (TextView) view.findViewById(R.id.tv_timestamp);
                TextView minutes = (TextView) view.findViewById(R.id.tv_minutesRead);
                TextView notes = (TextView) view.findViewById(R.id.tv_notes);

                DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                        .appendMonthOfYearShortText()
                        .appendLiteral(" ")
                        .appendDayOfMonth(1)
                        .appendLiteral(", ")
                        .appendYear(4, 4)
                        .toFormatter();
                timeStamp.setText(mLog.getTimeStamp().toString(fmt));
                minutes.setText("" + mLog.getMinutesRead() + " Minutes ");
                if (mLog.getNotes().equals("")) {
                    mLog.setNotes("Blank note");
                }
                notes.setText(mLog.getNotes());
                //inflate child view to listview
                return view;
            }
        };
    }
}
