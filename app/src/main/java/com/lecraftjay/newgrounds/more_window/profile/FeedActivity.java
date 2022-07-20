package com.lecraftjay.newgrounds.more_window.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.nav_window.ArtActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.lecraftjay.newgrounds.nav_window.GamesActivity;
import com.lecraftjay.newgrounds.nav_window.MoviesActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


public class FeedActivity extends AppCompatActivity {

    WebView feed;
    ProgressBar space;
    LinearLayout scrollLayout;
    ScrollView originalScroll;
    LinearLayout notLogged;

    int counter = 0;

    boolean noLoggin = false;

    String h = "null";
    String kind = "null";

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    boolean durchlauf = false;
    
    ArrayList<String> audioList = new ArrayList<>();
    ArrayList<String> artList = new ArrayList<>();
    ArrayList<String> gameList = new ArrayList<>();
    ArrayList<String> movieList = new ArrayList<>();

    ArrayList<String> series = new ArrayList<>();

    int audioPos = 0;
    int artPos = 0;
    int gamePos = 0;
    int moviePos = 0;

    Handler handlerForJavascriptInterface = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //-------------------------------------------------------------

        feed = findViewById(R.id.feedWeb);
        scrollLayout = findViewById(R.id.feedScroll);
        originalScroll = findViewById(R.id.feedOriginalScroll);
        space = findViewById(R.id.feedSpace);
        notLogged = findViewById(R.id.feedNoAccount);

        //-------------------------------------------------------------

        feed.getSettings().setJavaScriptEnabled(true);
        feed.getSettings().setDomStorageEnabled(true);

        feed.addJavascriptInterface(new MyJavaScriptInterface(FeedActivity.this), "HtmlViewer");
        feed.setWebViewClient(new WebViewClient() {
                 @Override
                 public void onPageFinished(WebView view, String url) {
                     feed.loadUrl("javascript:window.HtmlViewer.showHTML('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");
                 }
            }
        );
        feed.loadUrl("https://www.newgrounds.com/social");

        
    }

    public class MyJavaScriptInterface {
        private Context ctx;

        MyJavaScriptInterface(Context ctx)
        {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html)
        {
            h = html;
            //code to use html content here
            handlerForJavascriptInterface.post(new Runnable() {
                @Override
                public void run()
                {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true){
                                try {
                                    useDocument();
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    t.start();
                }});
        }
    }

    public void useDocument(){
        Document doc = (Document) Jsoup.parse(h);
        counter++;

        Elements ele = doc.getElementsByClass("noicon");
        for(Element l : ele){
            String contentKind = l.ownText();
            if(contentKind.contains("audio")){
                kind = "audio";
                series.add("audio");

                Elements iconLink = l.getElementsByClass("item-icon");
                Elements creatorText = l.select("strong");
                Elements genre = l.getElementsByClass("detail-description");
                Elements title = l.select("h4");
                Elements url = l.select("a");
                Elements time = l.getElementsByClass("notation-small");

                String sIconLink = "";
                String sCreatorText = creatorText.html();
                String sGenreText = genre.html();
                String sTitleString = title.html();
                String sLink = url.attr("abs:href");
                String sTime = "";

                for(Element e : iconLink){
                    Elements iLink = iconLink.select("img");
                    sIconLink = iLink.attr("abs:src");
                }



                for(Element e : time){
                    Element el = e.child(0);
                    sTime = el.html();
                }

                String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" +
                        sCreatorText + ";;;" + sGenreText + ";;;" + sTime;
                audioList.add(toAdd);

            }else if(contentKind.contains("art")){
                kind = "art";
                series.add("art");

                Elements contentLink = l.getElementsByClass("pod-body");
                Elements title = l.getElementsByClass("portal-feed-large-title");
                Elements image = l.select("img");
                Elements creator = l.getElementsByClass("noicon");

                String sContentLink = "";
                String sTitle = title.html();
                String sImage = image.attr("data-smartload-src");
                String sCreator = "";

                for(Element e : creator){
                    sCreator = e.child(1).html();
                }

                for(Element e : contentLink){
                    sContentLink = e.child(0).child(0).attr("href");
                }

                String toAdd = sContentLink + ";;;" + sTitle + ";;;" + sImage + ";;;" + sCreator;
                artList.add(toAdd);

            }else if(contentKind.contains("game")){
                kind = "game";
                series.add("game");

                Elements contentLink = l.getElementsByClass("item-portalsubmission");
                Elements image = l.getElementsByClass("item-icon");
                Elements creator = l.getElementsByClass("noicon");

                String sContentLink = contentLink.attr("href");
                String sTitle = "";
                String sImage = "";
                String sCreator = "";

                for(Element e : image){
                    Elements img = e.select("img");
                    for(Element el : img){
                        sTitle = el.attr("alt");
                        sImage = el.attr("src");
                    }
                }

                for(Element e : creator){
                    sCreator = e.child(1).html();
                }

                String toAdd = sContentLink + ";;;" + sTitle + ";;;" + sImage + ";;;" + sCreator;
                gameList.add(toAdd);

            }else if(contentKind.contains("movie")){
                kind = "movie";
                series.add("movie");

                Elements contentLink = l.getElementsByClass("item-portalsubmission");
                Elements image = l.getElementsByClass("item-icon");
                Elements creator = l.getElementsByClass("noicon");

                String sContentLink = contentLink.attr("href");
                String sTitle = "";
                String sImage = "";
                String sCreator = "";

                for(Element e : image){
                    Elements img = e.select("img");
                    for(Element el : img){
                        sTitle = el.attr("alt");
                        sImage = el.attr("src");
                    }
                }

                for(Element e : creator){
                    sCreator = e.child(1).html();
                }

                String toAdd = sContentLink + ";;;" + sTitle + ";;;" + sImage + ";;;" + sCreator;
                movieList.add(toAdd);

            }
        }

        /*for(Element tester : ele){
            Element c = tester.child(0);
            if(c.className().equals("itemlist alternating")){
                for(Element l : ele){

                    Elements iconLink = l.getElementsByClass("item-icon");
                    Elements creatorText = l.select("strong");
                    Elements genre = l.getElementsByClass("detail-description");
                    Elements title = l.select("h4");
                    Elements url = l.select("a");
                    Elements time = l.getElementsByClass("notation-small");

                    String sIconLink = "";
                    String sCreatorText = creatorText.html();
                    String sGenreText = genre.html();
                    String sTitleString = title.html();
                    String sLink = url.attr("abs:href");
                    String sTime = "";

                    for(Element e : iconLink){
                        Elements iLink = iconLink.select("img");
                        sIconLink = iLink.attr("abs:src");
                    }



                    for(Element e : time){
                        Element el = e.child(0);
                        sTime = el.html();
                    }


                    if(sLink.contains("listen")) {
                        boolean con = false;
                        for(int i = 0; i < audioList.size(); i++){
                            String[] splitter = audioList.get(i).split(";;;");
                            String li = splitter[0];
                            if(li.equals(sLink)){
                                con = true;
                            }
                        }
                        if(con){

                        }else {
                            String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" +
                                    sCreatorText + ";;;" + sGenreText + ";;;" + sTime;
                            audioList.add(toAdd);
                        }
                    }

                }
            }else{
                Elements noti = c.getElementsByClass("notification-login-side");
                if(noti.html().contains("Login")){
                    noLoggin = true;
                }
            }
        }*/

        
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        //IronSource.onResume(this);

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                try {
                    update();
                }catch (Exception e){
                    e.printStackTrace();
                }
                checkLogin();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        IronSource.onPause(this);
        super.onPause();
    }

    public void checkLogin(){
        if(noLoggin){
            noLoggin = false;
            notLogged.setVisibility(View.VISIBLE);
            space.setVisibility(View.INVISIBLE);
        }
    }

    public void update() throws ArrayIndexOutOfBoundsException{
        if(scrollLayout.getChildCount() < series.size() || Var.updateNow) {

            for(String str : series){
                if(str.equals("audio")){

                    String audio = audioList.get(audioPos);

                    String[] split = audio.split(";;;");

                    View view = LayoutInflater.from(FeedActivity.this).inflate(R.layout.track_layout, null);
                    TextView cardText = view.findViewById(R.id.cardText);
                    CircleImageView icon = view.findViewById(R.id.iconCard);
                    TextView creator = view.findViewById(R.id.cardCreator);
                    TextView description = view.findViewById(R.id.cardDescription);
                    TextView genre = view.findViewById(R.id.cardGenre);

                    try{
                        view.setTag(split[0]);
                        cardText.setText(Html.fromHtml(trim(split[1], 28)));
                        cardText.setTag(split[1]);
                        Picasso.get().load(split[2]).into(icon);
                        icon.setTag(split[2]);
                        creator.setText(split[3]);
                        description.setText(trim(split[4], 40));
                        genre.setText(split[5]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(split[0])){
                        cardText.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));
                    }

                    scrollLayout.addView(view);

                    audioPos++;

                }else if(str.equals("art")){

                    View view = LayoutInflater.from(FeedActivity.this).inflate(R.layout.art_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.artTitle);
                    ImageView image = view.findViewById(R.id.artImage);
                    TextView user = view.findViewById(R.id.artCreator);

                    String title = artList.get(artPos);
                    String[] splitter = title.split(";;;");



                    try {
                        view.setTag(title);
                        cardTitle.setText(trim(splitter[1], 12));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Art", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));
                    }

                    scrollLayout.addView(view);

                    artPos++;

                }else if(str.equals("movie")){

                    View view = LayoutInflater.from(FeedActivity.this).inflate(R.layout.movie_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.movieTitle);
                    ImageView image = view.findViewById(R.id.movieImage);
                    TextView user = view.findViewById(R.id.movieCreator);

                    String title = movieList.get(moviePos);
                    String[] splitter = title.split(";;;");

                    try {
                        view.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));
                    }

                    scrollLayout.addView(view);

                    moviePos++;

                }else if(str.equals("game")){

                    View view = LayoutInflater.from(FeedActivity.this).inflate(R.layout.games_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.gamesTitle);
                    ImageView image = view.findViewById(R.id.gamesImage);
                    TextView user = view.findViewById(R.id.gamesCreator);

                    String title = gameList.get(gamePos);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Games", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));
                    }

                    scrollLayout.addView(view);

                    gamePos++;

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

}