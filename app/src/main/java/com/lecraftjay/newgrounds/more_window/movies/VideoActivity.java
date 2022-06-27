package com.lecraftjay.newgrounds.more_window.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.UserActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoActivity extends AppCompatActivity {

    VideoView video;
    RelativeLayout layout;
    LinearLayout controlLayout;
    RelativeLayout touch;
    SeekBar seek;
    TextView title;
    TextView videoTitle;
    CircleImageView creatorImage;
    TextView creatorName;
    TextView description;
    LinearLayout creatorLayout;
    LinearLayout infoLayout;
    ImageButton pPLay;
    SeekBar pSeek;

    String orientation = "P";
    String videoContent = "";

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 100;
    int max = 0;

    boolean finishGetting = false;
    boolean einmal = false;
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
        controlLayout = findViewById(R.id.controlLayout);
        touch = findViewById(R.id.controlTouch);
        title = findViewById(R.id.controlTitle);
        videoTitle = findViewById(R.id.videoTitle);
        creatorImage = findViewById(R.id.videoCreatorIcon);
        creatorName = findViewById(R.id.videoCreatorName);
        description = findViewById(R.id.videoDescription);
        creatorLayout = findViewById(R.id.videoCreatorLayoutLink);
        infoLayout = findViewById(R.id.videoInfos);
        pPLay = findViewById(R.id.videoPortraitPlay);
        pSeek = findViewById(R.id.videoPortraitProgress);

        //------------------------------------------------------------

        getSupportActionBar().hide();

        Uri uri = Uri.parse(Var.videoUrl);
        video.setVideoURI(uri);
        video.requestFocus();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        video.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));

        int orient = getResources().getConfiguration().orientation;
        if(orient == Configuration.ORIENTATION_PORTRAIT){
            orientation = "P";
        }else{
            orientation = "L";
            infoLayout.setVisibility(View.INVISIBLE);
        }

        pPLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pPLay.getTag().equals("true")){
                    pPLay.setImageResource(R.drawable.play);
                    pPLay.setTag("false");
                    video.pause();
                }else{
                    pPLay.setImageResource(R.drawable.pause);
                    pPLay.setTag("true");
                    if(video != null){
                        video.start();
                    }
                }
            }
        });

        touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orientation.equals("L")) {
                    if (controlShow) {
                        controlLayout.setVisibility(View.INVISIBLE);
                        controlShow = false;
                    } else {
                        controlLayout.setVisibility(View.VISIBLE);
                        controlShow = true;
                    }
                }
            }
        });

        ImageButton playPause = controlLayout.findViewById(R.id.videoPlayPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playPause.getTag().equals("true")){
                    playPause.setImageResource(R.drawable.play);
                    playPause.setTag("false");
                    video.pause();
                }else{
                    playPause.setImageResource(R.drawable.pause);
                    playPause.setTag("true");
                    if(video != null){
                        video.start();
                    }
                }
            }
        });

        seek = controlLayout.findViewById(R.id.controlSeek);
        seek.setMax(video.getDuration());
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                video.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(video != null){
                    video.start();
                    video.seekTo(seekBar.getProgress());
                }
            }
        });

        pSeek = controlLayout.findViewById(R.id.controlSeek);
        pSeek.setMax(video.getDuration());
        pSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                video.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(video != null){
                    video.start();
                    video.seekTo(seekBar.getProgress());
                }
            }
        });

        getContent();

        video.start();
    }

    public void getContent(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    Document doc = (Document) Jsoup
                            .connect(Var.movieOpenLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();

                    String sTitle = "";
                    String sDesc = "";
                    String sCreatorLink = "";
                    String sCreatorName = "";
                    String sCreatorImage = "";

                    Elements title = doc.select("title");
                    Elements desc = doc.getElementsByClass("pod-body ql-body");
                    Elements creatorContainer = doc.getElementsByClass("column thin");

                    for(Element e : creatorContainer){
                        Elements creatorLink = e.getElementsByClass("item-icon");
                        sCreatorLink = creatorLink.attr("href");
                        Elements image = creatorContainer.select("image");
                        sCreatorImage = image.attr("href");
                        Elements el = creatorContainer.select("svg");
                        sCreatorName = el.attr("alt");

                    }

                    sTitle = title.html();
                    sDesc = desc.html();

                    videoContent = sTitle + ";;;" + sDesc + ";;;" + sCreatorLink + ";;;" + sCreatorName + ";;;" + sCreatorImage;

                    finishGetting = true;

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_video);
        loadStuff();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            infoLayout.setVisibility(View.INVISIBLE);
            orientation = "L";
            seek.setMax(max);
            pSeek.setMax(max);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            infoLayout.setVisibility(View.VISIBLE);
            orientation = "P";
            seek.setMax(max);
            pSeek.setMax(max);
        }
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        //IronSource.onResume(this);

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                update();
                checkDuration();
                setContent();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    public void update(){
        seek.setProgress(video.getCurrentPosition());
        pSeek.setProgress(video.getCurrentPosition());
    }

    public void checkDuration(){
        int dur = video.getDuration();
        if(dur != -1 && !einmal){
            einmal = true;
            max = dur;
            seek.setMax(max);
            pSeek.setMax(max);
        }
    }

    public void setContent(){
        if(finishGetting){
            finishGetting = false;

            String[] split = videoContent.split(";;;");

            try{

                videoTitle.setText(Html.fromHtml(trim(split[0], 28)));
                videoTitle.setTag(split[0]);
                videoTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(VideoActivity.this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                description.setText(Html.fromHtml(split[1]));
                creatorLayout.setTag(split[2]);
                creatorLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Var.userLink = v.getTag().toString();
                        startActivity(new Intent(VideoActivity.this, UserActivity.class));
                    }
                });
                creatorName.setText(split[3]);
                Picasso.get().load(split[4]).into(creatorImage);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }

}