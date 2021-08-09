package algonquin.cst2335.grouproject;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {

    /**
     * name of the database
     */
    public static final String database_name = "MyDatabase";
    /**
     * database version
     */
    public static final int version = 1;

    /**
     * columns' names in the database table
     */
    public static final String TABLE_NAME = "charging_stations";

    public static final String COL_ID = "id";

    public static final String COL_LAT = "latitude";

    public static final String COL_LocTitle = "locTitle";

    public static final String COL_LONG = "longitude";

    public static final String COL_PHONE = "phone";

    /**
     * a constructor to create an openHelper
     * @param ctx
     */
    public MyOpenHelper(Activity ctx) {
        super(ctx, database_name, null, version);

    }

    /**
     * generating the table
     * @param db
     */
    public void onCreate(SQLiteDatabase db){



        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_LocTitle + " TEXT, " + COL_LAT + " REAL, "
                + COL_LONG + " REAL, " + COL_PHONE + " TEXT)");
    }

    /**
     * a method to upgrade the database
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * method to downgrade the database
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
