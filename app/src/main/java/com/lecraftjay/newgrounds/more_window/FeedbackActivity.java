package com.lecraftjay.newgrounds.more_window;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecraftjay.newgrounds.R;
import com.lecraftjay.newgrounds.classes.SendMailTask;
import com.lecraftjay.newgrounds.classes.Var;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
    }
}