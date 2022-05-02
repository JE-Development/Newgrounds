package com.lecraftjay.newgrounds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

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
    Notification notification;

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

        //--------------------------------------------------------------------

        backgroundSwitch.setChecked(Var.allowBackgroundPlaying);

        ActionBar actionBar = getSupportActionBar();
        String titleBarLoading = "<font color='#ffc400'>" + actionBar.getTitle() + "</font>";
        actionBar.setTitle(Html.fromHtml(titleBarLoading));

        trackTitle.setText(trim(Var.currentTitle, 28));
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
                        }
                    }

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
                            System.out.println("jason doc createdLink: " + createdLink);
                            Var.listenLink = createdLink;
                        }
                        String link = l.attr("abs:href");
                        if(link.contains("listen")) {
                            counter++;
                            System.out.println("jason doc linkFound" + counter + ": " + link);

                        }
                    }
                    System.out.println("jason doc link: " + Var.openLink);
                    //System.out.println("jason doc data: " + doc.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public void startAudio() {

        play.setTag("isPlaying");
        play.setImageResource(R.drawable.pause);

        String audioUrl = Var.listenLink;

        if(Var.mediaPlayer == null) {
            Var.mediaPlayer = new MediaPlayer();
        }else{
            Var.mediaPlayer.stop();
            Var.mediaPlayer = new MediaPlayer();
        }

        Var.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            Var.mediaPlayer.setDataSource(audioUrl);
            Var.mediaPlayer.prepare();
            Var.mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        trackProgress.setMax(Var.mediaPlayer.getDuration());
        Toast.makeText(this, "Audio started playing.." + Var.mediaPlayer.getDuration(), Toast.LENGTH_SHORT).show();
        trackDuration = Var.mediaPlayer.getDuration();
        playerReady = true;

    }

    public void pauseAudio(){
        if(Var.mediaPlayer != null) {
            Var.mediaPlayer.pause();
        }
        play.setTag("isPaused");
        play.setImageResource(R.drawable.play);
    }

    public void playAudio(){
        Var.mediaPlayer.start();
        play.setTag("isPlaying");
        play.setImageResource(R.drawable.pause);
    }

    public void updateTrackProgress(){
        if(playerReady) {
            trackProgress.setProgress(Var.mediaPlayer.getCurrentPosition());
            timeLeft.setText(getTimeLeft(Var.mediaPlayer.getCurrentPosition()));
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
    }

    public void updateOneTime(){
        if(Var.updateWave){
            Var.updateWave = false;
            Picasso.get().load(Var.waveLink).into(trackWave);
            play.setVisibility(View.VISIBLE);

            creatorLink.setTag(Var.creatorLink);
            creatorName.setText(Var.creatorName);
            Picasso.get().load(Var.creatorIconLink).into(creatorIcon);
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
        int sec = dur/1000;

        int s = sec%60;
        int m = sec/60;
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

        String left = timeLeft.getText().toString();
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