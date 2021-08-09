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

import java.util.Locale;

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
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charging_station_details);
        titleView = findViewById(R.id.textTitle);
        phoneNum = findViewById(R.id.phone);
        latInput = findViewById(R.id.textLatitude);
        longInput = findViewById(R.id.textLongitude);


        Button mapButton = findViewById(R.id.loadMap);
        mapButton.setOnClickListener(clk -> {
            String ur = String.format(String.valueOf(latInput), longInput, Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f");
            Uri intent = Uri.parse(ur);
            Intent map = new Intent(Intent.ACTION_VIEW, intent);
            startActivity(map);

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



            if (title=="") {
                Toast.makeText(this, "Please input the title", Toast.LENGTH_SHORT).show();
            }


            ContentValues rowValues = new ContentValues();
            rowValues.put(MyOpenHelper.COL_PHONE, telephone);
            rowValues.put(MyOpenHelper.COL_LocTitle, stationTitle);
            rowValues.put(MyOpenHelper.COL_LONG, longitude);
            rowValues.put(MyOpenHelper.COL_LAT, latitude);
            long Id = db.insert(MyOpenHelper.TABLE_NAME, null, rowValues);

          // StationObject ClickedItem = (StationObject).getSerializableExtra("ClickedItem");

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

            EditText valLongitude = findViewById(R.id.longitudeResult);
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

