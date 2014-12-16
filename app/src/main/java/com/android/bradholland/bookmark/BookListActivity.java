package com.android.bradholland.bookmark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;


public class BookListActivity extends ActionBarActivity {

    private ParseQueryAdapter<Book> booksQueryAdapter;
    private String selectedBookObjectId;
    private String contextBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All books");


        //get current user config and store
        final ParseUser currentUser = ParseUser.getCurrentUser();

        //set up book query
        //doListQuery();

        //constructs an adapter to apply queried books' data into the listview
        ParseQueryAdapter.QueryFactory<Book> factory =
            new ParseQueryAdapter.QueryFactory<Book>() {
                public ParseQuery<Book> create() {
                    ParseQuery query = new ParseQuery("Books");
                    query.whereEqualTo("user", currentUser);
                    query.orderByDescending("createdAt");
                    return query;
            }
        };

        booksQueryAdapter = new ParseQueryAdapter<Book>(this, factory) {

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

                ParseImageView imageView = (ParseImageView) view.findViewById(R.id.iv_cover_photo);
                TextView titleView = (TextView) view.findViewById(R.id.title_view);
                TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
                TextView ratingView = (TextView) view.findViewById(R.id.rating_view);

                Drawable d = getResources().getDrawable(R.drawable.library_icon_128);
                imageView.setPlaceholder(d);
                titleView.setText(book.getTitle());
                descriptionView.setText(book.getDescription());
                ratingView.setText("Your Rating: " + book.getRating());

                ParseFile photoFile = book.getParseFile("coverPhoto");
                if (photoFile != null) {
                    imageView.setParseFile(photoFile);
                    imageView.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            // nothing to do
                        }
                    });
                } else {
                    imageView.setPlaceholder(d);
                }

                return view;
            }
        };

        booksQueryAdapter.setTextKey("title");
        booksQueryAdapter.setTextKey("description");
        booksQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Book>() {
            ProgressDialog progress = new ProgressDialog(BookListActivity.this);
            @Override
            public void onLoading() {
                Log.v("TAG", "ON LOADING CALLED");
                progress.setTitle("Loading your books");
                progress.setMessage("Please wait...");
                progress.show();
            }

            @Override
            public void onLoaded(List<Book> books, Exception e) {
                Log.v("TAG", "ON LOADED CALLED");
                progress.dismiss();
            }
        });

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
        if (view.getId() == R.id.books_listview) {
            ListView books_listview = (ListView) view;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Book selectedBook = (Book) books_listview.getItemAtPosition(info.position);
            contextBookId = selectedBook.getObjectId();

            Log.v("context menu", "this books' parse object id is = " + selectedBook.getObjectId() );

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.booklist_item_overflow, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String s = String.valueOf(info.id);
            Log.v("context menu", s + "is this books' position");

            switch (item.getItemId()) {

            case R.id.make_current_title:
                ParseQuery<Book> currentTitleQuery = ParseQuery.getQuery("Books");
                currentTitleQuery.getInBackground(contextBookId, new GetCallback<Book>() {
                    @Override
                    public void done(Book book, ParseException e) {
                        book.setCurrentTitle(true);
                    }
                });
                doListQuery();

            case R.id.delete_book:
                Log.v("context menu", "Book should be deleted!");
                ParseQuery<Book> deleteQuery = ParseQuery.getQuery("Books");
                deleteQuery.getInBackground(contextBookId, new GetCallback<Book>() {
                    @Override
                    public void done(Book book, ParseException e) {
                        book.deleteInBackground();
                        doListQuery();
                    }
                });


            default: return false;
        }
    }


  public void doListQuery() {
      booksQueryAdapter.loadObjects();
  }


    @Override
    protected void onResume() {
        super.onResume();
        doListQuery();
        Log.v("TAG", "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


