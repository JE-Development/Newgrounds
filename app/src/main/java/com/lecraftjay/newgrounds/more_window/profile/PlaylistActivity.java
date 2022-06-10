package com.lecraftjay.newgrounds.more_window.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.lecraftjay.newgrounds.nav_window.AudioActivity;

public class PlaylistActivity extends AppCompatActivity {

    Button newPlaylist;
    LinearLayout content;
    Button priority;

    Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //--------------------------------------------------

        newPlaylist = findViewById(R.id.playlistNew);
        content = findViewById(R.id.playlistContent);
        priority = findViewById(R.id.playlistPriority);

        //--------------------------------------------------

        popup = new Dialog(this);

        setContent();

        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                String getter = sp.getString("allPlaylist", "null");
                String[] split = getter.split(";;;");

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaylistActivity.this);
                builder.setTitle("Set priority (The playlist with priority loads fasster)");
                builder.setItems(split, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String z = split[which];

                        SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("priority", z);
                        editor.apply();

                        priority.setText("Priority: " + z);

                    }
                });
                builder.show();
            }
        });

        newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setContentView(R.layout.playlist_popup_layout);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();

                TextView close = popup.findViewById(R.id.popupCloseText);
                EditText nameEdit = popup.findViewById(R.id.playlistEditText);
                Button add = popup.findViewById(R.id.playlistAdd);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sha = getApplicationContext().getSharedPreferences("Playlist", 0);
                        String g = sha.getString("allPlaylist", "null");

                        String name = nameEdit.getText().toString();
                        String tester = name;
                        boolean valide = false;
                        while (tester.contains("  ")){
                            tester = tester.replace("  ", " ");
                        }
                        if(!tester.equals(" ")){
                            tester = name;
                            if(!tester.contains(";")){
                                if(!tester.equals("")){
                                    if(tester.length() <= 20){
                                        if(!g.contains(tester)) {
                                            valide = true;
                                        }else{
                                            Toast.makeText(PlaylistActivity.this, "Playlist with this name already exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(PlaylistActivity.this, "The playlist name ist too long (20 characters allowed)", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PlaylistActivity.this, "The text field has no character", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(PlaylistActivity.this, "\";\" (semicolon) is not allowed to use", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(PlaylistActivity.this, "You can't use space as name", Toast.LENGTH_SHORT).show();
                        }

                        if(valide){
                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
                            String getter = sp.getString("allPlaylist", "null");
                            String fin = "";
                            if(getter.equals("null") || getter.equals("")){
                                fin = name;
                            }else {
                                fin = getter + ";;;" + name;
                            }
                            SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putString("allPlaylist", fin);
                            editor.apply();

                            setContent();

                            popup.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void setContent(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString("allPlaylist", "null");

        content.removeAllViews();

        if(!getter.equals("null") && !getter.equals("")){
            priority.setEnabled(true);
            String[] split = getter.split(";;;");
            for(int i = 0; i < split.length; i++){
                View view = LayoutInflater.from(PlaylistActivity.this).inflate(R.layout.playlist_card_layout, null);
                TextView name = view.findViewById(R.id.playlistName);
                name.setText(split[i]);
                if(i == 0){
                    SharedPreferences shared = getApplicationContext().getSharedPreferences("Playlist", 0);
                    String pri = shared.getString("priority", "null");
                    if(pri.equals("null")) {

                        priority.setText("Priority: " + split[i]);

                        SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("priority", split[i]);
                        editor.apply();
                    }else{
                        priority.setText(pri);
                    }
                }

                SharedPreferences shared = getApplicationContext().getSharedPreferences("Playlist", 0);
                String get = shared.getString(split[i], "null");
                if(!get.equals("null")){
                    String[] spl = get.split(";;;");
                    String le = String.valueOf(spl.length);
                    TextView count = view.findViewById(R.id.playlistCount);
                    count.setText(le);
                }

                content.addView(view);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PlaylistActivity.this)
                                .setTitle("Are you sure to delete this playlist?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TextView text = v.findViewById(R.id.playlistName);
                                        String s = getter.replace(text.getText().toString(), "");
                                        s = s.replace(";;;;;;", ";;;");
                                        if(!s.equals("")) {
                                            if (s.charAt(0) == ';') {
                                                s = s.substring(3, s.length());
                                            }
                                            if (s.charAt(s.length() - 1) == ';') {
                                                s = s.substring(0, s.length() - 3);
                                            }
                                        }
                                        SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                        SharedPreferences.Editor editor = sh.edit();
                                        editor.putString("allPlaylist", s);
                                        editor.apply();

                                        setContent();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                        return true;
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView name = v.findViewById(R.id.playlistName);
                        String s = name.getText().toString();
                        Var.playlistName = s;
                        startActivity(new Intent(PlaylistActivity.this, PlaylistTrackActivity.class));
                    }
                });
            }
        }
    }
}