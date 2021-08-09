package algonquin.cst2335.grouproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manage all the saved news in the fragment activity. It is using the same layout file
 * as the main news list. But it only displays the news that are saved as favorite news.
 */
public class NewSavedList extends FragmentActivity {


    RecyclerView soccerList;
    Button savedButton;
    NewsAdapter adt;
    ArrayList<oneRowMessage> news = new ArrayList<>();
    oneRowMessage titleMessage;

    String numId;
    String pubDate;
    String link;
    String title;
    String description;
    String imgUrl;

    /**
     * When the activity is activated, the program will fetch all the data from the database to
     * populate the recycler view list. The program is using a cursor object to scan through the
     * database.
     *
     * @param savedInstanceState The previous user settings.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);


        soccerList = findViewById(R.id.myRecycler);
        savedButton = findViewById(R.id.savedButton);
        savedButton.setText(getResources().getString(R.string.go_back_button));
        savedButton.setOnClickListener(e -> {
            finish();
        });

        Button guideButton = findViewById(R.id.guide_button);
        guideButton.setOnClickListener(e -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.guide))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });

        adt = new NewsAdapter();
        soccerList.setAdapter(adt);
        soccerList.setLayoutManager(new LinearLayoutManager(this));


        DatabaseConnector opener = new DatabaseConnector(this);
        SQLiteDatabase db = opener.getReadableDatabase();

        Cursor results = db.rawQuery("SELECT * FROM " + DatabaseConnector.TABLE_NAME + ";", null);
        while (results.moveToNext()) {
            int idCol = results.getColumnIndex("id");
            int titleCol = results.getColumnIndex("title");
            int dateCol = results.getColumnIndex("date");
            int imgCol = results.getColumnIndex("img_url");
            int descriptionCol = results.getColumnIndex("description");
            int linkCol = results.getColumnIndex("link");

            numId = results.getString(idCol);
            pubDate = results.getString(dateCol);
            link = results.getString(linkCol);
            title = results.getString(titleCol);
            description = results.getString(descriptionCol);
            imgUrl = results.getString(imgCol);
            titleMessage = new oneRowMessage(Integer.parseInt(numId), pubDate, link, title, description, imgUrl);
            news.add(titleMessage);

            //once added, the app adds the news immediately.
        }
        soccerList.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * This is the view of one row.
     */
    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView dateText;
        ImageView imagePreview;
        int position = -1;

        //Constructor
        public MyRowViews(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.message);
            dateText = itemView.findViewById(R.id.time);
            imagePreview = itemView.findViewById(R.id.imageView);
        }

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
            imgUrl = news.get(position).getImgUrl();
            holder.itemView.setOnClickListener(e -> {
                Intent nextPage = new Intent(getApplicationContext(), DetailPage.class);
                nextPage.putExtra("Id", news.get(position).getId());
                nextPage.putExtra("ImgUrl", news.get(position).getImgUrl());
                nextPage.putExtra("Link", news.get(position).getLink());
                nextPage.putExtra("PubDate", news.get(position).getPubDate());
                nextPage.putExtra("TitleName", news.get(position).getTitleName());
                nextPage.putExtra("Description", news.get(position).getDescription());

                //directly send the info into the detail page.
                startActivity(nextPage);

            });

            ExecutorService newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {
                try {
                    String stringUrl = imgUrl.replace("http://", "https://");
                    URL url = new URL(stringUrl);

                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    runOnUiThread(new Runnable() {

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

        public oneRowMessage(int id, String pubDate, String link, String titleName, String description, String imgUrl) {
            this.id = id;
            this.pubDate = pubDate;
            this.link = link;
            this.titleName = titleName;
            this.description = description;
            this.imgUrl = imgUrl;
        }

        public int getId() {
            return id;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getLink() {
            return link;
        }

        public String getTitleName() {
            return titleName;
        }

        public String getDescription() {
            return description;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }

    /**
     * This function is to make sure whenever user click back to the fav list page, the page layout will
     * be refreshed to make sure it is up to date.
     */
    public void onRestart() {
        super.onRestart();
        recreate();
    }

}