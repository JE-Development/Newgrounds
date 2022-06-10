package com.lecraftjay.newgrounds.more_window.audio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lecraftjay.newgrounds.more_window.UserActivity;
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
    ImageButton loop;
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
    LinearLayout playlist;
    LinearLayout like;
    ImageView playlistIcon;
    ImageView likeIcon;
    String trackName = "";
    String playlistName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //--------------------------------------------------------------------

        play = findViewById(R.id.play);
        loop = findViewById(R.id.loop);
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
        playlist = findViewById(R.id.trackPlaylist);
        //like = findViewById(R.id.trackLike);
        playlistIcon = findViewById(R.id.trackPlaylistIcon);
        //likeIcon = findViewById(R.id.trackLikeIcon);

        //--------------------------------------------------------------------

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Settings", 0);
        String getter = sp.getString("loop", "false");
        loop.setTag(getter);
        if(getter.equals("true")){
            loop.setImageResource(R.drawable.loop_green);
            if(Var.mediaPlayer != null){
                Var.mediaPlayer.setLooping(true);
            }
        }

        SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Settings", 0);
        String getter1 = sp1.getString("backgroundPlaying", "false");
        backgroundSwitch.setChecked(getter1.equals("true") ? true : false);
        Var.allowBackgroundPlaying = getter1.equals("true") ? true : false;

        if(Var.openFromPlaylist){
            backgroundSwitch.setChecked(true);
            Var.allowBackgroundPlaying = true;
            backgroundSwitch.setEnabled(false);

            //wenn man audio welches in einer playlist from trackLayout startet spielt es nicht im playlistlayout
        }

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

        getTrackInPlaylist();

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistIcon.getTag().toString().equals("false")) {

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                    String getter = sp.getString("allPlaylist", "null");
                    if(!getter.equals("null")) {
                        String[] pl = getter.split(";;;");

                        AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this);
                        builder.setTitle("Choose a playlist");
                        builder.setItems(pl, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String z = pl[which];

                                playlistName = z;

                                SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                                String getter = sp.getString(z, "null");
                                if (getter.equals("null")) {
                                    String fin = Var.openLink + ";" + Var.currentTitle + ";" + Var.trackCreator + ";"
                                            + Var.trackGenre + ";" + Var.trackDescription + ";" + Var.trackIcon;

                                    SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                    SharedPreferences.Editor editor = sh.edit();
                                    editor.putString(z, fin);
                                    editor.apply();
                                } else {
                                    String fin = Var.openLink + ";" + Var.currentTitle + ";" + Var.trackCreator + ";"
                                            + Var.trackGenre + ";" + Var.trackDescription + ";" + Var.trackIcon + ";;;" + getter;

                                    SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                    SharedPreferences.Editor editor = sh.edit();
                                    editor.putString(z, fin);
                                    editor.apply();
                                }

                                playlistIcon.setImageResource(R.drawable.playlist_add_check);
                                playlistIcon.setTag("true");

                            }
                        });
                        builder.show();
                    }else{
                        Toast.makeText(TrackActivity.this, "there is no playlist", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(TrackActivity.this, "This feature is in progress", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    playlistIcon.setImageResource(R.drawable.playlist_add);
                    playlistIcon.setTag("false");

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                    String getter = sp.getString(playlistName, "null");

                    String[] splitter = getter.split(";;;");
                    for(int i = 0; i < splitter.length; i++){
                        String[] split = splitter[i].split(";");
                        for(int j = 0; j < split.length; j++){
                            if(Var.openLink.contains(split[0])){
                                String s = getter.replace(splitter[i], "");
                                s = s.replace(";;;;;;", ";;;");
                                if(s.charAt(0) == ';'){
                                    s = s.substring(3, s.length());
                                }
                                if(s.charAt(s.length()-1) == ';'){
                                    s = s.substring(0, s.length()-3);
                                }
                                SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                SharedPreferences.Editor editor = sh.edit();
                                editor.putString(playlistName, s);
                                editor.apply();
                            }
                        }
                    }
                }
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loop.getTag().equals("false")){
                    loop.setTag("true");
                    loop.setImageResource(R.drawable.loop_green);
                    if(Var.mediaPlayer != null){
                        Var.mediaPlayer.setLooping(true);
                    }
                    SharedPreferences s = getApplicationContext().getSharedPreferences("Settings", 0);
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("loop", "true");
                    editor.apply();
                }else if(loop.getTag().equals("true")){
                    loop.setTag("false");
                    loop.setImageResource(R.drawable.loop_red);
                    if(Var.mediaPlayer != null){
                        Var.mediaPlayer.setLooping(false);
                    }
                    SharedPreferences s = getApplicationContext().getSharedPreferences("Settings", 0);
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("loop", "false");
                    editor.apply();
                }
            }
        });

        backgroundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Var.allowBackgroundPlaying = backgroundSwitch.isChecked();
                SharedPreferences s = getApplicationContext().getSharedPreferences("Settings", 0);
                SharedPreferences.Editor editor = s.edit();
                editor.putString("backgroundPlaying", String.valueOf(backgroundSwitch.isChecked()));
                editor.apply();
            }
        });

        creatorLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Var.userLink = v.getTag().toString();
                startActivity(new Intent(TrackActivity.this, UserActivity.class));
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
        if(Var.openFromPlaylist){
            Var.externalStart = true;
        }
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

        Var.mediaPlayer.setLooping(loop.getTag().equals("true") ? true : false);

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

        Var.currentTitle = "";
        Var.openLink = "";
        Var.trackGenre = "";
        Var.trackDescription = "";
        Var.trackCreator = "";
        Var.trackIcon = "";

    }

    public void updateOneTime(){
        if(Var.updateWave){

            Var.updateWave = false;
            Picasso.get().load(Var.waveLink).into(trackWave);
            play.setVisibility(View.VISIBLE);
            loop.setVisibility(View.VISIBLE);
            playProgress.setVisibility(View.INVISIBLE);
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

    public void getTrackInPlaylist(){
        SharedPreferences shared = getApplicationContext().getSharedPreferences("Playlist", 0);
        String get = shared.getString("allPlaylist", "null");
        if(!get.equals("null")){
            String[] split = get.split(";;;");
            for(int i = 0; i < split.length; i++){
                SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                String getter = sp.getString(split[i], "null");
                if(!getter.equals("null")){
                    String[] splitter = getter.split(";;;");
                    for(int j = 0; j < splitter.length; j++){
                        String[] spl = splitter[j].split(";");
                        if(Var.openLink.contains(spl[i])){
                            playlistIcon.setImageResource(R.drawable.playlist_add_check);
                            playlistIcon.setTag("true");
                            playlistName = split[i];
                        }
                    }
                }
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