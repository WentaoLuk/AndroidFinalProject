package algonquin.cst2335.grouproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the news list class of the activity. All the n1ews will be fetched from the server and displayed on here.
 * The main page has Navigation menu and tool bar on the top, and the saved list button and guide button on the bottom.
 */
public class SoccerListFragment extends Fragment {
    oneRowMessage titleMessage;
    RecyclerView soccerList;
    NewsAdapter adt;
    Button savedButton;
    ArrayList<oneRowMessage> news = new ArrayList<>();
    private String stringURL;
    String mainTitle;
    HashMap<Integer, HashMap<String, String>> items = new HashMap<>();
    String email;
    View soccerLayout;

    /**
     * This method is to create option menu.
     *
     * @param menu     The menu to be displayed on Toolbar and navigation menu list.
     * @param inflater The menu inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * The method runs when the activity is activated.
     *
     * @param inflater           The inflater of the recyclerview.
     * @param container          the container of the activity
     * @param savedInstanceState the saved state and profile of the app.
     * @return the layout view itself.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        soccerLayout = getLayoutInflater().inflate(R.layout.news_list, container, false);
        soccerList = soccerLayout.findViewById(R.id.myRecycler);
        fetchData();

        Toolbar myToolbar = soccerLayout.findViewById(R.id.toolbar_soccer);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        DrawerLayout drawer = soccerLayout.findViewById(R.id.drawer_layout_soccer);
        NavigationView navigationView = soccerLayout.findViewById(R.id.popout_menu_soccer);
//        navigationView

        navigationView.setNavigationItemSelectedListener(item -> {

            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        savedButton = soccerLayout.findViewById(R.id.savedButton);
        savedButton.setOnClickListener(e -> {

            Intent intent = new Intent(getActivity(), NewSavedList.class);
            getActivity().startActivity(intent);

        });


        Button guideButton = soccerLayout.findViewById(R.id.guide_button);
        guideButton.setOnClickListener(e -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getResources().getString(R.string.guide))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });

        setHasOptionsMenu(true);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adt = new NewsAdapter();
        soccerList.setAdapter(adt);

        for (int i = 1; i <= items.size(); i++) {
            String title = items.get(i).get("title");
            String pubDate = items.get(i).get("pubDate");
            String link = items.get(i).get("link");
            String description = items.get(i).get("description");
            String imgUrl = items.get(i).get("imgUrl");
            titleMessage = new oneRowMessage(i, pubDate, link, title, description, imgUrl);
            news.add(titleMessage);
        }

        soccerList.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email") + "";
        }
        String snackMessage = getResources().getString(R.string.welcome) + email;
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), snackMessage, Snackbar.LENGTH_LONG);
        snackbar.show();


        return soccerLayout;

    }


    /**
     * This class represents one row message displayed in the main news list.
     * The message view contains title, date and the picture.
     */
    public class oneRowMessage {
        int id;
        String pubDate;
        String link;
        String titleName;
        String description;
        String imgUrl;


        /**
         * The constructor of the oneRowMessage object.
         *
         * @param id          The id given to each news.
         * @param pubDate     The publication date of the news.
         * @param link        The link of the news.
         * @param titleName   The title of the news.
         * @param description The description of the news.
         * @param imgUrl      The image URL of the news.
         */
        public oneRowMessage(int id, String pubDate, String link, String titleName, String description, String imgUrl) {
            this.id = id;
            this.pubDate = pubDate;
            this.link = link;
            this.titleName = titleName;
            this.description = description;
            this.imgUrl = imgUrl;
        }

        /**
         * The getter of the id
         *
         * @return Id itself.
         */
        public int getId() {
            return id;
        }

        /**
         * The getter of the publication date
         *
         * @return The publication date itself.
         */
        public String getPubDate() {
            return pubDate;
        }

        /**
         * The getter of the link.
         *
         * @return The link itself.
         */
        public String getLink() {
            return link;
        }

        /**
         * The getter of the title.
         *
         * @return The title itself.
         */
        public String getTitleName() {
            return titleName;
        }

        /**
         * The getter of the description.
         *
         * @return The description itself.
         */
        public String getDescription() {
            return description;
        }

        /**
         * The getter of the image URL.
         *
         * @return The image URL itself.
         */
        public String getImgUrl() {
            return imgUrl;
        }
    }

    /**
     * This is the view of one row.
     */
    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView dateText;
        ImageView imagePreview;
        int position = -1;

        /**
         * The constructor of the row view.
         *
         * @param itemView the view itself.
         */
        public MyRowViews(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.message);
            dateText = itemView.findViewById(R.id.time);
            imagePreview = itemView.findViewById(R.id.imageView);
        }

        /**
         * The setter of the position
         *
         * @param position the position number of each view, starting from 1.
         */
        public void setPosition(int position) {
            this.position = position;
        }
    }

    /**
     * The adapter of the recycler view in order to make the content.
     */
    private class NewsAdapter extends RecyclerView.Adapter<MyRowViews> {

        /**
         * The getter of Item id. The method helps to prevent image from being constantly changed.
         *
         * @param position the position of the Item
         * @return the position itself
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * The getter of Item ItemViewType. The method helps to prevent image from being constantly changed.
         *
         * @param position the position of the Item
         * @return the position itself
         */
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        /**
         * This method helps to create the view by building each single news into the recycler view.
         *
         * @param parent   the Recycler, which is the container.
         * @param viewType The type of the view.
         * @return the initial row view.
         */
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.single_news, parent, false);
            MyRowViews initRow = new MyRowViews(loadedRow);// the loadedRow is the new itemView

            return initRow;
        }

        /**
         * The method combines the elements inside the view so that all the information will be
         * displayed together.
         *
         * @param holder   the view object holding the contend.
         * @param position the position of the view. Different position means different news.
         */
        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.titleText.setText(news.get(position).getTitleName());
            holder.dateText.setText(news.get(position).getPubDate());

            holder.itemView.setOnClickListener(e -> {

                SharedPreferences prefs = getActivity().getSharedPreferences("MyId", Context.MODE_PRIVATE);
                Intent nextPage = new Intent(getActivity().getApplicationContext(), DetailPage.class);
                nextPage.putExtra("Id", news.get(position).getId());
                nextPage.putExtra("ImgUrl", news.get(position).getImgUrl());
                nextPage.putExtra("Link", news.get(position).getLink());
                nextPage.putExtra("PubDate", news.get(position).getPubDate());
                nextPage.putExtra("TitleName", news.get(position).getTitleName());
                nextPage.putExtra("Description", news.get(position).getDescription());

                startActivity(nextPage);

            });

            ExecutorService newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {
                try {
                    //replace http with https to access the input
                    String stringUrl = news.get(position).getImgUrl().replace("http://", "https://");

                    URL url = new URL(stringUrl);

                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            holder.imagePreview.setImageBitmap(bmp);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                newThread.shutdown();
                holder.setPosition(position);
            });
        }

        /**
         * Getter of the item count
         *
         * @return the size of the news list, which represents the count of the items as well.
         */
        @Override
        public int getItemCount() {
            return news.size();
        }

    }

    /**
     * This method is the main method to fetch the data from the server.
     * The data is from a String URL and the method will pull all the data and store it into
     * the news list for later use.
     */
    private void fetchData() {

        ExecutorService newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> {
            try {
                stringURL = "https://www.goal.com/feeds/en/news";

                URL url = new URL(stringURL);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                //Adding into XML
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(in, null);
                int itemIndex = 1;

                while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:

                            if (xpp.getName().equals("channel")) {
                                xpp.next();
                                xpp.next();
                                mainTitle = xpp.getText();// This step gets the main title for the webpage.


                            } else if (xpp.getName().equals("item")) { // if an item tag is found, create a hashmap object to store the data of the single news.
                                boolean continueScanning = true;
                                HashMap<String, String> item = new HashMap<>();
                                String key, value;
                                //If the parser can keep going and not reaching "de:language" tag, the parser will take important data into the hashmap.
                                while (continueScanning && xpp.next() != XmlPullParser.END_DOCUMENT) {
                                    switch (xpp.getEventType()) {
                                        case XmlPullParser.START_TAG:
                                            if (xpp.getName().equals("title") || xpp.getName().equals("pubDate")
                                                    || xpp.getName().equals("link") || xpp.getName().equals("description")) {
                                                key = xpp.getName();
                                                xpp.next();

                                                value = xpp.getText();
                                                xpp.next();

                                                item.put(key, value);//put the key and value into the hashmap
                                            } else if (xpp.getName().equals("media:content")) {
                                                String imgUrl = xpp.getAttributeValue(null, "url");
                                                item.put("imgUrl", imgUrl);
                                                continueScanning = false;
                                                ;
                                            }
                                            break;
                                        case XmlPullParser.TEXT:
                                            break;
                                        case XmlPullParser.END_TAG:
                                            break;
                                    }
                                }
                                items.put(itemIndex++, item);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                        case XmlPullParser.TEXT:
                            break;
                    }
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

        });
        newThread.shutdown();
    }

    /**
     * This method is to assign the task for the app when the menu items are selected by user.
     *
     * @param item The menu item, containing Guide and Saved List.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.saved_soccer:
                Intent intent = new Intent(getActivity(), NewSavedList.class);
                getActivity().startActivity(intent);
                break;

            case R.id.guide_soccer:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.guide))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
