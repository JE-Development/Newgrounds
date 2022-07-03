package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.more_window.profile.FeedActivity;

public class TesterActivity extends AppCompatActivity {

    WebView web;
    Button button;

    String h;

    Handler handlerForJavascriptInterface = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        //-------------------------------------------------------------------

        web = findViewById(R.id.testWeb);
        button = findViewById(R.id.testButton);

        //-------------------------------------------------------------------

        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new TesterActivity.MyJavaScriptInterface(TesterActivity.this), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
                                  @Override
                                  public void onPageFinished(WebView view, String url) {
                                      web.loadUrl("javascript:window.HtmlViewer.showHTML('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");
                                  }
                              }
        );
        web.loadUrl("https://www.google.com");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public class MyJavaScriptInterface {
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
                            System.out.println("-----------------------------------------------------------------------");
                            System.out.println("jason html: " + h);
                            System.out.println("-----------------------------------------------------------------------");
                        }
                    });
                    t.start();
                }});
        }
    }
}