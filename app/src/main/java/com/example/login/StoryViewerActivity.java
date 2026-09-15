package com.example.login;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class StoryViewerActivity extends AppCompatActivity {

    // ==================================================
    // STORY MEDIA
    // ==================================================

    private ImageView storyImage;
    private VideoView storyVideo;

    private TextView storyUsername;

    private ImageButton btnClose;
    private ImageButton btnLike;

    private TextView storyViewCount;

    private ProgressBar progressBar;

    // ==================================================
    // VIEWERS PANEL
    // ==================================================

    private LinearLayout viewersPanel;
    private LinearLayout viewersList;

    private TextView viewersTitle;
    private TextView emptyViewersText;

    // ==================================================
    // STORY DATA
    // ==================================================

    private ArrayList<String> storyIds;

    private ArrayList<String> mediaUrls;
    private ArrayList<String> mediaTypes;

    private ArrayList<String> usernames;

    private int currentIndex = 0;

    // ==================================================
    // FIREBASE
    // ==================================================

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;

    // ==================================================
    // HANDLER
    // ==================================================

    private final Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );

    private Runnable currentProgressRunnable;

    private final Runnable nextRunnable =
            () -> {

                if (mediaUrls == null) {
                    return;
                }

                if (currentIndex <
                        mediaUrls.size() - 1) {

                    currentIndex++;

                    showCurrentStory();

                } else {

                    finish();
                }
            };

    // ==================================================
    // GESTURE
    // ==================================================

    private GestureDetector gestureDetector;

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
                R.layout.activity_story_viewer
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
        // FIND VIEWS
        // ==================================================

        storyImage =
                findViewById(
                        R.id.storyImage
                );

        storyVideo =
                findViewById(
                        R.id.storyVideo
                );

        storyUsername =
                findViewById(
                        R.id.storyUsername
                );

        btnClose =
                findViewById(
                        R.id.btnCloseStory
                );

        btnLike =
                findViewById(
                        R.id.btnLikeStory
                );

        storyViewCount =
                findViewById(
                        R.id.storyViewCount
                );

        progressBar =
                findViewById(
                        R.id.storyProgress
                );

        viewersPanel =
                findViewById(
                        R.id.viewersPanel
                );

        viewersList =
                findViewById(
                        R.id.viewersList
                );

        viewersTitle =
                findViewById(
                        R.id.viewersTitle
                );

        emptyViewersText =
                findViewById(
                        R.id.emptyViewersText
                );

        // ==================================================
        // GET INTENT DATA
        // ==================================================

        storyIds =
                getIntent()
                        .getStringArrayListExtra(
                                "storyIds"
                        );

        mediaUrls =
                getIntent()
                        .getStringArrayListExtra(
                                "mediaUrls"
                        );

        mediaTypes =
                getIntent()
                        .getStringArrayListExtra(
                                "mediaTypes"
                        );

        usernames =
                getIntent()
                        .getStringArrayListExtra(
                                "usernames"
                        );

        // ==================================================
        // VALIDATE
        // ==================================================

        if (mediaUrls == null ||
                mediaUrls.isEmpty()) {

            finish();

            return;
        }

        // ==================================================
        // CLOSE
        // ==================================================

        btnClose.setOnClickListener(
                v -> finish()
        );

        // ==================================================
        // LIKE
        // ==================================================

        btnLike.setOnClickListener(
                v -> toggleLike()
        );

        // ==================================================
        // TAP TO NEXT
        // ==================================================

        storyImage.setOnClickListener(
                v -> {

                    if (!isViewersPanelVisible()) {
                        goNext();
                    }
                }
        );

        storyVideo.setOnClickListener(
                v -> {

                    if (!isViewersPanelVisible()) {
                        goNext();
                    }
                }
        );

        // ==================================================
        // SWIPE DETECTOR
        // ==================================================

        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {

                            private static final int SWIPE_THRESHOLD =
                                    100;

                            private static final int SWIPE_VELOCITY_THRESHOLD =
                                    100;

                            @Override
                            public boolean onDown(
                                    MotionEvent e
                            ) {

                                return true;
                            }

                            @Override
                            public boolean onFling(
                                    MotionEvent e1,
                                    MotionEvent e2,
                                    float velocityX,
                                    float velocityY
                            ) {

                                if (e1 == null ||
                                        e2 == null) {

                                    return false;
                                }

                                float diffY =
                                        e2.getY() -
                                                e1.getY();

                                float diffX =
                                        e2.getX() -
                                                e1.getX();

                                if (Math.abs(diffY) >
                                        Math.abs(diffX) &&
                                        Math.abs(diffY) >
                                                SWIPE_THRESHOLD &&
                                        Math.abs(velocityY) >
                                                SWIPE_VELOCITY_THRESHOLD) {

                                    if (diffY < 0) {

                                        // Swipe UP
                                        showViewersPanel();

                                    } else {

                                        // Swipe DOWN
                                        hideViewersPanel();
                                    }

                                    return true;
                                }

                                return false;
                            }
                        }
                );

        // ==================================================
        // SHOW FIRST STORY
        // ==================================================

        showCurrentStory();
    }

    // ==================================================
    // TOUCH
    // ==================================================

    @Override
    public boolean dispatchTouchEvent(
            MotionEvent event
    ) {

        if (gestureDetector != null) {

            gestureDetector.onTouchEvent(
                    event
            );
        }

        return super.dispatchTouchEvent(
                event
        );
    }

    // ==================================================
    // SHOW CURRENT STORY
    // ==================================================

    private void showCurrentStory() {

        handler.removeCallbacks(
                nextRunnable
        );

        if (currentProgressRunnable != null) {

            handler.removeCallbacks(
                    currentProgressRunnable
            );

            currentProgressRunnable = null;
        }

        hideViewersPanel();

        storyImage.setVisibility(
                View.GONE
        );

        storyVideo.setVisibility(
                View.GONE
        );

        progressBar.setProgress(
                0
        );

        if (currentIndex < 0 ||
                currentIndex >= mediaUrls.size()) {

            finish();

            return;
        }

        String url =
                mediaUrls.get(
                        currentIndex
                );

        String type =
                mediaTypes != null &&
                        currentIndex <
                                mediaTypes.size()
                        ? mediaTypes.get(
                        currentIndex
                )
                        : "image";

        String username =
                usernames != null &&
                        currentIndex <
                                usernames.size()
                        ? usernames.get(
                        currentIndex
                )
                        : "User";

        storyUsername.setText(
                username
        );

        // ==================================================
        // REGISTER VIEW
        // ==================================================

        registerStoryView();

        // ==================================================
        // LOAD LIKE STATE + VIEW COUNT
        // ==================================================

        loadStoryStats();

        // ==================================================
        // SHOW MEDIA
        // ==================================================

        if ("video".equalsIgnoreCase(type)) {

            showVideo(
                    url
            );

        } else {

            showImage(
                    url
            );
        }
    }

    // ==================================================
    // REGISTER STORY VIEW
    // ==================================================

    private void registerStoryView() {

        if (storyIds == null ||
                currentIndex >= storyIds.size()) {

            return;
        }

        String storyId =
                storyIds.get(
                        currentIndex
                );

        if (storyId == null ||
                storyId.trim().isEmpty()) {

            return;
        }

        db.collection("stories")
                .document(storyId)
                .update(
                        "viewedBy",
                        FieldValue.arrayUnion(
                                currentUserId
                        )
                )
                .addOnSuccessListener(
                        unused ->
                                loadStoryStats()
                );
    }

    // ==================================================
    // LOAD STORY STATS
    // ==================================================

    private void loadStoryStats() {

        if (storyIds == null ||
                currentIndex >= storyIds.size()) {

            return;
        }

        String storyId =
                storyIds.get(
                        currentIndex
                );

        if (storyId == null ||
                storyId.trim().isEmpty()) {

            return;
        }

        db.collection("stories")
                .document(storyId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {
                                return;
                            }

                            List<String> viewers =
                                    getStringList(
                                            document,
                                            "viewedBy"
                                    );

                            List<String> likes =
                                    getStringList(
                                            document,
                                            "likes"
                                    );

                            int viewCount =
                                    viewers.size();

                            storyViewCount.setText(
                                    String.format(Locale.getDefault(), "👁 %d", viewCount)
                            );

                            boolean liked =
                                    likes.contains(
                                            currentUserId
                                    );

                            updateLikeButton(
                                    liked
                            );
                        }
                );
    }

    // ==================================================
    // LIKE / UNLIKE
    // ==================================================

    private void toggleLike() {

        if (storyIds == null ||
                currentIndex >= storyIds.size()) {

            return;
        }

        String storyId =
                storyIds.get(
                        currentIndex
                );

        if (storyId == null ||
                storyId.trim().isEmpty()) {

            return;
        }

        db.collection("stories")
                .document(storyId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {
                                return;
                            }

                            List<String> likes =
                                    getStringList(
                                            document,
                                            "likes"
                                    );

                            boolean alreadyLiked =
                                    likes.contains(
                                            currentUserId
                                    );

                            if (alreadyLiked) {

                                db.collection("stories")
                                        .document(storyId)
                                        .update(
                                                "likes",
                                                FieldValue.arrayRemove(
                                                        currentUserId
                                                )
                                        )
                                        .addOnSuccessListener(
                                                unused -> {

                                                    updateLikeButton(
                                                            false
                                                    );

                                                    loadStoryStats();
                                                }
                                        );

                            } else {

                                db.collection("stories")
                                        .document(storyId)
                                        .update(
                                                "likes",
                                                FieldValue.arrayUnion(
                                                        currentUserId
                                                )
                                        )
                                        .addOnSuccessListener(
                                                unused -> {

                                                    updateLikeButton(
                                                            true
                                                    );

                                                    loadStoryStats();
                                                }
                                        );
                            }
                        }
                );
    }

    // ==================================================
    // LIKE BUTTON UI
    // ==================================================

    private void updateLikeButton(
            boolean liked
    ) {

        if (btnLike == null) {
            return;
        }

        if (liked) {

            btnLike.setImageResource(
                    android.R.drawable.btn_star_big_on
            );

            btnLike.setColorFilter(
                    Color.RED
            );

        } else {

            btnLike.setImageResource(
                    android.R.drawable.btn_star_big_off
            );

            btnLike.setColorFilter(
                    Color.WHITE
            );
        }
    }

    // ==================================================
    // SHOW IMAGE
    // ==================================================

    private void showImage(
            String url
    ) {

        storyImage.setVisibility(
                View.VISIBLE
        );

        Glide.with(this)
                .load(url)
                .into(storyImage);

        progressBar.setMax(
                100
        );

        progressBar.setProgress(
                0
        );

        final long duration =
                5000;

        final long start =
                System.currentTimeMillis();

        currentProgressRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        long elapsed =
                                System.currentTimeMillis()
                                        - start;

                        int progress =
                                (int)
                                        (
                                                Math.min(
                                                        100,
                                                        (
                                                                elapsed *
                                                                        100
                                                        ) /
                                                                duration
                                                )
                                        );

                        progressBar.setProgress(
                                progress
                        );

                        if (elapsed <
                                duration) {

                            handler.postDelayed(
                                    this,
                                    50
                            );

                        } else {

                            goNext();
                        }
                    }
                };

        handler.post(
                currentProgressRunnable
        );
    }

    // ==================================================
    // SHOW VIDEO
    // ==================================================

    private void showVideo(
            String url
    ) {

        storyVideo.setVisibility(
                View.VISIBLE
        );

        storyVideo.setVideoURI(
                Uri.parse(
                        url
                )
        );

        storyVideo.setOnPreparedListener(
                mp -> {

                    mp.setLooping(
                            false
                    );

                    int duration =
                            mp.getDuration();

                    progressBar.setMax(
                            100
                    );

                    progressBar.setProgress(
                            0
                    );

                    storyVideo.start();

                    updateVideoProgress(
                            duration
                    );
                }
        );

        storyVideo.setOnCompletionListener(
                mp -> goNext()
        );

        storyVideo.setOnErrorListener(
                (mp, what, extra) -> {

                    goNext();

                    return true;
                }
        );
    }

    // ==================================================
    // VIDEO PROGRESS
    // ==================================================

    private void updateVideoProgress(
            int duration
    ) {

        handler.postDelayed(
                new Runnable() {

                    @Override
                    public void run() {

                        if (!storyVideo.isPlaying()) {
                            return;
                        }

                        int position =
                                storyVideo.getCurrentPosition();

                        if (duration > 0) {

                            int progress =
                                    (
                                            position *
                                                    100
                                    ) /
                                            duration;

                            progressBar.setProgress(
                                    progress
                            );
                        }

                        handler.postDelayed(
                                this,
                                100
                        );
                    }
                },
                100
        );
    }

    // ==================================================
    // NEXT STORY
    // ==================================================

    private void goNext() {

        handler.removeCallbacks(
                nextRunnable
        );

        if (currentProgressRunnable != null) {

            handler.removeCallbacks(
                    currentProgressRunnable
            );

            currentProgressRunnable = null;
        }

        if (currentIndex <
                mediaUrls.size() - 1) {

            currentIndex++;

            showCurrentStory();

        } else {

            finish();
        }
    }

    // ==================================================
    // SHOW VIEWERS PANEL
    // ==================================================

    private void showViewersPanel() {

        if (viewersPanel == null) {
            return;
        }

        viewersPanel.setVisibility(
                View.VISIBLE
        );

        loadViewers();
    }

    // ==================================================
    // HIDE VIEWERS PANEL
    // ==================================================

    private void hideViewersPanel() {

        if (viewersPanel != null) {

            viewersPanel.setVisibility(
                    View.GONE
            );
        }
    }

    // ==================================================
    // PANEL CHECK
    // ==================================================

    private boolean isViewersPanelVisible() {

        return viewersPanel != null &&
                viewersPanel.getVisibility() ==
                        View.VISIBLE;
    }

    // ==================================================
    // LOAD VIEWERS
    // ==================================================

    private void loadViewers() {

        if (storyIds == null ||
                currentIndex >= storyIds.size()) {

            return;
        }

        String storyId =
                storyIds.get(
                        currentIndex
                );

        if (storyId == null ||
                storyId.trim().isEmpty()) {

            return;
        }

        db.collection("stories")
                .document(storyId)
                .get()
                .addOnSuccessListener(
                        storyDocument -> {

                            if (!storyDocument.exists()) {
                                return;
                            }

                            List<String> viewerIds =
                                    getStringList(
                                            storyDocument,
                                            "viewedBy"
                                    );

                            List<String> likes =
                                    getStringList(
                                            storyDocument,
                                            "likes"
                                    );

                            viewersList.removeAllViews();

                            viewersTitle.setText(
                                    String.format(Locale.getDefault(), "Views %d", viewerIds.size())
                            );

                            if (viewerIds.isEmpty()) {

                                emptyViewersText.setVisibility(
                                        View.VISIBLE
                                );

                                return;

                            } else {

                                emptyViewersText.setVisibility(
                                        View.GONE
                                );
                            }

                            for (
                                    String viewerId :
                                    viewerIds
                            ) {

                                loadViewerUser(
                                        viewerId,
                                        likes
                                );
                            }
                        }
                );
    }

    // ==================================================
    // LOAD VIEWER USER
    // ==================================================

    private void loadViewerUser(
            String viewerId,
            List<String> likes
    ) {

        if (viewerId == null ||
                viewerId.trim().isEmpty()) {

            return;
        }

        db.collection("users")
                .document(viewerId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            String username =
                                    getUserDisplayName(
                                            userDocument
                                    );

                            boolean liked =
                                    likes.contains(
                                            viewerId
                                    );

                            addViewerRow(
                                    username,
                                    liked
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                addViewerRow(
                                        "User",
                                        likes.contains(
                                                viewerId
                                        )
                                )
                );
    }

    // ==================================================
    // ADD VIEWER ROW
    // ==================================================

    private void addViewerRow(
            String username,
            boolean liked
    ) {

        LinearLayout row =
                new LinearLayout(
                        this
                );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                dp(16),
                dp(8),
                dp(16),
                dp(8)
        );

        TextView name =
                new TextView(
                        this
                );

        name.setText(
                username
        );

        name.setTextColor(
                Color.WHITE
        );

        name.setTextSize(
                15
        );

        LinearLayout.LayoutParams
                nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1
                );

        name.setLayoutParams(
                nameParams
        );

        row.addView(
                name
        );

        TextView like =
                new TextView(
                        this
                );

        like.setText(
                liked
                        ? "❤️"
                        : ""
        );

        like.setTextSize(
                20
        );

        row.addView(
                like
        );

        viewersList.addView(
                row
        );
    }

    // ==================================================
    // USER DISPLAY NAME
    // ==================================================

    private String getUserDisplayName(
            DocumentSnapshot document
    ) {

        if (document == null ||
                !document.exists()) {

            return "User";
        }

        String username =
                document.getString(
                        "username"
                );

        if (username != null &&
                !username.trim().isEmpty()) {

            return username;
        }

        String name =
                document.getString(
                        "name"
                );

        if (name != null &&
                !name.trim().isEmpty()) {

            return name;
        }

        String userId =
                document.getString(
                        "userId"
                );

        if (userId != null &&
                !userId.trim().isEmpty()) {

            return userId;
        }

        return "User";
    }

    // ==================================================
    // SAFE STRING LIST
    // ==================================================

    private List<String> getStringList(
            DocumentSnapshot document,
            String field
    ) {

        List<String> result =
                new ArrayList<>();

        if (document == null) {
            return result;
        }

        Object object =
                document.get(field);

        if (!(object instanceof List)) {
            return result;
        }

        for (
                Object item :
                (List<?>) object
        ) {

            if (item != null) {

                String value =
                        String.valueOf(
                                item
                        );

                if (!value.trim().isEmpty()) {

                    result.add(
                            value
                    );
                }
            }
        }

        return result;
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
    // DESTROY
    // ==================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(
                null
        );

        if (storyVideo != null) {

            storyVideo.stopPlayback();
        }

        super.onDestroy();
    }
}