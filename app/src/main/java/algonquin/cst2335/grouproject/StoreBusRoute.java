package algonquin.cst2335.grouproject;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;

public class StoreBusRoute {
    /**
     * The name of this class
     */
    public static final String ACTIVITY_NAME = "BusRouteBean";
    /**
     * The number of stop
     */
    private String stopNum;
    /**
     * The number of route
     */
    private String routeNum;
    /**
     * The destination of route
     */
    private String destination;
    /**
     * The coordinates of route
     */
    private String coordinates;
    /**
     * The speed of route
     */
    private String speed;
    /**
     * The start time of route
     */
    private String startTime;
    /**
     * The adjusted time of route
     */
    private String adjustedTime;
    /**
     * The direction of route
     */
    private String direction;
    /**
     * The URL of get route info API
     */
    public final String getRouteInfo = "https://api.octranspo1.com/v1.2/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=";
    /**
     * The trailer of of get route info API
     */
    public final String getRouteInfoTrailer = "&routeNo=";

    /**
     * default constructor
     * initialize the variables with default values
     */
    public StoreBusRoute(){
        this.stopNum = "unkown";
        this.routeNum = "unkown";
        this.destination = "unkown";
        this.coordinates = "unkown";
        this.speed = "unkown";
        this.startTime = "unkown";
        this.adjustedTime = "unkown";
        this.direction = "unkown";
    }

    /**
     * Initialize constructor
     * initialize the variables with specified values
     * @param stopNum
     * @param routeNum
     * @param destination
     * @param coordinates
     * @param speed
     * @param startTime
     * @param adjustedTime
     * @param direction
     */
    public StoreBusRoute(String stopNum, String routeNum, String destination, String coordinates, String speed, String startTime, String adjustedTime, String direction) {
        this.stopNum = stopNum;
        this.routeNum = routeNum;
        this.destination = destination;
        this.coordinates = coordinates;
        this.speed = speed;
        this.startTime = startTime;
        this.adjustedTime = adjustedTime;
        this.direction = direction;
    }

    /**
     * Initialize constructor
     * initialize the variables with specified values
     * @param routeno
     * @param destination
     * @param direction
     * @param stopNum
     */
    public StoreBusRoute(String routeno, String destination, String direction, String stopNum) {
        this.routeNum = routeno;
        this.destination = destination;
        this.direction = direction;
        this.stopNum = stopNum;

    }

    public void updateData() {
        Query query= new Query();
        query.execute("");
    }

    /**
     * AsyncTask Query inner class to connect to Api and parse the information from xml
     */
    public class Query extends AsyncTask<String, Integer, String> {

        String urlstring = "";
        String endTag = "";
        String fullCoordinates = "";
        int type;
        boolean foundDirection = false;
        boolean isConnected = true;

        @Override
        protected String doInBackground(String... array) {

            try {
                URL url = new URL(getRouteInfo.concat(stopNum).concat(getRouteInfoTrailer).concat(routeNum));
                HttpURLConnection httpURLconn = (HttpURLConnection) url.openConnection();
                httpURLconn.setReadTimeout(10000);
                httpURLconn.setConnectTimeout(12000);
                httpURLconn.setRequestMethod("GET");
                httpURLconn.setDoInput(true);
                httpURLconn.connect();

                Log.i(ACTIVITY_NAME+"query", "start parsing");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(httpURLconn.getInputStream(), "UTF-8");
                type = xpp.getEventType();
                Log.i(ACTIVITY_NAME+"query", "get event type"+type);
                while ((type != XmlPullParser.END_DOCUMENT) && isConnected) {

                    switch (type) {
                        case XmlPullParser.START_TAG:
                            endTag = xpp.getName();
                            break;
                        case XmlPullParser.TEXT:
                            if (endTag.equals("Direction") && xpp.getText().equals(direction)) {
                                foundDirection = true;
                            } else if (foundDirection) {
                                Log.i("TagValue", xpp.getText());
                                if (endTag.equals("TripDestination"))
                                    destination = xpp.getText();
                                else if (endTag.equals("TripStartTime"))
                                    startTime = xpp.getText();
                                else if (endTag.equals("AdjustedScheduleTime"))
                                    adjustedTime = xpp.getText();
                                else if (endTag.equals("Latitude"))
                                    fullCoordinates = (xpp.getText().concat("/"));
                                else if (endTag.equals("Longitude"))
                                    coordinates = fullCoordinates.concat(xpp.getText());
                                else if (endTag.equals("GPSSpeed")) {
                                    speed = xpp.getText();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (xpp.getName().equals("Trip") && foundDirection) {
                                isConnected = false;
                                Log.i(ACTIVITY_NAME+"  query", "end parse");
                            }
                            break;
                        default:
                            Log.i(ACTIVITY_NAME+" query", "parse switch default");
                            break;
                    }
                    xpp.next();
                    type = xpp.getEventType();
                }
                Log.i("Result:", destination +" "+ startTime +" "+ adjustedTime +" "+ coordinates +" "+ speed);
                httpURLconn.getInputStream().close();
                Log.i(ACTIVITY_NAME+" query", "parse complete");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME+" query", "Exception: " + e);
                return (ACTIVITY_NAME+" occurs exception: " + e);
            }
            return null;
        }

    }

    /**
     * Return true if some information is null
     * @return true if some information is null;
     */
    public boolean isNull(){
        return (startTime == null || speed == null || coordinates == null || adjustedTime == null);
    }

    /**
     * Getter for number of stop
     * @return stopNum
     */
    public String getStopNum() {
        return stopNum;
    }

    /**
     * Getter for number of route
     * @return routeNum
     */
    public String getRouteNum() {
        return routeNum;
    }

    /**
     * Getter for destination of route
     * @return destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Getter for coordinates of route
     * @return
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Getter for speed of route
     * @return speed
     */
    public String getSpeed() {
        return speed;
    }

    /**
     * Getter for  startTime of route
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Getter for adjusted time of route
     * @return  adjustedTime
     */
    public String getAdjustedTime() {
        return adjustedTime;
    }

    /**
     * Getter for direction of route
     * @return direction
     */
    public String getDirection() {
        return direction;
    }
}


