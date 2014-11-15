package com.android.bradholland.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;


public class BookListActivity extends ActionBarActivity {

    private String selectedBookObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_book);

        //get current user config and store
        ParseUser currentUser = ParseUser.getCurrentUser();

        //set up book query
        doListQuery(currentUser);

        //constructs an adapter to apply queried books' data into the listview
        final ParseQueryAdapter<Book> booksQueryAdapter = new ParseQueryAdapter<Book>(this, "Books") {
            @Override
            public View getItemView(Book book, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.book_item, null);
                }
                ImageButton overflowButton = (ImageButton) view.findViewById(R.id.ib_overflow_button);
                overflowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMenu(v);
                    }
                });

                TextView titleView = (TextView) view.findViewById(R.id.title_view);
                TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
                TextView ratingView = (TextView) view.findViewById(R.id.rating_view);
                titleView.setText(book.getTitle());
                descriptionView.setText(book.getDescription());
                ratingView.setText("Your Rating: " + book.getRating());


                //registerForContextMenu(view);

                return view;
            }
        };



        booksQueryAdapter.setTextKey("title");
        booksQueryAdapter.setTextKey("description");

        // attach query adapter to view
        ListView books_listview = (ListView) findViewById(R.id.books_listview);
        books_listview.setAdapter(booksQueryAdapter);
        registerForContextMenu(books_listview);



        //floating action button declaration for awesome material design type button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_add_book);
        fab.attachToListView(books_listview);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookListActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });


        // set up click listener for book items
        books_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book item = booksQueryAdapter.getItem(position);
                selectedBookObjectId = item.getObjectId();
                Log.v("BookID", selectedBookObjectId);

                Intent intent = new Intent(BookListActivity.this, bookDetailActivity.class);
                intent.putExtra("id", selectedBookObjectId);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doListQuery(ParseUser.getCurrentUser());
        Log.v("TAG", "onResume() called");
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
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;

            case R.id.log_out:
                Log.v("logout", "log out called");
                ParseUser.logOut();
                Intent intent = new Intent(BookListActivity.this, DispatchActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

  public void showMenu(View v) {
      PopupMenu popup = new PopupMenu(this, v);
      popup.getMenuInflater().inflate(R.menu.booklist_item_overflow, popup.getMenu());
      popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.make_current_title:
                        //book.setCurrentTitle(true);
                    case R.id.delete_book:
                        //book.delete();
                    default: return false;
                }
          }
      });
    popup.show();
  }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.booklist_item_overflow, menu);

    }

 /*
  public void registerForContextMenu(View view) {
      view.setOnCreateContextMenuListener(this);
  }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.booklist_item_overflow, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
       AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
            switch (item.getItemId()) {
            case R.id.make_current_title:

            case R.id.delete_book:
                // delete it
            default: return false;
        }
    }
*/




  public void doListQuery(ParseUser currentUser) {
      ParseQuery<Book> query = ParseQuery.getQuery("Books");
      query.whereEqualTo("user", currentUser);
      query.fromLocalDatastore();
      query.findInBackground(new FindCallback<Book>() {
          @Override
          public void done(List<Book> booksList, ParseException e) {
              Book.pinAllInBackground("books", booksList);
              Log.v("books", "retrieved" + booksList.size() + "books");
          }
      });
    }
}
