package com.lecraftjay.newgrounds.classes;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.lecraftjay.newgrounds.more_window.profile.PlaylistTrackActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class LoadTrack extends Thread{
   
   ArrayList<String> siteLink = new ArrayList<>();
   
   public LoadTrack(ArrayList<String> siteLink1){
      siteLink = siteLink1;
   }
   
   public void run(){
      try {
         System.out.println("jason back start");
         for(int a = 0; a < siteLink.size(); a++){
            System.out.println("jason open: " + Var.openLink);
            Document doc = (Document) Jsoup
                    .connect(siteLink.get(a))
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36").ignoreHttpErrors(true)
                    .timeout(5000).followRedirects(true).execute().parse();
            Elements titles = doc.select(".entrytitle");

            // print all titles in main page
            for (Element e : titles) {

            }

            // print all available links on page
            Elements links = doc.select("script");

            for (Element l : links) {
               //progressString = counterProgress + "/" + trackCounter;
               String html = l.html();
               if(html.contains("var embed_controller")){
                  html = html.replace("var embed_controller = new embedController([{\"url\":\"", "");
                  char[] htmlChar = html.toCharArray();
                  String createdLink = "";
                  for(int i = 0; i < htmlChar.length; i++){
                     if(htmlChar[i] == '?'){
                        createdLink = createdLink.replace("\\", "");
                        break;
                     }else{
                        createdLink = createdLink + htmlChar[i];
                     }
                  }
                  
                  Var.linkList.add(createdLink + ";;;" + siteLink.get(a));

                  MediaPlayer mediaPlayer = new MediaPlayer();
                  mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                  mediaPlayer.setDataSource(createdLink);
                  mediaPlayer.prepare();

                  System.out.println("jason back doing: " + Var.counterProgress);

                  Var.mediaLink.add(mediaPlayer);
                  Var.counterProgress++;
                  
               }
               String link = l.attr("abs:href");
               if(link.contains("listen")) {

               }
            }

            Var.trackReady = true;
         }
      }catch (Exception e){
         e.printStackTrace();
         System.out.println("jason back error");
      }
      System.out.println("jason back finished");
   }
   
}
