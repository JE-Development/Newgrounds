package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;

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