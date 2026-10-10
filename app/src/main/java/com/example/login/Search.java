 package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Search extends AppCompatActivity {

    // ==================================================
    // SEARCH UI
    // ==================================================

    private EditText searchEditText;

    private RecyclerView searchRecyclerView;

    private RecyclerView postRecyclerView;


    // ==================================================
    // ADAPTERS
    // ==================================================

    private SearchAdapter searchAdapter;

    private ProfilePostAdapter postAdapter;


    // ==================================================
    // DATA
    // ==================================================

    private final List<User> allUsers =
            new ArrayList<>();

    private final List<Post> postList =
            new ArrayList<>();


    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseFirestore db;


    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private ImageButton btnHome;

    private ImageButton btnCreate;

    private ImageButton btnMessage;

    private ImageButton btnSearch;

    private ImageButton btnProfile;


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        EdgeToEdge.enable(this);

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_search
        );

        View searchRoot = findViewById(R.id.searchRoot);
        if (searchRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(searchRoot, (view, insets) -> {
                Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout()
                );
                view.setPadding(
                        bars.left,
                        bars.top,
                        bars.right,
                        bars.bottom
                );
                return insets;
            });
        }


        // ==================================================
        // FIND SEARCH UI
        // ==================================================

        searchEditText =
                findViewById(
                        R.id.searchEditText
                );

        searchRecyclerView =
                findViewById(
                        R.id.searchRecyclerView
                );

        postRecyclerView =
                findViewById(
                        R.id.postRecyclerView
                );


        // ==================================================
        // FIND BOTTOM NAVIGATION
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
        // FIREBASE
        // ==================================================

        db =
                FirebaseFirestore.getInstance();


        // ==================================================
        // SEARCH RESULT RECYCLER
        // ==================================================

        searchRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        searchAdapter =
                new SearchAdapter(
                        new ArrayList<>()
                );


        searchRecyclerView.setAdapter(
                searchAdapter
        );


        searchRecyclerView.setVisibility(
                View.GONE
        );


        // ==================================================
        // POSTS RECYCLER
        // ==================================================

        postRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        3
                )
        );


        postAdapter =
                new ProfilePostAdapter(
                        postList
                );


        postRecyclerView.setAdapter(
                postAdapter
        );


        // ==================================================
        // LOAD USERS
        // ==================================================

        loadUsers();


        // ==================================================
        // LOAD POSTS
        // ==================================================

        loadPosts();


        // ==================================================
        // SEARCH TEXT LISTENER
        // ==================================================

        searchEditText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterUsers(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );


        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();
    }




    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private void setupBottomNavigation() {

        // ==================================================
        // HOME
        // ==================================================

        if (btnHome != null) {

            btnHome.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                homepage.class
                        );
                    }
            );
        }


        // ==================================================
        // CREATE
        // ==================================================

        if (btnCreate != null) {

            btnCreate.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                ReelsActivity.class
                        );
                    }
            );
        }


        // ==================================================
        // MESSAGE
        // ==================================================

        if (btnMessage != null) {

            btnMessage.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                ChatListActivity.class
                        );
                    }
            );
        }


        // ==================================================
        // SEARCH
        // ==================================================

        if (btnSearch != null) {

            btnSearch.setOnClickListener(
                    v -> {

                        // Already on Search.
                    }
            );
        }


        // ==================================================
        // PROFILE
        // ==================================================

        if (btnProfile != null) {

            btnProfile.setOnClickListener(
                    v -> {

                        hideKeyboard();

                        openActivity(
                                profilepage.class
                        );
                    }
            );
        }
    }


    // ==================================================
    // OPEN ACTIVITY
    // ==================================================

    private void openActivity(
            Class<?> targetActivity
    ) {

        Intent intent =
                new Intent(
                        Search.this,
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
    // LOAD USERS
    // ==================================================

    private void loadUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            allUsers.clear();


                            for (
                                    DocumentSnapshot document :
                                    queryDocumentSnapshots
                            ) {

                                // ==================================================
                                // UID
                                // ==================================================

                                String uid =
                                        document.getId();


                                // ==================================================
                                // OLD USERNAME FIELD
                                // ==================================================

                                String username =
                                        document.getString(
                                                "username"
                                        );


                                // ==================================================
                                // USER ID
                                // ==================================================

                                String userId =
                                        document.getString(
                                                "userId"
                                        );


                                // ==================================================
                                // UNIQUE USER ID
                                // ==================================================

                                String uniqueUserId =
                                        document.getString(
                                                "uniqueUserId"
                                        );


                                // ==================================================
                                // DISPLAY NAME / REAL NAME
                                // ==================================================

                                String name =
                                        document.getString(
                                                "name"
                                        );


                                // ==================================================
                                // PROFILE IMAGE
                                // ==================================================

                                String profileImage =
                                        document.getString(
                                                "profileImage"
                                        );


                                // ==================================================
                                // DISPLAY USER ID
                                //
                                // Priority:
                                // userId
                                // uniqueUserId
                                // username
                                // ==================================================

                                String displayName =
                                        null;


                                if (
                                        userId != null &&
                                                !userId.isEmpty()
                                ) {

                                    displayName =
                                            userId;

                                } else if (
                                        uniqueUserId != null &&
                                                !uniqueUserId.isEmpty()
                                ) {

                                    displayName =
                                            uniqueUserId;

                                } else if (
                                        username != null &&
                                                !username.isEmpty()
                                ) {

                                    displayName =
                                            username;
                                }


                                // ==================================================
                                // NO USER ID
                                // ==================================================

                                if (
                                        displayName == null ||
                                                displayName.isEmpty()
                                ) {

                                    continue;
                                }


                                // ==================================================
                                // ADD USER
                                //
                                // UID
                                // User ID
                                // Name
                                // Profile Photo
                                // ==================================================

                                allUsers.add(
                                        new User(
                                                uid,
                                                displayName,
                                                Objects.requireNonNullElse(name, ""),
                                                Objects.requireNonNullElse(profileImage, "")
                                        )
                                );
                            }


                            // ==================================================
                            // REFRESH CURRENT SEARCH
                            // ==================================================

                            if (
                                    searchEditText != null
                            ) {

                                filterUsers(
                                        searchEditText
                                                .getText()
                                                .toString()
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        Search.this,
                                        "Firebase Error: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }


    // ==================================================
    // FILTER USERS
    // ==================================================

    private void filterUsers(
            String text
    ) {

        if (
                searchRecyclerView == null ||
                        searchAdapter == null ||
                        postRecyclerView == null
        ) {

            return;
        }


        String searchText =
                text == null
                        ? ""
                        : text
                        .toLowerCase()
                        .trim();


        List<User> filteredUsers =
                new ArrayList<>();


        // ==================================================
        // EMPTY SEARCH
        // ==================================================

        if (
                searchText.isEmpty()
        ) {

            searchAdapter.updateList(
                    filteredUsers
            );


            searchRecyclerView.setVisibility(
                    View.GONE
            );


            postRecyclerView.setVisibility(
                    View.VISIBLE
            );


            return;
        }


        // ==================================================
        // ACTIVE SEARCH
        // ==================================================

        for (
                User currentUser :
                allUsers
        ) {

            if (
                    currentUser == null ||
                            currentUser.getUsername() == null
            ) {

                continue;
            }


            String username =
                    currentUser
                            .getUsername()
                            .toLowerCase();


            // ==================================================
            // SEARCH BY USER ID
            // ==================================================

            if (
                    username.contains(
                            searchText
                    )
            ) {

                filteredUsers.add(
                        currentUser
                );
            }
        }


        // ==================================================
        // UPDATE ADAPTER
        // ==================================================

        searchAdapter.updateList(
                filteredUsers
        );


        // ==================================================
        // SHOW / HIDE RESULTS
        // ==================================================

        if (
                filteredUsers.isEmpty()
        ) {

            searchRecyclerView.setVisibility(
                    View.GONE
            );


            postRecyclerView.setVisibility(
                    View.VISIBLE
            );

        } else {

            searchRecyclerView.setVisibility(
                    View.VISIBLE
            );


            postRecyclerView.setVisibility(
                    View.GONE
            );
        }
    }


    // ==================================================
    // LOAD POSTS
    // ==================================================

    private void loadPosts() {

        db.collection("posts")
                .addSnapshotListener(
                        (querySnapshot, error) -> {

                            if (
                                    error != null
                            ) {

                                Toast.makeText(
                                        Search.this,
                                        "Posts Error: "
                                                + error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }


                            if (
                                    querySnapshot == null
                            ) {

                                return;
                            }


                            postList.clear();


                            for (
                                    DocumentSnapshot document :
                                    querySnapshot.getDocuments()
                            ) {

                                Post post =
                                        document.toObject(
                                                Post.class
                                        );


                                if (
                                        post == null
                                ) {

                                    continue;
                                }


                                // ==================================================
                                // DOCUMENT ID
                                // ==================================================

                                post.setPostId(
                                        document.getId()
                                );


                                postList.add(
                                        post
                                );
                            }


                            if (
                                    postAdapter != null
                            ) {

                                postAdapter.updateList(
                                        new ArrayList<>(
                                                postList
                                        )
                                );
                            }
                        }
                );
    }


    // ==================================================
    // RESUME
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        UnreadChatManager.startListening(this, findViewById(R.id.btnMessageBadge));


        if (
                searchEditText != null
        ) {

            filterUsers(
                    searchEditText
                            .getText()
                            .toString()
            );
        }
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

    private final GestureDetector horizontalGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 120 && Math.abs(velocityX) > 150) {
                if (diffX > 0) {
                    // Swipe Right -> ChatListActivity
                    startActivity(new Intent(Search.this, ChatListActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    return true;
                } else {
                    // Swipe Left -> Profile
                    startActivity(new Intent(Search.this, profilepage.class));
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        }
    });

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        horizontalGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
