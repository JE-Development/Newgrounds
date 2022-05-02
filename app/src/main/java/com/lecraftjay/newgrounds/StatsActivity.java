package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class StatsActivity extends AppCompatActivity {

    Switch stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //-------------------------------------------------------

        stat = findViewById(R.id.statSwitch);

        //-------------------------------------------------------

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Settings", 0);
        String getter = sp.getString("collectStats", "");
        if(getter.equals("true")) {
            stat.setChecked(true);
        }else{
            stat.setChecked(false);
        }

        boolean checked = stat.isChecked();

        if(checked){
            SharedPreferences liste = getApplicationContext().getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = liste.edit();
            editor.putString("collectStats", "true");
            editor.apply();
        }else{
            SharedPreferences liste = getApplicationContext().getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = liste.edit();
            editor.putString("collectStats", "false");
            editor.apply();
        }

        stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat.isChecked()){
                    SharedPreferences liste = getApplicationContext().getSharedPreferences("Settings", 0);
                    SharedPreferences.Editor editor = liste.edit();
                    editor.putString("collectStats", "true");
                    editor.apply();
                }else{
                    SharedPreferences liste = getApplicationContext().getSharedPreferences("Settings", 0);
                    SharedPreferences.Editor editor = liste.edit();
                    editor.putString("collectStats", "false");
                    editor.apply();
                }
            }
        });

    }
}