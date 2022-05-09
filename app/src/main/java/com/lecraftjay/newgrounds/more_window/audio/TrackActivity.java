package com.lecraftjay.newgrounds.more_window.audio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrackActivity extends AppCompatActivity {

    int pos = 0;

    ImageButton play;
    TextView trackTitle;
    SeekBar trackProgress;
    Handler handler = new Handler();
    Runnable runnable;
    ImageView trackWave;
    TextView timeLeft;
    TextView timeRight;
    int delay = 100;
    boolean playerReady = false;
    int trackDuration = 0;
    ImageView openLink;
    CircleImageView creatorIcon;
    TextView creatorName;
    LinearLayout creatorLink;
    TextView description;
    Switch backgroundSwitch;
    ProgressBar playProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //--------------------------------------------------------------------

        play = findViewById(R.id.play);
        trackProgress = findViewById(R.id.trackProgress);
        trackTitle = findViewById(R.id.trackTitle);
        trackWave = findViewById(R.id.trackWave);
        timeLeft = findViewById(R.id.trackTimeLeft);
        timeRight = findViewById(R.id.trackTimeRight);
        openLink = findViewById(R.id.openLink);
        creatorName = findViewById(R.id.creatorName);
        creatorLink = findViewById(R.id.creatorLayoutLink);
        creatorIcon = findViewById(R.id.creatorIcon);
        description = findViewById(R.id.trackDescription);
        backgroundSwitch = findViewById(R.id.trackBackgroundSwitch);
        playProgress = findViewById(R.id.trackPlayProgress);

        //--------------------------------------------------------------------

        backgroundSwitch.setChecked(Var.allowBackgroundPlaying);

        ActionBar actionBar = getSupportActionBar();
        String titleBarLoading = "<font color='#ffc400'>" + actionBar.getTitle() + "</font>";
        actionBar.setTitle(Html.fromHtml(titleBarLoading));

        trackTitle.setText(Html.fromHtml(trim(Var.currentTitle, 28)));
        trackTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TrackActivity.this, Var.currentTitle, Toast.LENGTH_LONG).show();
            }
        });

        backgroundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Var.allowBackgroundPlaying = backgroundSwitch.isChecked();
            }
        });

        creatorLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TrackActivity.this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        openLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(Var.openLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        trackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Var.mediaPlayer.seekTo(seekBar.getProgress());
                playAudio();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(play.getTag().equals("start")) {
                    startAudio();
                }else if(play.getTag().equals("isPlaying")){
                    pauseAudio();
                }else if(play.getTag().equals("isPaused")){
                    playAudio();
                }
            }
        });

        pos += 25;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("jason open: " + Var.openLink);
                    Document doc = (Document) Jsoup
                            .connect(Var.openLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();
                    Elements titles = doc.select(".entrytitle");

                    // print all titles in main page
                    for (Element e : titles) {

                    }

                    // print all available links on page
                    Elements links = doc.select("script");
                    Elements creator = doc.getElementsByClass("pod");
                    Elements description = doc.getElementsByClass("pod");

                    for(Element l : description){
                        Elements desc = l.getElementsByClass("pod-body ql-body");
                        String test = desc.toString();
                        if(test.contains("pod-body ql-body")) {
                            Var.description = desc.html();
                        }
                    }

                    for(Element l : creator){
                        String tester = l.child(0).html();
                        if(tester.contains("class=\"user\"")){
                            Elements creatorLink = l.getElementsByClass("item-icon");
                            Elements creatorName = l.select("svg");
                            Elements creatorIcon = l.select("image");

                            String cl = creatorLink.attr("abs:href");
                            String cn = creatorName.attr("alt");
                            String ci = creatorIcon.attr("abs:href");

                            Var.creatorLink = cl;
                            Var.creatorName = cn;
                            Var.creatorIconLink = ci;
                        }
                    }

                    for(Element l : links){
                        String html = l.html();
                        if(html.contains("\"images\":{\"listen\":{\"playing\":{\"url\":\"")){
                            String[] splitter = html.split("\"images\":\\{\"listen\":\\{\"playing\":\\{\"url\":\"");
                            char[] finder = splitter[1].toCharArray();
                            String waveLink = "";
                            for(int i = 0; i < finder.length; i++){
                                if(finder[i] != '?'){
                                    waveLink = waveLink + finder[i];
                                }else{
                                    break;
                                }
                            }
                            Var.waveLink = waveLink.replace("\\", "");
                            Var.updateWave = true;
                        }else if(html.contains("listen.completed.png\"},\"playing\":{\"url\":\"\\/\\/")){
                            String[] splitter = html.split("\"playing\":\\{\"url\":\"");
                            char[] finder = splitter[2].toCharArray();
                            String waveLink = "";
                            for(int i = 0; i < finder.length; i++){
                                if(finder[i] != '\"'){
                                    waveLink = waveLink + finder[i];
                                }else{
                                    break;
                                }
                            }
                            Var.waveLink = "https://" + waveLink.replace("\\", "").replace("//", "");
                            Var.updateWave = true;
                        }
                    }

                    System.out.println("jason track: " + Var.waveLink);

                    int counter = 0;
                    for (Element l : links) {
                        String html = l.html();
                        if(html.contains("var embed_controller")){
                            html = html.replace("var embed_controller = new embedController([{\"url\":\"", "");
                            char[] htmlChar = html.toCharArray();
                            String createdLink = "";
                            for(int i = 0; i < htmlChar.length; i++){
                                if(htmlChar[i] == '?'){
                                    createdLink = createdLink.replace("\\", "");
                                    break;
                                }else{
                                    createdLink = createdLink + htmlChar[i];
                                }
                            }
                            Var.listenLink = createdLink;
                        }
                        String link = l.attr("abs:href");
                        if(link.contains("listen")) {
                            counter++;

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public void startAudio() {
        playProgress.setVisibility(View.VISIBLE);

        play.setTag("isPlaying");
        play.setImageResource(R.drawable.pause);

        String audioUrl = Var.listenLink;

        if(Var.mediaPlayer == null) {
            Var.mediaPlayer = new MediaPlayer();
        }else{
            Var.mediaPlayer.stop();
            Var.mediaPlayer = new MediaPlayer();
        }

        Thread t = new Thread(new Runnable() {
            public void run() {
                Var.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    Var.mediaPlayer.setDataSource(audioUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Var.einmal = true;
            }
        });
        t.start();


    }

    public void startPlayer(){
        if(Var.einmal){
            Var.einmal = false;

            try {
                Var.mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Var.mediaPlayer.start();

            trackProgress.setMax(Var.mediaPlayer.getDuration());
            trackDuration = Var.mediaPlayer.getDuration();
            playerReady = true;

            playProgress.setVisibility(View.INVISIBLE);
        }
    }

    public void pauseAudio(){
        if(Var.mediaPlayer != null) {
            Var.mediaPlayer.pause();
        }
        play.setTag("isPaused");
        play.setImageResource(R.drawable.play);
    }

    public void playAudio(){
        if(Var.mediaPlayer != null) {
            Var.mediaPlayer.start();
            play.setTag("isPlaying");
            play.setImageResource(R.drawable.pause);
        }else{
            startAudio();
        }
    }

    public void updateTrackProgress(){
        if(playerReady) {
            int dur = Var.mediaPlayer.getDuration();
            dur = dur / 1000;
            int m = dur/60;
            int s = dur%60;

            String min = String.valueOf(m);
            String sec = String.valueOf(s);
            if(min.length() <= 1){
                min = "0" + min;
            }
            if(sec.length() <= 1){
                sec = "0" + sec;
            }

            String time = min + ":" + sec;


            trackProgress.setProgress(Var.mediaPlayer.getCurrentPosition());
            timeLeft.setText(getTimeLeft(Var.mediaPlayer.getCurrentPosition()) + " / " + time);
            timeRight.setText(getTimeRight(Var.mediaPlayer.getDuration()));
        }
    }



    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                updateTrackProgress();
                updateOneTime();
                startPlayer();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        if(!Var.allowBackgroundPlaying) {
            pauseAudio();
        }
        super.onPause();

        Var.description = "";
        Var.creatorLink = "";
        Var.creatorIconLink = "";
        Var.creatorName = "";
        Var.waveLink = "";

    }

    public void updateOneTime(){
        if(Var.updateWave){

            Var.updateWave = false;
            Picasso.get().load(Var.waveLink).into(trackWave);
            play.setVisibility(View.VISIBLE);
            creatorLink.setTag(Var.creatorLink);
            creatorName.setText(Var.creatorName);
            try {
                Picasso.get().load(Var.creatorIconLink).into(creatorIcon);
            }catch (Exception e){
                e.printStackTrace();
                creatorName.setText("{error in user}");
                creatorName.setTextSize(18);
                creatorName.setTextColor(Color.rgb(255,50,50));
            }
            description.setText(Html.fromHtml(Var.description));
        }
    }

    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }

    public String getTimeLeft(int dur){
        int zeit = dur/1000;

        int s = zeit%60;
        int m = zeit/60;

        String second = String.valueOf(s);
        String minute = String.valueOf(m);
        if(second.length() <= 1){
            second = "0" + second;
        }
        if(minute.length() <= 1){
            minute = "0" + minute;
        }

        String time = minute + ":" + second;
        return time;
    }

    public String getTimeRight(int dur){
        int d = dur/1000;


        String left = timeLeft.getText().toString().substring(0, timeLeft.getText().toString().indexOf(" "));
        String[] splitter = left.split(":");
        int min1 = Integer.parseInt(splitter[0]);
        int sec1 = Integer.parseInt(splitter[1]);
        int second1 = min1*60+sec1;
        int sum = d-second1;

        int s = sum%60;
        int m = sum/60;
        String second = String.valueOf(s);
        String minute = String.valueOf(m);
        if(second.length() <= 1){
            second = "0" + second;
        }
        if(minute.length() <= 1){
            minute = "0" + minute;
        }

        String time = minute + ":" + second;
        return time;
    }
}