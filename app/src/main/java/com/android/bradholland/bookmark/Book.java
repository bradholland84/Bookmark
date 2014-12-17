package com.android.bradholland.bookmark;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Brad on 10/8/2014.
 */

@ParseClassName("Books")
public class Book extends ParseObject{

    public Book() {

    }

    public Date getMonthDate() {
        return getDate("monthDate");
    }

    public void setMonthDate(DateTime dt) {
        put("monthDate", dt.toDate());
    }

    public Date getWeekDate() {
        return getDate("weekDate");
    }

    public void setWeekDate(DateTime dt) {
        put("weekDate", dt.toDate());
    }

    public DateTime getCreatedAtDateTime() {
        return new DateTime(this.getCreatedAt());
    }

    public boolean isCurrentTitle() {
        return getBoolean("currentTitle");
    }

    public void setCurrentTitle(boolean isCurrentTitle) {
        put("currentTitle", isCurrentTitle);
        if (isCurrentTitle) {
            //TODO: make parse query here to set all other books as not current
        }
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public double getRating() {
        return getDouble("rating");
    }

    public void setRating(double value) {
        put("rating", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("coverPhoto");
    }

    public void setPhotoFile(ParseFile file) {
        put("coverPhoto", file);
    }

    public int getWeeklyMinutes() {
        return getInt("weeklyMinutes");
    }

    public void setWeeklyMinutes(int value) {
        put("weeklyMinutes", value);
    }

    public int getMonthlyMinutes() {
        return getInt("monthlyMinutes");
    }

    public void setMonthlyMinutes(int value) {
        put("monthlyMinutes", value);
    }

    public int getTotalMinutes() {
        return getInt("totalMinutes");
    }

    public void setTotalMinutes(int value) {
        put("totalMinutes", value);
    }

    public static ParseQuery<Book> getQuery() {
        return ParseQuery.getQuery(Book.class);
    }

}
