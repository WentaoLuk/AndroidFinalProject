package algonquin.cst2335.grouproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

/**
 * this class goes over the station details and loading it
 */
public class MainActivityCharging extends AppCompatActivity {

    String stationTitle;
    StationObject ClickedItem;
    TextView titleView;
    TextView phoneNum;
    TextView ID;
    TextView latInput;
    TextView longInput;

    /**
     * this method loads details and location and adds it to the favourite list
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charging_station_details);
        titleView = findViewById(R.id.textTitle);
        phoneNum = findViewById(R.id.phoneRes);
        latInput = findViewById(R.id.latitudeResult);
        longInput = findViewById(R.id.longitudeEdit);

        Button mapButton = findViewById(R.id.loadMap);
        mapButton.setOnClickListener(clk -> {


            String titleName = titleView.getText().toString();
            String latString = latInput.getText().toString();
            String longString = longInput.getText().toString();


            String ur1 = String.format("http://maps.google.com/maps?q=loc:%s,%s", latString, longString);


            System.out.println("1111111");
            System.out.println(ur1);
            Uri intent = Uri.parse(ur1);
            Intent intent1 = new Intent(MainActivityCharging.this, ChargingStation.class);
            startActivity(intent1);

        });

        MyOpenHelper opener = new MyOpenHelper(this);
        SQLiteDatabase db = opener.getWritableDatabase();
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(click -> {


            String title = titleView.getText().toString();
            String telephone = phoneNum.getText().toString();
            String id = ID.getText().toString();
            String latitude = latInput.getText().toString();
            String longitude = longInput.getText().toString();


            if (title == "") {
                Toast.makeText(this, "Please input the title", Toast.LENGTH_SHORT).show();
            }


            ContentValues rowValues = new ContentValues();
            rowValues.put(MyOpenHelper.COL_PHONE, telephone);
            rowValues.put(MyOpenHelper.COL_LocTitle, title);
            rowValues.put(MyOpenHelper.COL_LONG, longitude);
            rowValues.put(MyOpenHelper.COL_LAT, latitude);
            long Id = db.insert(MyOpenHelper.TABLE_NAME, null, rowValues);



            EditText titleMessage = findViewById(R.id.title);
            stationTitle = ClickedItem.getTitle();
            titleMessage.setText(stationTitle);

            EditText phone = findViewById(R.id.phoneRes);
            String number = ClickedItem.getPhone();
            if (number.equals("null")) {
                phone.setText(R.string.nullNumber);
            } else {
                phone.setText(number);
            }

            EditText valLatitude = findViewById(R.id.latitudeResult);
            double latValue = ClickedItem.getLatitude();
            latInput.setText(Double.toString(latValue));

            EditText valLongitude = findViewById(R.id.longitudeEdit);
            double longValue = ClickedItem.getLongitude();
            longInput.setText(Double.toString(longValue));

            AlertDialog.Builder build = new AlertDialog.Builder(this);
            AlertDialog dialog = build.setMessage("Adding station to favourites")
                    .create();
            dialog.show();

        });

        Button listButton = findViewById(R.id.listOfFavouritesButton);
        listButton.setOnClickListener(click -> {
            Intent next = new Intent(MainActivityCharging.this, FavouriteCarStations.class);
            startActivity(next);

        });
    }

}


