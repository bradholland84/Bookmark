package com.android.bradholland.bookmark;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Brad on 10/8/2014.
 */
public class App extends Application {

    public static final String INTENT_EXTRA_TITLE = "title";
    public static final String INTENT_EXTRA_DESCRIPTION = "description";

    @Override
    public void onCreate() {
        super.onCreate();

        //parse objects must be registered before Parse initialization!!!
        ParseObject.registerSubclass(Book.class);
        ParseObject.registerSubclass(Clock.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, "2EZ7UE9h7CmotK7mXoEDbADyJYOs9p3cbKdM1hdp", "3OHAX3n6EE4x4xfpw3FYvh8MJ6btuQQOHTIu3GXo");

    }
}
