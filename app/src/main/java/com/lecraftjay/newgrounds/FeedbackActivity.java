package com.lecraftjay.newgrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class FeedbackActivity extends AppCompatActivity {

    Button send;
    EditText content;
    EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //-----------------------------------------------------------------

        send = findViewById(R.id.feedbackSend);
        content = findViewById(R.id.emailContent);
        userEmail = findViewById(R.id.userEmail);

        //-----------------------------------------------------------------

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emails = {"jm.enns04@gmail.com"};
                String text = "Von: " + userEmail.getText() + " Inhalt: " + content.getText();
                new SendMailTask(FeedbackActivity.this).execute("mail.sender.je@gmail.com", Var.pass, Arrays.asList(emails), "Bug/Feedback", text);
            }
        });
    }
}