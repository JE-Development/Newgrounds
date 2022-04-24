package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout scrollLayout;
    int pos = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    boolean error = false;

    LinearLayout statusLayout;
    TextView status;

    boolean einmal = false;

    ScrollView originalScroll;

    ArrayList<String> linksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------------------------------------------------

        scrollLayout = findViewById(R.id.scroll);
        originalScroll = findViewById(R.id.originalScroll);
        statusLayout = findViewById(R.id.statusLayout);
        status = findViewById(R.id.status);

        //-----------------------------------------------------------------

        TextView test = new TextView(this);
        test.setText("test");
        scrollLayout.addView(test);

        originalScroll.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (originalScroll.getChildAt(0).getBottom() <= (originalScroll.getHeight() + originalScroll.getScrollY())) {
                            if(einmal == false) {
                                System.out.println("jason more tracks");
                                moreTracks();
                                einmal = true;
                            }
                        } else {
                            einmal = false;
                        }
                    }
                });

        Thread t = new Thread(new Runnable() {
            public void run() {
                error = false;
                try {
                    Document doc = (Document) Jsoup
                            .connect("https://www.newgrounds.com/audio")
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();
                    Elements titles = doc.select(".entrytitle");


                    // print all titles in main page

                    // print all available links on page
                    Elements links = doc.select("a[href]");
                    Elements ele = doc.getElementsByClass("audio-wrapper");
                    int counter = 0;
                    for (Element l : ele) {
                        System.out.println(l.toString());
                        System.out.println("\n\n\n");

                        Element linkEle = l.child(0).child(0);
                        Element title = l.child(0).child(0).child(1).child(0).child(0).child(0).child(0);
                        String titleString = title.html();
                        String link = linkEle.attr("abs:href");
                        System.out.println("jason doc link:" + link);
                        if(link.contains("listen")) {
                            counter++;
                            if(linksList.contains(link)){
                                System.out.print("----------------------------vorhanden------------------: ");

                            }else {
                                String toAdd = link + ";;;" + titleString;
                                linksList.add(toAdd);
                            }
                        }
                    }
                    //System.out.println(doc.toString());

                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    error = true;
                    Var.updateNow = true;
                }catch (Exception e){
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
        if(scrollLayout.getChildCount() < linksList.size() || Var.updateNow) {

            if(error){
                statusLayout.setBackgroundColor(Color.rgb(255,0,0));
                status.setText("Status: Error, Connection Timeout");
            }else{
                statusLayout.setBackgroundColor(Color.rgb(0,255,0));
                status.setText("Status: Success");
            }

            Var.updateNow = false;
            scrollLayout.removeAllViews();
            for (int i = 0; i < linksList.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(linksList.get(i));
                scrollLayout.addView(text);*/

                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                String title = linksList.get(i);
                System.out.println("jason doc saved title: " + title);
                String[] splitter = title.split(";;;");
                cardText.setText(splitter[1]);
                view.setTag(splitter[0]);

                //cardText.setText(title);
                scrollLayout.addView(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView openLink = view.findViewById(R.id.cardText);
                        Var.openLink = (String) view.getTag();
                        startActivity(new Intent(MainActivity.this, TrackActivity.class));
                    }
                });
            }
        }
    }

    public void moreTracks(){
        pos += 25;
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect("https://www.newgrounds.com/audio/featured?offset=" + pos + "&amp;inner=1")
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();
                    Elements titles = doc.select(".entrytitle");

                    // print all titles in main page
                    for (Element e : titles) {
                        System.out.println("text: " + e.text());
                        System.out.println("html: " + e.html());
                    }

                    // print all available links on page
                    Elements links = doc.select("a[href]");
                    Elements ele = doc.getElementsByClass("audio-wrapper");
                    int counter = 0;
                    for (Element l : ele) {
                        System.out.println(l.toString());
                        System.out.println("\n\n\n");

                        Element linkEle = l.child(0).child(0);
                        Element title = l.child(0).child(0).child(1).child(0).child(0).child(0).child(0);
                        String titleString = title.html();
                        String link = linkEle.attr("abs:href");
                        System.out.println("jason doc link:" + link);
                        if(link.contains("listen")) {
                            counter++;
                            if(linksList.contains(link)){
                                System.out.print("----------------------------vorhanden------------------: ");

                            }else {
                                String toAdd = link + ";;;" + titleString;
                                linksList.add(toAdd);
                            }
                        }
                    }
                    //System.out.println(doc.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public String getTitle(String link) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(link)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();
                    Elements titles = doc.select(".entrytitle");

                    // print all available links on page
                    Elements l = doc.select("title");
                    String html = l.html();
                    Var.trackTitle = html;
                    System.out.println("jason doc title: " + html);

                    //System.out.println("jason doc data: " + doc.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(Var.einmal1 == false) {
                    Var.updateNow = true;
                    Var.einmal1 = true;
                }
            }
        });
        t.start();

        return Var.trackTitle;
    }

}