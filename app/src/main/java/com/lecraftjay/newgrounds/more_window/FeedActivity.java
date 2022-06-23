package com.lecraftjay.newgrounds.more_window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.nav_window.ProfileActivity;
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

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    boolean durchlauf = false;
    
    ArrayList<String> audioList = new ArrayList<>();

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
        feed.loadUrl("https://www.newgrounds.com/portal/view/847551");

        
    }

    class MyJavaScriptInterface {
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

        if(doc.toString().contains("ungrounded")){
            System.out.println("jason video true juhuuuuuuuuu");
        }else{
            System.out.println("jason video false    " + counter);
        }

        Elements ele = doc.getElementsByClass("pod-body");
        for(Element tester : ele){
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
        }

        
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        IronSource.onResume(this);

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //update();
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

    public void update(){
        if(scrollLayout.getChildCount() < audioList.size() || Var.updateNow) {


            Var.updateNow = false;
            scrollLayout.removeAllViews();
            space.setVisibility(View.INVISIBLE);
            for (int i = 0; i < audioList.size(); i++) {
                /*TextView text = new TextView(MainActivity.this);
                text.setText(audioList.get(i));
                scrollLayout.addView(text);*/

                View view = LayoutInflater.from(FeedActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                CircleImageView icon = view.findViewById(R.id.iconCard);
                TextView creator = view.findViewById(R.id.cardCreator);
                TextView description = view.findViewById(R.id.cardDescription);
                TextView genre = view.findViewById(R.id.cardGenre);

                String title = audioList.get(i);
                String[] splitter = title.split(";;;");

                try {
                    view.setTag(splitter[0]);
                    cardText.setText(Html.fromHtml(trim(splitter[1], 28)));
                    cardText.setTag(splitter[1]);
                    Picasso.get().load(splitter[2]).into(icon);
                    icon.setTag(splitter[2]);
                    creator.setText(splitter[3]);
                    genre.setText(splitter[4]);
                    description.setText(splitter[5]);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                    System.out.println(Arrays.toString(splitter));
                    System.out.println(title);
                }

                SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                String getter = sp.getString("alreadySeen", "");

                if(getter.contains(splitter[0])){
                    cardText.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));
                }

                /*if(adCounter >= 8){
                    Bundle extras = new AppLovinExtras.Builder().setMuteAudio(true).build();

                    adCounter = 0;
                    AdView ad = new AdView(this);
                    //ad.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                    ad.setAdUnitId("ca-app-pub-3904729559747077/5829965422");
                    ad.setAdSize(AdSize.BANNER);

                    View adLayout = LayoutInflater.from(FeedActivity.this).inflate(R.layout.ad_content_layout, null);
                    LinearLayout lay = adLayout.findViewById(R.id.ad_content_linear_layout);
                    lay.addView(ad);
                    scrollLayout.addView(adLayout);

                    AdRequest request = new AdRequest.Builder().addNetworkExtrasBundle(ApplovinAdapter.class, extras).build();
                    ad.loadAd(request);
                }
                adCounter++;*/


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
                        System.out.println("jason check: " + Var.openLink);
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

                        title.setTextColor(ContextCompat.getColor(FeedActivity.this, R.color.audioSeen));

                        startActivity(new Intent(FeedActivity.this, TrackActivity.class));
                    }
                });
            }
            scrollLayout.addView(space);
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