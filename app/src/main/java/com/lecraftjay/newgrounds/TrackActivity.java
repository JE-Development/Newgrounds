package com.lecraftjay.newgrounds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TrackActivity extends AppCompatActivity {

    int pos = 0;

    Button play;
    Button pause;
    TextView trackTitle;
    SeekBar trackProgress;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 100;
    boolean playerReady = false;


    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //--------------------------------------------------------------------

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        trackProgress = findViewById(R.id.trackProgress);
        trackTitle = findViewById(R.id.trackTitle);

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

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    // pausing the media player if media player
                    // is playing we are calling below line to
                    // stop our media player.

                    mediaPlayer.pause();

                    // below line is to display a message
                    // when media player is paused.
                    Toast.makeText(TrackActivity.this, "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    // this method is called when media
                    // player is not playing.
                    mediaPlayer.start();
                    Toast.makeText(TrackActivity.this, "Audio has not played", Toast.LENGTH_SHORT).show();
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
                            String[] splitter = html.split("\"images\":{\"listen\":{\"playing\":{\"url\":\"");
                            char[] finder = splitter[1].toCharArray();
                            String waveLink = "";
                            for(int i = 0; i < finder.length; i++){
                                if(finder[i] != '?'){
                                    waveLink = waveLink + finder[i];
                                }else{
                                    break;
                                }
                            }
                            Var.waveLink = waveLink;
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

    public void playAudio() {

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
        playerReady = true;
    }

    public void updateTrackProgress(){
        if(playerReady) {
            trackProgress.setProgress(mediaPlayer.getCurrentPosition());
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
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void updateWave(){
        if(Var.updateWave){
            Var.updateWave = false;
            Picasso.get().load(splitter[2]).into(icon);
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