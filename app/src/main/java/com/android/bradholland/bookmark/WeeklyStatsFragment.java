package com.android.bradholland.bookmark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.BarChart;

/**
 * Created by Brad on 1/22/2015.
 */
public class WeeklyStatsFragment extends Fragment {

    private BarChart weekBarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_weekly,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
super.onActivityCreated(savedInstanceState);

    }
}
