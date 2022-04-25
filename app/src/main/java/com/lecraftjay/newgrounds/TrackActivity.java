package com.lecraftjay.newgrounds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

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
    MediaPlayer mediaPlayer;
    ImageView openLink;

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

        //--------------------------------------------------------------------

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
                mediaPlayer.seekTo(seekBar.getProgress());
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

        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // below line is use to display a toast message.

        trackProgress.setMax(mediaPlayer.getDuration());
        Toast.makeText(this, "Audio started playing.." + mediaPlayer.getDuration(), Toast.LENGTH_SHORT).show();
        trackDuration = mediaPlayer.getDuration();
        playerReady = true;

    }

    public void pauseAudio(){
        mediaPlayer.pause();
        play.setTag("isPaused");
        play.setImageResource(R.drawable.play);
    }

    public void playAudio(){
        mediaPlayer.start();
        play.setTag("isPlaying");
        play.setImageResource(R.drawable.pause);
    }

    public void updateTrackProgress(){
        if(playerReady) {
            trackProgress.setProgress(mediaPlayer.getCurrentPosition());
            timeLeft.setText(getTimeLeft(mediaPlayer.getCurrentPosition()));
            timeRight.setText(getTimeRight(mediaPlayer.getDuration()));
        }
    }



    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                updateTrackProgress();
                updateWave();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        pauseAudio();
        super.onPause();
    }

    public void updateWave(){
        if(Var.updateWave){
            Var.updateWave = false;
            Picasso.get().load(Var.waveLink).into(trackWave);
            play.setVisibility(View.VISIBLE);
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