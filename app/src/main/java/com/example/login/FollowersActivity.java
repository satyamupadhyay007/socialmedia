package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private String profileUserId;
    private String mode;
    private String currentUserId;
    private boolean isProfileOwner;

    private TextView tvTitle;
    private RecyclerView recyclerUsers;

    private final List<User> userList = new ArrayList<>();

    private FollowersAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 15 / 16 edge-to-edge
        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_followers);


        // =====================================================
        // FIRESTORE
        // =====================================================

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        }


        // =====================================================
        // GET DATA FROM INTENT
        // =====================================================

        profileUserId =
                getIntent().getStringExtra("userId");

        mode =
                getIntent().getStringExtra("mode");

        isProfileOwner =
                currentUserId != null &&
                        currentUserId.equals(profileUserId);


        if (profileUserId == null ||
                profileUserId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        // =====================================================
        // FIND VIEWS
        // =====================================================

        tvTitle =
                findViewById(R.id.tvFollowersTitle);

        recyclerUsers =
                findViewById(R.id.followersRecyclerView);


        // =====================================================
        // TITLE
        // =====================================================

        if ("following".equalsIgnoreCase(mode)) {

            tvTitle.setText("Following");

        } else {

            tvTitle.setText("Followers");
        }


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new FollowersAdapter(userList);

        recyclerUsers.setAdapter(adapter);


        // =====================================================
        // BACK
        // =====================================================

        ImageButton btnBack =
                findViewById(R.id.btnFollowersBack);

        btnBack.setOnClickListener(
                v -> finish()
        );


        // =====================================================
        // ANDROID 15 / 16 INSETS
        // =====================================================

        setupInsets();


        // =====================================================
        // LOAD USERS
        // =====================================================

        loadUsers();
    }


    // =========================================================
    // EDGE-TO-EDGE INSETS
    // =========================================================

    private void setupInsets() {

        View root =
                findViewById(R.id.followersRoot);

        View header =
                findViewById(R.id.followersHeader);

        View recycler =
                findViewById(R.id.followersRecyclerView);


        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );


                    // Keep header below status bar

                    header.setPadding(
                            header.getPaddingLeft(),
                            systemBars.top,
                            header.getPaddingRight(),
                            header.getPaddingBottom()
                    );


                    // Keep last list item above navigation area

                    recycler.setPadding(
                            recycler.getPaddingLeft(),
                            recycler.getPaddingTop(),
                            recycler.getPaddingRight(),
                            systemBars.bottom
                    );


                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }


    // =========================================================
    // LOAD FOLLOWERS / FOLLOWING
    // =========================================================

    private void loadUsers() {

        userList.clear();

        adapter.notifyDataSetChanged();


        if ("following".equalsIgnoreCase(mode)) {

            loadFollowing();

        } else {

            loadFollowers();
        }
    }


    // =========================================================
    // LOAD FOLLOWERS
    // =========================================================

    private void loadFollowers() {

        db.collection("relationships")
                .whereEqualTo(
                        "followingId",
                        profileUserId
                )
                .whereEqualTo(
                        "status",
                        "accepted"
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        String uid =
                                document.getString(
                                        "followerId"
                                );

                        if (uid != null &&
                                !uid.trim().isEmpty()) {

                            loadUser(uid);
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load followers",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // LOAD FOLLOWING
    // =========================================================

    private void loadFollowing() {

        db.collection("relationships")
                .whereEqualTo(
                        "followerId",
                        profileUserId
                )
                .whereEqualTo(
                        "status",
                        "accepted"
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        String uid =
                                document.getString(
                                        "followingId"
                                );

                        if (uid != null &&
                                !uid.trim().isEmpty()) {

                            loadUser(uid);
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load following",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // LOAD USER DOCUMENT
    // =========================================================

    private void loadUser(String uid) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }


                    String username =
                            document.getString("userId");

                    if (username == null ||
                            username.trim().isEmpty()) {

                        username =
                                document.getString("username");
                    }


                    String name =
                            document.getString("name");


                    String profileImage =
                            document.getString("profileImage");


                    User user =
                            new User(
                                    uid,
                                    username,
                                    name,
                                    profileImage
                            );


                    userList.add(user);

                    adapter.notifyItemInserted(
                            userList.size() - 1
                    );
                });
    }


    // =========================================================
    // REMOVE FOLLOWER
    // =========================================================

    private void removeFollower(User user) {

        if (!isProfileOwner ||
                currentUserId == null ||
                user == null ||
                user.getUid() == null ||
                user.getUid().trim().isEmpty()) {
            return;
        }

        String followerId = user.getUid();

        String relationshipId =
                followerId + "_" + profileUserId;

        db.collection("relationships")
                .document(relationshipId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Follower removed",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadUsers();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to remove follower",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }


    // =========================================================
    // ADAPTER
    // =========================================================

    private class FollowersAdapter
            extends RecyclerView.Adapter<
            FollowersAdapter.UserViewHolder> {

        private final List<User> list;


        FollowersAdapter(List<User> list) {
            this.list = list;
        }


        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(
                @NonNull android.view.ViewGroup parent,
                int viewType) {

            int layoutId =
                    "followers".equalsIgnoreCase(mode)
                            && isProfileOwner
                            ? R.layout.item_follower
                            : R.layout.item_user;

            View view =
                    getLayoutInflater().inflate(
                            layoutId,
                            parent,
                            false
                    );

            return new UserViewHolder(view);
        }


        @Override
        public void onBindViewHolder(
                @NonNull UserViewHolder holder,
                int position) {

            User user =
                    list.get(position);


            // =================================================
            // USERNAME
            // =================================================

            String username =
                    user.getUsername();

            if (username == null ||
                    username.trim().isEmpty()) {

                username = "Unknown User";
            }

            holder.username.setText(username);


            // =================================================
            // NAME
            // =================================================

            String name =
                    user.getName();

            if (name == null ||
                    name.trim().isEmpty()) {

                holder.name.setVisibility(
                        View.GONE
                );

            } else {

                holder.name.setVisibility(
                        View.VISIBLE
                );

                holder.name.setText(name);
            }


            // =================================================
            // PROFILE IMAGE
            // =================================================

            String image =
                    user.getProfileImage();

            if (image != null &&
                    !image.trim().isEmpty()) {

                Glide.with(
                                holder.image.getContext()
                        )
                        .load(image)
                        .circleCrop()
                        .placeholder(
                                android.R.drawable.ic_menu_gallery
                        )
                        .error(
                                android.R.drawable.ic_menu_gallery
                        )
                        .into(holder.image);

            } else {

                holder.image.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }


            // =================================================
            // OPEN PROFILE
            // =================================================

            final String uid =
                    user.getUid();

            holder.itemView.setOnClickListener(v -> {

                if (uid == null ||
                        uid.trim().isEmpty()) {

                    return;
                }


                Intent intent =
                        new Intent(
                                FollowersActivity.this,
                                UserProfileActivity.class
                        );

                intent.putExtra(
                        "userId",
                        uid
                );

                startActivity(intent);
            });

            if (holder.removeButton != null) {

                holder.removeButton.setVisibility(
                        "followers".equalsIgnoreCase(mode)
                                && isProfileOwner
                                ? View.VISIBLE
                                : View.GONE
                );

                holder.removeButton.setOnClickListener(v ->
                        removeFollower(user)
                );
            }
        }


        @Override
        public int getItemCount() {

            return list.size();
        }


        // =====================================================
        // VIEW HOLDER
        // =====================================================

        class UserViewHolder
                extends RecyclerView.ViewHolder {

            ImageView image;
            TextView username;
            TextView name;
            Button removeButton;


            UserViewHolder(
                    @NonNull View itemView
            ) {

                super(itemView);


                image =
                        itemView.findViewById(
                                R.id.userImage
                        );


                username =
                        itemView.findViewById(
                                R.id.userUsername
                        );


                name =
                        itemView.findViewById(
                                R.id.userName
                        );

                removeButton =
                        itemView.findViewById(
                                R.id.btnRemoveFollower
                        );
            }
        }
    }
}
