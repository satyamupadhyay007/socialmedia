package com.example.login;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String currentUserId;
    private String profileUserId;
    private String relationshipId;

    private ImageView imgUserProfile;

    private TextView tvUserProfileName;
    private TextView tvUserProfileUsername;
    private TextView tvUserProfileBio;

    private TextView tvUserPostCount;
    private TextView tvUserFollowerCount;
    private TextView tvUserFollowingCount;

    private Button btnFollow;

    private View followersStatsContainer;
    private View followingStatsContainer;

    private TextView tabUserPosts;
    private TextView tabUserReels;

    private RecyclerView userProfileRecyclerPosts;

    private final List<Post> imagePosts = new ArrayList<>();
    private final List<Post> reelPosts = new ArrayList<>();

    private ProfilePostAdapter postAdapter;

    private boolean isFollowing = false;
    private boolean isRequested = false;
    private boolean targetIsPrivate = false;

    private static final String ACCEPTED = "accepted";
    private static final String PENDING = "pending";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 15/16 compatible edge-to-edge
        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        profileUserId = getIntent().getStringExtra("userId");

        if (profileUserId == null || profileUserId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "User not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupInsets();
        setupClicks();

        loadUserProfile();
        loadFollowStatus();
        loadUserPosts();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        imgUserProfile =
                findViewById(R.id.imgUserProfile);

        tvUserProfileName =
                findViewById(R.id.tvUserProfileName);

        tvUserProfileUsername =
                findViewById(R.id.tvUserProfileUsername);

        tvUserProfileBio =
                findViewById(R.id.tvUserProfileBio);

        tvUserPostCount =
                findViewById(R.id.tvUserPostCount);

        tvUserFollowerCount =
                findViewById(R.id.tvUserFollowerCount);

        tvUserFollowingCount =
                findViewById(R.id.tvUserFollowingCount);

        btnFollow =
                findViewById(R.id.btnFollow);

        followersStatsContainer =
                findViewById(R.id.followersStatsContainer);

        followingStatsContainer =
                findViewById(R.id.followingStatsContainer);

        tabUserPosts =
                findViewById(R.id.tabUserPosts);

        tabUserReels =
                findViewById(R.id.tabUserReels);

        userProfileRecyclerPosts =
                findViewById(R.id.userProfileRecyclerPosts);
    }


    // =========================================================
    // RECYCLER
    // =========================================================

    private void setupRecyclerView() {

        userProfileRecyclerPosts.setLayoutManager(
                new GridLayoutManager(this, 3)
        );

        postAdapter =
                new ProfilePostAdapter(imagePosts);

        userProfileRecyclerPosts.setAdapter(postAdapter);

        userProfileRecyclerPosts.setNestedScrollingEnabled(false);
    }


    // =========================================================
    // ANDROID 15 / 16 INSETS
    // =========================================================

    private void setupInsets() {

        View root =
                findViewById(R.id.userProfileRoot);

        View topBar =
                findViewById(R.id.userProfileTopBar);

        View scroll =
                findViewById(R.id.userProfileScroll);


        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets bars =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );

                    // Top safe area
                    topBar.setPadding(
                            topBar.getPaddingLeft(),
                            bars.top,
                            topBar.getPaddingRight(),
                            topBar.getPaddingBottom()
                    );


                    // Bottom safe area
                    scroll.setPadding(
                            scroll.getPaddingLeft(),
                            scroll.getPaddingTop(),
                            scroll.getPaddingRight(),
                            Math.max(
                                    scroll.getPaddingBottom(),
                                    bars.bottom
                            )
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }


    // =========================================================
    // CLICK LISTENERS
    // =========================================================

    private void setupClicks() {

        findViewById(R.id.btnUserProfileBack)
                .setOnClickListener(v -> finish());


        btnFollow.setOnClickListener(v -> {

            if (currentUserId.equals(profileUserId)) {
                return;
            }

            if (isFollowing) {

                unfollowUser();

            } else if (isRequested) {

                cancelFollowRequest();

            } else {

                followUser();
            }
        });


        followersStatsContainer.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            UserProfileActivity.this,
                            FollowersActivity.class
                    );

            intent.putExtra(
                    "userId",
                    profileUserId
            );

            intent.putExtra(
                    "mode",
                    "followers"
            );

            startActivity(intent);
        });


        followingStatsContainer.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            UserProfileActivity.this,
                            FollowersActivity.class
                    );

            intent.putExtra(
                    "userId",
                    profileUserId
            );

            intent.putExtra(
                    "mode",
                    "following"
            );

            startActivity(intent);
        });


        tabUserPosts.setOnClickListener(v -> showPosts());

        tabUserReels.setOnClickListener(v -> showReels());
    }


    // =========================================================
    // LOAD PROFILE
    // =========================================================

    private void loadUserProfile() {

        db.collection("users")
                .document(profileUserId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        Toast.makeText(
                                this,
                                "User profile not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    String name =
                            document.getString("name");

                    if (name == null ||
                            name.trim().isEmpty()) {

                        tvUserProfileName.setVisibility(
                                View.GONE
                        );

                    } else {

                        tvUserProfileName.setVisibility(
                                View.VISIBLE
                        );

                        tvUserProfileName.setText(name);
                    }


                    String username =
                            document.getString("userId");

                    if (username == null ||
                            username.trim().isEmpty()) {

                        username =
                                document.getString("username");
                    }

                    if (username == null) {
                        username = "";
                    }

                    tvUserProfileUsername.setText(
                            "@" + username
                    );


                    String bio =
                            document.getString("bio");

                    if (bio == null ||
                            bio.trim().isEmpty()) {

                        tvUserProfileBio.setVisibility(
                                View.GONE
                        );

                    } else {

                        tvUserProfileBio.setVisibility(
                                View.VISIBLE
                        );

                        tvUserProfileBio.setText(bio);
                    }


                    String profileImage =
                            document.getString("profileImage");

                    if (profileImage != null &&
                            !profileImage.trim().isEmpty()) {

                        Glide.with(this)
                                .load(profileImage)
                                .circleCrop()
                                .placeholder(
                                        android.R.drawable.ic_menu_gallery
                                )
                                .error(
                                        android.R.drawable.ic_menu_gallery
                                )
                                .into(imgUserProfile);

                    } else {

                        imgUserProfile.setImageResource(
                                android.R.drawable.ic_menu_gallery
                        );
                    }


                    Boolean privateValue =
                            document.getBoolean("isPrivate");

                    targetIsPrivate =
                            privateValue != null &&
                                    privateValue;

                    updateFollowButton();
                });
    }


    // =========================================================
    // FOLLOW STATUS
    // =========================================================

    private void loadFollowStatus() {

        if (currentUserId.equals(profileUserId)) {

            btnFollow.setVisibility(View.GONE);

            return;
        }

        btnFollow.setVisibility(View.VISIBLE);

        relationshipId =
                currentUserId + "_" + profileUserId;

        db.collection("relationships")
                .document(relationshipId)
                .get()
                .addOnSuccessListener(document -> {

                    isFollowing = false;
                    isRequested = false;

                    if (document.exists()) {

                        String status =
                                document.getString("status");

                        if (ACCEPTED.equals(status)) {
                            isFollowing = true;
                        }

                        if (PENDING.equals(status)) {
                            isRequested = true;
                        }
                    }

                    updateFollowButton();
                });
    }


    // =========================================================
    // FOLLOW
    // =========================================================

    private void followUser() {

        if (relationshipId == null) {
            relationshipId =
                    currentUserId + "_" + profileUserId;
        }

        String status =
                targetIsPrivate
                        ? PENDING
                        : ACCEPTED;


        Map<String, Object> relationship =
                new HashMap<>();

        relationship.put(
                "followerId",
                currentUserId
        );

        relationship.put(
                "followingId",
                profileUserId
        );

        relationship.put(
                "status",
                status
        );

        relationship.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );


        db.collection("relationships")
                .document(relationshipId)
                .set(relationship)
                .addOnSuccessListener(unused -> {

                    if (ACCEPTED.equals(status)) {

                        isFollowing = true;
                        isRequested = false;

                        // Preserve existing Home/Stories
                        // following array.

                        db.collection("users")
                                .document(currentUserId)
                                .update(
                                        "following",
                                        FieldValue.arrayUnion(
                                                profileUserId
                                        )
                                );

                        createFollowNotification(
                                "follow",
                                "started following you"
                        );

                        Toast.makeText(
                                this,
                                "Following",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        isFollowing = false;
                        isRequested = true;

                        createFollowNotification(
                                "follow_request",
                                "requested to follow you"
                        );

                        Toast.makeText(
                                this,
                                "Requested",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    updateFollowButton();
                    loadFollowCounts();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to follow user",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // UNFOLLOW
    // =========================================================

    private void unfollowUser() {

        db.collection("relationships")
                .document(relationshipId)
                .delete()
                .addOnSuccessListener(unused -> {

                    isFollowing = false;
                    isRequested = false;


                    db.collection("users")
                            .document(currentUserId)
                            .update(
                                    "following",
                                    FieldValue.arrayRemove(
                                            profileUserId
                                    )
                            );


                    updateFollowButton();
                    loadFollowCounts();


                    Toast.makeText(
                            this,
                            "Unfollowed",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to unfollow",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // CANCEL REQUEST
    // =========================================================

    private void cancelFollowRequest() {

        db.collection("relationships")
                .document(relationshipId)
                .delete()
                .addOnSuccessListener(unused -> {

                    isFollowing = false;
                    isRequested = false;

                    updateFollowButton();
                    loadFollowCounts();

                    Toast.makeText(
                            this,
                            "Request cancelled",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to cancel request",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // BUTTON UI
    // =========================================================

    private void updateFollowButton() {

        if (btnFollow == null) {
            return;
        }

        if (currentUserId.equals(profileUserId)) {

            btnFollow.setVisibility(View.GONE);
            return;
        }

        btnFollow.setVisibility(View.VISIBLE);


        if (isFollowing) {

            btnFollow.setText("Following");

            btnFollow.setBackgroundTintList(
                    ColorStateList.valueOf(
                            0xFF333333
                    )
            );

        } else if (isRequested) {

            btnFollow.setText("Requested");

            btnFollow.setBackgroundTintList(
                    ColorStateList.valueOf(
                            0xFF333333
                    )
            );

        } else {

            btnFollow.setText("Follow");

            btnFollow.setBackgroundTintList(
                    ColorStateList.valueOf(
                            0xFF0095F6
                    )
            );
        }
    }


    // =========================================================
    // CREATE FOLLOW / FOLLOW REQUEST NOTIFICATION
    // =========================================================

    private void createFollowNotification(
            String type,
            String message
    ) {

        if (currentUserId == null ||
                profileUserId == null ||
                currentUserId.equals(profileUserId)) {
            return;
        }

        Map<String, Object> notification =
                new HashMap<>();

        notification.put("userId", profileUserId);
        notification.put("fromUserId", currentUserId);
        notification.put("type", type);
        notification.put("message", message);
        notification.put("read", false);
        notification.put("createdAt", FieldValue.serverTimestamp());

        db.collection("notifications")
                .add(notification);
    }


    // =========================================================
    // FOLLOW COUNTS
    // =========================================================

    private void loadFollowCounts() {

        db.collection("relationships")
                .whereEqualTo(
                        "followingId",
                        profileUserId
                )
                .whereEqualTo(
                        "status",
                        ACCEPTED
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    tvUserFollowerCount.setText(
                            String.valueOf(
                                    snapshot.size()
                            )
                    );
                });


        db.collection("relationships")
                .whereEqualTo(
                        "followerId",
                        profileUserId
                )
                .whereEqualTo(
                        "status",
                        ACCEPTED
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    tvUserFollowingCount.setText(
                            String.valueOf(
                                    snapshot.size()
                            )
                    );
                });
    }


    // =========================================================
    // LOAD POSTS + REELS
    // =========================================================

    private void loadUserPosts() {

        db.collection("posts")
                .whereEqualTo(
                        "userId",
                        profileUserId
                )
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    processPosts(snapshot.getDocuments());

                })
                .addOnFailureListener(e -> {

                    // Fallback if Firestore index/order
                    // causes an error.

                    loadUserPostsWithoutOrder();
                });
    }


    private void loadUserPostsWithoutOrder() {

        db.collection("posts")
                .whereEqualTo(
                        "userId",
                        profileUserId
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    processPosts(snapshot.getDocuments());
                });
    }


    private void processPosts(
            List<DocumentSnapshot> documents) {

        imagePosts.clear();
        reelPosts.clear();


        for (DocumentSnapshot document :
                documents) {

            try {

                Post post =
                        document.toObject(Post.class);

                if (post == null) {
                    continue;
                }


                if (post.getPostId() == null ||
                        post.getPostId().trim().isEmpty()) {

                    post.setPostId(
                            document.getId()
                    );
                }


                String mediaType =
                        post.getMediaType();

                if (mediaType == null ||
                        mediaType.trim().isEmpty()) {

                    mediaType = "image";

                    post.setMediaType("image");
                }


                if ("video".equalsIgnoreCase(
                        mediaType
                )) {

                    reelPosts.add(post);

                } else {

                    imagePosts.add(post);
                }

            } catch (Exception ignored) {
            }
        }


        int total =
                imagePosts.size() +
                        reelPosts.size();

        tvUserPostCount.setText(
                String.valueOf(total)
        );


        showPosts();
        loadFollowCounts();
    }


    // =========================================================
    // POSTS TAB
    // =========================================================

    private void showPosts() {

        postAdapter.updateList(imagePosts);

        tabUserPosts.setTextColor(
                0xFFFFFFFF
        );

        tabUserReels.setTextColor(
                0xFF777777
        );
    }


    // =========================================================
    // REELS TAB
    // =========================================================

    private void showReels() {

        postAdapter.updateList(reelPosts);

        tabUserPosts.setTextColor(
                0xFF777777
        );

        tabUserReels.setTextColor(
                0xFFFFFFFF
        );
    }


    // =========================================================
    // REFRESH
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null &&
                auth.getCurrentUser() != null &&
                profileUserId != null) {

            loadFollowStatus();
            loadFollowCounts();
        }
    }
}