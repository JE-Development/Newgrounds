package com.lecraftjay.newgrounds.more_window.audio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAudioActivity extends AppCompatActivity {

    EditText searchText;
    Button searchButton;
    ScrollView scroll;
    LinearLayout layout;
    Space space;

    String toSearch = "";

    ArrayList<String> audioSearchList = new ArrayList<>();

    int pos = 1;
    boolean einmal = false;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_audio);

        //---------------------------------------------------------

        searchText = findViewById(R.id.searchText);
        searchButton = findViewById(R.id.searchButton);
        scroll = findViewById(R.id.searchScroll);
        layout = findViewById(R.id.searchLayout);
        space = findViewById(R.id.searchSpace);

        //---------------------------------------------------------

        scroll.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scroll.getChildAt(0).getBottom() <= (scroll.getHeight() + scroll.getScrollY())) {
                            if(einmal == false) {
                                pos ++;
                                getContent("https://www.newgrounds.com/search/conduct/audio?suitabilities=etma&terms=" + toSearch + "&page=" + pos);
                                einmal = true;
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeAllViews();
                audioSearchList.clear();
                layout.addView(space);
                getContent("https://www.newgrounds.com/search/conduct/audio?suitabilities=etma&c=3&terms="
                        + searchText.getText().toString());
                toSearch = searchText.getText().toString();
            }
        });
    }

    public void getContent(String urlLink){
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(urlLink)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


                    Elements ele = doc.getElementsByClass("audio-wrapper");
                    for (Element l : ele) {

                        Elements iconLink = l.getElementsByClass("item-icon");
                        Elements creatorText = l.select("strong");
                        Elements desc = l.getElementsByClass("detail-description");
                        Elements genre = l.select("dl");
                        Elements url = l.select("a");

                        String sIconLink = "";
                        String sCreatorText = creatorText.html();
                        String sDescriptionText = desc.html();
                        String sGenreText = "";
                        String sTitleString = "";
                        String sLink = url.attr("abs:href");

                        for(Element e : iconLink){
                            Elements iLink = iconLink.select("img");
                            sIconLink = iLink.attr("abs:src");
                            sTitleString = iLink.attr("alt");
                        }

                        for(Element e : genre){
                            sGenreText = e.child(0).html() + " - " + e.child(1).html();
                        }


                        if(sLink.contains("listen")) {
                            if(audioSearchList.contains(sLink)){

                            }else {
                                String toAdd = sLink + ";;;" + sTitleString + ";;;" + sIconLink + ";;;" +
                                        sCreatorText + ";;;" + sDescriptionText + ";;;" + sGenreText;
                                audioSearchList.add(toAdd);
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
        if(layout.getChildCount() < audioSearchList.size() || Var.updateNow) {
            ActionBar actionBar = getSupportActionBar();
            String titleBarError = "<font color='#ff0000'>" + actionBar.getTitle() + "</font>";
            String titleBarSuccess = "<font color='#00ff00'>" + actionBar.getTitle() + "</font>";

            Var.updateNow = false;
            layout.removeAllViews();
            for (int i = 0; i < audioSearchList.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(audioSearchList.get(i));
                scrollLayout.addView(text);*/

                View view = LayoutInflater.from(SearchAudioActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                CircleImageView icon = view.findViewById(R.id.iconCard);
                TextView creator = view.findViewById(R.id.cardCreator);
                TextView description = view.findViewById(R.id.cardDescription);
                TextView genre = view.findViewById(R.id.cardGenre);

                String title = audioSearchList.get(i);
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
                    cardText.setTextColor(ContextCompat.getColor(SearchAudioActivity.this, R.color.audioSeen));
                }


                //cardText.setText(title);
                layout.addView(view);

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

                        title.setTextColor(ContextCompat.getColor(SearchAudioActivity.this, R.color.audioSeen));

                        startActivity(new Intent(SearchAudioActivity.this, TrackActivity.class));
                    }
                });
            }
            layout.addView(space);
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