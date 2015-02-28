package com.android.bradholland.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brad on 2/27/2015.
 */
public class BookSpinnerAdapter extends BaseAdapter {

    private List<Book> mItems = new ArrayList<>();
    private LayoutInflater mInflater;

    public BookSpinnerAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(Book yourObject) {
        mItems.add(yourObject);
    }

    public void addItems(List<Book> yourObjectList) {
        mItems.addAll(yourObjectList);
    }

    public int getPosition(String key) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getObjectId().equals(key)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Book getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = mInflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mInflater.inflate(R.layout.
                    toolbar_spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getTitle(): "";
    }
}
