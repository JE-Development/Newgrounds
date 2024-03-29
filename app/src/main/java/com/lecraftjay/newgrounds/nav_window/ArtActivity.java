package com.lecraftjay.newgrounds.nav_window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chartboost.sdk.Chartboost;
import com.ironsource.mediationsdk.IronSource;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.art.ArtContentActivity;
import com.lecraftjay.newgrounds.more_window.SearchActivity;
import com.lecraftjay.newgrounds.more_window.profile.PlaylistTrackActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ArtActivity extends AppCompatActivity {

    LinearLayout root;
    ScrollView scrollLayout;

    int pos = 0;

    int adCounter = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;
    ProgressBar space;
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

        checkPopup();

        Var.updateNow = false;

        Var.currentWindow = "art";


        /*IronSource.setConsent(true);
        AppLovinPrivacySettings.setHasUserConsent(true, this);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });*/

        scrollLayout.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scrollLayout.getChildAt(0).getBottom() <= (scrollLayout.getHeight() + scrollLayout.getScrollY())) {
                            if(einmal == false) {
                                space.setVisibility(View.VISIBLE);
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
                        Elements rating = l.getElementsByClass("rating");

                        String s = "---";
                        for(Element e : imgLink){
                            s = e.child(0).attr("abs:src");
                        }

                        String r = "";
                        for(Element e : rating){
                            Element el = e.child(0);
                            r = el.attr("class");
                        }

                        String sLink = link.attr("abs:href");
                        String sTitle = title.html();
                        String sImgLink = s;
                        String sCreator = creator.html();
                        String sRating = r;

                        if(sLink.contains("art") && !sRating.equals("nohue-ngicon-small-rated-a")) {
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
            space.setVisibility(View.INVISIBLE);
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

                /*if(adCounter >= 4){
                    Bundle extras = new AppLovinExtras.Builder().setMuteAudio(true).build();

                    adCounter = 0;
                    AdView ad = new AdView(this);
                    ad.setAdUnitId("ca-app-pub-3904729559747077/2409595162");
                    ad.setAdSize(AdSize.BANNER);

                    View adLayout = LayoutInflater.from(ArtActivity.this).inflate(R.layout.ad_content_layout, null);
                    LinearLayout lay = adLayout.findViewById(R.id.ad_content_linear_layout);
                    lay.addView(ad);
                    root.addView(adLayout);

                    AdRequest request = new AdRequest.Builder().addNetworkExtrasBundle(ApplovinAdapter.class, extras).build();
                    ad.loadAd(request);
                }
                adCounter++;*/

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
    public void onBackPressed() {
        // If an interstitial is on screen, close it.
        if (Chartboost.onBackPressed()) {
            return;
        } else {
            super.onBackPressed();
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
        IronSource.onResume(this);


        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        IronSource.onPause(this);

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
        LinearLayout community = findViewById(R.id.profile);

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
                startActivity(new Intent(ArtActivity.this, MoviesActivity.class));
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
                startActivity(new Intent(ArtActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.searchMenu:
                startActivity(new Intent(ArtActivity.this, SearchActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void checkPopup(){
        if(Var.showPopupWindow){
            if(Var.popupInfoWindow.equals("art")){
                AlertDialog alertDialog = new AlertDialog.Builder(ArtActivity.this)
                        .setTitle(Var.popupInfoText)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                            }
                        }).show();
            }
        }
    }

}