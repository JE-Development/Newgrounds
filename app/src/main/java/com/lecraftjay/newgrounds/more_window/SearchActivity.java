package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.art.ArtContentActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.more_window.movies.StartMovieActivity;
import com.lecraftjay.newgrounds.nav_window.MoviesActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    EditText searchText;
    Button searchButton;
    ScrollView scroll;
    LinearLayout layout;
    Space space;
    Button kindOfContent;

    String toSearch = "";
    String globalLink = "https://www.newgrounds.com/search/conduct/;;;content;;;?terms=;;;search;;;&match=tdtu&suitabilities=e%2Ct%2Cm";

    ArrayList<String> audioContent = new ArrayList<>();
    ArrayList<String> artContent = new ArrayList<>();
    ArrayList<String> gamesContent = new ArrayList<>();
    ArrayList<String> movieContent = new ArrayList<>();
    ArrayList<String> usersContent = new ArrayList<>();

    int pos = 1;
    boolean einmal = false;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;
    String currentWindow = Var.currentWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //---------------------------------------------------------

        searchText = findViewById(R.id.searchText);
        searchButton = findViewById(R.id.searchButton);
        scroll = findViewById(R.id.searchScroll);
        layout = findViewById(R.id.searchLayout);
        space = findViewById(R.id.searchSpace);
        kindOfContent = findViewById(R.id.searchContent);

        //---------------------------------------------------------

        kindOfContent.setText("search for: " + currentWindow);

        scroll.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scroll.getChildAt(0).getBottom() <= (scroll.getHeight() + scroll.getScrollY())) {
                            if(einmal == false) {
                                pos++;
                                String editedLink = globalLink.replace(";;;content;;;", currentWindow).replace(";;;search;;;", toSearch) + "&page=" + pos;
                                if(currentWindow.equals("users")){
                                    editedLink = "https://www.newgrounds.com/search/conduct/users?terms=" + searchText.getText().toString() + "&match=tdtu&page=" + pos;
                                }
                                getContent(editedLink);
                                einmal = true;
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        kindOfContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContent();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
    }

    public void getContent(String urlLink){
        System.out.println("jason url: " + urlLink);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(urlLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();

                    if(currentWindow.equals("audio")) {

                        Elements ele = doc.getElementsByClass("audio-wrapper");

                        for (Element l : ele) {

                            Elements iconLink = l.getElementsByClass("item-icon");
                            Elements creatorText = l.select("strong");
                            Elements desc = l.getElementsByClass("detail-description");
                            Elements genre = l.select("dl");
                            Elements url = l.select("a");

                            String sIconLink = "";
                            String sDescriptionText = desc.html();
                            String sGenreText = "";
                            String sTitleString = "";
                            String sLink = url.attr("abs:href");
                            String sCreator = creatorText.html();

                            for (Element e : iconLink) {
                                Elements iLink = iconLink.select("img");
                                sIconLink = iLink.attr("abs:src");
                                sTitleString = iLink.attr("alt");
                            }

                            for (Element e : genre) {
                                sGenreText = e.child(0).html() + " - " + e.child(1).html();
                            }


                            if (sLink.contains("listen")) {
                                if (audioContent.contains(sLink)) {

                                } else {
                                    String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" + sDescriptionText + ";;;" + sGenreText + ";;;" + sCreator;
                                    audioContent.add(toAdd);
                                }
                            }
                        }

                    }else if(currentWindow.equals("art")){
                        Elements ele = doc.getElementsByClass("span-1 align-center");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements imgLink = l.getElementsByClass("item-icon");
                            Elements creator = l.select("span");

                            String s = "---";
                            String s1 = "---";
                            for(Element e : imgLink){
                                s = e.child(0).attr("abs:src");
                                s1 = e.child(0).attr("alt");
                            }

                            String sLink = link.attr("abs:href");
                            String sTitle = s1;
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
                    }else if(currentWindow.equals("games")){
                        Elements ele = doc.getElementsByClass("item-portalsubmission");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements imgLink = l.select("img");
                            Elements creator = l.select("strong");

                            String s = "---";
                            String s1 = "---";
                            for(Element e : imgLink){
                                s1 = e.attr("src");
                                s = e.attr("alt");
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
                    }else if(currentWindow.equals("movies")){
                        Elements ele = doc.getElementsByClass("item-portalsubmission");
                        for (Element l : ele) {

                            Elements link = l.select("a");
                            Elements imgLink = l.select("img");
                            Elements creator = l.select("strong");

                            String s = "---";
                            String s1 = "---";
                            for(Element e : imgLink){
                                s1 = e.attr("src");
                                s = e.attr("alt");
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
                    }else if(currentWindow.equals("users")){
                        Elements ele = doc.getElementsByClass("item-user");
                        for (Element l : ele) {

                            Elements link = l.getElementsByClass("item-icon");
                            Elements name = l.select("h4");
                            Elements icon = l.select("image");

                            String s = "---";
                            for(Element e : name){
                                s = e.child(0).html();
                            }

                            String sLink = link.attr("href");
                            String sTitle = s;
                            String sImgLink = icon.attr("href");

                            if(sLink.contains("")) {
                                if(usersContent.contains(sLink)){

                                }else {
                                    String toAdd = sLink + ";;;" + sTitle + ";;;" + sImgLink;
                                    usersContent.add(toAdd);
                                    System.out.println("jason user: " + usersContent);
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

    public void update(){
        if (currentWindow.equals("audio")) {
            if (layout.getChildCount()-1 < audioContent.size() || Var.updateNow) {

                Var.updateNow = false;
                layout.removeAllViews();
                for (int i = 0; i < audioContent.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(Var.audioLinksList.get(i));
                layout.addView(text);*/

                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.track_layout, null);
                    //CardView card = view.findViewById(R.id.cardView);
                    TextView cardText = view.findViewById(R.id.cardText);
                    TextView creator = view.findViewById(R.id.cardCreator);
                    CircleImageView icon = view.findViewById(R.id.iconCard);
                    TextView description = view.findViewById(R.id.cardDescription);
                    TextView genre = view.findViewById(R.id.cardGenre);

                    String title = audioContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        view.setTag(splitter[0]);
                        cardText.setText(Html.fromHtml(trim(splitter[1], 28)));
                        cardText.setTag(splitter[1]);
                        creator.setText(Html.fromHtml(splitter[5]));
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
                        cardText.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    layout.addView(view);

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

                            title.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));

                            startActivity(new Intent(SearchActivity.this, TrackActivity.class));
                        }
                    });
                }
                layout.addView(space);
            }
        } else if(currentWindow.equals("art")){
            if(layout.getChildCount()-1 < artContent.size() || Var.updateNow) {

                Var.updateNow = false;
                layout.removeAllViews();
                for (int i = 0; i < artContent.size(); i++) {

                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.movie_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.movieTitle);
                    ImageView image = view.findViewById(R.id.movieImage);
                    TextView user = view.findViewById(R.id.movieCreator);

                    String title = artContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3].replace("By ", ""));
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    layout.addView(view);

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

                            title.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));

                            startActivity(new Intent(SearchActivity.this, ArtContentActivity.class));

                        }
                    });
                }
                layout.addView(space);
            }
        }else if(currentWindow.equals("games")){
            if(layout.getChildCount()-1 < gamesContent.size() || Var.updateNow) {

                Var.updateNow = false;
                layout.removeAllViews();
                for (int i = 0; i < gamesContent.size(); i++) {

                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.games_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.gamesTitle);
                    ImageView image = view.findViewById(R.id.gamesImage);
                    TextView user = view.findViewById(R.id.gamesCreator);

                    String title = gamesContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        user.setText(splitter[3].replace("Game", ""));
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Games", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    layout.addView(view);

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

                            title.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));

                            Uri uri = Uri.parse(Var.gamesOpenLink); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }
                layout.addView(space);
            }
        }else if(currentWindow.equals("movies")){
            if(layout.getChildCount()-1 < movieContent.size() || Var.updateNow) {

                Var.updateNow = false;
                layout.removeAllViews();
                for (int i = 0; i < movieContent.size(); i++) {

                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.movie_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.movieTitle);
                    ImageView image = view.findViewById(R.id.movieImage);
                    TextView user = view.findViewById(R.id.movieCreator);

                    String title = movieContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                        String u = splitter[3];
                        user.setText(u.replace("Movie", ""));
                        System.out.println("jason search: " + u);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    layout.addView(view);

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

                            title.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                            System.out.println("jason movie url: " + Var.movieOpenLink);

                            /*Uri uri = Uri.parse(Var.movieOpenLink); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);*/
                            startActivity(new Intent(SearchActivity.this, StartMovieActivity.class));
                        }
                    });
                }
                layout.addView(space);
            }
        }else if(currentWindow.equals("users")){
            if(layout.getChildCount()-1-1 < usersContent.size() || Var.updateNow) {

                Var.updateNow = false;
                layout.removeAllViews();
                for (int i = 0; i < usersContent.size(); i++) {

                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.user_card_layout, null);
                    TextView cardTitle = view.findViewById(R.id.userCreatorName);
                    ImageView image = view.findViewById(R.id.userCreatorIcon);

                    String title = usersContent.get(i);
                    String[] splitter = title.split(";;;");

                    try {
                        cardTitle.setTag(splitter[0]);
                        cardTitle.setText(trim(splitter[1], 25));
                        Picasso.get().load(splitter[2]).into(image);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Movie", 0);
                    String getter = sp.getString("alreadySeen", "");

                    if(getter.contains(splitter[0])){
                        cardTitle.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.audioSeen));
                    }


                    //cardText.setText(title);
                    layout.addView(view);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView title = view.findViewById(R.id.userCreatorName);

                            Var.userLink = (String) title.getTag();
                            startActivity(new Intent(SearchActivity.this, UserActivity.class));
                        }
                    });
                }
                layout.addView(space);
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

    public void pickContent(){
        String[] con = {"Movies", "Games", "Art", "Audio", "Users"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search For:");
        builder.setItems(con, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String z = con[which];
                currentWindow = z.toLowerCase();
                kindOfContent.setText("search for: " + z);
                startSearch();
            }
        });
        builder.show();
    }

    public void startSearch(){
        audioContent.clear();
        artContent.clear();
        gamesContent.clear();
        movieContent.clear();
        usersContent.clear();

        pos = 1;
        layout.removeAllViews();
        audioContent.clear();
        layout.addView(space);
        String editedLink = globalLink.replace(";;;content;;;",currentWindow).replace(";;;search;;;", searchText.getText().toString());
        if(currentWindow.equals("users")){
            editedLink = "https://www.newgrounds.com/search/conduct/users?terms=" + searchText.getText().toString() + "&match=tdtu";
        }
        getContent(editedLink);
        toSearch = searchText.getText().toString();
    }
}