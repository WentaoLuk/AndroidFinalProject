package algonquin.cst2335.grouproject;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.ListView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * class created to preview the favourite stations list, and allow the user to delete from the list
 */
public class FavouriteCarStations extends AppCompatActivity {
    /**
     * adapter for the list
     */
    StationAdapter adapter;

    /**
     * position of stations
     */
    public int positionClicked;

    /**
     * an arraylist for the charging stations favourite
     */
    ArrayList<StationObject> topStations = new ArrayList<StationObject>();

    /**
     * this method is to load favourite list on the screen and allow for deletion
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station);

        ListView favouriteList = (ListView)findViewById(R.id.favouriteList);

        MyOpenHelper opener = new MyOpenHelper(this);
        SQLiteDatabase db = opener.getWritableDatabase();

        String [] columns = {MyOpenHelper.COL_ID, MyOpenHelper.COL_LocTitle, MyOpenHelper.COL_LAT,
                MyOpenHelper.COL_LONG, MyOpenHelper.COL_PHONE};
        Cursor results = db.query(false, MyOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int titleColumnIndex = results.getColumnIndex(MyOpenHelper.COL_LocTitle);
        int latitudeColIndex = results.getColumnIndex(MyOpenHelper.COL_LAT);
        int idColIndex = results.getColumnIndex(MyOpenHelper.COL_ID);
        int longitudeColIndex = results.getColumnIndex(MyOpenHelper.COL_LONG);
        int phoneColIndex = results.getColumnIndex(MyOpenHelper.COL_PHONE);
        while(results.moveToNext()) {

            String title = results.getString(titleColumnIndex);
            double latitude = results.getDouble(latitudeColIndex);
            double longitude = results.getDouble(longitudeColIndex);
            long id = results.getLong(idColIndex);
            String phone = results.getString(phoneColIndex);
            topStations.add(new StationObject(phone, title, (long) latitude, longitude, id));
            adapter = new StationAdapter(true, getApplicationContext(), topStations);
            favouriteList.setAdapter(adapter);
        }
        favouriteList.setOnItemClickListener(( parent, view, position, id) -> {
            positionClicked = position;
        });

        Button delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(clk -> {
            StationObject stationToDelete = topStations.get(positionClicked);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle("Alert")
                    .setMessage("Do you want to delete?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            topStations.remove(positionClicked);
                            int numDeleted = db.delete(MyOpenHelper.TABLE_NAME,
                                    MyOpenHelper.COL_ID + "=?", new String[] {Long.toString(stationToDelete.getId())});
                            Log.i("ViewContact", "Deleted " + numDeleted + " rows");
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", (d,w) -> {

                    })
                    .create();
            dialog.show();
        });
    }
}
