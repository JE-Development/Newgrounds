package com.lecraftjay.newgrounds.nav_window;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArtActivity extends AppCompatActivity {

    LinearLayout root;
    ScrollView scrollLayout;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;
    Space space;

    ArrayList<String> artContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);

        //-------------------------------------------------------------------

        root = findViewById(R.id.artLayout);
        scrollLayout = findViewById(R.id.artScroll);
        space = findViewById(R.id.artSpace);

        //-------------------------------------------------------------------

        Var.updateNow = false;

        getContent("https://www.newgrounds.com/art");

        setNavigation();
    }

    public void getContent(String url){
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(url)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


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
                            if(artContent.contains(link)){

                            }else {
                                String toAdd = sLink + ";;;" + sTitle + ";;;" + sImgLink + ";;;" + sCreator;
                                artContent.add(toAdd);
                            }
                        }

                    }

                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    //error = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
    }

    public void update(){
        System.out.println("jason art child: " + root.getChildCount() + "   " + artContent.size());
        if(root.getChildCount() < artContent.size() || Var.updateNow) {
            root.removeAllViews();
            for (int i = 0; i < artContent.size(); i++) {
                View rowView = LayoutInflater.from(ArtActivity.this).inflate(R.layout.row_layout, null);
                LinearLayout row = rowView.findViewById(R.id.artRow);

                View view = LayoutInflater.from(ArtActivity.this).inflate(R.layout.art_card_layout, null);
                TextView cardTitle = view.findViewById(R.id.artTitle);
                ImageView image = view.findViewById(R.id.artImage);
                TextView user = view.findViewById(R.id.artCreator);

                String title = artContent.get(i);
                String[] splitter = title.split(";;;");

                try {
                    view.setTag(splitter[0]);
                    cardTitle.setText(trim(splitter[1], 12));
                    Picasso.get().load(splitter[2]).into(image);
                    user.setText(splitter[3]);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }


                row.addView(view);

                if(i++ < artContent.size()) {

                    View view1 = LayoutInflater.from(ArtActivity.this).inflate(R.layout.art_card_layout, null);
                    TextView cardTitle1 = view1.findViewById(R.id.artTitle);
                    ImageView image1 = view1.findViewById(R.id.artImage);
                    TextView user1 = view1.findViewById(R.id.artCreator);

                    String title1 = artContent.get(i);
                    String[] splitter1 = title1.split(";;;");

                    try {
                        view1.setTag(splitter1[0]);
                        cardTitle1.setText(trim(splitter1[1], 12));
                        Picasso.get().load(splitter1[2]).into(image1);
                        user1.setText(splitter1[3]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    row.addView(view1);
                    root.addView(rowView);
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView title = view.findViewById(R.id.cardText);
                        Var.currentTitle = (String) title.getTag();
                        Var.openLink = (String) view.getTag();

                        title.setTextColor(ContextCompat.getColor(ArtActivity.this, R.color.audioSeen));

                        startActivity(new Intent(ArtActivity.this, TrackActivity.class));
                    }
                });
            }
            root.addView(space);
        }
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


    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }













    public void setNavigation(){
        ImageView games = findViewById(R.id.games);
        ImageView movie = findViewById(R.id.movie);
        ImageView audio = findViewById(R.id.audio);
        ImageView art = findViewById(R.id.art);
        ImageView community = findViewById(R.id.games);

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, GamesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, MovieActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, AudioActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtActivity.this, CommunityActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
}