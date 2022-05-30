package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.art.ArtContentActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    ImageView banner;
    CircleImageView icon;
    TextView name;
    LinearLayout headerInfo;
    TextView content;
    LinearLayout linksLayout;
    ImageView openLink;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //------------------------------------------------------------

        banner = findViewById(R.id.userBanner);
        icon = findViewById(R.id.userIcon);
        name = findViewById(R.id.userName);
        headerInfo = findViewById(R.id.headerInfo);
        content = findViewById(R.id.userContent);
        linksLayout = findViewById(R.id.userLinksLayout);
        openLink = findViewById(R.id.userOpenLink);
        progress = findViewById(R.id.userProgress);

        //------------------------------------------------------------

        openLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Var.userLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(Var.userLink)
                            .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


                    Elements bannerLink = doc.select("script");
                    Elements userImageName = doc.getElementsByClass("user-header-bg");
                    Elements userButtons = doc.getElementsByClass("user-header-buttons");
                    Elements bio = doc.getElementsByClass("general fill-space text-content");
                    Elements userLinks = doc.getElementsByClass("itemlist user-links");

                    String sBanner = "";
                    String sName = "";
                    String sIcon = "";
                    ArrayList<String> sUserButtons = new ArrayList<>();
                    ArrayList<String> sUserLinks = new ArrayList<>();
                    String sBio = bio.html();

                    for(Element e : userLinks){
                        Elements a = userLinks.select("a");
                        for(Element el : a){
                            String imageLink = "";
                            String text = "";
                            String contentLink = "";
                            contentLink = el.attr("href");

                            Elements t = el.select("strong");
                            for(Element ele : t){
                                text = t.html();
                            }
                            Elements icon = el.select("img");
                            for(Element ele : icon){
                                imageLink = ele.attr("src");
                            }

                            String toAdd = contentLink + ";;;" + imageLink + ";;;" + text;
                            sUserLinks.add(toAdd);
                        }
                    }

                    for(Element e : userButtons){
                        Elements a =  e.select("a");
                        for (Element el : a){
                            String buttonLink = "";
                            String head = "";
                            String val = "";
                            buttonLink = el.attr("href");
                            Elements headline = el.select("span");
                            for(Element ele : headline){
                                head = ele.html();
                            }
                            Elements value = el.select("strong");
                            for(Element ele : value){
                                val = ele.html();
                            }
                            String toAdd = buttonLink + ";;;" + head + ";;;" + val;
                            sUserButtons.add(toAdd);
                        }
                    }


                    for(Element e : userImageName){
                        Elements icon = e.select("image");
                        Elements name = e.getElementsByClass("user-link");
                        sName = name.html();
                        sIcon = icon.attr("href");
                    }

                    for(Element e : bannerLink){
                        if(e.html().contains("var image_crop = new ngutils.croppable_image")){
                            String script = e.html();
                            script = script.replace("\"src\":\"\\/\\/", ";;;");
                            script = script.replace("\",\"frame\":{\"", ";;;");
                            String[] split = script.split(";;;");
                            sBanner = "https://" + split[1].replace("\\", "");
                        }
                    }

                    Var.userBanner = sBanner;
                    Var.userName = sName;
                    Var.userIcon = sIcon;
                    Var.userButton = sUserButtons;
                    Var.userBio = sBio;
                    Var.userBioLinks = sUserLinks;

                    Var.userUpdateOne = true;


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void updateOneTime(){
        if(Var.userUpdateOne){
            Var.userUpdateOne = false;
            progress.setVisibility(View.INVISIBLE);

            Picasso.get().load(Var.userBanner).into(banner);
            Picasso.get().load(Var.userIcon).into(icon);
            name.setText(Var.userName);
            openLink.setVisibility(View.VISIBLE);
            for(int i = 0; i < Var.userButton.size(); i++){
                View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.user_nav_module_layout, null);
                TextView head = view.findViewById(R.id.moduleTitle);
                TextView val = view.findViewById(R.id.moduleValue);
                String[] splitter = Var.userButton.get(i).split(";;;");
                head.setText(splitter[1]);
                val.setText(splitter[2]);
                view.setTag(splitter[0]);
                if(splitter[1].equals("AUDIO") || splitter[1].equals("ART") || splitter[1].equals("GAMES") || splitter[1].equals("MOVIES")){
                    head.setTextColor(getResources().getColor(R.color.orange));
                    val.setTextColor(getResources().getColor(R.color.orange));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView title = v.findViewById(R.id.moduleTitle);
                            Var.userContentTitle = title.getText().toString();
                            Var.userContentLink = v.getTag().toString();
                            startActivity(new Intent(UserActivity.this, UserContentActivity.class));
                        }
                    });
                }
                headerInfo.addView(view);
            }

            for(int i = 0; i < Var.userBioLinks.size(); i++){
                View view = LayoutInflater.from(UserActivity.this).inflate(R.layout.user_links_layout, null);
                ImageView icon = view.findViewById(R.id.userImageLink);
                TextView text = view.findViewById(R.id.userTextLink);
                String[] splitter = Var.userBioLinks.get(i).split(";;;");
                Picasso.get().load(splitter[1]).into(icon);
                text.setText(splitter[2]);
                text.setTag(splitter[0]);

                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(v.getTag().toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                linksLayout.addView(view);
            }

            content.setText(Html.fromHtml(Var.userBio));

        }
    }






    @Override
    protected void onResume() {

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                updateOneTime();

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();

        Var.description = "";
        Var.creatorLink = "";
        Var.creatorIconLink = "";
        Var.creatorName = "";
        Var.waveLink = "";

    }
}