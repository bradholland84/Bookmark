package com.android.bradholland.bookmark;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Brad on 2/13/2015.
 */

@ParseClassName("Logs")
public class Log extends ParseObject {

    public Log() {

    }

    public void setTimeStamp(DateTime dt) {
        put("timeStamp", dt.toDate());
    }

    public Date getTimeStamp() {
        return getDate("timeStamp");
    }

    public void setMinutesRead(int n) {
        put("minutes", n);
    }

    public int getMinutesRead() {
        return getInt("minutes");
    }

    public void setNotes(String note) {
        put("notes", note);
    }

    public String getNotes() {
        return getString("notes");
    }

    public void setBookId(String id) {
        put("bookId", id);
    }

    public String getBookId() {
        return getString("bookId");
    }
}
