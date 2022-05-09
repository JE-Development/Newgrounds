package com.lecraftjay.newgrounds.more_window.movie;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.lecraftjay.newgrounds.R;

public class MovieContentActivity extends AppCompatActivity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_content);

        //----------------------------------------------------

        video = findViewById(R.id.movieVideo);

        //----------------------------------------------------

        /*Uri uri = Uri.parse("https://uploads.ungrounded.net/alternate/1876000/1876089_alternate_180628.720p.mp4?1651927389");
        video.setVideoURI(uri);
        video.requestFocus();
        video.start();*/
    }
}