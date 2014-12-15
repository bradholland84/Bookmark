package com.android.bradholland.bookmark;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

/**
 * Created by Brad on 12/15/2014.
 */

@ParseClassName("Clocks")
public class Clock extends ParseObject {
    private DateTime initDateTime;


    //initial constructor
    public Clock() {
        initDateTime = new DateTime();
    }

    private Duration getDuration (DateTime initDateTime) {
        DateTime currentDateTime = new DateTime();
        Interval interval = new Interval(initDateTime, currentDateTime);
        return interval.toDuration();
    }





}
