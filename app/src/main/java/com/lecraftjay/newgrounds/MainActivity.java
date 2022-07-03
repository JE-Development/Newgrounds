package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.sdk.AppLovinSdk;
import com.lecraftjay.newgrounds.classes.LoadTrack;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.NewFeaturesActivity;
import com.lecraftjay.newgrounds.more_window.TesterActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView closed;
    TextView reason;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------------------------------------------

        closed = findViewById(R.id.mainService);
        reason = findViewById(R.id.mainReason);
        root = findViewById(R.id.root);

        //--------------------------------------------------

        SharedPreferences shared = getApplicationContext().getSharedPreferences("Playlist", 0);
        String pri = shared.getString("priority", "null");
        if(!pri.equals("null")){
            SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
            String getter = sp.getString(pri, "null");

            if(!getter.equals("null")){
                String[] trackLinks = getter.split(";;;");
                ArrayList<String> tl = new ArrayList<>();
                for(int i = 0; i < trackLinks.length; i++){
                    String[] s = trackLinks[i].split(";");
                    tl.add(s[0]);
                }
                System.out.println("jason back content: " + tl);
                LoadTrack t = new LoadTrack(tl);
                t.start();
            }
        }


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            writer();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }



        String hide = BuildConfig.HIDE_INFO;
        String[] split = hide.split(";;;");

        Var.pass = split[0];
        Var.toEmail = split[1];

        SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Info", 0);
        String getter = sp1.getString("updateVersionCode", "0");

        String updateCode = "10";

        if(getter.equals(updateCode)){
            startActivity(new Intent(MainActivity.this, AudioActivity.class));
            finish();
        }else{
            SharedPreferences spe = getApplicationContext().getSharedPreferences("Info", 0);
            SharedPreferences.Editor editor = spe.edit();
            editor.putString("updateVersionCode", updateCode);
            editor.apply();

            startActivity(new Intent(MainActivity.this, NewFeaturesActivity.class));
            finish();
        }
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
}