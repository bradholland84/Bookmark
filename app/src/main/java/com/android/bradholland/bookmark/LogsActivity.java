package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Created by Brad on 2/20/2015.
 */
public class LogsActivity extends ActionBarActivity {

    private String bookId;
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


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
