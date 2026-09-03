package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class profilepage extends AppCompatActivity {

    // ==================================================
    // PROFILE
    // ==================================================

    private ImageButton btnBack;
    private ImageButton btnSettings;

    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBio;

    private Button btnEditProfile;

    // ==================================================
    // TABS
    // ==================================================

    private TextView tabPosts;
    private TextView tabReels;
    private TextView tabTagged;

    // ==================================================
    // POSTS
    // ==================================================

    private RecyclerView recyclerPosts;

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    // ==================================================
    // ROOT
    // ==================================================

    private View profileRoot;
    private View bottomNavigation;

    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_profile
        );

        // ==================================================
        // FIND PROFILE VIEWS
        // ==================================================

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        btnSettings =
                findViewById(
                        R.id.btnSettings
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

        btnEditProfile =
                findViewById(
                        R.id.btnEditProfile
                );

        // ==================================================
        // TABS
        // ==================================================

        tabPosts =
                findViewById(
                        R.id.tabPosts
                );

        tabReels =
                findViewById(
                        R.id.tabReels
                );

        tabTagged =
                findViewById(
                        R.id.tabTagged
                );

        // ==================================================
        // POSTS
        // ==================================================

        recyclerPosts =
                findViewById(
                        R.id.recyclerPosts
                );

        // ==================================================
        // BOTTOM NAVIGATION
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

        profileRoot =
                findViewById(
                        R.id.profileRoot
                );

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
                );

        // ==================================================
        // POST GRID
        // ==================================================

        recyclerPosts.setLayoutManager(
                new GridLayoutManager(
                        this,
                        3
                )
        );

        recyclerPosts.setHasFixedSize(
                true
        );

        // ==================================================
        // CURRENT STATIC POSTS
        // ==================================================

        List<Integer> images =
                Arrays.asList(
                        R.drawable.post_1,
                        R.drawable.post_2,
                        R.drawable.post_3,
                        R.drawable.post_4,
                        R.drawable.post_5,
                        R.drawable.post_6,
                        R.drawable.post_7,
                        R.drawable.post_8,
                        R.drawable.post_9
                );

        ProfilePostAdapter adapter =
                new ProfilePostAdapter(
                        images
                );

        recyclerPosts.setAdapter(
                adapter
        );

        // ==================================================
        // BACK
        // ==================================================

        btnBack.setOnClickListener(
                v -> {

                    hideKeyboard();

                    finish();
                }
        );

        // ==================================================
        // SETTINGS
        // ==================================================

        btnSettings.setOnClickListener(
                v -> showSettings()
        );

        // ==================================================
        // EDIT PROFILE
        // ==================================================

        btnEditProfile.setOnClickListener(
                v -> showEditProfile()
        );

        // ==================================================
        // TABS
        // ==================================================

        setupTabs();

        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();

        // ==================================================
        // SAFE AREA
        // ==================================================

        setupWindowInsets();
    }

    // ==================================================
    // TABS
    // ==================================================

    private void setupTabs() {

        tabPosts.setOnClickListener(
                v -> {

                    selectTab(
                            tabPosts,
                            tabReels,
                            tabTagged
                    );

                    recyclerPosts.setVisibility(
                            View.VISIBLE
                    );
                }
        );

        tabReels.setOnClickListener(
                v -> {

                    selectTab(
                            tabReels,
                            tabPosts,
                            tabTagged
                    );

                    Toast.makeText(
                            this,
                            "Reels section coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        tabTagged.setOnClickListener(
                v -> {

                    selectTab(
                            tabTagged,
                            tabPosts,
                            tabReels
                    );

                    Toast.makeText(
                            this,
                            "Tagged section coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        selectTab(
                tabPosts,
                tabReels,
                tabTagged
        );
    }

    // ==================================================
    // TAB SELECT
    // ==================================================

    private void selectTab(
            TextView selected,
            TextView otherOne,
            TextView otherTwo
    ) {

        selected.setTextColor(
                0xFF111111
        );

        selected.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        otherOne.setTextColor(
                0xFF777777
        );

        otherOne.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );

        otherTwo.setTextColor(
                0xFF777777
        );

        otherTwo.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );
    }

    // ==================================================
    // SETTINGS
    // ==================================================

    private void showSettings() {

        String[] items =
                {
                        "Account Settings",
                        "Privacy",
                        "Notifications",
                        "Cancel"
                };

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Settings"
                )
                .setItems(
                        items,
                        (dialog, which) -> {

                            if (which == 0) {

                                Toast.makeText(
                                        this,
                                        "Account Settings coming soon",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else if (which == 1) {

                                Toast.makeText(
                                        this,
                                        "Privacy settings coming soon",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else if (which == 2) {

                                Toast.makeText(
                                        this,
                                        "Notification settings coming soon",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // ==================================================
    // EDIT PROFILE
    // ==================================================

    private void showEditProfile() {

        TextView message =
                new TextView(this);

        message.setText(
                "Edit Profile functionality can be connected to Firebase here."
        );

        message.setTextColor(
                0xFF111111
        );

        message.setTextSize(
                15
        );

        message.setPadding(
                dp(24),
                dp(20),
                dp(24),
                dp(10)
        );

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Edit Profile"
                )
                .setView(
                        message
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        // ==================================================
        // HOME
        // ==================================================

        btnHome.setOnClickListener(
                v -> openActivity(
                        homepage.class
                )
        );

        // ==================================================
        // CREATE
        // ==================================================

        btnCreate.setOnClickListener(
                v -> openActivity(
                        frontpage.class
                )
        );

        // ==================================================
        // MESSAGE
        // ==================================================

        btnMessage.setOnClickListener(
                v -> openActivity(
                        ChatListActivity.class
                )
        );

        // ==================================================
        // SEARCH
        // ==================================================

        btnSearch.setOnClickListener(
                v -> openActivity(
                        Search.class
                )
        );

        // ==================================================
        // PROFILE
        // ==================================================

        btnProfile.setOnClickListener(
                v -> {

                    // Already on profile.
                    // Do nothing.
                }
        );
    }

    // ==================================================
    // OPEN ACTIVITY
    // ==================================================

    private void openActivity(
            Class<?> targetActivity
    ) {

        hideKeyboard();

        Intent intent =
                new Intent(
                        profilepage.this,
                        targetActivity
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(
                intent
        );

        overridePendingTransition(
                0,
                0
        );
    }

    // ==================================================
    // WINDOW INSETS
    // ==================================================

    private void setupWindowInsets() {

        if (
                profileRoot == null ||
                        bottomNavigation == null
        ) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                profileRoot,
                (view, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type
                                            .systemBars()
                            );

                    Insets cutout =
                            insets.getInsets(
                                    WindowInsetsCompat.Type
                                            .displayCutout()
                            );

                    int safeTop =
                            Math.max(
                                    systemBars.top,
                                    cutout.top
                            );

                    int safeBottom =
                            Math.max(
                                    systemBars.bottom,
                                    cutout.bottom
                            );

                    // ==================================================
                    // TOP SAFE AREA
                    // ==================================================

                    view.setPadding(
                            0,
                            safeTop,
                            0,
                            0
                    );

                    // ==================================================
                    // BOTTOM SAFE AREA
                    // ==================================================

                    ViewGroup.MarginLayoutParams
                            navParams =
                            (ViewGroup.MarginLayoutParams)
                                    bottomNavigation
                                            .getLayoutParams();

                    navParams.bottomMargin =
                            safeBottom;

                    bottomNavigation.setLayoutParams(
                            navParams
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(
                profileRoot
        );
    }

    // ==================================================
    // HIDE KEYBOARD
    // ==================================================

    private void hideKeyboard() {

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        View currentFocus =
                getCurrentFocus();

        if (
                imm != null &&
                        currentFocus != null
        ) {

            imm.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(),
                    0
            );
        }
    }

    // ==================================================
    // DP
    // ==================================================

    private int dp(
            int value
    ) {

        return (int)
                (
                        value *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );
    }
}