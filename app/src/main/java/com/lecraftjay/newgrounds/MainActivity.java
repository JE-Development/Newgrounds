package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.NewFeaturesActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    TextView closed;
    TextView reason;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------------------------------------------

        closed = findViewById(R.id.mainService);
        reason = findViewById(R.id.mainReason);
        root = findViewById(R.id.root);

        //--------------------------------------------------


        Var.toEmail = "jm.enns04@gmail.com";
        Var.pass = "emailsender22";

        SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Info", 0);
        String getter = sp1.getString("updateVersionCode", "0");

        String updateCode = "5";

        if(getter.equals(updateCode)){
            startActivity(new Intent(MainActivity.this, AudioActivity.class));
            finish();
        }else{
            SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
            SharedPreferences.Editor editor = spe.edit();
            editor.putString("updateVersionCode", updateCode);
            editor.apply();

            startActivity(new Intent(MainActivity.this, NewFeaturesActivity.class));
            finish();
        }
    }
}