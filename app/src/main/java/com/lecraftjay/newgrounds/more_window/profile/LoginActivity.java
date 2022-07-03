package com.lecraftjay.newgrounds.more_window.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ironsource.mediationsdk.IronSource;
import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;

public class LoginActivity extends AppCompatActivity {

    WebView login;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //----------------------------------------------------------------------

        login = findViewById(R.id.loginWeb);
        layout = findViewById(R.id.loginLayout);

        //----------------------------------------------------------------------

        login.getSettings().setJavaScriptEnabled(true);
        login.getSettings().setDomStorageEnabled(true);
        if(Var.isLogin) {
            login.loadUrl("https://www.newgrounds.com/passport");
        }else{
            TextView t = new TextView(this);
            t.setText("You have to manually log out. Swipe to the left and click on your profile picture. Than you can see the \"Log Out\" button.");
            t.setTextColor(getResources().getColor(R.color.orange));
            layout.addView(t,0);
            login.loadUrl("https://www.newgrounds.com");
        }
    }

}