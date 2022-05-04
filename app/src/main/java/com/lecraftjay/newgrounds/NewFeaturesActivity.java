package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        whatsNew.setText("- watch development status");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFeaturesActivity.this, AudioActivity.class));
                finish();
            }
        });
    }
}