package com.lecraftjay.newgrounds.nav_window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.FeedActivity;
import com.lecraftjay.newgrounds.more_window.LoginActivity;
import com.lecraftjay.newgrounds.more_window.profile.PlaylistActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProfileActivity extends AppCompatActivity {

    CardView playlist;
    CardView feed;
    TextView count;
    Button login;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //-------------------------------------------------------

        playlist = findViewById(R.id.profilePlaylist);
        count = findViewById(R.id.playlistCount);
        login = findViewById(R.id.profileLogin);
        logout = findViewById(R.id.profileLogout);
        feed = findViewById(R.id.profileYourFeed);

        //-------------------------------------------------------

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString("allPlaylist", "null");
        if(!getter.equals("null") && !getter.equals("")){
            String[] split = getter.split(";;;");
            String c = String.valueOf(split.length);
            count.setText(c);
        }

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

}