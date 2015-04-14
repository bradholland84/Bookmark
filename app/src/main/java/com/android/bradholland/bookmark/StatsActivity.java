package com.android.bradholland.bookmark;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class StatsActivity extends ActionBarActivity {

    // Declaring Your View and Variables

    private Toolbar toolbar;
    private List<Book> bookList;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private Spinner spinner;
    private BookSpinnerAdapter spinnerAdapter;
    private Intent intent;
    private boolean selected;
    private CharSequence Titles[]={"Weekly","Monthly"};
    private int Numboftabs =2;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stats);

        intent = getIntent();
        bookId = intent.getStringExtra("id");

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateFragments();

        ParseQuery query = ParseQuery.getQuery("Books");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        if (!isNetworkAvailable()) {
            query.fromLocalDatastore();
        }
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> list, ParseException e) {
                bookList = list;
                View spinnerContainer = LayoutInflater.from(getBaseContext()).inflate(R.layout.toolbar_spinner,
                        toolbar, false);
                ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                toolbar.addView(spinnerContainer, lp);

                spinnerAdapter = new BookSpinnerAdapter(getApplicationContext());
                spinnerAdapter.addItems(bookList);

                spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(spinnerAdapter.getPosition(bookId));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        bookId = spinnerAdapter.getItem(position).getObjectId();
                        if (selected) {
                            populateFragments();
                        }
                        selected = true;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // do nothing
                    }
                });

            }
        });

       // getSupportActionBar().setTitle("Your Stats");

    }

    public void populateFragments() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs, bookId);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}