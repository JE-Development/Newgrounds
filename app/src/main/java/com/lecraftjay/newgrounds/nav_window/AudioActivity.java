package com.lecraftjay.newgrounds.nav_window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.more_window.FeedbackActivity;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.more_window.audio.SearchAudioActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.classes.Var;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioActivity extends AppCompatActivity {

    LinearLayout scrollLayout;
    int pos = 0;

    ArrayList<String> category = new ArrayList<>();

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    String categoryLink = "---";

    boolean error = false;
    Space space;

    boolean einmal = false;

    ScrollView originalScroll;
    Button feedback;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        //-----------------------------------------------------------------

        scrollLayout = findViewById(R.id.scroll);
        originalScroll = findViewById(R.id.originalScroll);
        space = findViewById(R.id.space);
        feedback = findViewById(R.id.feedback);

        //-----------------------------------------------------------------

        ActionBar actionBar = getSupportActionBar();
        String titleBarLoading = "<font color='#ffff00'>" + actionBar.getTitle() + "</font>";
        actionBar.setTitle(Html.fromHtml(titleBarLoading));

        setNavigation();

        TextView test = new TextView(this);
        test.setText("test");
        scrollLayout.addView(test);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AudioActivity.this, FeedbackActivity.class));
            }
        });

        originalScroll.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (originalScroll.getChildAt(0).getBottom() <= (originalScroll.getHeight() + originalScroll.getScrollY())) {
                            if(einmal == false) {
                                if(categoryLink.equals("---")) {
                                    pos += 30;
                                    getContent("https://www.newgrounds.com/audio/featured?offset=" + pos + "&amp;inner=1");
                                    einmal = true;
                                }else{
                                    pos += 30;
                                    getContent(categoryLink + "&offset=30&inner=1");
                                    einmal = true;
                                }
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        getContent("https://www.newgrounds.com/audio");


    }

    public void getContent(String urlLink){
        Thread t = new Thread(new Runnable() {
            public void run() {
                error = false;
                try {
                    Document doc = (Document) Jsoup
                            .connect(urlLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


                    Elements ele = doc.getElementsByClass("audio-wrapper");
                    Elements select = doc.getElementsByClass("select-wrapper");

                    int c = 0;
                    for(Element l : select){
                        Element s = l.child(0);
                        System.out.println("jason debug 1");
                        if(c == 2){
                            System.out.println("jason debug 2");
                            Elements cat = l.select("option");
                            for(Element e : cat){
                                System.out.println("jason debug 3");
                                String v = e.attr("value");
                                String t = e.html();
                                String fin = v + ";" + t;
                                category.add(fin.replace("&amp;", "&"));
                            }
                        }
                        c++;
                    }

                    for (Element l : ele) {

                        Elements iconLink = l.getElementsByClass("item-icon");
                        Elements creatorText = l.select("strong");
                        Elements desc = l.getElementsByClass("detail-description");
                        Elements genre = l.select("dl");
                        Elements title = l.select("h4");
                        Elements url = l.select("a");

                        String sIconLink = "";
                        String sCreatorText = creatorText.html();
                        String sDescriptionText = desc.html();
                        String sGenreText = "";
                        String sTitleString = title.html();
                        String sLink = url.attr("abs:href");

                        for(Element e : iconLink){
                            Elements iLink = iconLink.select("img");
                            sIconLink = iLink.attr("abs:src");
                        }

                        for(Element e : genre){
                            sGenreText = e.child(0).html() + " - " + e.child(1).html();
                        }


                        if(sLink.contains("listen")) {
                            if(Var.audioLinksList.contains(sLink)){

                            }else {
                                String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" +
                                        sCreatorText + ";;;" + sDescriptionText + ";;;" + sGenreText;
                                Var.audioLinksList.add(toAdd);
                            }
                        }
                    }

                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    error = true;
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
        if(scrollLayout.getChildCount() < Var.audioLinksList.size() || Var.updateNow) {
            ActionBar actionBar = getSupportActionBar();
            String titleBarError = "<font color='#ff0000'>" + actionBar.getTitle() + "</font>";
            String titleBarSuccess = "<font color='#00ff00'>" + actionBar.getTitle() + "</font>";

            if(error){
                actionBar.setTitle(Html.fromHtml(titleBarError));
            }else{
                actionBar.setTitle(Html.fromHtml(titleBarSuccess));
            }

            Var.updateNow = false;
            scrollLayout.removeAllViews();
            for (int i = 0; i < Var.audioLinksList.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(Var.audioLinksList.get(i));
                scrollLayout.addView(text);*/

                View view = LayoutInflater.from(AudioActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                CircleImageView icon = view.findViewById(R.id.iconCard);
                TextView creator = view.findViewById(R.id.cardCreator);
                TextView description = view.findViewById(R.id.cardDescription);
                TextView genre = view.findViewById(R.id.cardGenre);

                String title = Var.audioLinksList.get(i);
                String[] splitter = title.split(";;;");

                try {
                    view.setTag(splitter[0]);
                    cardText.setText(Html.fromHtml(trim(splitter[1], 28)));
                    cardText.setTag(splitter[1]);
                    Picasso.get().load(splitter[2]).into(icon);
                    creator.setText(splitter[3]);
                    description.setText(trim(splitter[4], 40));
                    genre.setText(splitter[5]);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }

                SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                String getter = sp.getString("alreadySeen", "");

                if(getter.contains(splitter[0])){
                    cardText.setTextColor(ContextCompat.getColor(AudioActivity.this, R.color.audioSeen));
                }


                //cardText.setText(title);
                scrollLayout.addView(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView title = view.findViewById(R.id.cardText);
                        Var.currentTitle = (String) title.getTag();
                        Var.openLink = (String) view.getTag();

                        SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                        String getter = sp.getString("alreadySeen", "");

                        SharedPreferences liste = getApplicationContext().getSharedPreferences("Audio", 0);
                        SharedPreferences.Editor editor = liste.edit();
                        editor.putString("alreadySeen", getter + ";;;" + Var.openLink);
                        editor.apply();

                        title.setTextColor(ContextCompat.getColor(AudioActivity.this, R.color.audioSeen));

                        startActivity(new Intent(AudioActivity.this, TrackActivity.class));
                    }
                });
            }
            scrollLayout.addView(space);
        }
    }

    public void setNavigation(){
        LinearLayout games = findViewById(R.id.games);
        LinearLayout movie = findViewById(R.id.movie);
        LinearLayout audio = findViewById(R.id.audio);
        LinearLayout art = findViewById(R.id.art);
        LinearLayout community = findViewById(R.id.games);

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AudioActivity.this, GamesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AudioActivity.this, MovieActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AudioActivity.this, ArtActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AudioActivity.this, CommunityActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }

    public void pickCategory(){
        String[] cat = new String[category.size()];
        for(int i = 0; i < cat.length; i++){
            String[] put = category.get(i).split(";");
            cat[i] = put[1];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Genre");
        builder.setItems(cat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String z = cat[which];
                pos = 0;
                category not working
                int id = getGenreId(z);
                categoryLink = "https://www.newgrounds.com/audio/featured?type=1&interval=all&sort=date&genre=50&suitabilities=etma";
                getContent(categoryLink);
            }
        });
        builder.show();
    }

    public int getGenreId(String flag){
        for(int i = 0; i < category.size(); i++){
            String[] split = category.get(i).split(";");
            int id = Integer.parseInt(split[0]);
            String name = split[1];
            if(name.equals(flag)){
                return id;
            }
        }
        return 0;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        getMenuInflater().inflate(R.menu.filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.searchMenu:
                startActivity(new Intent(AudioActivity.this, SearchAudioActivity.class));
                break;
            case R.id.filterMenu:
                pickCategory();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}