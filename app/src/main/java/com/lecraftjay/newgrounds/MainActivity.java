package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Settings", 0);
        String getter = sp.getString("collectStats", "true");

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        if(getter.equals("true")) {
            ParseQuery<ParseObject> query3 = ParseQuery.getQuery("AppControl");
            query3.whereEqualTo("ControlName", "Statistic");
            query3.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject control, ParseException e) {
                    if (e == null) {
                        boolean stop = control.getBoolean("StopService");
                        if (!stop) {
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Statistic");
                            query2.whereEqualTo("StatName", "ClickOnApp");
                            query2.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject info, ParseException e) {
                                    if (e == null) {
                                        Var.clickStat = info.getInt("IntStat");
                                        Var.clickStat = Var.clickStat + 1;
                                        info.put("IntStat", Var.clickStat);
                                        info.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // Saved successfully.
                                                } else {
                                                    // The save failed.
                                                    Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Stats not working", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Feedback not working", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Infos");
        query1.whereEqualTo("InfoHeadline", "FeedbackPass");
        query1.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject info, ParseException e) {
                if (e == null) {
                    String pass = info.getString("InfoContent");
                    String email = info.getString("InfoContent2");
                    Var.toEmail = email;
                    Var.pass = pass;
                } else {
                    Toast.makeText(MainActivity.this, "Feedback not working", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AppControl");
        query.whereEqualTo("ControlName", "Main");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject control, ParseException e) {
                if (e == null) {
                    boolean stopService = control.getBoolean("StopService");
                    String reas = control.getString("Reason");
                    if(stopService){
                        closed.setVisibility(View.VISIBLE);
                        reason.setVisibility(View.VISIBLE);
                        reason.setText("Reason: " + reas);
                        root.removeAllViews();
                    }else{

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Infos");
                        query.whereEqualTo("InfoHeadline", "newUpdateContent");
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject info, ParseException e) {
                                if (e == null) {
                                    String infoID = info.getString("InfoContent");
                                    SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Info", 0);
                                    String getter = sp1.getString("updateVersionCode", "0");
                                    if(getter.equals(infoID)){
                                        startActivity(new Intent(MainActivity.this, AudioActivity.class));
                                        finish();
                                    }else{
                                        Var.whatsNew = info.getString("InfoContent2").replace("\\n", "\n");

                                        SharedPreferences version = getApplicationContext().getSharedPreferences("Info", 0);
                                        SharedPreferences.Editor editor = version.edit();
                                        editor.putString("updateVersionCode", infoID);
                                        editor.apply();

                                        startActivity(new Intent(MainActivity.this, NewFeaturesActivity.class));
                                        finish();
                                    }


                                } else {
                                    closed.setVisibility(View.VISIBLE);
                                    closed.setText("No connection to BackEnd!Possible issues: no Internet connection, server not exist anymore, wifi issues, code issues");
                                    root.removeAllViews();
                                }
                            }
                        });

                        startActivity(new Intent(MainActivity.this, AudioActivity.class));
                        finish();
                    }
                } else {
                    closed.setVisibility(View.VISIBLE);
                    closed.setText("No connection to BackEnd!Possible issues: no Internet connection, server not exist anymore, wifi issues, code issues");
                    root.removeAllViews();
                }
            }
        });
    }
}