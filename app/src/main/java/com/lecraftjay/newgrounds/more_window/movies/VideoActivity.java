package com.lecraftjay.newgrounds.more_window.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;

public class VideoActivity extends AppCompatActivity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //------------------------------------------------------------

        video = findViewById(R.id.video);

        //------------------------------------------------------------

        Uri uri = Uri.parse(Var.videoUrl);
        video.setVideoURI(uri);
        video.requestFocus();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        video.setLayoutParams(new LinearLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));

        video.start();
    }
}