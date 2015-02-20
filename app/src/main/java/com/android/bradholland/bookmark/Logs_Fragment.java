package com.android.bradholland.bookmark;

import android.support.v4.app.Fragment;

/**
 * Created by Brad on 2/20/2015.
 */
public class Logs_Fragment extends Fragment {
    /*

    ParseQueryAdapter.QueryFactory<com.android.bradholland.bookmark.Log> logfactory =
            new ParseQueryAdapter.QueryFactory<com.android.bradholland.bookmark.Log>() {
                public ParseQuery<Log> create() {
                    ParseQuery logQuery = new ParseQuery("Logs");
                    logQuery.whereEqualTo("Books", mBook);
                    logQuery.setLimit(5);
                    logQuery.orderByDescending("timeStamp");
                    return logQuery;
                }
            };

    adapt(logfactory);


    public void adapt(ParseQueryAdapter.QueryFactory logFactory) {
        logsQueryAdapter = new ParseQueryAdapter<com.android.bradholland.bookmark.Log>(this, logFactory) {
            @Override
            public View getItemView(com.android.bradholland.bookmark.Log mLog, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.log_item, null);
                }
                TextView timeStamp = (TextView) findViewById(R.id.tv_timestamp);
                TextView minutes = (TextView) findViewById(R.id.tv_minutesRead);
                TextView notes = (TextView) findViewById(R.id.tv_notes);
                if (timeStamp == null) {
                    android.util.Log.v("STAMP", "IS NULL");
                }
                //timeStamp.setText(mLog.getTimeStamp().toString());
                // minutes.setText("" + mLog.getMinutesRead() + "mins");
                // notes.setText(mLog.getNotes());

                return view;
            }
        };
    }
*/
}
