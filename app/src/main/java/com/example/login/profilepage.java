package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;

public class profilepage extends AppCompatActivity {

    // ==================================================
    // UI
    // ==================================================

    private RecyclerView recyclerPosts;

    private View topBar;
    private View bottomNavigation;

    private ImageButton btnBack;
    private ImageButton btnSettings;

    private Button btnEditProfile;
    private Button btnUploadPost;

    private Button btnPosts;
    private Button btnReels;
    private Button btnTagged;

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    private ImageView imgProfile;

    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBio;

    private TextView tvPostCount;
    private TextView tvFollowerCount;
    private TextView tvFollowingCount;


    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    // ==================================================
    // CURRENT USER
    // ==================================================

    private String currentUserId;


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_profile
        );


        // ==================================================
        // FIREBASE
        // ==================================================

        mAuth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();


        if (
                mAuth.getCurrentUser() == null
        ) {

            finish();
            return;
        }


        currentUserId =
                mAuth.getCurrentUser()
                        .getUid();


        // ==================================================
        // FIND VIEWS
        // ==================================================

        topBar =
                findViewById(
                        R.id.topBar
                );

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );

        recyclerPosts =
                findViewById(
                        R.id.recyclerPosts
                );


        btnBack =
                findViewById(
                        R.id.btnBack
                );

        btnSettings =
                findViewById(
                        R.id.btnSettings
                );


        btnEditProfile =
                findViewById(
                        R.id.btnEditProfile
                );

        btnUploadPost =
                findViewById(
                        R.id.btnUploadPost
                );


        btnPosts =
                findViewById(
                        R.id.btnPosts
                );

        btnReels =
                findViewById(
                        R.id.btnReels
                );

        btnTagged =
                findViewById(
                        R.id.btnTagged
                );


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


        imgProfile =
                findViewById(
                        R.id.imgProfile
                );

        tvName =
                findViewById(
                        R.id.tvName
                );

        tvUsername =
                findViewById(
                        R.id.tvUsername
                );

        tvBio =
                findViewById(
                        R.id.tvBio
                );


        tvPostCount =
                findViewById(
                        R.id.tvPostCount
                );

        tvFollowerCount =
                findViewById(
                        R.id.tvFollowerCount
                );

        tvFollowingCount =
                findViewById(
                        R.id.tvFollowingCount
                );


        // ==================================================
        // POSTS
        // ==================================================

        recyclerPosts.setLayoutManager(
                new GridLayoutManager(
                        this,
                        3
                )
        );


        ProfilePostAdapter adapter =
                new ProfilePostAdapter(
                        Collections.emptyList()
                );


        recyclerPosts.setAdapter(
                adapter
        );


        // ==================================================
        // SYSTEM BARS
        // ==================================================

        View root =
                findViewById(
                        R.id.profileRoot
                );


        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets systemBars =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );


                    if (topBar != null) {

                        ViewGroup.LayoutParams
                                params =
                                topBar.getLayoutParams();


                        if (
                                params instanceof
                                        ViewGroup.MarginLayoutParams
                        ) {

                            ViewGroup.MarginLayoutParams
                                    marginParams =
                                    (ViewGroup.MarginLayoutParams)
                                            params;


                            marginParams.topMargin =
                                    systemBars.top;


                            topBar.setLayoutParams(
                                    marginParams
                            );
                        }
                    }


                    if (bottomNavigation != null) {

                        ViewGroup.LayoutParams
                                params =
                                bottomNavigation.getLayoutParams();


                        if (
                                params instanceof
                                        ViewGroup.MarginLayoutParams
                        ) {

                            ViewGroup.MarginLayoutParams
                                    marginParams =
                                    (ViewGroup.MarginLayoutParams)
                                            params;


                            marginParams.bottomMargin =
                                    systemBars.bottom;


                            bottomNavigation.setLayoutParams(
                                    marginParams
                            );
                        }
                    }


                    return windowInsets;
                }
        );


        // ==================================================
        // LOAD PROFILE
        // ==================================================

        loadProfile();


        // ==================================================
        // BACK
        // ==================================================

        btnBack.setOnClickListener(
                v -> finish()
        );


        // ==================================================
        // SETTINGS
        // ==================================================

        btnSettings.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    SettingsActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ==================================================
        // EDIT PROFILE
        // ==================================================

        btnEditProfile.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    EditProfileActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ==================================================
        // UPLOAD POST
        // ==================================================

        btnUploadPost.setOnClickListener(
                v -> {

                    Toast.makeText(
                            profilepage.this,
                            "Post upload will be added next",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );


        // ==================================================
        // POSTS TAB
        // ==================================================

        btnPosts.setOnClickListener(
                v -> {

                    recyclerPosts.setVisibility(
                            View.VISIBLE
                    );

                    Toast.makeText(
                            profilepage.this,
                            "Posts",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );


        // ==================================================
        // REELS TAB
        // ==================================================

        btnReels.setOnClickListener(
                v -> {

                    recyclerPosts.setVisibility(
                            View.GONE
                    );

                    Toast.makeText(
                            profilepage.this,
                            "Reels",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );


        // ==================================================
        // TAGGED TAB
        // ==================================================

        btnTagged.setOnClickListener(
                v -> {

                    recyclerPosts.setVisibility(
                            View.GONE
                    );

                    Toast.makeText(
                            profilepage.this,
                            "Tagged",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );


        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();
    }


    // ==================================================
    // LOAD PROFILE
    // ==================================================

    private void loadProfile() {

        if (
                currentUserId == null ||
                        currentUserId.isEmpty()
        ) {
            return;
        }


        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (
                                    !document.exists()
                            ) {
                                return;
                            }


                            // ==================================================
                            // USER ID
                            // ==================================================

                            String userId =
                                    document.getString(
                                            "userId"
                                    );


                            if (
                                    userId != null &&
                                            !userId.isEmpty()
                            ) {

                                tvUsername.setText(
                                        "@" + userId
                                );
                            } else {

                                tvUsername.setText(
                                        ""
                                );
                            }


                            // ==================================================
                            // NAME
                            // ==================================================

                            String name =
                                    document.getString(
                                            "name"
                                    );


                            if (
                                    name != null &&
                                            !name.trim().isEmpty()
                            ) {

                                tvName.setText(
                                        name
                                );

                                tvName.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                tvName.setText(
                                        ""
                                );

                                tvName.setVisibility(
                                        View.GONE
                                );
                            }


                            // ==================================================
                            // BIO
                            // ==================================================

                            String bio =
                                    document.getString(
                                            "bio"
                                    );


                            if (
                                    bio != null &&
                                            !bio.trim().isEmpty()
                            ) {

                                tvBio.setText(
                                        bio
                                );

                                tvBio.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                tvBio.setText(
                                        ""
                                );

                                tvBio.setVisibility(
                                        View.GONE
                                );
                            }


                            // ==================================================
                            // PROFILE PHOTO
                            // ==================================================

                            String profileImage =
                                    document.getString(
                                            "profileImage"
                                    );


                            if (
                                    profileImage != null &&
                                            !profileImage.isEmpty()
                            ) {

                                Glide.with(
                                                profilepage.this
                                        )
                                        .load(
                                                profileImage
                                        )
                                        .centerCrop()
                                        .into(
                                                imgProfile
                                        );
                            }


                            // ==================================================
                            // COUNTS
                            // ==================================================

                            Long postCount =
                                    document.getLong(
                                            "postCount"
                                    );

                            Long followerCount =
                                    document.getLong(
                                            "followerCount"
                                    );

                            Long followingCount =
                                    document.getLong(
                                            "followingCount"
                                    );


                            tvPostCount.setText(
                                    String.valueOf(
                                            postCount != null
                                                    ? postCount
                                                    : 0
                                    )
                            );


                            tvFollowerCount.setText(
                                    String.valueOf(
                                            followerCount != null
                                                    ? followerCount
                                                    : 0
                                    )
                            );


                            tvFollowingCount.setText(
                                    String.valueOf(
                                            followingCount != null
                                                    ? followingCount
                                                    : 0
                                    )
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        profilepage.this,
                                        "Could not load profile",
                                        Toast.LENGTH_SHORT
                                ).show()
                );
    }


    // ==================================================
    // REFRESH PROFILE
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (
                mAuth != null &&
                        mAuth.getCurrentUser() != null
        ) {

            currentUserId =
                    mAuth.getCurrentUser()
                            .getUid();

            loadProfile();
        }
    }


    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        // ==================================================
        // HOME
        // ==================================================

        btnHome.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    homepage.class
                            );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );


        // ==================================================
        // CREATE
        // ==================================================

        btnCreate.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    frontpage.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ==================================================
        // MESSAGE
        // ==================================================

        btnMessage.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    ChatListActivity.class
                            );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );


        // ==================================================
        // SEARCH
        // ==================================================

        btnSearch.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    Search.class
                            );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );


        // ==================================================
        // PROFILE
        // ==================================================

        btnProfile.setOnClickListener(
                v -> {
                    // Already on Profile
                }
        );
    }
}