package com.android.bradholland.bookmark;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Brad on 1/22/2015.
 */
public class StatsFragment extends Fragment {

    private BarChart weekBarChart;
    private BarChart allTitlesBarChart;
    private String title;
    private JSONArray minutesHistory;

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

        weekBarChart = (BarChart) v.findViewById(R.id.current_title_chart);
        allTitlesBarChart = (BarChart) v.findViewById(R.id.all_books_chart);

        ParseQuery<Book> query = ParseQuery.getQuery("Books");
        //query.fromLocalDatastore();
        query.whereEqualTo("objectId", getArguments().getString("bookId"));
        query.getFirstInBackground(new GetCallback<Book>() {
            @Override
            public void done(Book book, ParseException e) {
                title = book.getTitle();
                minutesHistory = book.getWeekMinutesHistory();
                populateChart(minutesHistory, weekBarChart);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // Populates the chart view with data
    private void populateChart(JSONArray array, BarChart chart) {
        try {
            for (int i = 0; i < array.length(); i++) {
                BarModel bm = new BarModel(array.getInt(i));
                chart.addBar(bm);
            }
        } catch (JSONException E) {
            E.printStackTrace();
        }
    }

}
