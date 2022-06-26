package com.lecraftjay.newgrounds.more_window.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

import org.jetbrains.annotations.NotNull;

public class VideoActivity extends AppCompatActivity {

    VideoView video;
    RelativeLayout layout;
    RelativeLayout controlLayout;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 100;

    boolean controlShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        loadStuff();
    }

    public void loadStuff(){
        //------------------------------------------------------------

        video = findViewById(R.id.video);
        layout = findViewById(R.id.videoRoot);
        controlLayout = findViewById(R.id.videoControlLayout);

        //------------------------------------------------------------

        getSupportActionBar().hide();

        Uri uri = Uri.parse(Var.videoUrl);
        video.setVideoURI(uri);
        video.requestFocus();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        video.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));


        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlShow) {
                    controlLayout.removeAllViews();
                    controlShow = false;
                }else{
                    View view = LayoutInflater.from(VideoActivity.this).inflate(R.layout.video_controll_layout, null);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            controlLayout.removeAllViews();
                            controlShow = false;
                        }
                    });
                    ImageButton playPause = view.findViewById(R.id.videoPlayPause);
                    playPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(playPause.getTag().equals("true")) {
                                playPause.setImageResource(R.drawable.play);
                                playPause.setTag("false");
                            }else{
                                playPause.setImageResource(R.drawable.pause);
                                playPause.setTag("true");
                            }
                        }
                    });
                    controlLayout.addView(view);
                    controlShow = true;
                }
            }
        });

        video.start();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_video);
        loadStuff();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        //IronSource.onResume(this);

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                update();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    public void update(){
        if(controlShow){
            try {
                Thread.sleep(3000);
                controlShow = false;
                controlLayout.removeAllViews();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}