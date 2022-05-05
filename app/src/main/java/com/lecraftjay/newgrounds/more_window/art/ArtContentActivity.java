package com.lecraftjay.newgrounds.more_window.art;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArtContentActivity extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_content);

        //---------------------------------------------------------------------

        title = findViewById(R.id.artTitle);

        //---------------------------------------------------------------------

        String[] split = Var.artInfo.split(";;;");

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

                    String[] split = title.html().split(" by");

                    String sTitle = split[0];
                    String sCreator = "";
                    String sDesc = "";
                    String sImage = "";

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

                    System.out.println("jason art check: " + sCreator);
                    System.out.println("jason art check: " + sDesc);
                    System.out.println("jason art check: " + sImage);
                    System.out.println("jason art check: " + sTitle);

                    //alle informationen funktionieren :D


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }
}