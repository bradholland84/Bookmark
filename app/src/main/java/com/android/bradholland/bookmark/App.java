package com.android.bradholland.bookmark;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Brad on 10/8/2014.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        //parse objects must be registered before Parse initialization!!!
        ParseObject.registerSubclass(Book.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, "2EZ7UE9h7CmotK7mXoEDbADyJYOs9p3cbKdM1hdp", "3OHAX3n6EE4x4xfpw3FYvh8MJ6btuQQOHTIu3GXo");

    }
}
