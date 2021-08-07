package algonquin.cst2335.grouproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoccerListFragment extends Fragment {
    oneRowMessage titleMessage;
    RecyclerView soccerList;
    NewsAdapter adt;
    SQLiteDatabase db;
    Button savedButton;
    ArrayList<oneRowMessage> news = new ArrayList<>();
    private String stringURL;
    String mainTitle;
    HashMap<Integer, HashMap<String, String>> items = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View soccerLayout = getLayoutInflater().inflate(R.layout.news_list, container, false);

        //Remove the current content in the message edit box

        soccerList = soccerLayout.findViewById(R.id.myRecycler);
        fetchData();

        savedButton = soccerLayout.findViewById(R.id.savedButton);
//        savedButton.setOnClickListener(e -> {
//
//        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adt = new NewsAdapter();
        soccerList.setAdapter(adt);
//        soccerList.setLayoutManager(new LinearLayoutManager(getContext()));

        for (int i = 1; i <= items.size(); i++) {
            String title = items.get(i).get("title");
            String pubDate = items.get(i).get("pubDate");
            String link = items.get(i).get("link");
            String description = items.get(i).get("description");
            String imgUrl = items.get(i).get("imgUrl");
            titleMessage = new oneRowMessage(i, pubDate, link, title, description, imgUrl);
            news.add(titleMessage);
            adt.notifyItemInserted(news.size() - 1);
            soccerList.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        System.out.println("www");
        return soccerLayout;

    }


    public class oneRowMessage {
        int id;
        String pubdate;
        String link;
        String titleName;
        String description;
        String imgUrl;

        public oneRowMessage(int id, String pubdate, String link, String titleName, String description, String imgUrl) {
            this.id = id;
            this.pubdate = pubdate;
            this.link = link;
            this.titleName = titleName;
            this.description = description;
            this.imgUrl = imgUrl;
        }

        public int getId() {
            return id;
        }

        public String getPubdate() {
            return pubdate;
        }

        public String getLink() {
            return link;
        }

        public String getTitleName() {
            return titleName;
        }


        public String getImgUrl() {
            return imgUrl;
        }
    }


    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView dateText;
        ImageView imagePreview;
        int position = -1;


        //        int position = -1;
        //Constructor
        public MyRowViews(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.message);
            dateText = itemView.findViewById(R.id.time);
            imagePreview= itemView.findViewById(R.id.imageView);

        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<MyRowViews> {

        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.single_news, parent, false);
            MyRowViews initRow = new MyRowViews(loadedRow);// the loadedRow is the new itemView

            return initRow;
        }


        @Override
        public void onBindViewHolder(MyRowViews holder, int position){
            holder.titleText.setText(news.get(position).getTitleName());
            holder.dateText.setText(news.get(position).getPubdate());

            ExecutorService newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {
                try {

                    String stringUrl = news.get(position).getImgUrl().replace("http://","https://");

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
                holder.setPosition(position);
            });
        }

        @Override
        public int getItemCount() {
            return news.size();
        }

    }

    private class Builder extends AlertDialog.Builder {

        public Builder(Context context) {
            super(context);
        }

        public Builder(Context context, int themeResId) {

            super(context, themeResId);
        }
    }

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
//                                        System.out.println("11111111111111111111"+xpp.getName());
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
                            break;
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

//    private Bitmap retrieveImage(String url) throws IOException {
//        InputStream in = new URL(url).openStream();
//        Bitmap bitmap = BitmapFactory.decodeStream(in);
//        return bitmap;
//    }

}
