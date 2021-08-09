package algonquin.cst2335.grouproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This page will ask user to input valid email and name to log in to the soccer news page.
 */
public class LoginPage extends AppCompatActivity {
    private static String TAG = "LoginPage";

    int seconds_in_millis = 2000 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        Button loginButton = findViewById(R.id.loginButton);
        TextView emailEditText = findViewById(R.id.emailEditText);


        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String emailAddress = prefs.getString("email", "");

        //Setting email into pre-saved email address
        emailEditText.setText(emailAddress);


        loginButton.setOnClickListener(e -> {

            if (!isEmailValid(emailEditText.getText().toString())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.incorrect_email_alert))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                emailEditText.requestFocus(); //give focus back to emailEditText
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                // put the email into sharedPreference for the next time use

                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("email", emailEditText.getText().toString());
                editor.apply();

                Intent nextPage = new Intent(LoginPage.this, BridgePage.class);
                nextPage.putExtra("email", emailEditText.getText().toString());
                startActivity(nextPage);
            }
        });

    }

    /**
     * This method verifies the email format
     *
     * @param email the email user input
     * @return true if the email is valid or false if it is not valid.
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}