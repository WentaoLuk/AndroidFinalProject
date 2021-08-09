package algonquin.cst2335.grouproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Fragment to handle bus route list view
 */
public class OC_Route_Fragment extends Fragment {

    protected  static final String ACTIVITY_NAME = "OCRouteFragment";



    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oc_fragment, container, false);
    }
}
