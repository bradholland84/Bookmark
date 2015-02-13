package com.android.bradholland.bookmark;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Weeks;

/**
 * Created by Brad on 12/15/2014.
 * helper class to give books their time data and handle distribution of
 * weekly, monthly, and total minutes for a book
 */


public class Clock {

    public Duration getDuration (DateTime initialDateTime) {
        DateTime currentDateTime = new DateTime();
        Interval interval = new Interval(initialDateTime, currentDateTime);
        return interval.toDuration();
    }

    // returns the length of this reading session in number of minutes
    public int sessionMinsTimeCalc(DateTime stampedDateTime) {
        DateTime currentDateTime = new DateTime();
        Duration sessionTime = new Duration(stampedDateTime, currentDateTime);
        return sessionTime.toStandardMinutes().getMinutes();
    }


    public boolean monthPassed(DateTime dt1) {
        DateTime currentDateTime = new DateTime();
        Log.v("instant", "current date/time is: " + currentDateTime.toString());
        Log.v("instant", "DT1 date/time is: " + dt1.toString());

        Months months = Months.monthsBetween(currentDateTime, dt1);
        Log.v("instant", "number of months between = " + months.getMonths());
        if (months.getMonths() < 0) {
            return true;
        }
        return false;
    }

    public boolean weekPassed(DateTime dt1) {
        DateTime currentDateTime = new DateTime();
        Weeks weeks = Weeks.weeksBetween(currentDateTime, dt1);
        if (weeks.getWeeks() < 0) {
            return true;
        }
        return false;
    }

   /*

   if (mClock.monthPassed(mBookMonthDateTime)) {
                    //a month has passed since the date has been updated
                    Log.v("time", "A month has passed");
                    if (monthMinutesHistory == null) {
                        Log.v("null", "monthMinutesHistory is a NULL VALUE");
                    }
                    mBook.add("monthMinutesHistory", mBook.getMonthlyMinutes());
                    mBook.setMonthlyMinutes(0);
                    int dayOfMonth = mBookMonthDateTime.getDayOfMonth();
                    DateTime dtNow = DateTime.now();
                    DateTime fixedDt =  dtNow.withField(DateTimeFieldType.dayOfMonth(), dayOfMonth);
                    mBook.setMonthDate(fixedDt);
                    mBook.saveEventually();
                } else {
                    Log.v("time", "A month has NOT passed");
                }

                if (mClock.weekPassed(mBookWeekDateTime)) {
                    //a week has passed since the date has been updated
                    Log.v("time", "A week has passed");
                    mBook.add("weekMinutesHistory", mBook.getWeeklyMinutes());
                    mBook.setWeeklyMinutes(0);
                    int dayOfWeek = mBookWeekDateTime.getDayOfWeek();
                    DateTime dtNow = DateTime.now();
                    DateTime fixedDt = dtNow.withField(DateTimeFieldType.dayOfWeek(), dayOfWeek);
                    mBook.setWeekDate(fixedDt);
                    mBook.saveEventually();
                } else {
                    Log.v("time", "A week has NOT passed");
                }
            }
        });

        // button used to start and stop reading session,
        // ---->  will eventually start an activity showing a timer
        // if that activity is left, reading session will pause
        toggleReading = (Button) findViewById(R.id.btn_start_reading);
        toggleReading.setTag(1);
        toggleReading.setText("Start reading");
        toggleReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int status =(Integer) view.getTag();
                if (status == 1) {
                    sessionStart = new DateTime();
                    toggleReading.setText("Stop reading");
                    view.setTag(0);
                } else {
                    //user stops reading, time is added to the total minutes
                    int minutesRead = mClock.sessionMinsTimeCalc(sessionStart);
                    minutes += minutesRead;
                    mBook.setTotalMinutes(minutes);

                    //still in the current month
                    int minsMonth = mBook.getMonthlyMinutes();
                    minsMonth += minutesRead;
                    mBook.setMonthlyMinutes(minsMonth);

                    //still in the current week
                    int minsWeek = mBook.getWeeklyMinutes();
                    minsWeek += minutesRead;
                    mBook.setWeeklyMinutes(minsWeek);

                    //save book with updated times
                    mBook.saveEventually(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Saved successfully.
                            } else {
                                // The save failed.
                            }
                        }
                    });
                    toggleReading.setText("Start reading");
                    view.setTag(1);
                }

    */

}
