package com.android.bradholland.bookmark;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;


public class GraphActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);
        mBarChart.addBar(new BarModel("Jan", 28.3f, 0xFF123456));
        mBarChart.addBar(new BarModel("Feb", 20.f,  0xFF343456));
        mBarChart.addBar(new BarModel("Mar", 30.3f, 0xFF563456));
        mBarChart.addBar(new BarModel("Apr", 12.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel("May", 20.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel("Jun", 5.6f,  0xFF343456));
        mBarChart.addBar(new BarModel("Jul", 67f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel("Aug", 4.f,  0xFF1BA4E6));
        mBarChart.addBar(new BarModel("Sept", 40.5f,  0xFF1FF4AC ));
        mBarChart.addBar(new BarModel("Oct", 1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel("Nov", 2.f,  0xFF343456));
        mBarChart.addBar(new BarModel("Dec", 120f, 0xFF873F56));

        mBarChart.startAnimation();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
