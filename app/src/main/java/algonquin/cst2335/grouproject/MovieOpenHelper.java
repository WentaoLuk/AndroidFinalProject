package algonquin.cst2335.grouproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class helps manage the database for the movies
 * @author Jade Mak
 */
public class MovieOpenHelper extends SQLiteOpenHelper {
    /**This holds the name of the database **/
    public static final String name = "TheDatabase";
    /**This holds the version of the database **/
    public static final int version = 1;
    /**This holds the name of the table**/
    public static final String TABLE_NAME = "Movies";
    /**This holds the name of column title **/
    public static final String col_title = "Movie_Title";
    /**This holds the name of column year **/
    public static final String col_year = "Year";
    /**This holds the name of column rating **/
    public static final String col_rating = "Rating";
    /**This holds the name of column runtime **/
    public static final String col_runtime = "Runtime";
    /**This holds the name of column actors **/
    public static final String col_actors = "Actors";
    /**This holds the name of column plot**/
    public static final String col_plot = "Plot";
    /**This holds the name of column poster **/
    public static final String col_poster = "Poster";

    /**
     * Constructor of the helper class MovieOpenHelper
     * @param context context of the activity
     */
    public MovieOpenHelper(Context context) {
        super(context, name, null, version);
    }

    /**
     * Creates the database and the movie table
     * @param db the database for the movies
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + col_title + " TEXT,"
                + col_year + " TEXT,"
                + col_rating + " TEXT,"
                + col_runtime + " TEXT,"
                + col_actors + " TEXT,"
                + col_plot + " TEXT,"
                + col_poster + " TEXT);");
    }

    /**
     * This function recreates/updates the table whenever a row is added/deleted
     * @param db the database to be updated
     * @param oldVersion the previous table version
     * @param newVersion the new table version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
