package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ArtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);
        
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
                startActivity(new Intent(ArtActivity.this, GamesActivity.class));
                finish();
            }
        });

        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, MovieActivity.class));
                finish();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, AudioActivity.class));
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, CommunityActivity.class));
                finish();
            }
        });
    }
}