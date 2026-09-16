package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private VideoView postVideo;

    private ImageButton btnBack;
    private ImageButton btnLike;
    private ImageButton btnComment;
    private ImageButton btnShare;
    private ImageButton btnMorePost;

    private TextView tvTitle;
    private TextView tvCaption;
    private TextView tvLikeCount;
    private TextView tvCommentCount;

    private View postHeader;
    private View actionLayout;
    private View postDetailRoot;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String postId;
    private String imageUrl;
    private String caption;
    private String mediaType = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews();

        postId = getIntent().getStringExtra("postId");
        imageUrl = getIntent().getStringExtra("imageUrl");
        caption = getIntent().getStringExtra("caption");

        String incomingType =
                getIntent().getStringExtra("mediaType");

        if (incomingType != null &&
                !incomingType.trim().isEmpty()) {
            mediaType = incomingType;
        }

        if (postId == null ||
                postId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Post ID missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        setupInsets();
        setupButtons();

        // Show the media immediately from Intent.
        showMedia();

        showCaption();

        // Then get the latest Firestore data.
        loadPost();

        loadLikeState();
        loadCommentCount();
    }

    // ============================================================
    // BIND VIEWS
    // ============================================================

    private void bindViews() {

        postDetailRoot =
                findViewById(R.id.postDetailRoot);

        postHeader =
                findViewById(R.id.postHeader);

        actionLayout =
                findViewById(R.id.actionLayout);

        postImage =
                findViewById(R.id.postImage);

        postVideo =
                findViewById(R.id.postVideo);

        btnBack =
                findViewById(R.id.btnBack);

        btnLike =
                findViewById(R.id.btnLike);

        btnComment =
                findViewById(R.id.btnComment);

        btnShare =
                findViewById(R.id.btnShare);

        btnMorePost =
                findViewById(R.id.btnMorePost);

        tvTitle =
                findViewById(R.id.tvTitle);

        tvCaption =
                findViewById(R.id.tvCaption);

        tvLikeCount =
                findViewById(R.id.tvLikeCount);

        tvCommentCount =
                findViewById(R.id.tvCommentCount);
    }

    // ============================================================
    // EDGE TO EDGE - ANDROID 14 / 15 / 16
    // ============================================================

    private void setupInsets() {

        if (postDetailRoot == null) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                postDetailRoot,
                (view, insets) -> {

                    Insets bars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );

                    if (postHeader != null &&
                            postHeader.getLayoutParams()
                                    instanceof ViewGroup.MarginLayoutParams) {

                        ViewGroup.MarginLayoutParams lp =
                                (ViewGroup.MarginLayoutParams)
                                        postHeader.getLayoutParams();

                        // Keep header below status bar.
                        lp.topMargin = bars.top;

                        postHeader.setLayoutParams(lp);
                    }

                    if (actionLayout != null &&
                            actionLayout.getLayoutParams()
                                    instanceof ViewGroup.MarginLayoutParams) {

                        ViewGroup.MarginLayoutParams lp =
                                (ViewGroup.MarginLayoutParams)
                                        actionLayout.getLayoutParams();

                        lp.bottomMargin =
                                dp(8) + bars.bottom;

                        actionLayout.setLayoutParams(lp);
                    }

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(
                postDetailRoot
        );
    }

    private int dp(int value) {
        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    // ============================================================
    // BUTTONS
    // ============================================================

    private void setupButtons() {

        if (btnBack != null) {
            btnBack.setOnClickListener(
                    v -> finish()
            );
        }

        if (btnLike != null) {
            btnLike.setOnClickListener(
                    v -> likePost()
            );
        }

        if (btnComment != null) {
            btnComment.setOnClickListener(
                    v -> openComments()
            );
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(
                    v -> showShareDialog()
            );
        }

        if (btnMorePost != null) {

            // Hidden until Firestore confirms ownership.
            btnMorePost.setVisibility(
                    View.GONE
            );

            btnMorePost.setOnClickListener(
                    v -> showDeletePostDialog()
            );
        }

        if (postVideo != null) {

            postVideo.setOnClickListener(
                    v -> {

                        if (postVideo.isPlaying()) {
                            postVideo.pause();
                        } else {
                            postVideo.start();
                        }
                    }
            );
        }
    }

    // ============================================================
    // LOAD POST
    // ============================================================

    private void loadPost() {

        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                Toast.makeText(
                                        this,
                                        "Post not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                                return;
                            }

                            String url =
                                    document.getString(
                                            "imageUrl"
                                    );

                            if (url != null &&
                                    !url.trim().isEmpty()) {

                                imageUrl = url;
                            }

                            String savedCaption =
                                    document.getString(
                                            "caption"
                                    );

                            if (savedCaption != null) {
                                caption = savedCaption;
                            }

                            String savedType =
                                    document.getString(
                                            "mediaType"
                                    );

                            if (savedType != null &&
                                    !savedType.trim().isEmpty()) {

                                mediaType =
                                        savedType;
                            }

                            // IMPORTANT:
                            // Video/reel detail title.
                            updateTitle();

                            showMedia();
                            showCaption();

                            // Owner-only delete option.
                            String ownerId =
                                    document.getString(
                                            "userId"
                                    );

                            String currentUid =
                                    auth.getCurrentUser() == null
                                            ? null
                                            : auth.getCurrentUser()
                                            .getUid();

                            if (btnMorePost != null) {

                                boolean owner =
                                        currentUid != null &&
                                                currentUid.equals(
                                                        ownerId
                                                );

                                btnMorePost.setVisibility(
                                        owner
                                                ? View.VISIBLE
                                                : View.GONE
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            updateTitle();
                            showMedia();
                            showCaption();

                            if (btnMorePost != null) {
                                btnMorePost.setVisibility(
                                        View.GONE
                                );
                            }
                        }
                );
    }

    // ============================================================
    // TITLE
    // ============================================================

    private void updateTitle() {

        if (tvTitle == null) {
            return;
        }

        if ("video".equalsIgnoreCase(
                mediaType
        )) {

            tvTitle.setText(
                    "Reels"
            );

        } else {

            tvTitle.setText(
                    "Post"
            );
        }
    }

    // ============================================================
    // SHOW PHOTO / VIDEO
    // ============================================================

    private void showMedia() {

        if (imageUrl == null ||
                imageUrl.trim().isEmpty()) {

            return;
        }

        boolean isVideo =
                "video".equalsIgnoreCase(
                        mediaType
                );

        if (isVideo) {

            // ---------------- VIDEO ----------------

            if (postImage != null) {

                Glide.with(this)
                        .clear(postImage);

                postImage.setVisibility(
                        View.GONE
                );
            }

            if (postVideo != null) {

                postVideo.stopPlayback();

                postVideo.setVisibility(
                        View.VISIBLE
                );

                // Make sure the VideoView surface is
                // above other media views.
                postVideo.bringToFront();

                postVideo.setBackgroundColor(
                        Color.BLACK
                );

                /*
                 * MediaController gives the user
                 * play/pause/seek controls.
                 */
                MediaController controller =
                        new MediaController(this);

                controller.setAnchorView(
                        postVideo
                );

                postVideo.setMediaController(
                        controller
                );

                Uri videoUri =
                        Uri.parse(
                                imageUrl
                        );

                postVideo.setVideoURI(
                        videoUri
                );

                postVideo.setOnPreparedListener(
                        mp -> {

                            mp.setLooping(false);

                            // Start only after the
                            // video surface is ready.
                            postVideo.start();
                        }
                );

                postVideo.setOnCompletionListener(
                        mp -> {
                            // Keep last frame / controller.
                        }
                );

                postVideo.setOnErrorListener(
                        (mp, what, extra) -> {

                            Toast.makeText(
                                    PostDetailActivity.this,
                                    "This video format cannot be played on this phone.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return true;
                        }
                );
            }

        } else {

            // ---------------- PHOTO ----------------

            if (postVideo != null) {
                postVideo.stopPlayback();
                postVideo.setVisibility(
                        View.GONE
                );
            }

            if (postImage != null) {

                postImage.setVisibility(
                        View.VISIBLE
                );

                postImage.bringToFront();

                Glide.with(this)
                        .load(imageUrl)
                        .fitCenter()
                        .into(postImage);
            }
        }
    }

    // ============================================================
    // CAPTION
    // ============================================================

    private void showCaption() {

        if (tvCaption == null) {
            return;
        }

        if (caption != null &&
                !caption.trim().isEmpty()) {

            tvCaption.setText(
                    caption
            );

            tvCaption.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvCaption.setVisibility(
                    View.GONE
            );
        }
    }

    // ============================================================
    // LIKE
    // ============================================================

    private void likePost() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(uid)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                db.collection("posts")
                                        .document(postId)
                                        .collection("likes")
                                        .document(uid)
                                        .delete()
                                        .addOnSuccessListener(
                                                v ->
                                                        loadLikeState()
                                        );

                            } else {

                                Map<String, Object> like =
                                        new HashMap<>();

                                like.put(
                                        "userId",
                                        uid
                                );

                                like.put(
                                        "timestamp",
                                        System.currentTimeMillis()
                                );

                                db.collection("posts")
                                        .document(postId)
                                        .collection("likes")
                                        .document(uid)
                                        .set(like)
                                        .addOnSuccessListener(
                                                v ->
                                                        loadLikeState()
                                        );
                            }
                        }
                );
    }

    private void loadLikeState() {

        if (tvLikeCount == null ||
                btnLike == null) {
            return;
        }

        db.collection("posts")
                .document(postId)
                .collection("likes")
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            tvLikeCount.setText(
                                    snapshot.size() +
                                            " Likes"
                            );

                            boolean liked =
                                    false;

                            if (auth.getCurrentUser() != null) {

                                String uid =
                                        auth.getCurrentUser()
                                                .getUid();

                                for (DocumentSnapshot document :
                                        snapshot.getDocuments()) {

                                    if (uid.equals(
                                            document.getId()
                                    )) {

                                        liked = true;
                                        break;
                                    }
                                }
                            }

                            btnLike.setColorFilter(
                                    liked
                                            ? Color.RED
                                            : Color.WHITE
                            );
                        }
                );
    }

    // ============================================================
    // COMMENTS
    // ============================================================

    private void openComments() {

        Intent intent =
                new Intent(
                        this,
                        CommentsActivity.class
                );

        intent.putExtra(
                "postId",
                postId
        );

        startActivity(intent);
    }

    private void loadCommentCount() {

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (tvCommentCount != null) {

                                tvCommentCount.setText(
                                        snapshot.size() +
                                                " Comments"
                                );
                            }
                        }
                );
    }

    // ============================================================
    // SHARE
    // ============================================================

    private void showShareDialog() {

        String[] options = {
                "Add to Story",
                "Send in Chat"
        };

        new AlertDialog.Builder(this)
                .setTitle("Share")
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                shareToStory();

                            } else {

                                shareToChat();
                            }
                        }
                )
                .show();
    }

    private void shareToStory() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        Intent intent =
                new Intent(
                        this,
                        StoryEditorActivity.class
                );

        intent.putExtra(
                "mediaUrl",
                imageUrl
        );

        intent.putExtra(
                "mediaType",
                "video".equalsIgnoreCase(
                        mediaType
                )
                        ? "video"
                        : "image"
        );

        intent.putExtra(
                "postId",
                postId
        );

        intent.putExtra(
                "initialCaption",
                caption == null
                        ? ""
                        : caption
        );

        startActivity(intent);
    }

    private void shareToChat() {

        Intent intent =
                new Intent(
                        this,
                        ChatListActivity.class
                );

        intent.putExtra(
                "sharePost",
                true
        );

        intent.putExtra(
                "sharedPostId",
                postId
        );

        intent.putExtra(
                "sharedPostImage",
                imageUrl
        );

        intent.putExtra(
                "sharedPostCaption",
                caption
        );

        intent.putExtra(
                "sharedPostMediaType",
                mediaType
        );

        startActivity(intent);
    }

    // ============================================================
    // DELETE POST
    // ============================================================

    private void showDeletePostDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete post?"
                )
                .setMessage(
                        "This post will be permanently deleted."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deletePost()
                )
                .show();
    }

    private void deletePost() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        final String uid =
                auth.getCurrentUser().getUid();

        /*
         * Verify ownership again before deleting.
         * Firestore rules remain the final security layer.
         */
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                Toast.makeText(
                                        this,
                                        "Post already deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                                return;
                            }

                            String ownerId =
                                    document.getString(
                                            "userId"
                                    );

                            if (!uid.equals(
                                    ownerId
                            )) {

                                Toast.makeText(
                                        this,
                                        "Only the post owner can delete it",
                                        Toast.LENGTH_LONG
                                ).show();

                                if (btnMorePost != null) {

                                    btnMorePost.setVisibility(
                                            View.GONE
                                    );
                                }

                                return;
                            }

                            db.collection("posts")
                                    .document(postId)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Post deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                setResult(
                                                        RESULT_OK
                                                );

                                                finish();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e ->
                                                    Toast.makeText(
                                                            this,
                                                            "Delete failed: " +
                                                                    e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show()
                                    );
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Could not verify post owner",
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (postId != null &&
                !postId.isEmpty()) {

            loadLikeState();
            loadCommentCount();
        }
    }

    @Override
    protected void onPause() {

        if (postVideo != null &&
                postVideo.isPlaying()) {

            postVideo.pause();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (postVideo != null) {
            postVideo.stopPlayback();
        }

        super.onDestroy();
    }
}
