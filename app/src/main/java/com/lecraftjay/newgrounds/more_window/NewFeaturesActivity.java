package com.lecraftjay.newgrounds.more_window;

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

        whatsNew.setText("- there is now a user window. When you click on a user (in the track or art window) a window will open where are all information about the user");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFeaturesActivity.this, AudioActivity.class));
                finish();
            }
        });
    }
}