package com.android.bradholland.bookmark;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Brad on 1/22/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[];
    int NumOfTabs;

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);

        this.Titles = mTitles;
        this.NumOfTabs = mNumbOfTabs;
    }
    //This method returns the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) {
            WeeklyStatsFragment weekly = new WeeklyStatsFragment();
            return weekly;
        } else {
            MonthlyStatsFragment monthly = new MonthlyStatsFragment();
            return monthly;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
    @Override
    public int getCount() {
        return NumOfTabs;
    }
}
