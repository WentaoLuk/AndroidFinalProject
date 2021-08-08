package algonquin.cst2335.grouproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * This class holds the saved movie fragments
 * @author Jade Mak
 */
public class SavedMovies extends AppCompatActivity {
    /** Holds the movie list fragment**/
    MovieListFragment movieFragment;

    /**
     * This function sets the empty layout and adds the fragments
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);

        movieFragment = new MovieListFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.fragmentRoom,movieFragment);
        tx.commit();
    }

    /**
     * This function loads the movie details fragment of a clicked movie
     * @param movie this is the chosen movie
     * @param position this is the chosen movies position
     */
    public void userClickedMessage(MovieListFragment.SavedMovie movie, int position) {
        MovieDetailsFragment mdFragment = new MovieDetailsFragment(movie,position);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentRoom,mdFragment).commit();

    }

    /**
     * This function notifies the moviefragment if its been deleted
     * @param chosenMovie This is the chosen movie deleted
     * @param chosenPosition this is the chosen movie's position
     */
    public void notifyMessageDeleted(MovieListFragment.SavedMovie chosenMovie, int chosenPosition) {
        movieFragment.notifyMovieDeleted(chosenMovie,chosenPosition);
    }
}
