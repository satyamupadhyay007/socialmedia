package com.example.login;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class profilepage extends AppCompatActivity {

    RecyclerView recyclerPosts;
    View topBar;
    View bottomNavigation;

    ImageButton btnBack;
    ImageButton btnSettings;

    Button btnEditProfile;
    Button btnUploadPost;
    Button btnPosts;
    Button btnReels;
    Button btnTagged;

    // ================= GALLERY PICKER =================

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            Toast.makeText(
                                    profilepage.this,
                                    "Image selected",
                                    Toast.LENGTH_SHORT
                            ).show();

                            /*
                             * Next step:
                             *
                             * Firebase Storage mein image upload
                             * aur Firestore mein post save karenge.
                             */
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= EDGE TO EDGE =================

        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_profile);


        // ================= FIND VIEWS =================

        topBar = findViewById(R.id.topBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        recyclerPosts = findViewById(R.id.recyclerPosts);

        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnUploadPost = findViewById(R.id.btnUploadPost);

        btnPosts = findViewById(R.id.btnPosts);
        btnReels = findViewById(R.id.btnReels);
        btnTagged = findViewById(R.id.btnTagged);


        // ================= SYSTEM BARS =================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.profileRoot),
                (view, windowInsets) -> {

                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    // ---------------- TOP BAR ----------------
                    // Status bar ke neeche content rahega.
                    // Top bar ki original padding preserve rahegi.

                    topBar.setPadding(
                            topBar.getPaddingLeft(),
                            systemBars.top,
                            topBar.getPaddingRight(),
                            0
                    );


                    // ---------------- BOTTOM NAVIGATION ----------------
                    // Navigation bar ke upar icons rahenge.

                    bottomNavigation.setPadding(
                            bottomNavigation.getPaddingLeft(),
                            0,
                            bottomNavigation.getPaddingRight(),
                            systemBars.bottom
                    );

                    return windowInsets;
                }
        );


        // ================= POSTS RECYCLERVIEW =================

        recyclerPosts.setLayoutManager(
                new GridLayoutManager(this, 3)
        );

        /*
         * Currently user has 0 posts.
         * Isliye empty list.
         *
         * Koi Item 0, Item 1 etc. show nahi hoga.
         */

        ProfilePostAdapter adapter =
                new ProfilePostAdapter(Collections.emptyList());

        recyclerPosts.setAdapter(adapter);


        // ================= BACK =================

        btnBack.setOnClickListener(v -> {

            finish();

        });


        // ================= SETTINGS =================

        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    profilepage.this,
                    SettingsActivity.class
            );

            startActivity(intent);

        });


        // ================= EDIT PROFILE =================

        btnEditProfile.setOnClickListener(v -> {

            Toast.makeText(
                    profilepage.this,
                    "Edit Profile",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Next step:
             *
             * EditProfileActivity
             *
             * Profile Photo
             * Name
             * Username
             * Bio
             * Save
             *
             * Firebase Firestore mein save hoga.
             */
        });


        // ================= UPLOAD POST =================

        btnUploadPost.setOnClickListener(v -> {

            imagePicker.launch("image/*");

        });


        // ================= POSTS TAB =================

        btnPosts.setOnClickListener(v -> {

            recyclerPosts.setVisibility(View.VISIBLE);

            Toast.makeText(
                    profilepage.this,
                    "Posts",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= REELS TAB =================

        btnReels.setOnClickListener(v -> {

            recyclerPosts.setVisibility(View.GONE);

            Toast.makeText(
                    profilepage.this,
                    "Reels",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ================= TAGGED TAB =================

        btnTagged.setOnClickListener(v -> {

            recyclerPosts.setVisibility(View.GONE);

            Toast.makeText(
                    profilepage.this,
                    "Tagged",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }
}