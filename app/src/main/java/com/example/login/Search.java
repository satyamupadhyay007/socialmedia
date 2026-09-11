package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

    private PostAdapter postAdapter;


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
    // ROOT / NAVIGATION
    // ==================================================

    private View searchRoot;

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


        // ==================================================
        // LAYOUT
        // ==================================================

        setContentView(
                R.layout.activity_search
        );


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
        // ROOT / BOTTOM NAV
        // ==================================================

        searchRoot =
                findViewById(
                        R.id.searchRoot
                );

        bottomNavigation =
                findViewById(
                        R.id.bottomNavigation
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


        postRecyclerView.setAdapter(
                new PostAdapter(
                        postList
                )
        );


        postAdapter =
                (PostAdapter)
                        postRecyclerView.getAdapter();


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
                new android.text.TextWatcher() {

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
                            android.text.Editable s
                    ) {
                    }
                }
        );


        // ==================================================
        // BOTTOM NAVIGATION
        // ==================================================

        setupBottomNavigation();


        // ==================================================
        // KEYBOARD HANDLING
        // ==================================================

        setupKeyboardHandling();
    }


    // ==================================================
    // KEYBOARD HANDLING
    //
    // Hides bottom navigation when keyboard opens.
    // ==================================================

    private void setupKeyboardHandling() {

        if (
                searchRoot == null ||
                        bottomNavigation == null
        ) {

            return;
        }


        final Rect visibleFrame =
                new Rect();


        searchRoot
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {

                                if (
                                        searchRoot
                                                .getWindowToken()
                                                == null
                                ) {

                                    return;
                                }


                                searchRoot
                                        .getWindowVisibleDisplayFrame(
                                                visibleFrame
                                        );


                                int rootHeight =
                                        searchRoot
                                                .getRootView()
                                                .getHeight();


                                int visibleHeight =
                                        visibleFrame.height();


                                if (
                                        rootHeight <= 0 ||
                                                visibleHeight <= 0
                                ) {

                                    return;
                                }


                                int heightDifference =
                                        rootHeight -
                                                visibleHeight;


                                // ==================================================
                                // KEYBOARD THRESHOLD
                                // ==================================================

                                int keyboardThreshold =
                                        (int)
                                                (
                                                        rootHeight *
                                                                0.15f
                                                );


                                boolean keyboardOpen =
                                        heightDifference >
                                                keyboardThreshold;


                                // ==================================================
                                // KEYBOARD OPEN
                                // ==================================================

                                if (keyboardOpen) {

                                    if (
                                            bottomNavigation
                                                    .getVisibility()
                                                    != View.GONE
                                    ) {

                                        bottomNavigation.setVisibility(
                                                View.GONE
                                        );
                                    }

                                }


                                // ==================================================
                                // KEYBOARD CLOSED
                                // ==================================================

                                else {

                                    if (
                                            bottomNavigation
                                                    .getVisibility()
                                                    != View.VISIBLE
                                    ) {

                                        bottomNavigation.setVisibility(
                                                View.VISIBLE
                                        );
                                    }
                                }
                            }
                        }
                );
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
                                frontpage.class
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
                                                name != null
                                                        ? name
                                                        : "",
                                                profileImage != null
                                                        ? profileImage
                                                        : ""
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
}