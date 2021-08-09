package algonquin.cst2335.grouproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * this class takes user input and searches for charging stations to display
 */
public class ChargingStation extends AppCompatActivity {

    /**
     * name of activity
     */
    public static final String ACTIVITY_NAME = "CHARGING_STATION";

    /**
     * charging stations list
     */
    ArrayList<StationObject> stations = new ArrayList<StationObject>();

    /**
     * those fields to input longitude and list from user
     */
    private EditText longitude;

    private ListView list;

    private SharedPreferences sharedPref;

    /**
     * this method is to interact with the user and load the layout
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_stations);
        Button findButton = findViewById(R.id.searchButton);
        final EditText latitude = findViewById(R.id.latitudeInput);
        longitude = findViewById(R.id.longitudeInput);
        list = findViewById(R.id.favouriteList);
        sharedPref = getSharedPreferences("chargingStationSharedPreferences", MODE_PRIVATE);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=CA&latitude="
                        + latitude.getText().toString() + "&longitude=" + longitude.getText().toString() + "&maxresults=10";
                DownloadFilesTask downloadFileTask = new DownloadFilesTask();
                downloadFileTask.execute(url);
                longitude.onEditorAction(EditorInfo.IME_ACTION_DONE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Latitude", latitude.getText().toString());
                editor.putString("Longitude", longitude.getText().toString());
                editor.commit();
            }
        });
        list.setOnItemClickListener(( parent,  view,  position,  id) ->{
            StationObject chosenOne = stations.get(position);
            Intent nextPage = new Intent(ChargingStation.this, MainActivityCharging.class);
            nextPage.putExtra("itemClicked", (Parcelable) chosenOne);
            startActivity(nextPage);
        });
        String latitudeValue = sharedPref.getString("Latitude", "");
        latitude.setText(latitudeValue);
        String longitudeValue = sharedPref.getString("Longitude", "");
        longitude.setText(longitudeValue);
    }

/**
 * this class is to connect to the server to process data input
 */
    private class DownloadFilesTask extends AsyncTask<String, String, String> {
    /**
     * dialog created to show progression
     */
        private ProgressDialog pro;

    /**
     * this method is to view and show progression of data
     */
    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pro = new ProgressDialog(ChargingStation.this);
            pro.setMessage("Please wait...");
            pro.setCancelable(false);
            pro.show();
        }

    /**
     * this method connects to the server to retrieve data input from the user
     * @param urls link
     * @return data regarding charging stations
     */
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL link = new URL(urls[0]);
                urlConnection = (HttpURLConnection) link.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }
                return result;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

    /**
     * this methods takes input of charging stations and display them in a list layout
     * @param result
     */
    protected void onPostExecute(String result) {
            pro.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject stationJSON = jsonArray.getJSONObject(i);
                    JSONObject addressJSON = stationJSON.getJSONObject("AddressInfo");

                    StationObject stationObject = new StationObject();
                    stationObject.setTitle(addressJSON.getString("Title"));
                    stationObject.setLatitude(addressJSON.getDouble("Latitude"));
                    stationObject.setLongitude(addressJSON.getDouble("Longitude"));
                    stationObject.setPhone(addressJSON.getString("phone"));
                    stations.add(stationObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StationAdapter adapter = new StationAdapter(false, getApplicationContext(), stations);
            list.setAdapter(adapter);
        }
    }

}
