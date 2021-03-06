package com.android.bradholland.bookmark;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.DateTime;

/**
 * Created by Brad on 10/8/2014.
 */

@ParseClassName("Books")
public class Book extends ParseObject{

    public Book() {

    }

    public DateTime getCreatedAtDateTime() {
        return new DateTime(this.getCreatedAt());
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

    public static ParseQuery<Book> getQuery() {
        return ParseQuery.getQuery(Book.class);
    }

}
