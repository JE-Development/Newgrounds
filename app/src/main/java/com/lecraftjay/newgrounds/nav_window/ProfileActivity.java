package com.lecraftjay.newgrounds.nav_window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.profile.FeedActivity;
import com.lecraftjay.newgrounds.more_window.profile.LoginActivity;
import com.lecraftjay.newgrounds.more_window.profile.PlaylistActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class ProfileActivity extends AppCompatActivity {

    CardView playlist;
    CardView feed;
    TextView count;
    Button login;
    Button logout;
    TextView serverButton;

    int delay = 100;
    Handler handler = new Handler();
    Runnable runnable;

    boolean serverTextReady = false;

    String serverContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //-------------------------------------------------------

        playlist = findViewById(R.id.profilePlaylist);
        count = findViewById(R.id.playlistCount);
        login = findViewById(R.id.profileLogin);
        logout = findViewById(R.id.profileLogout);
        feed = findViewById(R.id.profileYourFeed);
        serverButton = findViewById(R.id.profileServerButton);

        //-------------------------------------------------------

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString("allPlaylist", "null");
        if(!getter.equals("null") && !getter.equals("")){
            String[] split = getter.split(";;;");
            String c = String.valueOf(split.length);
            count.setText(c);
        }

        getServerText("https://newgrounds-worker.jason-apps.workers.dev/android/newgrounds_mobile/profile_message");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Var.isLogin = true;
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Var.isLogin = false;
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, FeedActivity.class));
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PlaylistActivity.class));
            }
        });

        setNavigation();
    }

    public void setNavigation(){
        LinearLayout games = findViewById(R.id.games);
        LinearLayout movie = findViewById(R.id.movie);
        LinearLayout audio = findViewById(R.id.audio);
        LinearLayout art = findViewById(R.id.art);
        LinearLayout community = findViewById(R.id.profile);

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, GamesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MoviesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ArtActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AudioActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    public void getServerText(String url){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn = new URL(url).openConnection();
                    System.out.println("-----------------------------------------------------");
                    InputStream in = conn.getInputStream();
                    String contents = convertStreamToString(in);
                    serverContent  = contents;
                    serverTextReady = true;
                    System.out.println("jason getText: " + contents);
                    System.out.println("-----------------------------------------------------/");

                }catch (Exception e){
                    System.out.println("jason server error");
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                update();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();

    }

    public void update(){
        if(serverTextReady){
            System.out.println("jason profile");
            serverTextReady = false;

            Toast.makeText(this, "server: " + serverContent, Toast.LENGTH_SHORT).show();


            if(serverContent.contains(";;;")){
                String[] split = serverContent.split(";;;");
                String text = split[0];
                String link = split[1];
                serverButton.setText(text);
                serverButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }
            if(serverContent.contains("null;;;null")){
                serverButton.setVisibility(View.INVISIBLE);
                serverButton.setText("null");
            }
        }
    }

}