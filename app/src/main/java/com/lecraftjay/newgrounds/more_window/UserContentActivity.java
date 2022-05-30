package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.art.ArtContentActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.nav_window.ArtActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.lecraftjay.newgrounds.nav_window.GamesActivity;
import com.lecraftjay.newgrounds.nav_window.MovieActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserContentActivity extends AppCompatActivity {

    TextView title;
    LinearLayout scrollLayout;
    ScrollView originalScroll;
    Space space;

    int pos = 1;
    String newLink = "";

    boolean einmal = false;

    ArrayList<String> userContentLinksList = new ArrayList<>();
    ArrayList<String> artContent = new ArrayList<>();
    ArrayList<String> gamesContent = new ArrayList<>();
    ArrayList<String> movieContent = new ArrayList<>();

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_content);

        //----------------------------------------------------------------

        title = findViewById(R.id.userContentTitle);
        scrollLayout = findViewById(R.id.userContentScrollLayout);
        originalScroll = findViewById(R.id.userContentOriginalScroll);
        space = findViewById(R.id.userContentSpace);

        //----------------------------------------------------------------

        title.setText(Var.userContentTitle);

        originalScroll.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scrollLayout.getChildAt(0).getBottom() <= (scrollLayout.getHeight() + scrollLayout.getScrollY())) {
                            if(einmal == false) {
                                if(Var.userContentTitle.equals("AUDIO")) {
                                    getContent(Var.userContentLink, true);
                                }
                                einmal = true;
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        getContent(Var.userContentLink, false);
    }



    public void getContent(String urlLink, boolean useAdvanced){

        if(useAdvanced){
            pos++;
            newLink = Var.userLink + urlLink + "?page=" + pos;
        }else{
            newLink = Var.userLink + urlLink;
        }

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(newLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();

                    if(Var.userContentTitle.equals("AUDIO")) {

                        Elements ele = doc.getElementsByClass("audio-wrapper");

                        for (Element l : ele) {

                            Elements iconLink = l.getElementsByClass("item-icon");
                            Elements desc = l.getElementsByClass("detail-description");
                            Elements genre = l.select("dl");
                            Elements title = l.select("h4");
                            Elements url = l.select("a");

                            String sIconLink = "";
                            String sDescriptionText = desc.html();
                            String sGenreText = "";
                            String sTitleString = title.html();
                            String sLink = url.attr("abs:href");

                            for (Element e : iconLink) {
                                Elements iLink = iconLink.select("img");
                                sIconLink = iLink.attr("abs:src");
                            }

                            for (Element e : genre) {
                                sGenreText = e.child(0).html() + " - " + e.child(1).html();
                            }


                            if (sLink.contains("listen")) {
                                if (userContentLinksList.contains(sLink)) {

                                } else {
                                    String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" + sDescriptionText + ";;;" + sGenreText;
                                    userContentLinksList.add(toAdd);
                                }
                            }
                        }

                    }else if(Var.userContentTitle.equals("ART")){
                        Elements ele = doc.getElementsByClass("span-1 align-center");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements title = l.select("h4");
                            Elements imgLink = l.getElementsByClass("item-icon");
                            Elements creator = l.select("span");

                            String s = "---";
                            for(Element e : imgLink){
                                s = e.child(0).attr("abs:src");
                            }

                            String sLink = link.attr("abs:href");
                            String sTitle = title.html();
                            String sImgLink = s;
                            String sCreator = creator.html();

                            if(sLink.contains("art")) {
                                if(artContent.contains(sLink)){

                                }else {
                                    String toAdd = sLink + ";;;" + sTitle + ";;;" + sImgLink + ";;;" + sCreator;
                                    artContent.add(toAdd);
                                }
                            }

                        }
                    }else if(Var.userContentTitle.equals("GAMES")){
                        Elements ele = doc.getElementsByClass("portalsubmission-cell");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements title = l.select("img");
                            Elements imgLink = l.getElementsByClass("card-img");
                            Elements creator = l.select("span");

                            String s = "";
                            for(Element e : title){
                                s = e.attr("alt");
                            }

                            String s1 = "---";
                            for(Element e : imgLink){
                                s1 = e.attr("src");
                            }

                            String sLink = link.attr("abs:href");
                            String sTitle = s;
                            String sImgLink = s1;
                            String sCreator = creator.html();

                            if(sLink.contains("")) {
                                if(gamesContent.contains(sLink)){

                                }else {
                                    String toAdd = sLink + ";;;" + sTitle + ";;;" + sImgLink + ";;;" + sCreator;
                                    gamesContent.add(toAdd);
                                }
                            }

                        }
                    }else if(Var.userContentTitle.equals("MOVIES")){
                        Elements ele = doc.getElementsByClass("portalsubmission-cell");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements title = l.select("img");
                            Elements imgLink = l.getElementsByClass("card-img");
                            Elements creator = l.select("span");

                            String s = "";
                            for(Element e : title){
                                s = e.attr("alt");
                            }

                            String s1 = "---";
                            for(Element e : imgLink){
                                s1 = e.attr("src");
                            }

                            String sLink = link.attr("abs:href");
                            String sTitle = s;
                            String sImgLink = s1;
                            String sCreator = creator.html();

                            if(sLink.contains("")) {
                                if(movieContent.contains(sLink)){

                                }else {
                                    String toAdd = sLink + ";;;" + sTitle + ";;;" + sImgLink + ";;;" + sCreator;
                                    movieContent.add(toAdd);
                                }
                            }

                        }
                    }

                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    Var.updateNow = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
    }



    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                update();

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




    public void update() {
        if (Var.userContentTitle.equals("AUDIO")) {
            if (scrollLayout.getChildCount() < userContentLinksList.size() || Var.updateNow) {

                Var.updateNow = false;
                scrollLayout.removeAllViews();
                for (int i = 0; i < userContentLinksList.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(Var.audioLinksList.get(i));
                scrollLayout.addView(text);*/

                    View view = LayoutInflater.from(UserContentActivity.this).inflate(R.layout.track_layout, null);
                    //CardView card = view.findViewById(R.id.cardView);
                    TextView cardText = view.findViewById(R.id.cardText);
                    CircleImageView icon = view.findViewById(R.id.iconCard);
                    TextView description = view.findViewById(R.id.cardDescription);
                    TextView genre = view.findViewById(R.id.cardGenre);

                    String title = userContentLinksList.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        view.setTag(splitter[0]);
                        cardText.setText(Html.fromHtml(trim(splitter[1], 28)));
                        cardText.setTag(splitter[1]);
                        Picasso.get().load(splitter[2]).into(icon);
                        description.setText(trim(splitter[3], 40));
                        genre.setText(splitter[4]);
                        icon.setTag(splitter[2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if (getter.contains(splitter[0])) {
                        cardText.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    scrollLayout.addView(view);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView title = view.findViewById(R.id.cardText);
                            TextView genre = view.findViewById(R.id.cardGenre);
                            TextView desc = view.findViewById(R.id.cardDescription);
                            TextView creator = view.findViewById(R.id.cardCreator);
                            ImageView icon = view.findViewById(R.id.iconCard);
                            Var.currentTitle = (String) title.getTag();
                            Var.openLink = (String) view.getTag();
                            Var.trackGenre = genre.getText().toString();
                            Var.trackDescription = desc.getText().toString();
                            Var.trackCreator = creator.getText().toString();
                            Var.trackIcon = icon.getTag().toString();

                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                            String getter = sp.getString("alreadySeen", "");

                            SharedPreferences liste = getApplicationContext().getSharedPreferences("Audio", 0);
                            SharedPreferences.Editor editor = liste.edit();
                            editor.putString("alreadySeen", getter + ";;;" + Var.openLink);
                            editor.apply();

                            title.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));

                            startActivity(new Intent(UserContentActivity.this, TrackActivity.class));
                        }
                    });
                }
                scrollLayout.addView(space);
            }
        } else if(Var.userContentTitle.equals("ART")){
            if(scrollLayout.getChildCount() < artContent.size() || Var.updateNow) {

                Var.updateNow = false;
                scrollLayout.removeAllViews();
                for (int i = 0; i < artContent.size(); i++) {

                    View view = LayoutInflater.from(UserContentActivity.this).inflate(R.layout.movie_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.movieTitle);
                    ImageView image = view.findViewById(R.id.movieImage);
                    TextView user = view.findViewById(R.id.movieCreator);

                    String title = artContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    scrollLayout.addView(view);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView title = (TextView) v.findViewById(R.id.movieTitle);
                            Var.artInfo = (String) title.getTag();
                            String[] split = Var.artInfo.split(";;;");
                            Var.artOpenLink = split[0];

                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Art", 0);
                            String getter = sp.getString("alreadySeen", "");

                            SharedPreferences liste = getApplicationContext().getSharedPreferences("Art", 0);
                            SharedPreferences.Editor editor = liste.edit();
                            editor.putString("alreadySeen", getter + ";;;" + Var.artOpenLink);
                            editor.apply();

                            title.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));

                            startActivity(new Intent(UserContentActivity.this, ArtContentActivity.class));

                        }
                    });
                }
                scrollLayout.addView(space);
            }
        }else if(Var.userContentTitle.equals("GAMES")){
            if(scrollLayout.getChildCount() < gamesContent.size() || Var.updateNow) {

                Var.updateNow = false;
                scrollLayout.removeAllViews();
                for (int i = 0; i < gamesContent.size(); i++) {

                    View view = LayoutInflater.from(UserContentActivity.this).inflate(R.layout.games_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.gamesTitle);
                    ImageView image = view.findViewById(R.id.gamesImage);
                    TextView user = view.findViewById(R.id.gamesCreator);

                    String title = gamesContent.get(i);
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
                        cardTitle.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    scrollLayout.addView(view);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView title = view.findViewById(R.id.gamesTitle);
                            Var.gamesTitle = (String) title.getTag();
                            Var.gamesOpenLink = (String) title.getTag();

                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Games", 0);
                            String getter = sp.getString("alreadySeen", "");

                            SharedPreferences liste = getApplicationContext().getSharedPreferences("Games", 0);
                            SharedPreferences.Editor editor = liste.edit();
                            editor.putString("alreadySeen", getter + ";;;" + Var.gamesOpenLink);
                            editor.apply();

                            title.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));

                            Uri uri = Uri.parse(Var.gamesOpenLink); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }
                scrollLayout.addView(space);
            }
        }else if(Var.userContentTitle.equals("MOVIES")){
            if(scrollLayout.getChildCount() < movieContent.size() || Var.updateNow) {

                Var.updateNow = false;
                scrollLayout.removeAllViews();
                for (int i = 0; i < movieContent.size(); i++) {

                    View view = LayoutInflater.from(UserContentActivity.this).inflate(R.layout.movie_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.movieTitle);
                    ImageView image = view.findViewById(R.id.movieImage);
                    TextView user = view.findViewById(R.id.movieCreator);

                    String title = movieContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    scrollLayout.addView(view);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView title = view.findViewById(R.id.movieTitle);
                            Var.movieTitle = (String) title.getTag();
                            Var.movieOpenLink = (String) title.getTag();

                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                            String getter = sp.getString("alreadySeen", "");

                            SharedPreferences liste = getApplicationContext().getSharedPreferences("Movie", 0);
                            SharedPreferences.Editor editor = liste.edit();
                            editor.putString("alreadySeen", getter + ";;;" + Var.movieOpenLink);
                            editor.apply();

                            title.setTextColor(ContextCompat.getColor(UserContentActivity.this, R.color.audioSeen));
                            System.out.println("jason movie url: " + Var.movieOpenLink);

                            Uri uri = Uri.parse(Var.movieOpenLink); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }
                scrollLayout.addView(space);
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