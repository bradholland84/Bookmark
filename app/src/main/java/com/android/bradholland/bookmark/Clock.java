package com.android.bradholland.bookmark;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**
 * Created by Brad on 12/15/2014.
 * helper class to give books their time data
 */


public class Clock {

    public Duration getDuration (DateTime initialDateTime) {
        DateTime currentDateTime = new DateTime();
        Interval interval = new Interval(initialDateTime, currentDateTime);
        return interval.toDuration();
    }

    private long minsDuration(Duration d) {
        return d.getStandardMinutes();
    }

}
