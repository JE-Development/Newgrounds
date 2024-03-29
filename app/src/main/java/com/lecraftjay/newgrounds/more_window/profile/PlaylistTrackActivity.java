package com.lecraftjay.newgrounds.more_window.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.UserContentActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaylistTrackActivity extends AppCompatActivity {

    TextView title;
    LinearLayout scrollLayout;
    ScrollView originalScroll;

    public static ArrayList<String> linkList = new ArrayList<>();

    ImageButton play;
    ImageButton next;
    ImageButton prev;
    ImageButton shuffle;
    ImageButton loop;
    SeekBar trackProgress;
    LinearLayout controlLayout;
    LinearLayout controlProgress;
    TextView trackName;
    ProgressBar loadingBar;
    TextView progressText;

    ArrayList<String> sortLink = new ArrayList<>();
    ArrayList<String> siteLink = new ArrayList<>();

    int delay = 100;
    Handler handler = new Handler();
    Runnable runnable;
    boolean trackReady = false;

    int trackPos = 0;
    int trackDuration = 0;
    int counterProgress = 0;
    int trackCounter = 0;


    View currentView;

    boolean einmal = false;
    boolean playerReady = false;
    boolean einmal1 = true;
    boolean einmal2 = false;

    String audioUrl = "";
    String progressString = "---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_track);

        //------------------------------------------------------------

        title = findViewById(R.id.playlistTrackTitle);
        scrollLayout = findViewById(R.id.playlistTrackScrollLayout);
        originalScroll = findViewById(R.id.playlistTrackOriginalScroll);
        play = findViewById(R.id.playPlaylist);
        next = findViewById(R.id.nextPlaylist);
        prev = findViewById(R.id.previousPlaylist);
        shuffle = findViewById(R.id.shufflePlaylist);
        loop = findViewById(R.id.loopPlaylist);
        trackProgress = findViewById(R.id.trackProgressPlaylist);
        controlLayout = findViewById(R.id.audioControllerLayoutPlaylist);
        controlProgress = findViewById(R.id.controlProgressPlaylist);
        trackName = findViewById(R.id.playerTrackName);
        loadingBar = findViewById(R.id.controlProgressPlaylistBar);
        progressText = findViewById(R.id.controlProgressText);

        //------------------------------------------------------------

        title.setText(Var.playlistName);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString(Var.playlistName, "null");
        if(!getter.equals("null") && !getter.equals("")){
            String[] splitter = getter.split(";;;");
            trackCounter = splitter.length;
            loadingBar.setMax(trackCounter);
            loadingBar.setMin(0);
        }

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

        trackName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if(currentView != null) {
                    TextView title = currentView.findViewById(R.id.cardText);
                    TextView genre = currentView.findViewById(R.id.cardGenre);
                    TextView desc = currentView.findViewById(R.id.cardDescription);
                    TextView creator = currentView.findViewById(R.id.cardCreator);
                    ImageView icon = currentView.findViewById(R.id.iconCard);
                    Var.currentTitle = (String) title.getTag();
                    Var.openLink = (String) currentView.getTag();
                    Var.trackGenre = genre.getText().toString();
                    Var.trackDescription = desc.getText().toString();
                    Var.trackCreator = creator.getText().toString();
                    Var.trackIcon = icon.getTag().toString();

                    startActivity(new Intent(PlaylistTrackActivity.this, TrackActivity.class));
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play.getTag().toString().equals("start")){
                    play.setImageResource(R.drawable.pause);
                    play.setTag("isPlaying");
                    startAudio();
                }else if(play.getTag().toString().equals("isPlaying")){
                    play.setImageResource(R.drawable.play);
                    play.setTag("isPaused");
                    pauseAudio();
                }else if(play.getTag().toString().equals("isPaused")){
                    play.setImageResource(R.drawable.pause);
                    play.setTag("isPlaying");
                    playAudio();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Var.mediaLink.get(trackPos).stop();
                if(trackPos < linkList.size()-1){
                    trackPos++;
                    startAudio();
                }else{
                    trackPos = 2;
                    //trackPos = 0;
                    startAudio();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trackPos > 0){
                    trackPos--;
                    startAudio();
                }
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle.getTag().toString().equals("random")){
                    shuffle.setImageResource(R.drawable.right);
                    shuffle.setTag("normal");
                    sortArrayList();
                }else{
                    shuffle.setImageResource(R.drawable.shuffle);
                    shuffle.setTag("random");
                    Collections.shuffle(linkList);
                }
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loop.getTag().toString().equals("false")){
                    loop.setImageResource(R.drawable.loop_green);
                    loop.setTag("true");
                }else{
                    loop.setImageResource(R.drawable.loop_red);
                    loop.setTag("false");
                }
            }
        });

        setAudioInPlaylist();
    }

    public void setAudioInPlaylist(){
        /*SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
        SharedPreferences.Editor editor = sh.edit();
        editor.putString(Var.playlistName, "null");
        editor.apply();*/

        scrollLayout.removeAllViews();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString(Var.playlistName, "null");

        if(!getter.equals("null") && !getter.equals("")){
            String[] splitter = getter.split(";;;");

            for(int i = 0; i < splitter.length; i++){
                String[] split = splitter[i].split(";");

                View view = LayoutInflater.from(PlaylistTrackActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                CircleImageView icon = view.findViewById(R.id.iconCard);
                TextView description = view.findViewById(R.id.cardDescription);
                TextView genre = view.findViewById(R.id.cardGenre);

                try {
                    cardText.setText(Html.fromHtml(trim(split[1], 28)));
                    Picasso.get().load(split[5]).into(icon);
                    description.setText(trim(split[4], 40));
                    genre.setText(split[3]);
                    icon.setTag(split[5]);

                    cardText.setTag(split[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }

                view.setTag(split[0]);

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog alertDialog = new AlertDialog.Builder(PlaylistTrackActivity.this)
                                .setTitle("Are you sure to remove this audio from the playlist?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TextView text = v.findViewById(R.id.cardText);
                                        String toDelete = getAllInfos(v.getTag().toString());
                                        String s = getter.replace(toDelete, "");
                                        s = s.replace(";;;;;;", ";;;");
                                        if(s.charAt(0) == ';'){
                                            s = s.substring(3, s.length());
                                        }
                                        if(s.charAt(s.length()-1) == ';'){
                                            s = s.substring(0, s.length()-3);
                                        }
                                        SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                        SharedPreferences.Editor editor = sh.edit();
                                        editor.putString(Var.playlistName, s);
                                        editor.apply();

                                        setAudioInPlaylist();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                        return true;
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(controlProgress.getVisibility() == View.INVISIBLE) {
                            for (int i = 0; i < linkList.size(); i++) {
                                String[] splitter = linkList.get(i).split(";;;");
                                if (splitter[1].equals(v.getTag().toString())) {
                                    trackPos = i;
                                    play.setImageResource(R.drawable.pause);
                                    play.setTag("isPlaying");
                                    startAudio();
                                }
                            }
                        }
                    }
                });

                scrollLayout.addView(view);
            }
            getAllLinks();
        }else{
            Toast.makeText(this, "no audio in playlist", Toast.LENGTH_SHORT).show();
        }

    }

    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }

    public String getAllInfos(String link){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString(Var.playlistName, "null");

        String[] splitter = getter.split(";;;");
        for(int i = 0; i < splitter.length; i++){
            String[] split = splitter[i].split(";");
            for(int j = 0; j < split.length; j++){
                if(split[0].equals(link)){
                    return splitter[i];
                }
            }
        }
        return "-;-;-;-;-;-";
    }

    public void startAudio() {

        /*for(int i = 0; i < Var.mediaLink.size(); i++){
            Var.mediaLink.get(i).stop();
        }*/
        System.out.println("jason linkist: " + linkList.toString());

        if(linkList.size() >= 1){
            String[] split = linkList.get(trackPos).split(";;;");
            audioUrl = split[0];
            getPlayingCard(split[1]);
        }

        Var.mediaPlayer = Var.mediaLink.get(trackPos);
        Var.mediaPlayer.seekTo(0);
        /*if(!skiped) {
            Var.mediaPlayer = Var.mediaLink.get(trackPos);
            Var.mediaPlayer.seekTo(0);
        }else{
            if(Var.mediaPlayer == null) {
                Var.mediaPlayer = new MediaPlayer();
            }else{
                Var.mediaPlayer.stop();
                Var.mediaPlayer = new MediaPlayer();
            }
        }*/
        Var.mediaPlayer.setLooping(loop.getTag().equals("true") ? true : false);

        Var.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                if(loop.getTag().toString().equals("true")){
                    Var.mediaPlayer.seekTo(0);
                    Var.mediaPlayer.start();
                }else{
                    int check = trackPos;
                    check++;
                    if(check < linkList.size()){
                        trackPos++;
                        //startAudio();
                    }else{
                        if(shuffle.getTag().toString().equals("random")) {
                            Collections.shuffle(linkList);
                        }else{
                            //sortArrayList();
                        }
                        trackPos--;
                        //trackPos = 0;
                        //startAudio();
                    }
                }
            }

        });

        /*if(skiped) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Var.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    try {
                        Var.mediaPlayer.setDataSource(audioUrl);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    einmal2 = true;
                }
            });
            t.start();
        }*/

        startPlayerNoSkip();
    }

    public void startPlayer(){
        if(einmal2) {
            try {
                Var.mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Var.mediaPlayer.start();

            System.out.println("------------------------------------------duration1");
            trackProgress.setMax(Var.mediaPlayer.getDuration());
            trackDuration = Var.mediaPlayer.getDuration();
            playerReady = true;
        }
    }

    public void startPlayerNoSkip(){
        Var.mediaPlayer.start();

        System.out.println("------------------------------------------duration2");
        trackProgress.setMax(Var.mediaPlayer.getDuration());
        trackDuration = Var.mediaPlayer.getDuration();
        playerReady = true;
    }

    public void updateTrackProgress(){
        if(playerReady) {
            /*int dur = Var.mediaPlayer.getDuration();
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

            String time = min + ":" + sec;*/


            trackProgress.setProgress(Var.mediaPlayer.getCurrentPosition());
            //timeLeft.setText(getTimeLeft(Var.mediaPlayer.getCurrentPosition()) + " / " + time);
            //timeRight.setText(getTimeRight(Var.mediaPlayer.getDuration()));
        }
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //startPlayer();
                updateTrackProgress();
                updateAudioController();
                checkPlayer();
                checkProgress();
                setProgressText();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        if(!Var.allowBackgroundPlaying) {
            //pauseAudio();
        }
        super.onPause();

    }

    public void checkProgress(){
        if(controlProgress.getVisibility() != View.INVISIBLE){
            if(counterProgress <= loadingBar.getMax()){
                loadingBar.setProgress(counterProgress);
            }
        }else{
            if(einmal == false){
                einmal = true;
                loadingBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setProgressText(){
        if(controlProgress.getVisibility() == View.VISIBLE){
            progressText.setText(progressString);
        }
    }

    public void checkPlayer(){
        if(Var.externalStart){
            if(einmal1){
                einmal1 = false;

                try {
                    Var.mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Var.mediaPlayer.start();

                System.out.println("------------------------------------------duration4");
                trackProgress.setMax(Var.mediaPlayer.getDuration());
                trackDuration = Var.mediaPlayer.getDuration();
                playerReady = true;
            }
        }
    }

    public void updateAudioController(){
        if(trackReady){
            System.out.println("jason background: test");
            trackReady = false;
            controlLayout.setVisibility(View.VISIBLE);
            controlProgress.setVisibility(View.INVISIBLE);
            if(shuffle.getTag().toString().equals("random")){
                Collections.shuffle(linkList);
            }else{
                sortArrayList();
            }
        }
        /*
        if(!skiped){
            if(trackReady && scrollLayout.getChildCount() == Var.mediaLink.size()){
                trackReady = false;
                controlLayout.setVisibility(View.VISIBLE);
                controlProgress.setVisibility(View.INVISIBLE);
                if(shuffle.getTag().toString().equals("random")){
                    Collections.shuffle(linkList);
                }else{
                    sortArrayList();
                }
            }
        }else{
            if(trackReady && scrollLayout.getChildCount() == linkList.size()){
                trackReady = false;
                controlLayout.setVisibility(View.VISIBLE);
                controlProgress.setVisibility(View.INVISIBLE);
                if(shuffle.getTag().toString().equals("random")){
                    Collections.shuffle(linkList);
                }else{
                    sortArrayList();
                }
            }
        }*/
    }

    public void getTrack(){
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int a = 0; a < siteLink.size(); a++) {
                        System.out.println("jason open: " + Var.openLink);
                        Document doc = (Document) Jsoup
                                .connect(siteLink.get(a))
                                .userAgent(
                                        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                                .timeout(5000).followRedirects(true).execute().parse();
                        Elements titles = doc.select(".entrytitle");

                        // print all titles in main page
                        for (Element e : titles) {

                        }

                        // print all available links on page
                        Elements links = doc.select("script");

                        int counter = 0;
                        for (Element l : links) {
                            progressString = counterProgress + "/" + trackCounter;
                            String html = l.html();
                            if (html.contains("var embed_controller")) {
                                html = html.replace("var embed_controller = new embedController([{\"url\":\"", "");
                                char[] htmlChar = html.toCharArray();
                                String createdLink = "";
                                for (int i = 0; i < htmlChar.length; i++) {
                                    if (htmlChar[i] == '?') {
                                        createdLink = createdLink.replace("\\", "");
                                        break;
                                    } else {
                                        createdLink = createdLink + htmlChar[i];
                                    }
                                }
                                linkList.add(createdLink + ";;;" + siteLink.get(a));

                                MediaPlayer mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(createdLink);
                                mediaPlayer.prepare();

                                Var.mediaLink.add(mediaPlayer);

                                System.out.println("jason playlisttrack");

                                counter++;
                                counterProgress++;
                            }
                            String link = l.attr("abs:href");
                            if (link.contains("listen")) {
                                counter++;

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("jason playlisttrack out");
                trackReady = true;
            }
        });
        t.start();
    }

    public void pauseAudio(){
        if(Var.mediaPlayer != null) {
            Var.mediaPlayer.pause();
        }
    }

    public void playAudio(){
        if(Var.mediaPlayer != null) {
            Var.mediaPlayer.start();
        }else{
            startAudio();
        }
    }

    public void getPlayingCard(String url){
        for(int i = 0; i < scrollLayout.getChildCount(); i++){
            View v = scrollLayout.getChildAt(i);
            CardView card = v.findViewById(R.id.cardView);
            TextView t = v.findViewById(R.id.cardText);
            String link = v.getTag().toString();
            if(link.equals(url)) {
                card.getBackground().setTint(Color.rgb(0, 80, 0));
                currentView = v;
                trackName.setText(t.getText());
            }else{
                card.getBackground().setTint(Color.parseColor("#111111"));
            }
        }
    }

    public void getAllLinks(){
        for(int i = 0; i < scrollLayout.getChildCount(); i++){
            View v = scrollLayout.getChildAt(i);
            String link = v.getTag().toString();
            siteLink.add(link);
        }
        getTrack();
    }

    public void sortArrayList(){
        System.out.println("jason sort vorher: " + linkList.toString());
        for(int i = 0; i < siteLink.size(); i++){
            for(int j = 0; j < linkList.size(); j++){
                String[] split = linkList.get(j).split(";;;");
                String link = split[1];
                if(siteLink.get(i).equals(link)){
                    sortLink.add(linkList.get(j));
                }
            }
        }
        linkList = sortLink;
    }
}