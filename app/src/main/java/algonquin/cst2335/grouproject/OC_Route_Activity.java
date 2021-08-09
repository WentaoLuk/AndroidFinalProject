package algonquin.cst2335.grouproject; /**
 * @Author Jenya Pribylov
 * @Version 1.0
 */

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OC_Route_Activity extends AppCompatActivity {

    /**
     * The name of this class
     */
    public static final String ACTIVITY_NAME = "BusRouteActivity";

    /**
     * BusRouteBean object
     */
    private StoreBusRoute route;
    /**
     * Destination of route
     */
    private TextView routeDestination;
    /**
     * Direction of route
     */
    private TextView direction;
    /**
     * Start time of route
     */
    private TextView startTime;
    /**
     * Adjusted time of route
     */
    private TextView adjustedTime;
    /**
     * Coordinates of route
     */
    private TextView coordinates;
    /**
     * Speed of route
     */
    private TextView speed;
    /**
     * Average adjusted time of route
     */
    private TextView averageAdjustedTime;
    /**
     * The button of back to previous activity
     */
    private Button backButton;
    /**
     * The SQLite database helper of route
     */
    private OC_Route_DB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_route);
        dbHelper = new OC_Route_DB(this);
        //bind the node from layout
        averageAdjustedTime = (TextView) findViewById(R.id.averageAdjustedTime);
        routeDestination = (TextView) findViewById(R.id.routenoDestinationView);
        direction = (TextView) findViewById(R.id.directionView);
        startTime = (TextView) findViewById(R.id.startTimeView);
        adjustedTime = (TextView) findViewById(R.id.adjustedTimeView);
        coordinates = (TextView) findViewById(R.id.coordinatesView);
        speed = (TextView) findViewById(R.id.speedView);
        backButton = (Button) findViewById(R.id.busBackButton);
        Bundle bundle = getIntent().getExtras();
        //instantiate a new Route java bean
        route = new StoreBusRoute(bundle.getString("routeno"), bundle.getString("destination"),
                bundle.getString("direction"), bundle.getString("stationNum")
        );
        //add the back previous activity button
        backButton.setOnClickListener((e) -> {
            finish();
        });

        Query query = new Query();
        query.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Log.i(ACTIVITY_NAME, "onCreate complete");


    }

    /**
     * add the options menu to this activity
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oc_transpo_menu, menu);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");

    }

    @Override
    protected void onPause() {
        Log.i(ACTIVITY_NAME, "In onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(ACTIVITY_NAME, "In onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(ACTIVITY_NAME, "In onDestroy()");
        super.onDestroy();
    }

    private void insertEntry() {
        if (route == null) {
            Log.i(ACTIVITY_NAME, "route object empty");
        }
        String routeNo = route.getRouteNum();
        String adjustedTime = route.getAdjustedTime();
        if (adjustedTime == null) {
            adjustedTime = "0";

        }
        dbHelper.openDatabase();
        dbHelper.insertEntry(routeNo, adjustedTime);
        dbHelper.closeDatabase();
    }

    /**
     * get the adjusted time from database then calculate the average
     * @return average adjusted time
     */

    private String getAverageAdjustedTime() {
        dbHelper.openDatabase();
        Log.i(ACTIVITY_NAME, "start getAverageAdjustedTime  ");
        /**
         * Counts how many rows of database
         */
        int count=1;
        /**
         * The total of adjusted time
         */
        int total = 0;
        /**
         * The result of calculated average of adjusted time
         */
        int avgAdjustedTime=0;
        Cursor cursor = dbHelper.getAverageAdjustedTime(route.getRouteNum());
        int colIndex = cursor.getColumnIndex(OC_Route_DB.ADJUSTED_TIME);
        cursor.moveToFirst();
        Log.i(ACTIVITY_NAME, "after move to first");
        while (!cursor.isAfterLast()) {
            Log.i(ACTIVITY_NAME, "after cursor= " + cursor.getString(colIndex));

            total += Integer.parseInt(cursor.getString(colIndex));
            Log.i(ACTIVITY_NAME, "total = " + total);

            cursor.moveToNext();

        }
        Log.i(ACTIVITY_NAME, "Cursor's column count = " + cursor.getColumnCount());


        for (int i = 0; i < cursor.getColumnCount(); i++) {
            Log.i(ACTIVITY_NAME, "The " + i + " row is " + cursor.getColumnName(i));
        }
        count = cursor.getCount();
        dbHelper.closeDatabase();
        if(total==0){
            return"null";
        }else{
            avgAdjustedTime=total/count;
        }
        return avgAdjustedTime+"";
    }
    /**
     * Refresh the content of the layout
     */
    private void display() {
        routeDestination.setText(getString(R.string.route_text) + route.getRouteNum() + " " + route.getDestination());
        direction.setText(getString(R.string.bus_coordinates) + route.getDirection());
        startTime.setText(getString(R.string.bus_startTime) + route.getStartTime());
       // coordinates.setText(getString(R.string. + route.getCoordinates());
        speed.setText(getString(R.string.bus_speed) + route.getSpeed());
        adjustedTime.setText(getString(R.string.bus_adjustedTime) + route.getAdjustedTime());
        averageAdjustedTime.setText(getString(R.string.bus_avgAdjustedTime) + getAverageAdjustedTime());

    }

    /**
     * AsyncTask query inner class to connect the api and parse the provided information.
     */
    public class Query extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            route.updateData();
            if (route.isNull()) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Log.i(ACTIVITY_NAME, "occurs "+e.toString());
                }
                route.updateData();
            }
            insertEntry();// call insertEntry() to insert the adjusted time to database;
            display();
            return null;
        }
    }

}
