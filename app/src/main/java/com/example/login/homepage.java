package com.example.login;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class homepage extends AppCompatActivity {

    TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        usernameText = findViewById(R.id.usernameText);

        String username = getIntent().getStringExtra("username");

        if (username != null && !username.isEmpty()) {
            usernameText.setText("Welcome, " + username + "!");
        } else {
            usernameText.setText("Welcome!");
        }
    }
}