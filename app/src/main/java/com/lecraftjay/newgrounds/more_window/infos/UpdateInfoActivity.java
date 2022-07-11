package com.lecraftjay.newgrounds.more_window.infos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

public class UpdateInfoActivity extends AppCompatActivity {

    TextView title;
    TextView text;
    Button ok;
    Button playstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        //----------------------------------------------------------------------

        title = findViewById(R.id.updateInfoTitle);
        text = findViewById(R.id.updateInfoText);
        ok = findViewById(R.id.updateInfoOk);
        playstore = findViewById(R.id.updateInfoPlaystore);

        //----------------------------------------------------------------------

        title.setText(Var.updateInfoTitle);
        text.setText(Var.updateInfoText);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateInfoActivity.this, AudioActivity.class));
                finish();
            }
        });

        playstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Var.updateInfoPlaystoreLink); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                finish();
            }
        });
    }
}