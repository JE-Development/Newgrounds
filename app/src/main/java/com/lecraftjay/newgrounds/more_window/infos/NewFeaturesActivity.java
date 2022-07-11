package com.lecraftjay.newgrounds.more_window.infos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

public class NewFeaturesActivity extends AppCompatActivity {

    TextView whatsNew;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_features);

        //----------------------------------------------------

        whatsNew = findViewById(R.id.newFeaturesText);
        ok = findViewById(R.id.newOk);

        //----------------------------------------------------

        whatsNew.setText("- Movies can now be played in this app\n- You can now login in this app to see your \"Your Feed\" " +
                        "content (only audio available, more content comming soon). To find it go to the window \"Profile\" and then " +
                        "you will find the \"Your Feed\" button but you have to be logged in in this app. You can login in the \"Profile\" window.");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFeaturesActivity.this, AudioActivity.class));
                finish();
            }
        });
    }
}