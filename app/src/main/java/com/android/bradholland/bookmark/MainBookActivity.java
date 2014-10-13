package com.android.bradholland.bookmark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MainBookActivity extends Activity {

    private ParseQueryAdapter<Book> booksQueryAdapter;
    private String selectedBookObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_book);


        ParseQueryAdapter.QueryFactory<Book> factory =
         new ParseQueryAdapter.QueryFactory<Book>() {
            @Override
            public ParseQuery<Book> create() {
                ParseQuery<Book> query = Book.getQuery();
                query.include("title");
                query.include("description");
                //query.orderByDescending("date");
                return query;
            }
        };


        booksQueryAdapter = new ParseQueryAdapter<Book>(this, factory) {
        @Override
        public View getItemView(Book book, View view, ViewGroup parent) {
            if (view == null) {
                view = View.inflate(getContext(), R.layout.book_item, null);
            }
            TextView titleView = (TextView) view.findViewById(R.id.title_view);
            TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
            titleView.setText(book.getTitle());
            descriptionView.setText(book.getDescription());
            return view;
        }
    };

        booksQueryAdapter.setAutoload(false);

        // attach query adapter to view
        ListView books_listview = (ListView) findViewById(R.id.books_listview);
        books_listview.setAdapter(booksQueryAdapter);

        books_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book item = booksQueryAdapter.getItem(position);
                selectedBookObjectId = item.getObjectId();

            }
        });



    Button addNewBook = (Button) findViewById(R.id.btn_add_book);
        addNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainBookActivity.this, AddBookActivity.class);
                //intent.putExtra(App.INTENT_EXTRA_TITLE, ****book title****)
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        doListQuery();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_book, menu);
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

    private void doListQuery() {
        booksQueryAdapter.loadObjects();
    }
}
