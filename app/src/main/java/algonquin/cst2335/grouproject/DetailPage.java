package algonquin.cst2335.grouproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_page);

        Button saveButton = findViewById(R.id.save_button);
        Button guideButton = findViewById(R.id.guide_button);
//        guideButton.
        TextView browserButton = findViewById(R.id.browserButton);

        TextView title = findViewById(R.id.detail_title);
        TextView date = findViewById(R.id.detail_date);
        TextView description = findViewById(R.id.detail_description);
        ImageView image = findViewById(R.id.detailImage);


        //Adding database
        DatabaseConnector opener = new DatabaseConnector(this);
        SQLiteDatabase db = opener.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getInt("Id") + "";
            title.setText(extras.getString("TitleName"));
            date.setText(extras.getString("PubDate"));
            description.setText(extras.getString("Description"));
            String rawImgUrl = (extras.getString("ImgUrl"));
            String browserLink = extras.getString("Link");

            ExecutorService newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {
                try {
                    String stringUrl = rawImgUrl.replace("http://", "https://");

                    URL url = new URL(stringUrl);

                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            image.setImageBitmap(bmp);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            //This is to judge if the article is already saved
            saveButton.setText(getCount(db, id) == 0 ? getResources().getString(R.string.save)
                    : getResources().getString(R.string.unsave));
            saveButton.setOnClickListener(e -> {
                if (saveButton.getText().toString().equals(getResources().getString(R.string.save))) {

                    ContentValues newRow = new ContentValues();
                    newRow.put(DatabaseConnector.COLUMN_NAME_ID, id);
                    newRow.put(DatabaseConnector.COLUMN_NAME_TITLE, title.getText().toString());
                    newRow.put(DatabaseConnector.COLUMN_NAME_DATE, date.getText().toString());
                    newRow.put(DatabaseConnector.COLUMN_NAME_IMG_URL, rawImgUrl);
                    newRow.put(DatabaseConnector.COLUMN_NAME_DESCRIPTION, description.getText().toString());
                    newRow.put(DatabaseConnector.COLUMN_NAME_LINK, browserLink);

                    db.insert(DatabaseConnector.TABLE_NAME, DatabaseConnector.COLUMN_NAME_DESCRIPTION, newRow);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.article_saved_notice), Toast.LENGTH_SHORT).show();
                    saveButton.setText(getResources().getString(R.string.unsave));
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(this)
//set icon
                            .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                            .setTitle(getResources().getString(R.string.alert))
//set message
                            .setMessage(getResources().getString(R.string.article_delete_confirm))
//set positive button
                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.execSQL("DELETE FROM " + DatabaseConnector.TABLE_NAME + " WHERE id = " + id + "");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.article_removed_notice),
                                            Toast.LENGTH_SHORT).show();
                                    saveButton.setText(getResources().getString(R.string.save));
                                }
                            })
//set negative button
                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }

                if (getCount(db, id) == 0) {

                } else {

                }

            });


            browserButton.setOnClickListener(e -> {
                Uri uri = Uri.parse(browserLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });

        }

    }

    public int getCount(SQLiteDatabase db, String id) {
        Cursor c = null;
        try {
            String query = "select count(*) from fave_list where id = ?";
            c = db.rawQuery(query, new String[]{id});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {

        }
    }


}
