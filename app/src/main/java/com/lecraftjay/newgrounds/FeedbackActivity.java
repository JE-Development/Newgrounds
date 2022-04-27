package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;

public class FeedbackActivity extends AppCompatActivity {

    Button send;
    EditText content;
    EditText userEmail;
    TextView closed;
    TextView reason;
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //-----------------------------------------------------------------

        send = findViewById(R.id.feedbackSend);
        content = findViewById(R.id.emailContent);
        userEmail = findViewById(R.id.userEmail);
        closed = findViewById(R.id.feedbackService);
        reason = findViewById(R.id.feedbackReason);
        root = findViewById(R.id.feedbackRoot);

        //-----------------------------------------------------------------

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emails = {Var.toEmail};
                String text = "Von: " + userEmail.getText() + " Inhalt: " + content.getText();
                new SendMailTask(FeedbackActivity.this).execute("mail.sender.je@gmail.com", Var.pass, Arrays.asList(emails), "Bug/Feedback", text);
            }
        });

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AppControl");
        query.whereEqualTo("ControlName", "Feedback");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject control, ParseException e) {
                if (e == null) {
                    boolean stopService = control.getBoolean("StopService");
                    String reas = control.getString("Reason");
                    if(stopService){
                        closed.setVisibility(View.VISIBLE);
                        reason.setVisibility(View.VISIBLE);
                        reason.setText("Reason: " + reas);
                        root.removeAllViews();
                    }
                } else {
                    Toast.makeText(FeedbackActivity.this, "No Connection to BackEnd", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}