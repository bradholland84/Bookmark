package com.android.bradholland.bookmark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brad on 1/22/2015.
 */
public class StatsFragment extends Fragment {

    private BarChart barChart;
    private BarChart allTitlesBarChart;
    private String title;
    private TextView allTitles;
    private TextView currentTitle;
    private Book mBook;


    public static StatsFragment newInstance(String bookId, int layoutInt) {
        StatsFragment f = new StatsFragment();

        Bundle args = new Bundle();
        args.putString("bookId", bookId);
        args.putInt("layoutInt", layoutInt);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getArguments().getInt("layoutInt"),container,false);

        currentTitle = (TextView) v.findViewById((R.id.current_title));
        allTitles = (TextView) v.findViewById((R.id.overall_titles_text));
        barChart = (BarChart) v.findViewById(R.id.current_title_chart);
        allTitlesBarChart = (BarChart) v.findViewById(R.id.all_books_chart);

        ParseQuery<Book> query = ParseQuery.getQuery("Books");
        query.whereEqualTo("objectId", getArguments().getString("bookId"));
        query.getFirstInBackground(new GetCallback<Book>() {
            @Override
            public void done(Book book, ParseException e) {
                mBook = book;
                title = book.getTitle();
                currentTitle.setText(title);
                newPopulateChart();
            }
        });

        populateAllBooksChart();

        allTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookListActivity.class);
                startActivity(intent);
            }
        });

        currentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookDetailActivityParallax.class);
                intent.putExtra("id", getArguments().getString("bookId"));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // Populates the data chart for the specific book
    private void newPopulateChart() {
        ParseQuery<Log> logQuery = ParseQuery.getQuery("Logs");
        logQuery.whereEqualTo("parent", mBook);
        logQuery.orderByAscending("timeStamp");
        logQuery.findInBackground(new FindCallback<Log>() {
            @Override
            public void done(List<Log> logs, ParseException e) {
                if (getArguments().getInt("layoutInt") == R.layout.tab_weekly) {
                    logsToChartData(logs, barChart, true);
                } else {
                    logsToChartData(logs, barChart, false);
                }
            }
        });
    }

    // Populates the data chart for all titles
    private void populateAllBooksChart() {
        ParseQuery<Log> logQuery = ParseQuery.getQuery("Logs");
        logQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        logQuery.orderByAscending("timeStamp");
        logQuery.findInBackground(new FindCallback<Log>() {
            @Override
            public void done(List<Log> logs, ParseException e) {
                if (getArguments().getInt("layoutInt") == R.layout.tab_weekly) {
                    logsToChartData(logs, allTitlesBarChart, true);
                } else {
                    logsToChartData(logs, allTitlesBarChart, false);
                }
            }
        });
    }

    // Uses the logs for either the specific book or all books, and populates the charts with
    // their data
    private void logsToChartData(List<Log> logs, BarChart chart, boolean isWeek) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                //weekly data formatter

                .appendWeekOfWeekyear(1)
                .appendLiteral("/52  '")
                .appendTwoDigitYear(2050)
                .toFormatter();
        if (!isWeek) {
            //monthly data formatter
            fmt = new DateTimeFormatterBuilder()
                    .appendMonthOfYearShortText()
                    .appendLiteral(" '")
                    .appendTwoDigitYear(2050)
                    .toFormatter();
        }

        Map<String, Integer> logMap = new LinkedHashMap<>();
        DateTime current = new DateTime();
        String currentKey = current.toString(fmt);
        if (isWeek) {
            for (int i = 1; i < 6; i++) {
                DateTime next = current.minusWeeks(6 - i);
                String nextKey = next.toString(fmt);
                logMap.put(nextKey, 0);
            }
        } else {
            for (int i = 1; i < 6; i++) {
                DateTime next = current.minusMonths(6 - i);
                String nextKey = next.toString(fmt);
                logMap.put(nextKey, 0);
            }
        }
        logMap.put(currentKey, 0);
        for (Log entry : logs) {
            String keyString = entry.getTimeStamp().toString(fmt);
            //create key string for comparison
            if (logMap.containsKey(keyString)) {
                int mins = logMap.get(keyString);
                logMap.put(keyString, entry.getMinutesRead() + mins);
            }
        }
        // Use map data to create bar models, add to chart
        for (String key : logMap.keySet()) {
            BarModel bm;
            if (key.equals(currentKey)) {
                // highlight current week/month bar
                 bm = new BarModel("current", logMap.get(key), 0xffffca28);
            } else {
                 bm = new BarModel(key, logMap.get(key), 0xFF44B8B8);
            }
            chart.addBar(bm);
        }
        chart.startAnimation();
    }
}
