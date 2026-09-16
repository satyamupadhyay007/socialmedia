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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class profilepage extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private RecyclerView recyclerPosts;

    private View topBar;
    private View bottomNavigation;

    private ImageButton btnBack;
    private ImageButton btnSettings;

    private Button btnEditProfile;
    private Button btnUploadPost;

    // ONLY Posts + Reels
    private Button btnPosts;
    private Button btnReels;

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

    // ============================================================
    // FIREBASE
    // ============================================================

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;

    // ============================================================
    // POSTS
    // ============================================================

    private final ArrayList<Post> allPosts =
            new ArrayList<>();

    private final ArrayList<Post> imagePosts =
            new ArrayList<>();

    private final ArrayList<Post> reelPosts =
            new ArrayList<>();

    private ProfilePostAdapter postAdapter;

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 14 / 15 / 16
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_profile);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        currentUserId =
                mAuth.getCurrentUser().getUid();

        findViews();

        setupRecyclerView();

        setupSystemBars();

        setupButtons();

        setupBottomNavigation();

        loadProfile();

        loadMyPosts();
    }

    // ============================================================
    // FIND VIEWS
    // ============================================================

    private void findViews() {

        topBar =
                findViewById(R.id.topBar);

        bottomNavigation =
                findViewById(R.id.bottomNavigation);

        recyclerPosts =
                findViewById(R.id.recyclerPosts);

        btnBack =
                findViewById(R.id.btnBack);

        btnSettings =
                findViewById(R.id.btnSettings);

        btnEditProfile =
                findViewById(R.id.btnEditProfile);

        btnUploadPost =
                findViewById(R.id.btnUploadPost);

        btnPosts =
                findViewById(R.id.btnPosts);

        btnReels =
                findViewById(R.id.btnReels);

        btnHome =
                findViewById(R.id.btnHome);

        btnCreate =
                findViewById(R.id.btnCreate);

        btnMessage =
                findViewById(R.id.btnMessage);

        btnSearch =
                findViewById(R.id.btnSearch);

        btnProfile =
                findViewById(R.id.btnProfile);

        imgProfile =
                findViewById(R.id.imgProfile);

        tvName =
                findViewById(R.id.tvName);

        tvUsername =
                findViewById(R.id.tvUsername);

        tvBio =
                findViewById(R.id.tvBio);

        tvPostCount =
                findViewById(R.id.tvPostCount);

        tvFollowerCount =
                findViewById(R.id.tvFollowerCount);

        tvFollowingCount =
                findViewById(R.id.tvFollowingCount);
    }

    // ============================================================
    // RECYCLER VIEW
    // ============================================================

    private void setupRecyclerView() {

        postAdapter =
                new ProfilePostAdapter(
                        new ArrayList<>()
                );

        recyclerPosts.setLayoutManager(
                new GridLayoutManager(
                        this,
                        3
                )
        );

        recyclerPosts.setAdapter(
                postAdapter
        );

        recyclerPosts.setClipToPadding(
                false
        );
    }

    // ============================================================
    // ANDROID 14 / 15 / 16 EDGE-TO-EDGE
    // ============================================================

    private void setupSystemBars() {

        View root =
                findViewById(
                        R.id.profileRoot
                );

        if (root == null) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets bars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );

                    // TOP BAR
                    if (topBar != null &&
                            topBar.getLayoutParams()
                                    instanceof ViewGroup.MarginLayoutParams) {

                        ViewGroup.MarginLayoutParams lp =
                                (ViewGroup.MarginLayoutParams)
                                        topBar.getLayoutParams();

                        lp.topMargin =
                                bars.top;

                        topBar.setLayoutParams(lp);
                    }

                    // BOTTOM NAVIGATION
                    if (bottomNavigation != null &&
                            bottomNavigation.getLayoutParams()
                                    instanceof ViewGroup.MarginLayoutParams) {

                        ViewGroup.MarginLayoutParams lp =
                                (ViewGroup.MarginLayoutParams)
                                        bottomNavigation.getLayoutParams();

                        lp.bottomMargin =
                                bars.bottom;

                        bottomNavigation.setLayoutParams(lp);
                    }

                    // Recycler bottom safe area
                    recyclerPosts.setPadding(
                            recyclerPosts.getPaddingLeft(),
                            recyclerPosts.getPaddingTop(),
                            recyclerPosts.getPaddingRight(),
                            bars.bottom
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }

    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        // BACK
        btnBack.setOnClickListener(
                v -> finish()
        );

        // SETTINGS
        btnSettings.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    SettingsActivity.class
                            );

                    startActivity(intent);
                }
        );

        // EDIT PROFILE
        btnEditProfile.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    EditProfileActivity.class
                            );

                    startActivity(intent);
                }
        );

        // CREATE POST
        btnUploadPost.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    UploadPostActivity.class
                            );

                    startActivity(intent);
                }
        );

        // POSTS TAB
        btnPosts.setOnClickListener(
                v -> showPosts()
        );

        // REELS TAB
        btnReels.setOnClickListener(
                v -> showReels()
        );
    }

    // ============================================================
    // POSTS TAB
    // ============================================================

    private void showPosts() {

        recyclerPosts.setVisibility(
                View.VISIBLE
        );

        postAdapter.updateList(
                imagePosts
        );

        if (imagePosts.isEmpty()) {

            Toast.makeText(
                    this,
                    "No photo posts yet",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ============================================================
    // REELS TAB
    // ============================================================

    private void showReels() {

        recyclerPosts.setVisibility(
                View.VISIBLE
        );

        postAdapter.updateList(
                reelPosts
        );

        if (reelPosts.isEmpty()) {

            Toast.makeText(
                    this,
                    "No reels yet",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ============================================================
    // LOAD PROFILE
    // ============================================================

    private void loadProfile() {

        if (currentUserId == null ||
                currentUserId.isEmpty()) {
            return;
        }

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {
                                return;
                            }

                            // USERNAME
                            String username =
                                    document.getString(
                                            "userId"
                                    );

                            if (username != null &&
                                    !username.trim().isEmpty()) {

                                tvUsername.setText(
                                        "@" + username
                                );
                            }

                            // NAME
                            String name =
                                    document.getString(
                                            "name"
                                    );

                            if (name != null &&
                                    !name.trim().isEmpty()) {

                                tvName.setText(name);

                                tvName.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                tvName.setVisibility(
                                        View.GONE
                                );
                            }

                            // BIO
                            String bio =
                                    document.getString(
                                            "bio"
                                    );

                            if (bio != null &&
                                    !bio.trim().isEmpty()) {

                                tvBio.setText(bio);

                                tvBio.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                tvBio.setVisibility(
                                        View.GONE
                                );
                            }

                            // PROFILE IMAGE
                            String profileImage =
                                    document.getString(
                                            "profileImage"
                                    );

                            if (profileImage != null &&
                                    !profileImage.trim().isEmpty()) {

                                Glide.with(
                                                profilepage.this
                                        )
                                        .load(profileImage)
                                        .circleCrop()
                                        .into(imgProfile);
                            }

                            // FOLLOWERS
                            Long followers =
                                    document.getLong(
                                            "followerCount"
                                    );

                            tvFollowerCount.setText(
                                    String.valueOf(
                                            followers == null
                                                    ? 0
                                                    : followers
                                    )
                            );

                            // FOLLOWING
                            Long following =
                                    document.getLong(
                                            "followingCount"
                                    );

                            tvFollowingCount.setText(
                                    String.valueOf(
                                            following == null
                                                    ? 0
                                                    : following
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

    // ============================================================
    // LOAD MY POSTS + REELS
    // ============================================================

    private void loadMyPosts() {

        if (mAuth == null ||
                mAuth.getCurrentUser() == null) {
            return;
        }

        currentUserId =
                mAuth.getCurrentUser().getUid();

        db.collection("posts")
                .whereEqualTo(
                        "userId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            allPosts.clear();
                            imagePosts.clear();
                            reelPosts.clear();

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                Post post =
                                        document.toObject(
                                                Post.class
                                        );

                                if (post == null) {
                                    continue;
                                }

                                String mediaType =
                                        post.getMediaType();

                                /*
                                 * Old posts do not have mediaType.
                                 * They are treated as normal photos.
                                 */
                                if (mediaType == null ||
                                        mediaType.trim().isEmpty()) {

                                    mediaType =
                                            "image";

                                    post.setMediaType(
                                            "image"
                                    );
                                }

                                allPosts.add(post);

                                // VIDEO = REEL
                                if ("video".equalsIgnoreCase(
                                        mediaType
                                )) {

                                    reelPosts.add(post);

                                } else {

                                    // IMAGE = POST
                                    imagePosts.add(post);
                                }
                            }

                            // Newest first
                            sortPosts(allPosts);
                            sortPosts(imagePosts);
                            sortPosts(reelPosts);

                            /*
                             * Default screen = Posts
                             */
                            postAdapter.updateList(
                                    imagePosts
                            );

                            /*
                             * Total count includes both
                             * photos + reels.
                             */
                            tvPostCount.setText(
                                    String.valueOf(
                                            allPosts.size()
                                    )
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            allPosts.clear();
                            imagePosts.clear();
                            reelPosts.clear();

                            postAdapter.updateList(
                                    imagePosts
                            );

                            tvPostCount.setText(
                                    "0"
                            );

                            Toast.makeText(
                                    profilepage.this,
                                    "Could not load posts",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // ============================================================
    // SORT POSTS
    // ============================================================

    private void sortPosts(
            ArrayList<Post> list
    ) {

        list.sort(
                (p1, p2) -> {

                    if (p1.getCreatedAt() == null ||
                            p2.getCreatedAt() == null) {

                        return 0;
                    }

                    return p2.getCreatedAt()
                            .compareTo(
                                    p1.getCreatedAt()
                            );
                }
        );
    }

    // ============================================================
    // REFRESH WHEN RETURNING TO PROFILE
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (mAuth != null &&
                mAuth.getCurrentUser() != null) {

            currentUserId =
                    mAuth.getCurrentUser().getUid();

            loadProfile();
            loadMyPosts();
        }
    }

    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private void setupBottomNavigation() {

        // HOME
        btnHome.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    homepage.class
                            );

                    startActivity(intent);

                    finish();
                }
        );

        // CREATE
        btnCreate.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    frontpage.class
                            );

                    startActivity(intent);
                }
        );

        // MESSAGE
        btnMessage.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    ChatListActivity.class
                            );

                    startActivity(intent);

                    finish();
                }
        );

        // SEARCH
        btnSearch.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    profilepage.this,
                                    Search.class
                            );

                    startActivity(intent);

                    finish();
                }
        );

        // PROFILE
        btnProfile.setOnClickListener(
                v -> {
                    // Already on Profile
                }
        );
    }
}
