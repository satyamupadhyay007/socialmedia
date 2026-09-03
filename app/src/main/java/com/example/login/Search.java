package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

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
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState
        );


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
                new PostAdapter(
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
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterUsers(
                                s.toString()
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
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

        if (
                btnHome != null
        ) {

            btnHome.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        Search.this,
                                        homepage.class
                                );


                        startActivity(
                                intent
                        );


                        finish();
                    }
            );
        }


        // ==================================================
        // CREATE
        // ==================================================

        if (
                btnCreate != null
        ) {

            btnCreate.setOnClickListener(
                    v -> {

                        Toast.makeText(
                                Search.this,
                                "Create screen coming soon",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );
        }


        // ==================================================
        // MESSAGE
        // ==================================================

        if (
                btnMessage != null
        ) {

            btnMessage.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        Search.this,
                                        ChatListActivity.class
                                );


                        startActivity(
                                intent
                        );


                        finish();
                    }
            );
        }


        // ==================================================
        // SEARCH
        // ==================================================

        if (
                btnSearch != null
        ) {

            btnSearch.setOnClickListener(
                    v -> {
                        // Already on Search
                    }
            );
        }


        // ==================================================
        // PROFILE
        // ==================================================

        if (
                btnProfile != null
        ) {

            btnProfile.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        Search.this,
                                        profilepage.class
                                );


                        startActivity(
                                intent
                        );


                        finish();
                    }
            );
        }
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

                                String uid =
                                        document.getId();


                                String username =
                                        document.getString(
                                                "username"
                                        );


                                String userId =
                                        document.getString(
                                                "userId"
                                        );


                                String uniqueUserId =
                                        document.getString(
                                                "uniqueUserId"
                                        );


                                String displayName =
                                        null;


                                // ==================================================
                                // USER ID FIRST
                                // ==================================================

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


                                if (
                                        displayName == null ||
                                                displayName.isEmpty()
                                ) {

                                    continue;
                                }


                                allUsers.add(
                                        new User(
                                                uid,
                                                displayName
                                        )
                                );
                            }


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
                        e -> {

                            Toast.makeText(
                                    Search.this,
                                    "Firebase Error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // FILTER USERS
    // ==================================================

    private void filterUsers(
            String text) {


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


        searchAdapter.updateList(
                filteredUsers
        );


        // ==================================================
        // SHOW / HIDE RESULT
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
                                // SAVE DOCUMENT ID
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
    // ON RESUME
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
}