package algonquin.cst2335.grouproject;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is the movie activity, allows users to search for movies and save them in a database.
 * @author Jade Mak
 */
public class MovieActivity extends AppCompatActivity {
    /** This holds the plot of the searched movie**/
    private String plot = null;
    /** This holds the poster url of the searched movie**/
    private String iconName = null;
    /** This holds the title of the searched movie**/
    private String title = null;
    /** This holds the year of the searched movie**/
    private String year = null;
    /** This holds the rating of the searched movie**/
    private String rating = null;
    /** This holds the runtime of the searched movie**/
    private String runtime = null;
    /** This holds the actors of the searched movie**/
    private String actors = null;
    /** This holds the url of the searched movie**/
    private String stringURL;
    /** This is used to connect to the db**/
    SQLiteDatabase db;

    /**
     * This function navigates menu item selection and function
     * @param item is the menu option clicked
     * @return true or false if any of these menu options are selected
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        TextView movieTitle = findViewById(R.id.movieTitle);
        TextView movieYear = findViewById(R.id.year);
        TextView rating = findViewById(R.id.rating);
        TextView runtime = findViewById(R.id.runtime);
        TextView plot = findViewById(R.id.plot);
        TextView actors = findViewById(R.id.actors);
        ImageView icon = findViewById(R.id.icon);
        Button savebtn = findViewById(R.id.saveButton);
        EditText movieField = findViewById(R.id.movieTextField);
        float oldSize = 14;
        switch(item.getItemId()){
            case 5:
                String movieName = item.getTitle().toString();
                runSearch(movieName);
                movieField.setText(movieName);
                break;

            case R.id.hide_views:
                movieTitle.setVisibility(View.INVISIBLE);
                movieYear.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                runtime.setVisibility(View.INVISIBLE);
                plot.setVisibility(View.INVISIBLE);
                actors.setVisibility(View.INVISIBLE);
                icon.setVisibility(View.INVISIBLE);
                savebtn.setVisibility(View.INVISIBLE);
                movieField.setText("");
                break;
            case R.id.id_saved:
                Intent savePage = new Intent(MovieActivity.this,SavedMovies.class);
                startActivity(savePage);

                break;
            case R.id.id_homepage:
                Intent homePage = new Intent(MovieActivity.this,MainActivity.class);
                startActivity(homePage);
                break;
            case R.id.help:
                new AlertDialog.Builder(MovieActivity.this)
                        .setTitle("Need Help?")
                        .setMessage("Press the clear icon to clear search. To redo a previous search, select a query from the toolbar with the 3 dots." +
                                "To search for a movie, enter the movie title and press 'search'." +
                                "To save movie details for later, press 'SAVE MOVIE' at the bottom of the screen. To view saved movies, click on the movie history icon in the toolbar.")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function connects to the server  and searches for the movie name. Then it displays the movie info to the screen.
     * @param movieName name of the movie typed in the edittext
     */
    private void runSearch(String movieName) {
        // make the alert dialog with the spinning progress bar
        AlertDialog dialog = new AlertDialog.Builder(MovieActivity.this)
                .setTitle("Getting Movie")
                .setMessage("Time travelling back to BlockBusters to ask the employees for the movie " +movieName)
                .setView( new ProgressBar(MovieActivity.this))
                .show();


        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            try {
                // URL for the weather server info
                stringURL ="http://www.omdbapi.com/?apikey=6c9862c2&r=xml&t="
                        + URLEncoder.encode(movieName,"UTF-8");

                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( in  , "UTF-8");

                while( xpp.next() != XmlPullParser.END_DOCUMENT )
                {
                    switch (xpp.getEventType())
                    {
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("movie"))
                            {
                                title = xpp.getAttributeValue(null, "title");  //this gets the current temperature
                                year = xpp.getAttributeValue(null, "year"); //this gets the min temperature
                                rating = xpp.getAttributeValue(null, "rated"); //this gets the max temperature
                                runtime = xpp.getAttributeValue(null,"runtime");
                                actors = xpp.getAttributeValue(null,"actors");
                                plot = xpp.getAttributeValue(null, "plot");  //this gets the weather
                                iconName = xpp.getAttributeValue(null, "poster"); //this gets the icon name

                            }
                            break;

                        case XmlPullParser.END_TAG:

                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }
                }

                Bitmap image = null;

                File file = new File(getFilesDir(),title);
                if (file.exists()){
                    image = BitmapFactory.decodeFile(getFilesDir()+"/"+title);
                }
                else{
                    URL imgUrl = new URL( iconName );
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());

                    }
                }

                FileOutputStream fOut = null;
                try {
                    fOut = openFileOutput(title, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                }



                Bitmap finalImage = image;
                runOnUiThread(() ->{
                    TextView tv = findViewById(R.id.movieTitle);
                    tv.setText("The movie title is: " + title);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.year);
                    tv.setText("Year released: "+year);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.rating);
                    tv.setText("Movie Rating:  "+ rating);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.runtime);
                    tv.setText("Movie Runtime: "+ runtime);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.actors);
                    tv.setText("Main Actors: "+ actors);
                    tv.setVisibility(View.VISIBLE);

                    tv = findViewById(R.id.plot);
                    tv.setText("Movie Description: "+plot);
                    tv.setVisibility(View.VISIBLE);

                    ImageView iv = findViewById(R.id.icon);
                    iv.setImageBitmap(finalImage);
                    iv.setVisibility(View.VISIBLE);

                    Button saveBtn = findViewById(R.id.saveButton);
                    saveBtn.setVisibility(View.VISIBLE);
                    //hide dialog
                    dialog.hide();
                });


            }
            catch(IOException |  XmlPullParserException ioe){
                Log.e("Connection error: ",ioe.getMessage());
            }
        });
    }

    /**
     * Inflates the option menu
     * @param menu the menu to be inflated
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_activity_actions, menu);
        return true;
    }


    /**
     * This function sets the layout of the movie activity as well as the drawerlayout, popoutmenu.
     * It also connects to the database and sets actions to the search button and save movie button.
     * Also adds the search term to the menu
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        MovieOpenHelper opener = new MovieOpenHelper(this);
        db = opener.getWritableDatabase();

        SharedPreferences prefs = getSharedPreferences("MovieData",Context.MODE_PRIVATE);
        String movieTitle = prefs.getString("MovieTitle","");
        EditText movieText = findViewById(R.id.movieTextField);
        movieText.setText(movieTitle);
        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item -> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }));

        Button searchBtn = findViewById(R.id.searchButton);
        searchBtn.setOnClickListener((clk)->{
            //get the moviename for the alert
            String movieName = movieText.getText().toString();
            //Add stuff to overflow menu
            myToolbar.getMenu().add( 0, 5, 0, movieName).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runSearch(movieName);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("MovieTitle",movieName);
            editor.apply();
        });

        Button saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener( clk ->{
            saveMovie();
        });

    }

    /**
     * This function inserts the loaded movie into the database.
     */
    public void saveMovie(){
        ContentValues newRow = new ContentValues();
        newRow.put(MovieOpenHelper.col_title,title );
        newRow.put(MovieOpenHelper.col_runtime,runtime);
        newRow.put(MovieOpenHelper.col_year,year);
        newRow.put(MovieOpenHelper.col_rating,rating);
        newRow.put(MovieOpenHelper.col_plot,plot);
        newRow.put(MovieOpenHelper.col_poster,iconName);
        newRow.put(MovieOpenHelper.col_actors,actors);

        db.insert(MovieOpenHelper.TABLE_NAME,MovieOpenHelper.col_title,newRow);
        Toast toast = Toast.makeText(getApplicationContext(),"Movie Saved",Toast.LENGTH_SHORT);
        toast.show();
    }



}