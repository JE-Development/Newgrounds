package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.lecraftjay.newgrounds.nav_window.AudioActivity;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView closed;
    TextView reason;
    RelativeLayout root;

    WebView webView;
    Button htmlButton;
    boolean durchlauf = true;

    String h = "null";

    Handler handlerForJavascriptInterface = new Handler();

    /*class MyJavaScriptInterface
    {
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
                            while (true) {
                                if(durchlauf) {
                                    durchlauf = false;
                                    //Toast toast = Toast.makeText(ctx, "Page has been loaded in webview. html content :" + h, Toast.LENGTH_LONG).show();
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");

                                    System.out.println("jason html: " + h);

                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");
                                    System.out.println("---------------------------------------------------------------------");

                                }
                            }
                        }
                    });
                    t.start();
                }});
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*htmlButton = findViewById(R.id.webHtml);

        final WebView webview = (WebView) findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(MainActivity.this), "HtmlViewer");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                 webview.loadUrl("javascript:window.HtmlViewer.showHTML('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");
                }
            }
        );
        webview.loadUrl("https://www.newgrounds.com/portal/view/847415");

        htmlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durchlauf = true;
            }
        });*/








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




        String hide = BuildConfig.HIDE_INFO;
        String[] split = hide.split(";;;");

        Var.pass = split[0];
        Var.toEmail = split[1];

        SharedPreferences sp1 = getApplicationContext().getSharedPreferences("Info", 0);
        String getter = sp1.getString("updateVersionCode", "0");

        String updateCode = "9";

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
}