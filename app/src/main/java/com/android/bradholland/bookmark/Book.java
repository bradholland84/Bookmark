package com.android.bradholland.bookmark;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Brad on 10/8/2014.
 */

@ParseClassName("Books")
public class Book extends ParseObject{

    public Book() {

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

    public void setrating(double value) {
        put("rating", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public static ParseQuery<Book> getQuery() {
        return ParseQuery.getQuery(Book.class);
    }

}
