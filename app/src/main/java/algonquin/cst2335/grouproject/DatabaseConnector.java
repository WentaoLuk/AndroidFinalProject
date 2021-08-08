package algonquin.cst2335.grouproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseConnector extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String TABLE_NAME = "fave_list";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_IMG_URL = "img_url";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_LINK = "link";
    public static final String COLUMN_NAME_ID = "id";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_NAME_ID + " TEXT, " +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_DATE + " TEXT," +
                    COLUMN_NAME_IMG_URL + " TEXT," +
                    COLUMN_NAME_DESCRIPTION + " TEXT," +
                    COLUMN_NAME_LINK + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseConnector(Context context) {
        super(context, TABLE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}