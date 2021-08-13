package algonquin.cst2335.grouproject; /**
 * This is the main SQL database helper for the OC Transpo App
 * @Author Jenya Pribylov
 * @Version 1.0
 * @since 1.0
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class OCDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Name of this class
     */
    protected static final String ACTIVITY_NAME = "OCDatabaseHelper";
    /**
     * SQL DB for the bus stops
     */
    private SQLiteDatabase db;
    /**
     * Database Name
     */

    public static final String DB_NAME = "stoplist.db";
    /**
     * Version number
     */
    public static final int VERSION_NUM = 1;
    /**
     * table name for stations
     */
    public static final String TABLE_NAME = "stations";
    /**
     * table name for number of stops
     */
    public static final String STOP_NO = "station_number";
    /**
     * the name of each station
     */
    public static final String STOP_NAME = "station_name";
    /**
     * name of routes
     */
    public static final String TABLE_ROUTES = "routes";
    /**
     * number of each bus route
     */
    public static final String ROUTE_NO = "route_numhber";

    public static final String ADJUSTED_TIME = "adjustedtime";

    /**
     * OC Database Constructor
     * @param cntx
     */
    public OCDatabaseHelper (Context cntx) {
        super(cntx, DB_NAME, null, VERSION_NUM);

    }


    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    /**
     * Getter for query results
     * @return cursor which contains query results
     */
    public Cursor getRecords() {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Close the database if it is opened and it is not null
     */
    public void closeDatabase() {
        if(db!= null && db.isOpen()){
            db.close();
        }
    }
   
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + STOP_NO + " text, " + STOP_NAME + " text);" );

        db.execSQL(" CREATE TABLE " + TABLE_ROUTES + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," + ROUTE_NO + "text);");

        Log.i(ACTIVITY_NAME, "onCreate Called()");
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(ACTIVITY_NAME, "onOpen db was called");
    }

    public void openDB() {
        db = this.getWritableDatabase();
    }


    public Cursor getDBRecords() {
        return db.query(TABLE_NAME, null,null,null,null,null,null);
    }


    public void closeDB () {
        if (db != null && db.isOpen()){
            db.close();
        }
    }

    public void delete(){
        db.execSQL("delete from " + TABLE_NAME);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME   );
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_ROUTES);
        Log.i(ACTIVITY_NAME, "Calling onDowngrade(), oldVersion =" + oldVersion + ".newVersion =" + newVersion + ".");

       onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
    db.execSQL(" DROP TABLE IF EXISTS " + TABLE_ROUTES);
    Log.i(ACTIVITY_NAME, "Calling OnUpgrade() function, oldVersion=" + oldVersion + ". newversion= " + newVersion + ".");
    onCreate(db);




    }



    }

