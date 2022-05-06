package com.lecraftjay.newgrounds.more_window.art;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArtContentActivity extends AppCompatActivity {

    TextView title;
    TextView desc;
    ImageView image;
    CircleImageView creatorImage;
    TextView creatorName;
    LinearLayout creatorLink;
    ImageView contentLink;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_content);

        //---------------------------------------------------------------------

        title = findViewById(R.id.artTitle);
        desc = findViewById(R.id.artDescription);
        image = findViewById(R.id.artImage);
        creatorImage = findViewById(R.id.artCreatorIcon);
        creatorName = findViewById(R.id.artCreatorName);
        creatorLink = findViewById(R.id.artCreatorLayoutLink);
        contentLink = findViewById(R.id.artOpenLink);

        //---------------------------------------------------------------------

        String[] split = Var.artInfo.split(";;;");

        creatorLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ArtContentActivity.this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        contentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Var.artOpenLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = (Document) Jsoup
                            .connect(split[0])
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                            .timeout(5000).followRedirects(true).execute().parse();


                    // print all available links on page
                    Elements image = doc.getElementsByClass("image");
                    Elements creaotrParent = doc.getElementsByClass("item-details-main");
                    Elements description = doc.getElementsByClass("padded-top  ql-body ");
                    Elements title = doc.select("title");
                    Elements creatorLink = doc.getElementsByClass("item-user");
                    Elements creatorImage = doc.getElementsByClass("user-icon-bordered");

                    String[] split = title.html().split(" by");

                    String sTitle = split[0];
                    String sCreator = "";
                    String sDesc = "";
                    String sImage = "";
                    String sCreatorLink = "";
                    String sCreatorImage = "";

                    for(Element l : creatorLink){
                        Element li = l.child(0);
                        sCreatorLink = li.attr("abs:href");
                    }

                    for(Element l : creatorImage){
                        Elements a = l.select("image");
                        sCreatorImage = a.attr("abs:href");
                    }

                    for(Element l : creaotrParent){
                        Elements creator = l.select("a");
                        sCreator = creator.html();
                    }

                    for(Element l : description){
                        Elements desc = l.select("p");
                        sDesc = desc.html();
                    }

                    for(Element l : image){
                        Elements img = l.select("img");
                        sImage = img.attr("abs:src").substring(0, img.attr("abs:src").indexOf("?"));
                    }

                    Var.artCreator = sCreator;
                    Var.artDescription = sDesc;
                    Var.artImage = sImage;
                    Var.artTitle = sTitle;
                    Var.artCreatorLink = sCreatorLink;
                    Var.artCreatorImage = sCreatorImage;

                    Var.artUpdateOnce = true;


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

        Var.artTitle = "";
        Var.artImage = "";
        Var.artDescription = "";
        Var.artCreator = "";
        Var.artCreatorImage = "";
        Var.artCreatorLink = "";
    }

    public void updateOneTime(){
        if(Var.artUpdateOnce){
            Var.artUpdateOnce = false;

            title.setText(Html.fromHtml(trim(Var.artTitle, 30)));
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ArtContentActivity.this, Var.artTitle, Toast.LENGTH_LONG).show();
                }
            });

            desc.setText(Html.fromHtml(Var.artDescription));
            Picasso.get().load(Var.artImage).into(image);
            Picasso.get().load(Var.artCreatorImage).into(creatorImage);
            creatorName.setText(Var.artCreator);
            creatorLink.setTag(Var.artCreatorLink);

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