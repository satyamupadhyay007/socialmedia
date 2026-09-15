package com.example.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Button btnAccount;
    Button btnPrivacy;
    Button btnSecurity;
    Button btnNotifications;
    Button btnHelp;
    Button btnAbout;
    Button btnLogout;

    ImageButton btnBackSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);


        // ================= FIND VIEWS =================

        btnAccount = findViewById(R.id.btnAccount);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnSecurity = findViewById(R.id.btnSecurity);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnHelp = findViewById(R.id.btnHelp);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);

        // Back button is ImageButton
        btnBackSettings = findViewById(R.id.btnBackSettings);


        // ================= BACK =================

        btnBackSettings.setOnClickListener(v -> finish());


        // ================= ACCOUNT =================

        btnAccount.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Account settings",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= PRIVACY =================

        btnPrivacy.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Privacy settings",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= SECURITY =================

        btnSecurity.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Security settings",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= NOTIFICATIONS =================

        btnNotifications.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Notification settings",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= HELP =================

        btnHelp.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Help & Support",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= ABOUT =================

        btnAbout.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "About SocialMedia App",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= LOGOUT =================

        btnLogout.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Logged out",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Abhi tumhara login system Firebase Authentication
             * se connected nahi hai.
             *
             * Firebase Authentication connect karne ke baad:
             *
             * FirebaseAuth.getInstance().signOut();
             *
             * yahan add karenge.
             */

            finishAffinity();

        });
    }
}