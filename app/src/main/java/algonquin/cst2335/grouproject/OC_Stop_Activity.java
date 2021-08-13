package algonquin.cst2335.grouproject;
/**
 * @Author Jenya Pribylov
 * @version 1.0
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Bus top detail activity to displays bus stop details information
 */
public class OC_Stop_Activity extends AppCompatActivity {
    /**
     * The url of the bus stop API
     */
    public static final String API_URL = "https://api.octranspo1.com/v2.0/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=";
    /**
     * The name of this class
     */
    protected static final String ACTIVITY_NAME = "BusStopActivity";
    /**
     * The boolean of delete action
     */
    private static boolean deleteStop;
    /**
     * The last station of route
     */
    private static String lastStop;
    /**
     * The number of bus stop
     */
    private int busStopNumber;
    /**
     * The name of stop
     */
    private String stopName;
    /**
     * The current context
     */
    private Context ctx;
    /**
     * The list of routes
     */
    private ArrayList<StoreBusRoute> routeList;
    /**
     * The list of routes info
     */
    private ArrayList<String> routeDetail;
    /**
     * The list view of routes
     */
    private ListView routeListView;
    /**
     * The progress bar
     */
    private ProgressBar progressBar;
    /**
     * The text view for stop name
     */
    private TextView stopNameView;
    /**
     * The button of back to previous activity
     */
    private Button backButton;
    /**
     * The button of delete item
     */
    private Button deleteStopButton;
    /**
     *The array adapter ofr stop list view
     */
    private StopAdapter adapter;
    /**
     *The list view of stops
     */
    private  ListView routesView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_stops);
        Log.i(ACTIVITY_NAME, "on create");
        //initialize variables
        ctx = this;
        deleteStop = false;
        stopName = "";
        lastStop = "";
        routeList = new ArrayList<>();
        routeDetail = new ArrayList<>();
        //bind the layout
        routeListView = (ListView) findViewById(R.id.routesView);
        backButton = (Button) findViewById(R.id.busStopBackButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        stopNameView = (TextView) findViewById(R.id.stopName);
        deleteStopButton = (Button) findViewById(R.id.deleteStopButton);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.i(ACTIVITY_NAME, "Error: Stop Not Found");
        } else {
            busStopNumber = Integer.parseInt(extras.getString("busStopNumber"));
            stopNameView.setText("Stop: " + stopName);
        }
        Query query = new Query();
        query.execute("");
        adapter = new StopAdapter(this);
        routeListView.setAdapter(adapter);

        //add the back previous activity button
        backButton.setOnClickListener((e) ->{
            finish();
        });

        //add the delete stop button action
        deleteStopButton.setOnClickListener((e) -> {
            Log.i(ACTIVITY_NAME, "Delete button clicked");
            deleteStop = true;
            lastStop = Integer.toString(busStopNumber);
            finish();
        });

        //displays route list, when user clicks the item on the list will jump to the route detail activity
        routeListView.setOnItemClickListener((parent, view, position, id) -> {
            String s = routeDetail.get(position);
            Log.i(ACTIVITY_NAME, "setOnItemClickListener message: " + s);
            Intent i = new Intent(OC_Stop_Activity.this, OC_Route_Activity.class);
            i.putExtra("routeno", routeList.get(position).getRouteNum());
            i.putExtra("destination", routeList.get(position).getDestination());
            i.putExtra("stationNum", routeList.get(position).getStopNum());
            i.putExtra("direction", routeList.get(position).getDirection());
            startActivity(i);
        });


    }

    @Override
    protected void onResume() {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.i(ACTIVITY_NAME, "In onStart()");
        super.onStart();
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

    /**
     * Updating the progress bar loading
     * @param num
     * @param max
     */
    private void loadProgressBar(int num, int max) {
        if (num > max) {
            progressBar.setProgress(max);
        }else {
            progressBar.setProgress(num);
        }
    }

    /**
     * To handles when the stop is not found from API
     */
    private void stopNotFound() {
        AlertDialog alertDialog = new AlertDialog.Builder(OC_Stop_Activity.this).create();
        alertDialog.setTitle("Stop not found");
        alertDialog.setMessage("Stop not found in database, please try it again");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * The array adapter of stop list view
     */
    public class StopAdapter extends ArrayAdapter<String> {
        public StopAdapter(Context ctx) {
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return (routeDetail.size());
        }

        @Override
        public String getItem(int position) {
            return routeDetail.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = OC_Stop_Activity.this.getLayoutInflater();

            View result = inflater.inflate(R.layout.bus_route, null);

            TextView routeText = (TextView)result.findViewById(R.id.route_text);

            routeText.setText (getItem(position) );
            return result;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

    }
    /**
     * add the options menu to this activity
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oc_transpo_menu, menu);
        return true;
    }
    /**
     * add the options menu to this activity
     * @param item
     * @return true
     */


    /**
     * AsyncTask query inner class to handles connect to the api and parse the information
     */
    public class Query extends AsyncTask<String, Integer, String> {
        private ArrayList<StoreBusRoute> routesList = new ArrayList<StoreBusRoute>();
        private String routeNumber;
        private String direction;
        private String destination;

        @Override
        protected String doInBackground(String... array) {
            Log.i(ACTIVITY_NAME, "Do in background");
            String lastTag = "";
            try {
                URL url = new URL(API_URL.concat(Integer.toString(busStopNumber)));
                HttpURLConnection httpURLconn = (HttpURLConnection) url.openConnection();
                httpURLconn.setReadTimeout(10000);
                httpURLconn.setConnectTimeout(12000);
                httpURLconn.setRequestMethod("GET");
                httpURLconn.setDoInput(true);
                httpURLconn.connect();
                loadProgressBar(10, 20);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(httpURLconn.getInputStream(), "UTF-8");


                int eventType = xpp.getEventType();
                loadProgressBar(10,30);

                Log.i(ACTIVITY_NAME, "Start parse: ");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            Log.i(ACTIVITY_NAME, "Tag is found.");
                            lastTag = xpp.getName();
                            loadProgressBar(3,80);
                            Log.i(ACTIVITY_NAME, "Tag is " + lastTag);
                            break;
                        case XmlPullParser.TEXT:
                            if (lastTag.equals("StopDescription")) {
                                Log.i(ACTIVITY_NAME, "Stop name found: ");
                                stopName = xpp.getText();
                                loadProgressBar(12,100);
                            } else if (lastTag.equals("RouteNo")) {
                                routeNumber = xpp.getText();
                                loadProgressBar(12,100);
                            } else if (lastTag.equals("Direction")) {
                                direction = xpp.getText();
                                loadProgressBar(10,100);
                            } else if (lastTag.equals("RouteHeading")) {
                                destination = xpp.getText();
                                loadProgressBar(10,100);
                                routesList.add(new StoreBusRoute(routeNumber, destination, direction, Integer.toString(busStopNumber)));
                            }
                            break;
                        default:
                            break;
                    }
                    xpp.next();
                    eventType = xpp.getEventType();
                }
                httpURLconn.getInputStream().close();
                Log.i(ACTIVITY_NAME, "closed input stream");
                loadProgressBar(100,90);
                Log.i(ACTIVITY_NAME, "parse complete");
            } catch (Exception e) {

                Log.i(ACTIVITY_NAME, "Error: " + e.toString());
                return ("Error: " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            adapter = new StopAdapter(ctx);
            routesView = (ListView)findViewById(R.id.routesView);
            routesView.setAdapter(adapter);
            stopNameView.setText("Stop: " + stopName);
            for (StoreBusRoute route : routesList) {
                routeDetail.add(route.getRouteNum().concat(" ").concat(route.getDestination()));
                adapter.notifyDataSetChanged();
                routeList.add(route);

            }
            loadProgressBar(100,100);
            if (stopName.equals(""))
                stopNotFound();
        }
    }

    /**
     * Reset delete stop boolean
     */
    public static void resetDeleteStop()
    {

        deleteStop = false;
    }

    /**
     * Getter for if user click delete button
     * @return true if user click delete button
     */
    public static boolean getDeleteStop() {

        return deleteStop;
    }

    /**
     * Getter for deleted stop number
     * @return the last stop
     */
    public static String getDeletedStopNo() {
        return lastStop; }
}