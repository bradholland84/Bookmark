package com.android.bradholland.bookmark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;


public class BookListActivity extends ActionBarActivity {

    private ParseQueryAdapter<Book> booksQueryAdapter;
    private ListView booksListView;
    private String selectedBookObjectId;
    private String contextBookId;
    public static final int DATA_CHANGED_REQUEST = 1;

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

        //constructs an adapter to apply queried books' data into the listview
        ParseQueryAdapter.QueryFactory<Book> factory =
            new ParseQueryAdapter.QueryFactory<Book>() {
                public ParseQuery<Book> create() {
                    ParseQuery query = new ParseQuery("Books");
                    query.fromLocalDatastore();
                    query.whereEqualTo("user", currentUser);
                    query.orderByDescending("createdAt");
                    return query;
            }
        };

        adapt(factory);

        // attach query adapter to view
       booksListView = (ListView) findViewById(R.id.books_listview);
        booksListView.setAdapter(booksQueryAdapter);
        registerForContextMenu(booksListView);

        // usually when the user logs in on this device for the first time and has
        // not created any book objects (none are pinned in the background)
        if (booksQueryAdapter.getCount() == 0) {
            ParseQueryAdapter.QueryFactory<Book> onlineFactory =
                    new ParseQueryAdapter.QueryFactory<Book>() {
                        public ParseQuery<Book> create() {
                            ParseQuery query = new ParseQuery("Books");
                            query.whereEqualTo("user", currentUser);
                            query.orderByDescending("createdAt");
                            return query;
                        }
                    };
            adapt(onlineFactory);
            booksListView.setAdapter(booksQueryAdapter);
        }

        //floating action button declaration for awesome material design type button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_add_book);
        fab.attachToListView(booksListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookListActivity.this, AddBookActivity.class);
                startActivityForResult(intent, DATA_CHANGED_REQUEST);
            }
        });


        // set up click listener for book items
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book item = booksQueryAdapter.getItem(position);
                selectedBookObjectId = item.getObjectId();
                Log.v("BookID", selectedBookObjectId);

                Intent intent = new Intent(BookListActivity.this, BookDetailActivityParallax.class);
                intent.putExtra("id", selectedBookObjectId);
                startActivityForResult(intent, DATA_CHANGED_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("act", "result method called");
        if (requestCode == DATA_CHANGED_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.v("act", "result code OK");
                doListQuery();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu, adds items to the action bar
        getMenuInflater().inflate(R.menu.main_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
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

            case R.id.delete_book:
                deleteConfirmationDialog(contextBookId);

            default: return false;
        }
    }

    public void deleteConfirmationDialog(final String bookId) {
        new MaterialDialog.Builder(this)
                .title("Are you sure you want to delete this book?")
                .content("This action cannot be undone.")
                .positiveText("Delete")
                .negativeText("Cancel")
                .negativeColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.v("context menu", "Book should be deleted!");
                        ParseQuery<Book> deleteQuery = ParseQuery.getQuery("Books");
                        deleteQuery.fromLocalDatastore();
                        deleteQuery.getInBackground(bookId, new GetCallback<Book>() {
                            @Override
                            public void done(Book book, ParseException e) {
                                book.unpinInBackground();
                                book.deleteInBackground();
                                Toast.makeText(getBaseContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                                doListQuery();
                            }
                        });
                    }
                })
                .show();
    }

  public void adapt(ParseQueryAdapter.QueryFactory factory) {
      booksQueryAdapter = new ParseQueryAdapter<Book>(this, factory) {
          @Override
          public View getItemView(Book book, View view, ViewGroup parent) {
              if (view == null) {
                  view = View.inflate(getContext(), R.layout.book_item, null);
              }
              ParseImageView imageView = (ParseImageView) view.findViewById(R.id.iv_cover_photo);
              TextView titleView = (TextView) view.findViewById(R.id.title_view);
              TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
              TextView ratingView = (TextView) view.findViewById(R.id.rating_view);

              Drawable d = getResources().getDrawable(R.drawable.library_icon_128);
              imageView.setPlaceholder(d);
              titleView.setText(book.getTitle());
              descriptionView.setText(book.getDescription());
              String stars = "\u2605";
              String half = "\u00BD";
              String rating = "";
              for (double i = 0.0; i < Math.floor(book.getRating()); i++) {
                  rating += stars;
              }
              if (Math.floor(book.getRating()) != book.getRating()) {
                  rating += half;
              }
              ratingView.setText(rating);

              ParseFile photoFile = book.getParseFile("coverPhotoThumbnail");
              if (photoFile != null) {
                  imageView.setParseFile(photoFile);
                  imageView.loadInBackground(new GetDataCallback() {
                      @Override
                      public void done(byte[] data, ParseException e) {
                          // nothing to do
                      }
                  });
              } else {
                  // stock placeholder if no cover photo
                  imageView.setImageDrawable(d);
              }

              //inflate child view in listview
              return view;
          }
      };

      booksQueryAdapter.setTextKey("title");
      booksQueryAdapter.setTextKey("description");
      booksQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Book>() {
          ProgressDialog progress = new ProgressDialog(BookListActivity.this);
          @Override
          public void onLoading() {
              progress.setTitle("Loading your books");
              progress.setMessage("Please wait...");
              progress.show();
          }

          @Override
          public void onLoaded(List<Book> books, Exception e) {
              progress.dismiss();
              ParseObject.pinAllInBackground(books);
          }
      });
  }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

  public void doListQuery() {
      booksQueryAdapter.loadObjects();
  }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TAG", "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


