package com.lecraftjay.newgrounds.more_window.infos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

public class StartInfoActivity extends AppCompatActivity {

    TextView title;
    TextView text;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_info);

        //-----------------------------------------------------------------

        title = findViewById(R.id.startInfoTitle);
        text = findViewById(R.id.startInfoText);
        ok = findViewById(R.id.startInfoOk);

        //-----------------------------------------------------------------

        title.setText(Var.startInfoTitle);
        text.setText(Var.startInfoText);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartInfoActivity.this, AudioActivity.class));
                finish();
            }
        });
    }
}