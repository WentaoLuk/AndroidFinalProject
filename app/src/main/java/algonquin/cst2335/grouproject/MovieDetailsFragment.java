package algonquin.cst2335.grouproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * This class loads the movie details fragment based on the movie chosen from the recycler view
 * @author Jade Mak
 */
public class MovieDetailsFragment extends Fragment {
    /** Movie chosen**/
    MovieListFragment.SavedMovie chosenMovie;
    /** The position of the chosen movie**/
    int chosenPosition;
    /** Constructor that takes in the movie and position**/
    public MovieDetailsFragment(MovieListFragment.SavedMovie movie, int position)
    {
        chosenMovie = movie;
        chosenPosition = position;
    }
    /** Loads the layout based on the chosen movie details, gets the poster from saved file**/
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.movie_details_layout,container,false);

        TextView titleView = detailsView.findViewById(R.id.titleView);
        TextView yearView = detailsView.findViewById(R.id.yearView);
        TextView ratingView = detailsView.findViewById(R.id.ratingView);
        TextView runtimeView = detailsView.findViewById(R.id.runtimeView);
        TextView plotView = detailsView.findViewById(R.id.plotView);
        TextView actorsView = detailsView.findViewById(R.id.actorsView);
        ImageView posterView = detailsView.findViewById(R.id.posterView);

        titleView.setText("Movie Title is: "+ chosenMovie.getTitle());
        yearView.setText("Year: " + chosenMovie.getYear());
        ratingView.setText("Rating: " + chosenMovie.getRating());
        runtimeView.setText("Runtime is:"+chosenMovie.getRuntime());
        plotView.setText("Plot: " +chosenMovie.getPlot());
        actorsView.setText("Actors: " +chosenMovie.getActors());


        posterView.setImageBitmap(BitmapFactory.decodeFile(getActivity().getFilesDir()+"/"+chosenMovie.getTitle()));


        Button closeButton = detailsView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeClicked -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        Button deleteButton = detailsView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(deleteClicked -> {
            SavedMovies parentActivity = (SavedMovies)getContext();
            parentActivity.notifyMessageDeleted(chosenMovie,chosenPosition);
            getParentFragmentManager().beginTransaction().remove(this).commit();

        });
        return detailsView;
    }
}
