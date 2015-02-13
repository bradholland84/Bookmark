package com.android.bradholland.bookmark;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Brad on 1/22/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence Titles[];
    private int NumOfTabs;
    private String bookId;

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabs, String bookId) {
        super(fm);
        this.bookId = bookId;
        this.Titles = mTitles;
        this.NumOfTabs = mNumbOfTabs;
    }
    //This method returns the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) {
            StatsFragment weekly = StatsFragment.newInstance(bookId, R.layout.tab_weekly);
            return weekly;
        } else {
            StatsFragment monthly = StatsFragment.newInstance(bookId, R.layout.tab_monthly);
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
