package com.lecraftjay.newgrounds.more_window.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.Var;
import com.lecraftjay.newgrounds.more_window.UserContentActivity;
import com.lecraftjay.newgrounds.more_window.audio.TrackActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaylistTrackActivity extends AppCompatActivity {

    TextView title;
    LinearLayout scrollLayout;
    ScrollView originalScroll;

    ArrayList<String> linkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_track);

        //------------------------------------------------------------

        title = findViewById(R.id.playlistTrackTitle);
        scrollLayout = findViewById(R.id.playlistTrackScrollLayout);
        originalScroll = findViewById(R.id.playlistTrackOriginalScroll);

        //------------------------------------------------------------

        title.setText(Var.playlistName);

        setAudioInPlaylist();
    }

    public void setAudioInPlaylist(){
        /*SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
        SharedPreferences.Editor editor = sh.edit();
        editor.putString(Var.playlistName, "null");
        editor.apply();*/

        scrollLayout.removeAllViews();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString(Var.playlistName, "null");

        if(!getter.equals("null")){
            String[] splitter = getter.split(";;;");

            for(int i = 0; i < splitter.length; i++){
                String[] split = splitter[i].split(";");
                System.out.println("jason pt: " + Arrays.toString(split));

                View view = LayoutInflater.from(PlaylistTrackActivity.this).inflate(R.layout.track_layout, null);
                //CardView card = view.findViewById(R.id.cardView);
                TextView cardText = view.findViewById(R.id.cardText);
                CircleImageView icon = view.findViewById(R.id.iconCard);
                TextView description = view.findViewById(R.id.cardDescription);
                TextView genre = view.findViewById(R.id.cardGenre);

                try {
                    cardText.setText(Html.fromHtml(trim(split[1], 28)));
                    Picasso.get().load(split[5]).into(icon);
                    description.setText(trim(split[4], 40));
                    genre.setText(split[3]);
                    icon.setTag(split[5]);

                    cardText.setTag(split[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }

                view.setTag(split[0]);
                linkList.add(split[0]);

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog alertDialog = new AlertDialog.Builder(PlaylistTrackActivity.this)
                                .setTitle("Are you sure to remove this audio from the playlist?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TextView text = v.findViewById(R.id.cardText);
                                        String toDelete = getAllInfos(v.getTag().toString());
                                        String s = getter.replace(toDelete, "");
                                        s = s.replace(";;;;;;", ";;;");
                                        if(s.charAt(0) == ';'){
                                            s = s.substring(3, s.length());
                                        }
                                        if(s.charAt(s.length()-1) == ';'){
                                            s = s.substring(0, s.length()-3);
                                        }
                                        SharedPreferences sh = getApplicationContext().getSharedPreferences("Playlist", 0);
                                        SharedPreferences.Editor editor = sh.edit();
                                        editor.putString(Var.playlistName, s);
                                        editor.apply();

                                        setAudioInPlaylist();
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
                        TextView title = v.findViewById(R.id.cardText);
                        TextView genre = v.findViewById(R.id.cardGenre);
                        TextView desc = v.findViewById(R.id.cardDescription);
                        TextView creator = v.findViewById(R.id.cardCreator);
                        ImageView icon = v.findViewById(R.id.iconCard);
                        Var.currentTitle = (String) title.getTag();
                        Var.openLink = (String) v.getTag();
                        Var.trackGenre = genre.getText().toString();
                        Var.trackDescription = desc.getText().toString();
                        Var.trackCreator = creator.getText().toString();
                        Var.trackIcon = icon.getTag().toString();

                        SharedPreferences sp = getApplicationContext().getSharedPreferences("Audio", 0);
                        String getter = sp.getString("alreadySeen", "");

                        SharedPreferences liste = getApplicationContext().getSharedPreferences("Audio", 0);
                        SharedPreferences.Editor editor = liste.edit();
                        editor.putString("alreadySeen", getter + ";;;" + Var.openLink);
                        editor.apply();

                        title.setTextColor(ContextCompat.getColor(PlaylistTrackActivity.this, R.color.audioSeen));

                        startActivity(new Intent(PlaylistTrackActivity.this, TrackActivity.class));
                    }
                });

                scrollLayout.addView(view);
            }
        }else{
            Toast.makeText(this, "no audio in playlist", Toast.LENGTH_SHORT).show();
        }

    }

    public String trim(String text, int index){
        if(text.length() > index){
            text = text.substring(0,index) + "...";
            return text;
        }
        return text;
    }

    public String getAllInfos(String link){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("Playlist", 0);
        String getter = sp.getString(Var.playlistName, "null");

        String[] splitter = getter.split(";;;");
        for(int i = 0; i < splitter.length; i++){
            String[] split = splitter[i].split(";");
            for(int j = 0; j < split.length; j++){
                if(split[0].equals(link)){
                    return splitter[i];
                }
            }
        }
        return "-;-;-;-;-;-";
    }
}