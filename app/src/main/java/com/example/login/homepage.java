package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class homepage extends AppCompatActivity {

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_homepage
        );

        // ==================================================
        // SAFE AREA / STATUS BAR
        // ==================================================

        View root =
                findViewById(
                        R.id.appContainer
                );

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );


        // ==================================================
        // CONNECT BUTTONS
        // ==================================================

        btnHome =
                findViewById(
                        R.id.btnHome
                );

        btnCreate =
                findViewById(
                        R.id.btnCreate
                );

        btnMessage =
                findViewById(
                        R.id.btnMessage
                );

        btnSearch =
                findViewById(
                        R.id.btnSearch
                );

        btnProfile =
                findViewById(
                        R.id.btnProfile
                );


        // ==================================================
        // HOME
        // ==================================================

        btnHome.setOnClickListener(
                v -> {
                    // Already on Home
                }
        );


        // ==================================================
        // CREATE
        // ==================================================

        btnCreate.setOnClickListener(
                v -> {
                    // Create screen later
                }
        );


        // ==================================================
        // CHAT
        // ==================================================

        btnMessage.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    homepage.this,
                                    ChatListActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ==================================================
        // SEARCH
        // ==================================================

        btnSearch.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    homepage.this,
                                    Search.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ==================================================
        // PROFILE
        // ==================================================

        btnProfile.setOnClickListener(
                v -> {
                    // Profile screen later
                }
        );
    }
}