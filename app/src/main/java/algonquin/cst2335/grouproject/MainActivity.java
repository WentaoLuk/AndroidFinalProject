package algonquin.cst2335.grouproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

/**
 * This is the homepage for the group project, the buttons and navigation bar to go each person's activity.
 * @author Jade Mak
 */
public class MainActivity extends AppCompatActivity {
    Button busBtn;
    Button carBtn;
    Button soccerBtn;

    public boolean onOptionsItemSelected( MenuItem item) {
        switch(item.getItemId()) {
            case R.id.id_OC:
                break;
            case R.id.id_car:
                break;
            case R.id.id_movie:
                Intent movieActivity = new Intent(MainActivity.this,MovieActivity.class);
                startActivity(movieActivity);
                break;
            case R.id.id_soccer:
                Intent loginPage = new Intent(MainActivity.this, LoginPage.class);
                startActivity(loginPage);
                break;
            case R.id.help:
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Need Help?")
                    .setMessage("To navigate to the activities, select any of the four buttons on the homescreen or the navigation bar")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function loads the activity_main layout and sets actions on the buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item -> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }));

        //Go to MovieActivity
        Button movieBtn = findViewById(R.id.movie_btn);
        movieBtn.setOnClickListener(clk->{
            Intent moviePage = new Intent(MainActivity.this, MovieActivity.class);
            startActivity(moviePage);
        });

//        //Go to OCActivity
//        Button busBtn = findViewById(R.id.bus_btn);
//        busBtn.setOnClickListener(clk->{
//            Intent yourPage = new Intent(MainActivity.this, YourActivity.class);
//            startActivity(yourPage);
//        });
//
       //Go to SoccerActivity
       Button soccerBtn = findViewById(R.id.soccer_btn);
       soccerBtn.setOnClickListener(clk->{
           Intent loginPage = new Intent(MainActivity.this, LoginPage.class);
           startActivity(loginPage);
       });

       //Go to CarActivity
       Button carBtn = findViewById(R.id.car_btn);
       carBtn.setOnClickListener(clk->{
           Intent chargingStationPage = new Intent(MainActivity.this, ChargingStation.class);
           startActivity(chargingStationPage);
       });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }
}