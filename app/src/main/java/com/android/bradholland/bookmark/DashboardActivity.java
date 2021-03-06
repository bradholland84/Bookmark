package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class DashboardActivity extends ActionBarActivity {

    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<Log> query = ParseQuery.getQuery("Logs");
        query.whereEqualTo("createdBy", currentUser);
        query.orderByDescending("timeStamp");
        query.getFirstInBackground(new GetCallback<Log>() {
            @Override
            public void done(Log mLog, ParseException e) {
                bookId = mLog.getBookId();
            }
        });



        Button viewAllTitlesButton = (Button) findViewById(R.id.btn_title_list_intent);
        viewAllTitlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        Button statsButton = (Button) findViewById(R.id.btn_stats_button);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, StatsActivity.class);
                intent.putExtra("id", bookId);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
