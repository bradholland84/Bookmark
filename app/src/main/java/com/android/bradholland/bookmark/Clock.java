package com.android.bradholland.bookmark;

import com.parse.ParseUser;

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
        //TODO instant here is null, causes illegal argument exception
        Months months = Months.monthsBetween(currentDateTime, dt1);
        if (months.getMonths() > 0) {
            return true;
        }
        return false;
    }

    public boolean weekPassed(DateTime dt1) {
        DateTime currentDateTime = new DateTime();
        Weeks weeks = Weeks.weeksBetween(currentDateTime, dt1);
        if (weeks.getWeeks() > 0) {
            return true;
        }
        return false;
    }


    public void updateBookTimes(Book book, ParseUser user) {
        DateTime userCreatedAtDateTime = new DateTime(user.getCreatedAt());
        DateTime bookCreatedAtDateTime = new DateTime(book.getCreatedAt());
        DateTime currentDateTime = new DateTime();

        // duration of time representing the time between when the user signed up
        // and when they first created this book
        Duration userBookCreatedGap = new Duration(
                userCreatedAtDateTime, bookCreatedAtDateTime);

        // duration representing how long it has been since the book was
        // created and the current moment
        Duration sinceCreated = new Duration(
                bookCreatedAtDateTime, currentDateTime);


    }

}
