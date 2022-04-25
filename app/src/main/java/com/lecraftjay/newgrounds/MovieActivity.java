package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        
        setNavigation();
    }

    public void setNavigation(){
        ImageView games = findViewById(R.id.games);
        ImageView movie = findViewById(R.id.movie);
        ImageView audio = findViewById(R.id.audio);
        ImageView art = findViewById(R.id.art);
        ImageView community = findViewById(R.id.games);

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MovieActivity.this, GamesActivity.class));
                finish();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MovieActivity.this, AudioActivity.class));
                finish();
            }
        });

        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MovieActivity.this, ArtActivity.class));
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MovieActivity.this, CommunityActivity.class));
                finish();
            }
        });
    }
}