package com.lecraftjay.newgrounds.more_window.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ironsource.mediationsdk.IronSource;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.SearchActivity;
import com.lecraftjay.newgrounds.nav_window.MoviesActivity;

import org.jetbrains.annotations.NotNull;

public class StartMovieActivity extends AppCompatActivity {
    
    WebView web;

    Handler handlerForJavascriptInterface = new Handler();

    boolean found = false;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_movie);
        
        //---------------------------------------------------------------
        
        web = findViewById(R.id.movieWeb);
        
        //---------------------------------------------------------------

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);

        web.addJavascriptInterface(new MyJavaScriptInterface(StartMovieActivity.this), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
                                  @Override
                                  public void onPageFinished(WebView view, String url) {
                                      web.loadUrl("javascript:window.HtmlViewer.showHTML('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");
                                  }
                              }
        );
        web.loadUrl(Var.movieOpenLink);

        web.setWebViewClient(new WebViewClient(){
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if(url.contains("uploads.ungrounded.net")){
                    Var.videoUrl = url;
                    found = true;
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
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
            //code to use html content here
            handlerForJavascriptInterface.post(new Runnable() {
                @Override
                public void run()
                {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    t.start();
                }});
        }
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
        finish();
        super.onPause();
    }

    public void update(){
        if(found){
            found = false;
            web.loadUrl("https://www.google.com");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(StartMovieActivity.this, VideoActivity.class));
            finish();
        }
    }
}