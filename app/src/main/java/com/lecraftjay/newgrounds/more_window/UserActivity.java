package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.art.ArtContentActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    ImageView banner;
    CircleImageView icon;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //------------------------------------------------------------

        banner = findViewById(R.id.userBanner);
        icon = findViewById(R.id.userIcon);
        name = findViewById(R.id.userName);

        //------------------------------------------------------------


        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(Var.userLink)
                            .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


                    Elements bannerLink = doc.select("script");
                    Elements userImageName = doc.getElementsByClass("user-header-bg");

                    String sBanner = "";
                    String sName = "";
                    String sIcon = "";

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

            Picasso.get().load(Var.userBanner).into(banner);
            Picasso.get().load(Var.userIcon).into(icon);
            name.setText(Var.userName);

        }
    }






    @Override
    protected void onResume() {
        //start handler as activity become visible

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