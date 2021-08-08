package algonquin.cst2335.grouproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * This class is the movie list fragment and it shows the list of saved movies
 * @author Jade Mak
 */
public class MovieListFragment extends Fragment {
    /** An array of saved movies & their details**/
    ArrayList<SavedMovie> movies = new ArrayList<>();
    /** recycler view adapter**/
    MyMovieAdapter adt;
    /** db connection**/
    SQLiteDatabase db;
    /**holds the back button**/
    Button back;

    /**
     * This class creates the view and inflates the layout including the recycler view
     * @param inflater instantiate the contents of layout XML files into their corresponding View objects.
     * @param container contains the views
     * @param savedInstanceState  a reference to a Bundle object
     * @return returns movieLayout view
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View movieLayout = inflater.inflate(R.layout.movie_layout,container,false);
        back = movieLayout.findViewById(R.id.backbutton);
        RecyclerView movieList = movieLayout.findViewById(R.id.myrecycler);

        Button backBtn = movieLayout.findViewById(R.id.backbutton);

        MovieOpenHelper opener = new MovieOpenHelper(getContext());
        db = opener.getWritableDatabase();
        Cursor results = db.rawQuery("Select * from "+ MovieOpenHelper.TABLE_NAME+";", null);
        int _idCol = results.getColumnIndex("_id");
        int titleCol = results.getColumnIndex(MovieOpenHelper.col_title);
        int yearCol = results.getColumnIndex(MovieOpenHelper.col_year);
        int runtimeCol = results.getColumnIndex(MovieOpenHelper.col_runtime);
        int ratingCol = results.getColumnIndex(MovieOpenHelper.col_rating);
        int plotCol = results.getColumnIndex(MovieOpenHelper.col_plot);
        int posterCol = results.getColumnIndex(MovieOpenHelper.col_poster);
        int actorCol = results.getColumnIndex(MovieOpenHelper.col_actors);

        while (results.moveToNext()){
            long id = results.getInt(_idCol);
            String title = results.getString(titleCol);
            String year = results.getString(yearCol);
            String runtime = results.getString(runtimeCol);
            String rating = results.getString(ratingCol);
            String plot = results.getString(plotCol);
            String poster = results.getString(posterCol);
            String actor = results.getString(actorCol);
            movies.add(new SavedMovie(title,year,runtime,rating,plot,poster,actor,id));
        }

        // adapter
        adt = new MyMovieAdapter();
        movieList.setAdapter(adt);
        movieList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        backBtn.setOnClickListener(clk->{
            Intent back = new Intent(getActivity(),MovieActivity.class);
            startActivity(back);
        });
        return movieLayout;
    }

    /**
     * This function notifies if a movie has been deleted based on the clicked movie and its position
     * and removes it from the database
     * @param chosenMovie
     * @param chosenPosition
     */
    public void notifyMovieDeleted(SavedMovie chosenMovie, int chosenPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to delete the movie: " + chosenMovie.getTitle())
                .setTitle("Question: ")
                .setNegativeButton("No",(dialog,cl)->{})
                .setPositiveButton("Yes",(dialog,cl) -> {
                    //position =getAbsoluteAdapterPosition();
                    SavedMovie removedMessage = movies.get(chosenPosition);
                    movies.remove(chosenPosition);
                    adt.notifyItemRemoved(chosenPosition);
                    //delete message from DB
                    db.delete(MovieOpenHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(removedMessage.getId())});


                    Snackbar.make(back, "You deleted movie #" + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("undo", click ->{
                                movies.add(chosenPosition,removedMessage);
                                adt.notifyItemInserted(chosenPosition);
                                //undo delete from DB
                                db.execSQL("Insert into " + MovieOpenHelper.TABLE_NAME + " values('" + removedMessage.getId()+
                                        "','"+ removedMessage.getTitle()+
                                        "','" + removedMessage.getYear() +
                                        "','" + removedMessage.getRating() +
                                        "','" + removedMessage.getRuntime() +
                                        "','" + removedMessage.getActors() +
                                        "','" + removedMessage.getPlot() +
                                        "','" + removedMessage.getPoster() + "');"
                                );
                            })

                            .show();
                }).create().show();
    }

    /**
     * This function loads the rows inside the recycler view
     */
    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView yearText;
        ImageView poster;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title);
            yearText = itemView.findViewById(R.id.year);
            poster = itemView.findViewById(R.id.posteriv);


            itemView.setOnClickListener(clk -> {
                SavedMovies parentActivity = (SavedMovies)getContext();
                int position = getAbsoluteAdapterPosition();
                parentActivity.userClickedMessage(movies.get(position),position);
            });
        }

        public void setPosition(int p) { position = p;}
    }

    /**
     * This function loads the layout for the saved movie rows
     */
    private class MyMovieAdapter extends RecyclerView.Adapter <MyRowViews> {

        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.saved_movie_row,parent,false);
            return new MyRowViews(loadedRow);
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(movies.get(position).getTitle());
            holder.yearText.setText(movies.get(position).getYear());
            holder.poster.setImageBitmap(BitmapFactory.decodeFile(getContext().getFilesDir()+"/"+movies.get(position).getTitle()));
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

    }

    /**
     * This class represents saved movies and all their details
     */
    class SavedMovie {
        String title;
        String runtime;
        String year;
        String rating;
        String plot;
        String poster;
        String actors;

        long id;
        public SavedMovie(String title,  String year, String rating,String runtime, String plot,String poster,String actors) {
            this.title = title;
            this.year = year;
            this.rating = rating;
            this.runtime = runtime;
            this.plot = plot;
            this.poster = poster;
            this.actors = actors;
        }
        public SavedMovie(String title,  String year, String rating,String runtime, String plot,String poster,String actors, long id) {
            this.title = title;
            this.year = year;
            this.rating = rating;
            this.runtime = runtime;
            this.plot = plot;
            this.poster = poster;
            this.actors = actors;
            setId(id);
        }
        public void setId(long l){ id =l; }
        public long getId() {return id;}
        public String getTitle() {
            return title;
        }
        public String getRuntime(){return runtime;}
        public String getYear(){return year;}
        public String getRating(){return rating;}
        public String getPlot(){return plot;}
        public String getPoster(){return poster;}
        public String getActors(){return actors;}

    }
}


