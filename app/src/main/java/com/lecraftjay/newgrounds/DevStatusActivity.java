package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DevStatusActivity extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_status);

        //------------------------------------------------------------

        text = findViewById(R.id.statusText);

        //------------------------------------------------------------

        text.setText(Var.devStatus);
    }
}