package com.example.login;
import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class homepage extends AppCompatActivity {

    // ==================================================
    // BOTTOM NAVIGATION
    // ==================================================

    private ImageButton btnHome;
    private ImageButton btnCreate;
    private ImageButton btnMessage;
    private ImageButton btnSearch;
    private ImageButton btnProfile;

    // ==================================================
    // NOTIFICATIONS
    // ==================================================

    private FrameLayout notificationContainer;
    private ImageButton btnNotifications;
    private TextView notificationBadge;

    // ==================================================
    // STORIES
    // ==================================================

    private LinearLayout storyRow;

    // Posts
    private PostAdapter postAdapter;
    private final List<Post> postList = new ArrayList<>();

    // Upload spinner
    private ProgressBar storyUploadProgress;

    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;

    // ==================================================
    // CLOUDINARY
    // ==================================================

    private static final String UPLOAD_PRESET =
            "frenzo_media";

    private static boolean cloudinaryInitialized =
            false;

    // ==================================================
    // STORY PICKER
    // ==================================================

    private String selectedStoryType = "";

    private final ActivityResultLauncher<String> storyPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        uploadStory(
                                uri,
                                selectedStoryType
                        );
                    }
            );

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

        EdgeToEdge.enable(
                this
        );

        setContentView(
                R.layout.activity_homepage
        );

        // ==================================================
        // FIREBASE
        // ==================================================

        mAuth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {

            finish();

            return;
        }

        currentUserId =
                mAuth.getCurrentUser()
                        .getUid();

        // ==================================================
        // NOTIFICATIONS
        // ==================================================

        notificationContainer =
                findViewById(
                        R.id.notificationContainer
                );

        btnNotifications =
                findViewById(
                        R.id.btnNotifications
                );

        notificationBadge =
                findViewById(
                        R.id.notificationBadge
                );

        setupNotifications();

        requestNotificationPermission();

        // ==================================================
        // SAFE AREA
        // ==================================================

        View root =
                findViewById(
                        R.id.appContainer
                );

        if (root != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    root,
                    (view, insets) -> {

                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                                | WindowInsetsCompat.Type.displayCutout()
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
        }

        // ==================================================
        // STORY ROW
        // ==================================================

        storyRow =
                findViewById(
                        R.id.storyRow
                );

        // ==================================================
        // POSTS RECYCLER
        // ==================================================

        RecyclerView postRecyclerView =
                findViewById(
                        R.id.postRecyclerView
                );

        postAdapter =
                new PostAdapter(
                        postList
                );

        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        postRecyclerView.setAdapter(
                postAdapter
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

        // ==================================================
        // CLOUDINARY
        // ==================================================

        initializeCloudinary();

        horizontalGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 120 && Math.abs(velocityX) > 150) {
                    if (diffX < 0) {
                        // Swipe Left -> Reels
                        startActivity(new Intent(homepage.this, ReelsActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        return true;
                    }
                }
                return false;
            }
        });

        // ==================================================
        // BOTTOM NAV
        // ==================================================

        setupBottomNavigation();

        // ==================================================
        // LOAD POSTS
        // ==================================================

        loadPosts();

        horizontalGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 120 && Math.abs(velocityX) > 150) {
                    if (diffX < 0) {
                        // Swipe Left -> Reels
                        startActivity(new Intent(homepage.this, ReelsActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        return true;
                    }
                }
                return false;
            }
        });

        // ==================================================
        // IMPORTANT
        //
        // loadStories() is NOT called here.
        // It is called from onResume().
        //
        // This prevents duplicate story bubbles.
        // ==================================================
    }

    // ==================================================
    // ON RESUME
    // ==================================================

    @Override
    protected void onResume() {

        super.onResume();

        UnreadChatManager.startListening(this, findViewById(R.id.btnMessageBadge));

        if (mAuth == null) {
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        currentUserId =
                mAuth.getCurrentUser()
                        .getUid();

        if (storyRow != null) {

            // NEW: clean expired stories before loading active stories.
            deleteExpiredOwnStories();

            loadStories();
        }
    }

    // ==================================================
    // CLOUDINARY INITIALIZE
    // ==================================================

    private void initializeCloudinary() {

        if (cloudinaryInitialized) {
            return;
        }

        try {

            Map<String, Object> config =
                    new HashMap<>();

            config.put(
                    "cloud_name",
                    "klhzpjaj"
            );

            config.put(
                    "secure",
                    true
            );

            MediaManager.init(
                    this,
                    config
            );

            cloudinaryInitialized =
                    true;

        } catch (Exception e) {

            // Already initialized.
            cloudinaryInitialized =
                    true;
        }
    }

    // ==================================================
    // LOAD STORIES
    // ==================================================

    private void loadStories() {

        if (storyRow == null) {
            return;
        }

        // Remove all old story bubbles.
        storyRow.removeAllViews();

        // Make sure an old loader reference is cleared.
        storyUploadProgress =
                null;

        loadOwnStoryForBubble();
    }

    // ==================================================
    // LOAD OWN STORY
    // ==================================================

    private void loadOwnStoryForBubble() {

        db.collection("stories")
                .whereEqualTo(
                        "userId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            List<DocumentSnapshot> ownStories =
                                    filterActiveStories(
                                            snapshot.getDocuments()
                                    );

                            sortStoriesNewestFirst(
                                    ownStories
                            );

                            loadOwnProfileData(
                                    ownStories
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                loadOwnProfileData(
                                        new ArrayList<>()
                                )
                );
    }

    // ==================================================
    // LOAD OWN PROFILE
    // ==================================================

    private void loadOwnProfileData(
            List<DocumentSnapshot> ownStories
    ) {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            String username =
                                    getSafeString(
                                            userDocument,
                                            "userId"
                                    );

                            if (username.isEmpty()) {

                                username =
                                        getSafeString(
                                                userDocument,
                                                "username"
                                        );
                            }

                            if (username.isEmpty()) {

                                username =
                                        "Your story";
                            }

                            String profileImage =
                                    getSafeString(
                                            userDocument,
                                            "profileImage"
                                    );

                            addStoryBubble(
                                    currentUserId,
                                    username,
                                    profileImage,
                                    ownStories,
                                    true
                            );

                            loadFollowingStories();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            addStoryBubble(
                                    currentUserId,
                                    "Your story",
                                    "",
                                    ownStories,
                                    true
                            );

                            loadFollowingStories();
                        }
                );
    }

    // ==================================================
    // LOAD FOLLOWING STORIES
    // ==================================================

    private void loadFollowingStories() {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            Object followingObject =
                                    userDocument.get(
                                            "following"
                                    );

                            if (!(followingObject
                                    instanceof List)) {

                                return;
                            }

                            List<String> followingIds =
                                    new ArrayList<>();

                            for (
                                    Object item :
                                    (List<?>) followingObject
                            ) {

                                if (item == null) {
                                    continue;
                                }

                                String uid =
                                        String.valueOf(
                                                item
                                        );

                                if (uid.isEmpty()) {
                                    continue;
                                }

                                if (uid.equals(
                                        currentUserId
                                )) {
                                    continue;
                                }

                                followingIds.add(
                                        uid
                                );
                            }

                            loadFollowingUserStories(
                                    followingIds,
                                    0
                            );
                        }
                );
    }

    // ==================================================
    // FOLLOWING USER STORIES
    // ==================================================

    private void loadFollowingUserStories(
            List<String> followingIds,
            int index
    ) {

        if (followingIds == null) {
            return;
        }

        if (index >= followingIds.size()) {
            return;
        }

        String followedUid =
                followingIds.get(
                        index
                );

        if (followedUid == null ||
                followedUid.isEmpty()) {

            loadFollowingUserStories(
                    followingIds,
                    index + 1
            );

            return;
        }

        db.collection("stories")
                .whereEqualTo(
                        "userId",
                        followedUid
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            List<DocumentSnapshot> stories =
                                    filterActiveStories(
                                            snapshot.getDocuments()
                                    );

                            sortStoriesNewestFirst(
                                    stories
                            );

                            if (!stories.isEmpty()) {

                                DocumentSnapshot firstStory =
                                        stories.get(0);

                                String username =
                                        getSafeString(
                                                firstStory,
                                                "username"
                                        );

                                String profileImage =
                                        getSafeString(
                                                firstStory,
                                                "profileImage"
                                        );

                                if (username.isEmpty()) {

                                    username =
                                            getSafeString(
                                                    firstStory,
                                                    "name"
                                            );
                                }

                                if (username.isEmpty()) {

                                    username =
                                            "User";
                                }

                                addStoryBubble(
                                        followedUid,
                                        username,
                                        profileImage,
                                        stories,
                                        false
                                );
                            }

                            loadFollowingUserStories(
                                    followingIds,
                                    index + 1
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                loadFollowingUserStories(
                                        followingIds,
                                        index + 1
                                )
                );
    }

    // ==================================================
    // NEW: DELETE EXPIRED OWN STORIES
    // ==================================================
    //
    // Stories expire after 24 hours because every story stores
    // an expiresAt timestamp. This cleanup removes expired
    // stories belonging to the currently logged-in user when
    // Home is opened/resumed.
    //
    // The existing story loading/filtering code is unchanged.
    // ==================================================

    private void deleteExpiredOwnStories() {

        if (db == null ||
                currentUserId == null ||
                currentUserId.isEmpty()) {

            return;
        }

        final long now =
                System.currentTimeMillis();

        db.collection("stories")
                .whereEqualTo(
                        "userId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (snapshot == null ||
                                    snapshot.isEmpty()) {

                                return;
                            }

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                Long expiresAt =
                                        document.getLong(
                                                "expiresAt"
                                        );

                                if (expiresAt == null) {
                                    continue;
                                }

                                if (expiresAt <= now) {

                                    document.getReference()
                                            .delete();
                                }
                            }
                        }
                );
    }

    // ==================================================
    // FILTER ACTIVE STORIES
    // ==================================================

    private List<DocumentSnapshot> filterActiveStories(
            List<DocumentSnapshot> documents
    ) {

        List<DocumentSnapshot> active =
                new ArrayList<>();

        if (documents == null) {
            return active;
        }

        long now =
                System.currentTimeMillis();

        for (
                DocumentSnapshot document :
                documents
        ) {

            Long expiresAt =
                    document.getLong(
                            "expiresAt"
                    );

            if (expiresAt == null) {
                continue;
            }

            if (expiresAt > now) {

                active.add(
                        document
                );
            }
        }

        return active;
    }

    // ==================================================
    // SORT STORIES
    // ==================================================

    private void sortStoriesNewestFirst(
            List<DocumentSnapshot> stories
    ) {

        if (stories == null) {
            return;
        }

        stories.sort((a, b) -> {

            Long timeA =
                    a.getLong(
                            "timestamp"
                    );

            Long timeB =
                    b.getLong(
                            "timestamp"
                    );

            if (timeA == null) {
                timeA = 0L;
            }

            if (timeB == null) {
                timeB = 0L;
            }

            return Long.compare(
                    timeB,
                    timeA
            );
        });
    }

    // ==================================================
    // ADD STORY BUBBLE
    // ==================================================

    private void addStoryBubble(
            String userId,
            String username,
            String profileImage,
            List<DocumentSnapshot> stories,
            boolean isMine
    ) {

        if (storyRow == null) {
            return;
        }

        // ==================================================
        // HAS STORY?
        // ==================================================

        boolean hasStories =
                stories != null &&
                        !stories.isEmpty();

        // ==================================================
        // CHECK UNSEEN STORY
        // ==================================================

        boolean hasUnseenStory =
                false;

        if (hasStories) {

            for (
                    DocumentSnapshot story :
                    stories
            ) {

                if (!hasBeenViewed(
                        story
                )) {

                    hasUnseenStory =
                            true;

                    break;
                }
            }
        }

        // ==================================================
        // OUTER ITEM
        // ==================================================

        LinearLayout item =
                new LinearLayout(
                        this
                );

        item.setOrientation(
                LinearLayout.VERTICAL
        );

        item.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams
                itemParams =
                new LinearLayout.LayoutParams(
                        dp(78),
                        dp(104)
                );

        item.setLayoutParams(
                itemParams
        );

        // ==================================================
        // STORY FRAME
        // ==================================================

        FrameLayout frame =
                new FrameLayout(
                        this
                );

        LinearLayout.LayoutParams
                frameParams =
                new LinearLayout.LayoutParams(
                        dp(70),
                        dp(70)
                );

        frameParams.gravity =
                Gravity.CENTER;

        frame.setLayoutParams(
                frameParams
        );

        // ==================================================
        // STORY RING
        // ==================================================

        if (hasStories) {

            GradientDrawable ring;

            if (hasUnseenStory) {

                // ==========================================
                // UNSEEN = COLORFUL
                // ==========================================

                ring =
                        new GradientDrawable(
                                GradientDrawable.Orientation.BL_TR,
                                new int[]{
                                        Color.rgb(
                                                225,
                                                0,
                                                255
                                        ),
                                        Color.rgb(
                                                255,
                                                0,
                                                110
                                        ),
                                        Color.rgb(
                                                255,
                                                145,
                                                0
                                        ),
                                        Color.rgb(
                                                255,
                                                220,
                                                0
                                        )
                                }
                        );

            } else {

                // ==========================================
                // SEEN = WHITE
                // ==========================================

                ring =
                        new GradientDrawable();

                ring.setColor(
                        Color.WHITE
                );
            }

            ring.setShape(
                    GradientDrawable.OVAL
            );

            frame.setBackground(
                    ring
            );

            frame.setPadding(
                    dp(4),
                    dp(4),
                    dp(4),
                    dp(4)
            );

        } else {

            // ==========================================
            // NO STORY = NO RING
            // ==========================================

            frame.setBackgroundColor(
                    Color.TRANSPARENT
            );

            frame.setPadding(
                    0,
                    0,
                    0,
                    0
            );
        }

        // ==================================================
        // PROFILE IMAGE
        // ==================================================

        ImageView image =
                new ImageView(
                        this
                );

        image.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        GradientDrawable imageBackground =
                new GradientDrawable();

        imageBackground.setColor(
                Color.rgb(
                        40,
                        40,
                        43
                )
        );

        imageBackground.setShape(
                GradientDrawable.OVAL
        );

        image.setBackground(
                imageBackground
        );

        FrameLayout.LayoutParams
                imageParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        image.setLayoutParams(
                imageParams
        );

        if (profileImage != null &&
                !profileImage.trim().isEmpty()) {

            Glide.with(this)
                    .load(profileImage)
                    .placeholder(
                            android.R.drawable.ic_menu_myplaces
                    )
                    .error(
                            android.R.drawable.ic_menu_myplaces
                    )
                    .circleCrop()
                    .into(image);

        } else {

            image.setImageResource(
                    android.R.drawable.ic_menu_myplaces
            );
        }

        frame.addView(
                image
        );

        // ==================================================
        // YOUR STORY PLUS
        // ==================================================

        if (isMine) {

            TextView plus =
                    new TextView(
                            this
                    );

            plus.setText(
                    "+"
            );

            plus.setTextColor(
                    Color.WHITE
            );

            plus.setTextSize(
                    13
            );

            plus.setGravity(
                    Gravity.CENTER
            );

            GradientDrawable plusBackground =
                    new GradientDrawable();

            plusBackground.setColor(
                    Color.rgb(
                            0,
                            149,
                            246
                    )
            );

            plusBackground.setShape(
                    GradientDrawable.OVAL
            );

            plus.setBackground(
                    plusBackground
            );

            FrameLayout.LayoutParams
                    plusParams =
                    new FrameLayout.LayoutParams(
                            dp(21),
                            dp(21)
                    );

            plusParams.gravity =
                    Gravity.BOTTOM |
                            Gravity.END;

            plus.setLayoutParams(
                    plusParams
            );

            frame.addView(
                    plus
            );
        }

        // ==================================================
        // STORY CLICK
        // ==================================================

        frame.setOnClickListener(
                v -> {

                    if (hasStories) {

                        // ==================================
                        // OPEN STORY
                        // ==================================

                        openStoryViewer(
                                stories
                        );

                    } else if (isMine) {

                        // ==================================
                        // ADD STORY
                        // ==================================

                        showStoryTypeDialog();
                    }
                }
        );

        // ==================================================
        // USERNAME
        // ==================================================

        TextView usernameText =
                new TextView(
                        this
                );

        String displayUsername =
                username != null &&
                        !username.trim().isEmpty()
                        ? username
                        : (
                        isMine
                        ? "Your story"
                        : "User"
                );

        usernameText.setText(
                displayUsername
        );

        usernameText.setTextColor(
                Color.WHITE
        );

        usernameText.setTextSize(
                10
        );

        usernameText.setGravity(
                Gravity.CENTER
        );

        usernameText.setSingleLine(
                true
        );

        usernameText.setEllipsize(
                TextUtils.TruncateAt.END
        );

        LinearLayout.LayoutParams
                textParams =
                new LinearLayout.LayoutParams(
                        dp(74),
                        dp(25)
                );

        textParams.gravity =
                Gravity.CENTER;

        usernameText.setLayoutParams(
                textParams
        );

        // ==================================================
        // ADD TO STORY ROW
        // ==================================================

        item.addView(
                frame
        );

        item.addView(
                usernameText
        );

        storyRow.addView(
                item
        );
    }

    // ==================================================
    // SHOW STORY UPLOAD LOADER
    //
    // Loader is INSIDE the profile photo.
    // ==================================================

    private void showStoryUploadLoader() {

        if (storyRow == null) {
            return;
        }

        // Your Story is always the first story item.
        if (storyRow.getChildCount() == 0) {
            return;
        }

        View yourStoryItem =
                storyRow.getChildAt(0);

        if (!(yourStoryItem
                instanceof LinearLayout)) {

            return;
        }

        LinearLayout item =
                (LinearLayout)
                        yourStoryItem;

        if (item.getChildCount() == 0) {
            return;
        }

        View firstChild =
                item.getChildAt(0);

        if (!(firstChild instanceof FrameLayout)) {
            return;
        }

        FrameLayout storyFrame =
                (FrameLayout) firstChild;

        // Prevent duplicate spinner.
        if (storyUploadProgress != null) {
            return;
        }

        // ==================================================
        // CREATE CIRCULAR SPINNER
        // ==================================================

        storyUploadProgress =
                new ProgressBar(
                        this
                );

        storyUploadProgress.setIndeterminate(
                true
        );

        // ==================================================
        // SPINNER SIZE
        // ==================================================

        FrameLayout.LayoutParams
                progressParams =
                new FrameLayout.LayoutParams(
                        dp(30),
                        dp(30)
                );

        progressParams.gravity =
                Gravity.CENTER;

        storyUploadProgress.setLayoutParams(
                progressParams
        );

        // ==================================================
        // TRANSPARENT BACKGROUND
        // ==================================================

        storyUploadProgress.setBackgroundColor(
                Color.TRANSPARENT
        );

        // ==================================================
        // WHITE ROTATING CIRCLE
        // ==================================================

        storyUploadProgress
                .getIndeterminateDrawable()
                .setTint(
                        Color.WHITE
                );

        // ==================================================
        // ADD OVER PHOTO
        // ==================================================

        storyFrame.addView(
                storyUploadProgress
        );
    }

    // ==================================================
    // HIDE STORY UPLOAD LOADER
    // ==================================================

    private void hideStoryUploadLoader() {

        if (storyUploadProgress == null) {
            return;
        }

        if (storyUploadProgress.getParent()
                instanceof FrameLayout) {

            FrameLayout parent =
                    (FrameLayout)
                            storyUploadProgress
                                    .getParent();

            parent.removeView(
                    storyUploadProgress
            );
        }

        storyUploadProgress =
                null;
    }

    // ==================================================
    // CHECK WHETHER STORY WAS VIEWED
    // ==================================================

    private boolean hasBeenViewed(
            DocumentSnapshot story
    ) {

        if (story == null) {
            return false;
        }

        Object viewedByObject =
                story.get(
                        "viewedBy"
                );

        if (!(viewedByObject instanceof List)) {

            return false;
        }

        for (
                Object uid :
                (List<?>) viewedByObject
        ) {

            if (uid == null) {
                continue;
            }

            if (Objects.equals(currentUserId, String.valueOf(uid))) {

                return true;
            }
        }

        return false;
    }

    // ==================================================
    // OPEN YOUR STORY OR ADD
    // ==================================================

    private void openOwnStoryOrAdd() {

        db.collection("stories")
                .whereEqualTo(
                        "userId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            List<DocumentSnapshot> stories =
                                    filterActiveStories(
                                            snapshot.getDocuments()
                                    );

                            sortStoriesNewestFirst(
                                    stories
                            );

                            if (stories.isEmpty()) {

                                showStoryTypeDialog();

                            } else {

                                openStoryViewer(
                                        stories
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e ->
                                showStoryTypeDialog()
                );
    }

    // ==================================================
    // STORY TYPE DIALOG
    // ==================================================

    private void showStoryTypeDialog() {

        String[] options = {
                "Photo",
                "Video"
        };

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Add Story"
                )
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                selectedStoryType =
                                        "image";

                                storyPicker.launch(
                                        "image/*"
                                );

                            } else {

                                selectedStoryType =
                                        "video";

                                storyPicker.launch(
                                        "video/*"
                                );
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // ==================================================
    // UPLOAD STORY
    // ==================================================

    private void uploadStory(
            Uri uri,
            String mediaType
    ) {

        if (uri == null) {
            return;
        }

        // ==================================================
        // FINAL MEDIA TYPE
        // ==================================================

        final String finalMediaType =
                (
                        mediaType == null ||
                                mediaType.isEmpty()
                )
                        ? "image"
                        : mediaType;

        // ==================================================
        // SHOW LOADER INSIDE PHOTO
        // ==================================================

        showStoryUploadLoader();

        Toast.makeText(
                this,
                "Uploading story...",
                Toast.LENGTH_SHORT
        ).show();

        // ==================================================
        // CLOUDINARY UPLOAD
        // ==================================================

        MediaManager
                .get()
                .upload(
                        uri
                )
                .unsigned(
                        UPLOAD_PRESET
                )
                .callback(
                        new UploadCallback() {

                            @Override
                            public void onStart(
                                    String requestId
                            ) {
                            }

                            @Override
                            public void onProgress(
                                    String requestId,
                                    long bytes,
                                    long totalBytes
                            ) {
                            }

                            @Override
                            public void onSuccess(
                                    String requestId,
                                    Map resultData
                            ) {

                                Object urlObject =
                                        resultData.get(
                                                "secure_url"
                                        );

                                if (urlObject == null) {

                                    hideStoryUploadLoader();

                                    showStoryError(
                                            "Upload URL missing"
                                    );

                                    return;
                                }

                                String mediaUrl =
                                        String.valueOf(
                                                urlObject
                                        );

                                saveStoryToFirestore(
                                        mediaUrl,
                                        finalMediaType
                                );
                            }

                            @Override
                            public void onError(
                                    String requestId,
                                    ErrorInfo error
                            ) {

                                hideStoryUploadLoader();

                                String message =
                                        "Story upload failed";

                                if (error != null &&
                                        error.getDescription() != null &&
                                        !error.getDescription()
                                                .isEmpty()) {

                                    message =
                                            error.getDescription();
                                }

                                showStoryError(
                                        message
                                );
                            }

                            @Override
                            public void onReschedule(
                                    String requestId,
                                    ErrorInfo error
                            ) {
                            }
                        }
                )
                .dispatch();
    }

    // ==================================================
    // SAVE STORY TO FIRESTORE
    // ==================================================

    private void saveStoryToFirestore(
            String mediaUrl,
            String mediaType
    ) {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            String username =
                                    getSafeString(
                                            userDocument,
                                            "userId"
                                    );

                            if (username.isEmpty()) {

                                username =
                                        getSafeString(
                                                userDocument,
                                                "username"
                                        );
                            }

                            String name =
                                    getSafeString(
                                            userDocument,
                                            "name"
                                    );

                            String profileImage =
                                    getSafeString(
                                            userDocument,
                                            "profileImage"
                                    );

                            long timestamp =
                                    System.currentTimeMillis();

                            long expiresAt =
                                    timestamp +
                                            (
                                                    24L *
                                                            60L *
                                                            60L *
                                                            1000L
                                            );

                            Map<String, Object> story =
                                    new HashMap<>();

                            story.put(
                                    "userId",
                                    currentUserId
                            );

                            story.put(
                                    "username",
                                    username
                            );

                            story.put(
                                    "name",
                                    name
                            );

                            story.put(
                                    "profileImage",
                                    profileImage
                            );

                            story.put(
                                    "mediaUrl",
                                    mediaUrl
                            );

                            story.put(
                                    "mediaType",
                                    mediaType
                            );

                            story.put(
                                    "timestamp",
                                    timestamp
                            );

                            story.put(
                                    "expiresAt",
                                    expiresAt
                            );

                            story.put(
                                    "viewedBy",
                                    new ArrayList<String>()
                            );

                            db.collection("stories")
                                    .add(
                                            story
                                    )
                                    .addOnSuccessListener(
                                            documentReference -> {

                                                // ==================================
                                                // UPLOAD FINISHED
                                                // ==================================

                                                hideStoryUploadLoader();

                                                Toast.makeText(
                                                        homepage.this,
                                                        "Story added",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                // ==================================
                                                // RELOAD STORIES
                                                // ==================================

                                                loadStories();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> {

                                                hideStoryUploadLoader();

                                                showStoryError(
                                                        e.getMessage() != null
                                                                ? e.getMessage()
                                                                : "Could not save story"
                                                );
                                            }
                                    );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            hideStoryUploadLoader();

                            showStoryError(
                                    "Could not load profile"
                            );
                        }
                );
    }

    // ==================================================
    // OPEN STORY VIEWER
    // ==================================================

    private void openStoryViewer(
            List<DocumentSnapshot> stories
    ) {

        if (stories == null ||
                stories.isEmpty()) {

            return;
        }

        ArrayList<String> storyIds =
                new ArrayList<>();

        ArrayList<String> mediaUrls =
                new ArrayList<>();

        ArrayList<String> mediaTypes =
                new ArrayList<>();

        ArrayList<String> usernames =
                new ArrayList<>();

        ArrayList<String> profileImages =
                new ArrayList<>();

        for (
                DocumentSnapshot story :
                stories
        ) {

            if (story == null) {
                continue;
            }

            String mediaUrl =
                    getSafeString(
                            story,
                            "mediaUrl"
                    );

            if (mediaUrl.isEmpty()) {
                continue;
            }

            storyIds.add(
                    story.getId()
            );

            mediaUrls.add(
                    mediaUrl
            );

            String mediaType =
                    getSafeString(
                            story,
                            "mediaType"
                    );

            if (mediaType.isEmpty()) {

                mediaType =
                        "image";
            }

            mediaTypes.add(
                    mediaType
            );

            usernames.add(
                    getSafeString(
                            story,
                            "username"
                    )
            );

            profileImages.add(
                    getSafeString(
                            story,
                            "profileImage"
                    )
            );
        }

        if (mediaUrls.isEmpty()) {
            return;
        }

        Intent intent =
                new Intent(
                        homepage.this,
                        StoryViewerActivity.class
                );

        intent.putStringArrayListExtra(
                "storyIds",
                storyIds
        );

        intent.putStringArrayListExtra(
                "mediaUrls",
                mediaUrls
        );

        intent.putStringArrayListExtra(
                "mediaTypes",
                mediaTypes
        );

        intent.putStringArrayListExtra(
                "usernames",
                usernames
        );

        intent.putStringArrayListExtra(
                "profileImages",
                profileImages
        );

        startActivity(
                intent
        );
    }

    // ==================================================
    // SAFE FIRESTORE STRING
    // ==================================================

    private String getSafeString(
            DocumentSnapshot document,
            String field
    ) {

        if (document == null) {
            return "";
        }

        return Objects.requireNonNullElse(document.getString(field), "");
    }

    // ==================================================
    // ERROR
    // ==================================================

    private void showStoryError(
            String message
    ) {

        if (isFinishing()) {
            return;
        }

        Toast.makeText(
                this,
                message != null &&
                        !message.trim().isEmpty()
                        ? message
                        : "Story failed",
                Toast.LENGTH_LONG
        ).show();
    }

    // ==================================================
    // NOTIFICATIONS
    // ==================================================

    private void setupNotifications() {

        if (notificationContainer != null) {
            notificationContainer.setOnClickListener(
                    v -> openNotifications()
            );
        }

        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(
                    v -> openNotifications()
            );
        }

        loadUnreadNotificationCount();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
        try {
            Intent serviceIntent = new Intent(this, FrenzoPushService.class);
            startService(serviceIntent);
        } catch (Exception ignored) {
        }
    }

    private void loadUnreadNotificationCount() {

        if (db == null || currentUserId == null ||
                currentUserId.isEmpty() || notificationBadge == null) {
            return;
        }

        db.collection("notifications")
                .whereEqualTo(
                        "toUserId",
                        currentUserId
                )
                .addSnapshotListener(
                        (snapshot, error) -> {

                            if (error != null || snapshot == null) {
                                return;
                            }

                            int unreadCount = 0;

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                Boolean read =
                                        document.getBoolean("read");

                                if (read == null || !read) {
                                    unreadCount++;
                                }
                            }

                            updateNotificationBadge(
                                    unreadCount
                            );
                        }
                );
    }

    private void updateNotificationBadge(
            int count
    ) {

        if (notificationBadge == null) {
            return;
        }

        if (count <= 0) {
            notificationBadge.setVisibility(
                    View.GONE
            );
            return;
        }

        notificationBadge.setVisibility(
                View.VISIBLE
        );

        notificationBadge.setText(
                count > 99
                        ? "99+"
                        : String.valueOf(count)
        );
    }

    private void openNotifications() {
        startActivity(new Intent(homepage.this, NotificationsActivity.class));
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
                        // Already on Home
                    }
            );
        }

        // ==================================================
        // CREATE
        // ==================================================

        if (btnCreate != null) {

            btnCreate.setOnClickListener(
                    v -> {

                        startActivity(
                                new Intent(
                                        homepage.this,
                                        ReelsActivity.class
                                )
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

                        startActivity(
                                new Intent(
                                        homepage.this,
                                        ChatListActivity.class
                                )
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

                        startActivity(
                                new Intent(
                                        homepage.this,
                                        Search.class
                                )
                        );
                    }
            );
        }

        // ==================================================
        // PROFILE
        // ==================================================

        if (btnProfile != null) {

            btnProfile.setOnClickListener(
                    v -> startActivity(
                            new Intent(
                                    homepage.this,
                                    profilepage.class
                            )
                    )
            );
        }
    }


    // ==================================================
    // LOAD POSTS
    // ==================================================

    private void loadPosts() {

        if (db == null) {
            return;
        }

        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        Toast.makeText(
                                homepage.this,
                                "Error loading posts: " + error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    postList.clear();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        Post post = document.toObject(Post.class);

                        if (post != null) {

                            // Set Document ID as Post ID
                            post.setPostId(document.getId());

                            postList.add(post);
                        }
                    }

                    if (postAdapter != null) {
                        postAdapter.updateList(
                                new ArrayList<>(postList)
                        );
                    }
                });
    }

    private GestureDetector horizontalGestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        horizontalGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}

