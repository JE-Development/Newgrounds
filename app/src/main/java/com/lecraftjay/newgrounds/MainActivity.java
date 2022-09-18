package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.classes.LoadTrack;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.infos.NewFeaturesActivity;
import com.lecraftjay.newgrounds.more_window.infos.StartInfoActivity;
import com.lecraftjay.newgrounds.more_window.infos.UpdateInfoActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView closed;
    TextView reason;
    RelativeLayout root;

    int delay = 100;
    Handler handler = new Handler();
    Runnable runnable;

    String serverContent = "";
    String serverPopupContent = "";
    String serverUpdateContent = "";
    boolean serverTextReady = false;
    boolean serverPopupReady = false;
    boolean serverUpdateReady = false;

    boolean goAhead = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------------------------------------------

        closed = findViewById(R.id.mainService);
        reason = findViewById(R.id.mainReason);
        root = findViewById(R.id.root);

        //--------------------------------------------------

        /*


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            writer();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }*/

/*
        getServerText("https://newgrounds-worker.jason-apps.workers.dev/android/newgrounds_mobile/start_message");
        getPopupInfos("https://newgrounds-worker.jason-apps.workers.dev/android/newgrounds_mobile/popup_message");
        getUpdateInfos("https://newgrounds-worker.jason-apps.workers.dev/android/newgrounds_mobile/update_message");*/

        startActivity(new Intent(MainActivity.this, AudioActivity.class));
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                writer();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void writer(){
        File folder = new File(getApplicationContext().getFilesDir() + "/NewgroundsData" );
        File file = new File(getApplicationContext().getFilesDir() + "/NewgroundsData/newgroundsfile.mp3");
        boolean b = folder.mkdirs();
        boolean bb = false;
        try {
            bb = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "bool: " + b + "    " + folder.exists() + "    " + bb + "     " + file.exists(), Toast.LENGTH_SHORT).show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedInputStream in = new BufferedInputStream(new URL("https://audio.ngfiles.com/824000/824781_Base-After-Base-20.mp3?f1537611366").openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                } catch (IOException e) {
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
        handler.removeCallbacks(runnable);
        super.onPause();

    }

    public void update(){
        if(serverTextReady && serverPopupReady && serverUpdateReady){
            serverTextReady = false;
            serverPopupReady = false;
            serverUpdateReady = false;

            analysePopup();
/*
            String hide = BuildConfig.HIDE_INFO;
            String[] split = hide.split(";;;");

            Var.pass = split[0];
            Var.toEmail = split[1];*/

            SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Info", 0);
            String getter = sp1.getString("updateVersionCode", "0");

            String updateCode = "11";
            //String updateCode = "10";

            if(getter.equals(updateCode)){
                if(serverContent.equals("null;;;null;;;null")){

                    checkUpdateInfo();

                }else{
                    if(serverContent.contains(";;;")) {

                        SharedPreferences sp2 = getApplicationContext().getSharedPreferences("Info", 0);
                        String getter2 = sp2.getString("startInfoVersionCode", "0");

                        String[] split = serverContent.split(";;;");
                        Var.startInfoTitle = split[0];
                        Var.startInfoText = split[1];
                        Var.startInfoId = split[2];

                        if (getter2.equals(Var.startInfoId)) {
                            checkUpdateInfo();

                        } else {


                            SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
                            SharedPreferences.Editor editor = spe.edit();
                            editor.putString("startInfoVersionCode", Var.startInfoId);
                            editor.apply();

                            goAhead = false;

                            startActivity(new Intent(MainActivity.this, StartInfoActivity.class));
                            finish();
                        }
                    }
                }

                if(goAhead) {

                    startActivity(new Intent(MainActivity.this, AudioActivity.class));
                    finish();
                }

            }else{
                SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
                SharedPreferences.Editor editor = spe.edit();
                editor.putString("updateVersionCode", updateCode);
                editor.apply();

                startActivity(new Intent(MainActivity.this, NewFeaturesActivity.class));
                finish();
            }
        }
    }

    public void getServerText(String url){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn = new URL(url).openConnection();
                    System.out.println("-----------------------------------------------------");
                    InputStream in = conn.getInputStream();
                    String contents = convertStreamToString(in);
                    serverContent  = contents;
                    serverTextReady = true;
                    System.out.println("jason getText: " + contents);
                    System.out.println("-----------------------------------------------------/");

                }catch (Exception e){
                    System.out.println("jason server error");
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void getPopupInfos(String url){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn = new URL(url).openConnection();
                    System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    InputStream in = conn.getInputStream();
                    String contents = convertStreamToString(in);
                    serverPopupContent  = contents;
                    serverPopupReady = true;
                    System.out.println("jason getText: " + contents);
                    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::/");

                }catch (Exception e){
                    System.out.println("jason server error");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getUpdateInfos(String url){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn = new URL(url).openConnection();
                    InputStream in = conn.getInputStream();
                    String contents = convertStreamToString(in);
                    serverUpdateContent  = contents;
                    serverUpdateReady = true;

                }catch (Exception e){
                    System.out.println("jason server error");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void analysePopup(){
        if(serverPopupContent.contains(";;;")) {
            String[] split = serverPopupContent.split(";;;");
            Var.popupInfoText = split[0];
            Var.popupInfoWindow = split[1];
            Var.popupInfoId = split[2];

            SharedPreferences sp = getApplicationContext().getSharedPreferences("Info", 0);
            String getter = sp.getString("popupVersionCode", "0");

            if (Var.popupInfoText.equals("null;;;null;;;null")) {
                Var.showPopupWindow = false;
            } else {
                if (getter.equals(Var.popupInfoId)) {
                    Var.showPopupWindow = false;
                } else {
                    SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
                    SharedPreferences.Editor editor = spe.edit();
                    editor.putString("popupVersionCode", Var.popupInfoId);
                    editor.apply();

                    Var.showPopupWindow = true;
                }
            }
        }
    }

    public void checkUpdateInfo(){
        if(serverUpdateContent.equals("null;;;null")){
            startActivity(new Intent(MainActivity.this, AudioActivity.class));
            finish();
        }else{
            SharedPreferences sp = getApplicationContext().getSharedPreferences("Info", 0);
            String getter = sp.getString("updateAvailableVersionCode", "0");

            String[] split = serverUpdateContent.split(";;;");

            Var.updateInfoTitle = split[0];
            Var.updateInfoText = split[1];
            Var.updateInfoPlaystoreLink = split[2];
            int buildVersionCode =  Integer.parseInt(split[3]);
            String updateInfoId = split[4];

            if(!getter.equals(updateInfoId)){

                if(BuildConfig.VERSION_CODE <= buildVersionCode){

                    SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
                    SharedPreferences.Editor editor = spe.edit();
                    editor.putString("updateAvailableVersionCode", updateInfoId);
                    editor.apply();

                    goAhead = false;

                    startActivity(new Intent(MainActivity.this, UpdateInfoActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(MainActivity.this, AudioActivity.class));
                    finish();
                }
            }
        }
    }
}