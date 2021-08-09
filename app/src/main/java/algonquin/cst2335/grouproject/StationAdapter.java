package algonquin.cst2335.grouproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * this class serves as an adpater for the list
 */
public class StationAdapter extends BaseAdapter {
    /**
     * takes user info for charging stations that are displayed
     */
    public boolean info;
    /**
     * context of the app
     */
    public Context context;
    /**
     * arraylist to hold the station object
     */
    public ArrayList<StationObject> stationList;

    /**
     * constructor for the adapter created
     * @param info
     * @param context
     * @param stationList
     */
    public StationAdapter(Boolean info, Context context, ArrayList<StationObject> stationList) {

    super();
    this.context=context;
    this.stationList=stationList;
    this.info=info;

}

    /**
     * method to count charging stations
     * @return number of stations
     */
    @Override
    public int getCount() {

        return stationList.size();
    }

    /**
     * method to get charging station
     * @param i the position of charging station
     * @return object of charging station
     */
    @Override
    public StationObject getItem(int i) {

        return stationList.get(i);
    }

    /**
     * method to get an id for charging station
     * @param i position of charging station
     * @return id
     */
    @Override
    public long getItemId(int i) {

        return (getItem(i)).getId();
    }

    /**
     * method to get a view for charging station
     * @param i position of charging station
     * @param view
     * @param viewGroup
     * @return view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View newView = inflater.inflate(R.layout.station_row, viewGroup, false);

        StationObject row = getItem(i);
        TextView rowDetails = (TextView) newView.findViewById(R.id.row_title);

        if(info) {
            rowDetails.setText(String.format("Station: %s\n Latitude: %.2f\n Longitude: %.2f\n Phone: %s", row.getTitle(), row.getLatitude(),
                    row.getLongitude(), row.getPhone()));
        }
        else{
            rowDetails.setText(row.getTitle());
        }
        return newView;
    }
}
