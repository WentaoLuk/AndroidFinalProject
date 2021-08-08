package algonquin.cst2335.grouproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_page_not_saved);

        Button saveButton = findViewById(R.id.save_button);
        TextView browserButton = findViewById(R.id.browserButton);

        TextView title = findViewById(R.id.detail_title);
        TextView date = findViewById(R.id.detail_date);
        TextView description = findViewById(R.id.detail_description);
        ImageView image = findViewById(R.id.detailImage);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            title.setText(extras.getString("TitleName"));
            date.setText(extras.getString("PubDate"));
            description.setText(extras.getString("Description"));
            String rawImgUrl = (extras.getString("ImgUrl"));


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


                //The key argument here must match that used in the other activity
            });


//        loginButton.setOnClickListener(e->{
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("LoginName",emailEditText.getText().toString());
//            editor.apply();
//            Intent nextPage = new Intent( MainActivity.this, SecondActivity.class);
//            nextPage.putExtra( "EmailAddress", emailEditText.getText().toString() );
//            startActivity( nextPage );
//        });

        }

    }
}
