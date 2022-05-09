package com.lecraftjay.newgrounds.nav_window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.fonts.SystemFonts;
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

    int pos = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;
    Space space;
    boolean einmal = false;


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

        scrollLayout.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scrollLayout.getChildAt(0).getBottom() <= (scrollLayout.getHeight() + scrollLayout.getScrollY())) {
                            if(einmal == false) {
                                getContent("https://www.newgrounds.com/art/featured?offset=;;;pos;;;&amp;inner=1", true);
                                einmal = true;
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        getContent("https://www.newgrounds.com/art", false);

        setNavigation();
    }

    public void getContent(String url, boolean useAdvanced){
        if(useAdvanced){
            pos += 30;
        }

        String newLink = url.replace(";;;pos;;;", String.valueOf(pos));

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(newLink)
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
                            if(artContent.contains(sLink)){

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
        if(root.getChildCount()*2 < artContent.size() || Var.updateNow) {
            root.removeAllViews();
            for (int i = 0; i < artContent.size(); i++) {
                View rowView = LayoutInflater.from(ArtActivity.this).inflate(R.layout.row_layout, null);
                LinearLayout row = rowView.findViewById(R.id.artRow);

                LinearLayout l = new LinearLayout(this);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                l.setLayoutParams(param);

                View view = LayoutInflater.from(ArtActivity.this).inflate(R.layout.art_card_layout, null);
                TextView cardTitle = view.findViewById(R.id.artTitle);
                ImageView image = view.findViewById(R.id.artImage);
                TextView user = view.findViewById(R.id.artCreator);

                String title = artContent.get(i);
                String[] splitter = title.split(";;;");

                try {
                    cardTitle.setTag(title);
                    cardTitle.setText(trim(splitter[1], 12));
                    Picasso.get().load(splitter[2]).into(image);
                    user.setText(splitter[3]);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }

                SharedPreferences sp = getApplicationContext().getSharedPreferences("Art", 0);
                String getter = sp.getString("alreadySeen", "");

                if(getter.contains(splitter[0])){
                    cardTitle.setTextColor(ContextCompat.getColor(ArtActivity.this, R.color.audioSeen));
                }

                l.addView(view);
                row.addView(l);

                if(i++ < artContent.size()) {

                    LinearLayout l1 = new LinearLayout(this);

                    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                    );
                    l1.setLayoutParams(param1);

                    View view1 = LayoutInflater.from(ArtActivity.this).inflate(R.layout.art_card_layout, null);
                    TextView cardTitle1 = view1.findViewById(R.id.artTitle);
                    ImageView image1 = view1.findViewById(R.id.artImage);
                    TextView user1 = view1.findViewById(R.id.artCreator);

                    String title1 = artContent.get(i);
                    String[] splitter1 = title1.split(";;;");


                    try {
                        cardTitle1.setTag(splitter1[0]);
                        cardTitle1.setText(trim(splitter1[1], 12));
                        Picasso.get().load(splitter1[2]).into(image1);
                        user1.setText(splitter1[3]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Art", 0);
                    String getter1 = sp1.getString("alreadySeen", "");

                    if(getter1.contains(splitter1[0])){
                        cardTitle1.setTextColor(ContextCompat.getColor(ArtActivity.this, R.color.audioSeen));
                    }

                    l1.addView(view1);
                    row.addView(l1);
                    root.addView(rowView);

                    view1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView title = (TextView) v.findViewById(R.id.artTitle);
                            Var.artInfo = (String) title.getTag();
                            String[] split = Var.artInfo.split(";;;");
                            Var.artOpenLink = split[0];

                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Art", 0);
                            String getter = sp.getString("alreadySeen", "");

                            SharedPreferences liste = getApplicationContext().getSharedPreferences("Art", 0);
                            SharedPreferences.Editor editor = liste.edit();
                            editor.putString("alreadySeen", getter + ";;;" + Var.artOpenLink);
                            editor.apply();

                            title.setTextColor(ContextCompat.getColor(ArtActivity.this, R.color.audioSeen));

                            startActivity(new Intent(ArtActivity.this, ArtContentActivity.class));

                        }
                    });
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView title = (TextView) v.findViewById(R.id.artTitle);
                        Var.artInfo = (String) title.getTag();
                        String[] split = Var.artInfo.split(";;;");
                        Var.artOpenLink = split[0];

                        SharedPreferences sp = getApplicationContext().getSharedPreferences("Art", 0);
                        String getter = sp.getString("alreadySeen", "");

                        SharedPreferences liste = getApplicationContext().getSharedPreferences("Art", 0);
                        SharedPreferences.Editor editor = liste.edit();
                        editor.putString("alreadySeen", getter + ";;;" + Var.artOpenLink);
                        editor.apply();

                        title.setTextColor(ContextCompat.getColor(ArtActivity.this, R.color.audioSeen));

                        startActivity(new Intent(ArtActivity.this, ArtContentActivity.class));

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
        LinearLayout games = findViewById(R.id.games);
        LinearLayout movie = findViewById(R.id.movie);
        LinearLayout audio = findViewById(R.id.audio);
        LinearLayout art = findViewById(R.id.art);
        LinearLayout community = findViewById(R.id.games);

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