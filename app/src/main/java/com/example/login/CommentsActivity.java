package com.example.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView recyclerComments;
    private EditText editComment;
    private ImageButton btnSend;
    private TextView tvNoComments;
    private TextView tvCommentCount;
    private LinearLayout replyBar;
    private TextView tvReplyingTo;
    private TextView btnCancelReply;
    private LinearLayout commentsHeader;
    private LinearLayout commentComposer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String postId;

    private final ArrayList<CommentModel> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;

    private String replyingToCommentId = "";
    private String replyingToUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_comments);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = getIntent().getStringExtra("postId");

        btnBack = findViewById(R.id.btnBack);
        recyclerComments = findViewById(R.id.recyclerComments);
        editComment = findViewById(R.id.editComment);
        btnSend = findViewById(R.id.btnSend);
        tvNoComments = findViewById(R.id.tvNoComments);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        replyBar = findViewById(R.id.replyBar);
        tvReplyingTo = findViewById(R.id.tvReplyingTo);
        btnCancelReply = findViewById(R.id.btnCancelReply);
        commentsHeader = findViewById(R.id.commentsHeader);
        commentComposer = findViewById(R.id.commentComposer);

        commentAdapter = new CommentAdapter(commentList);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(commentAdapter);
        recyclerComments.setHasFixedSize(false);
        recyclerComments.setItemAnimator(null);
        recyclerComments.setClipToPadding(false);
        recyclerComments.setOverScrollMode(View.OVER_SCROLL_NEVER);

        btnBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> addComment());
        btnCancelReply.setOnClickListener(v -> cancelReply());

        setupEdgeToEdge();
        setupComposer();
        loadComments();
    }

    private void setupEdgeToEdge() {
        final View root = findViewById(R.id.commentsRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            boolean keyboardVisible =
                    insets.isVisible(WindowInsetsCompat.Type.ime());

            // Header gets the real status-bar space without overlapping content.
            if (commentsHeader != null) {
                commentsHeader.setPadding(
                        dp(8),
                        bars.top,
                        dp(12),
                        dp(6)
                );
            }

            // Composer stays above navigation bar and keyboard.
            if (commentComposer != null) {
                ViewGroup.LayoutParams raw = commentComposer.getLayoutParams();
                if (raw instanceof android.widget.FrameLayout.LayoutParams) {
                    android.widget.FrameLayout.LayoutParams lp =
                            (android.widget.FrameLayout.LayoutParams) raw;
                    lp.bottomMargin = keyboardVisible ? ime.bottom : bars.bottom;
                    commentComposer.setLayoutParams(lp);
                }
            }

            // Header height is 60dp + status bar inset.
            int headerHeight = dp(60) + bars.top;
            ViewGroup.LayoutParams headerParams =
                    commentsHeader.getLayoutParams();
            headerParams.height = headerHeight;
            commentsHeader.setLayoutParams(headerParams);

            // RecyclerView starts below the real header, including the status bar.
            ViewGroup.MarginLayoutParams listParams =
                    (ViewGroup.MarginLayoutParams) recyclerComments.getLayoutParams();
            listParams.topMargin = headerHeight;
            recyclerComments.setLayoutParams(listParams);

            // Reply bar stays directly above the composer.
            if (replyBar != null) {
                ViewGroup.MarginLayoutParams replyParams =
                        (ViewGroup.MarginLayoutParams) replyBar.getLayoutParams();
                replyParams.bottomMargin = dp(78) + (keyboardVisible ? ime.bottom : bars.bottom);
                replyBar.setLayoutParams(replyParams);
            }

            // Keep list above the fixed composer.
            int composerSpace = dp(82) + Math.max(bars.bottom, ime.bottom);
            recyclerComments.setPadding(
                    0,
                    0,
                    0,
                    composerSpace
            );

            if (keyboardVisible) {
                recyclerComments.postDelayed(() -> {
                    if (!commentList.isEmpty()) {
                        recyclerComments.scrollToPosition(
                                commentList.size() - 1
                        );
                    }
                }, 120);
            }

            return insets;
        });

        ViewCompat.requestApplyInsets(root);
    }

    private void setupComposer() {
        editComment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerComments.postDelayed(() -> {
                    if (!commentList.isEmpty()) {
                        recyclerComments.scrollToPosition(
                                commentList.size() - 1
                        );
                    }
                }, 180);
            }
        });

        editComment.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                updateSendButton();
            }

            @Override public void afterTextChanged(
                    android.text.Editable s) {}
        });

        updateSendButton();
    }

    private void updateSendButton() {
        if (btnSend == null) return;

        boolean hasText = editComment != null &&
                !editComment.getText().toString().trim().isEmpty();

        btnSend.setAlpha(hasText ? 1f : 0.35f);
        btnSend.setEnabled(hasText);
    }

    private void loadComments() {
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Post ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    commentList.clear();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String commentId = document.getId();
                        String userId = safeString(document.getString("userId"));
                        String comment = safeString(document.getString("comment"));
                        Long timestamp = document.getLong("timestamp");
                        String parentCommentId =
                                safeString(document.getString("parentCommentId"));
                        String parentUsername =
                                safeString(document.getString("parentUsername"));

                        commentList.add(new CommentModel(
                                commentId,
                                userId,
                                comment,
                                timestamp,
                                parentCommentId,
                                parentUsername
                        ));
                    }

                    commentAdapter.notifyDataSetChanged();

                    int count = commentList.size();
                    tvCommentCount.setText(
                            count == 1 ? "1 comment" : count + " comments"
                    );

                    tvNoComments.setVisibility(
                            commentList.isEmpty() ? View.VISIBLE : View.GONE
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Could not load comments",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addComment() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = editComment.getText().toString().trim();

        if (text.isEmpty()) return;

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", uid);
        data.put("comment", text);
        data.put("timestamp", System.currentTimeMillis());

        if (!replyingToCommentId.isEmpty()) {
            data.put("parentCommentId", replyingToCommentId);
            data.put("parentUsername", replyingToUsername);
        }

        btnSend.setEnabled(false);

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .add(data)
                .addOnSuccessListener(ref -> {
                    editComment.setText("");
                    btnSend.setEnabled(true);
                    cancelReply();
                    hideKeyboard();
                    loadComments();
                })
                .addOnFailureListener(e -> {
                    btnSend.setEnabled(true);
                    updateSendButton();

                    Toast.makeText(
                            this,
                            "Comment failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void startReply(CommentModel model, String username) {
        if (model == null) return;

        replyingToCommentId =
                model.commentId == null ? "" : model.commentId;

        replyingToUsername =
                username == null || username.isEmpty()
                        ? "user"
                        : username;

        replyBar.setVisibility(View.VISIBLE);
        tvReplyingTo.setText("Replying to @" + replyingToUsername);
        editComment.setHint("Reply to @" + replyingToUsername);

        editComment.requestFocus();

        InputMethodManager imm =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (imm != null) {
            imm.showSoftInput(
                    editComment,
                    InputMethodManager.SHOW_IMPLICIT
            );
        }
    }

    private void cancelReply() {
        replyingToCommentId = "";
        replyingToUsername = "";

        if (replyBar != null) {
            replyBar.setVisibility(View.GONE);
        }

        editComment.setHint("Add a comment...");
    }

    private void deleteComment(CommentModel model) {
        if (auth.getCurrentUser() == null || model == null) return;

        String uid = auth.getCurrentUser().getUid();

        if (!uid.equals(model.userId)) {
            Toast.makeText(
                    this,
                    "You can delete only your own comment",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete comment?")
                .setMessage("Are you sure you want to delete this comment?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        db.collection("posts")
                                .document(postId)
                                .collection("comments")
                                .document(model.commentId)
                                .delete()
                                .addOnSuccessListener(v -> loadComments())
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                this,
                                                "Delete failed",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                )
                )
                .show();
    }

    public static class CommentModel {
        String commentId;
        String userId;
        String comment;
        Long timestamp;
        String parentCommentId;
        String parentUsername;

        public CommentModel(
                String commentId,
                String userId,
                String comment,
                Long timestamp,
                String parentCommentId,
                String parentUsername
        ) {
            this.commentId = commentId;
            this.userId = userId;
            this.comment = comment;
            this.timestamp = timestamp;
            this.parentCommentId = parentCommentId;
            this.parentUsername = parentUsername;
        }
    }

    private class CommentAdapter
            extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

        private final ArrayList<CommentModel> list;

        public CommentAdapter(ArrayList<CommentModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {
            LinearLayout root = new LinearLayout(
                    CommentsActivity.this
            );
            root.setOrientation(LinearLayout.HORIZONTAL);
            root.setGravity(Gravity.TOP);
            root.setPadding(
                    dp(16), dp(12), dp(14), dp(10)
            );
            root.setBackgroundColor(Color.TRANSPARENT);

            TextView avatar = new TextView(CommentsActivity.this);
            avatar.setGravity(Gravity.CENTER);
            avatar.setTextColor(Color.WHITE);
            avatar.setTextSize(14);
            avatar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            avatar.setBackground(circleBackground(0xFF4F46E5));

            root.addView(
                    avatar,
                    new LinearLayout.LayoutParams(dp(42), dp(42))
            );

            LinearLayout content = new LinearLayout(
                    CommentsActivity.this
            );
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(dp(11), 0, dp(6), 0);

            root.addView(
                    content,
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f
                    )
            );

            TextView username = new TextView(CommentsActivity.this);
            username.setTextColor(Color.WHITE);
            username.setTextSize(14);
            username.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            content.addView(username);

            TextView commentText = new TextView(CommentsActivity.this);
            commentText.setTextColor(0xFFE9E9EA);
            commentText.setTextSize(15);
            commentText.setLineSpacing(0, 1.08f);
            commentText.setPadding(0, dp(2), 0, 0);
            content.addView(commentText);

            LinearLayout actionRow = new LinearLayout(
                    CommentsActivity.this
            );
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setGravity(Gravity.CENTER_VERTICAL);
            actionRow.setPadding(0, dp(5), 0, 0);
            content.addView(actionRow);

            TextView reply = actionText("Reply");
            TextView time = actionText("now");

            actionRow.addView(
                    reply,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            dp(28)
                    )
            );

            actionRow.addView(
                    time,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            dp(28)
                    )
            );

            LinearLayout likeColumn = new LinearLayout(
                    CommentsActivity.this
            );
            likeColumn.setOrientation(LinearLayout.VERTICAL);
            likeColumn.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView likeButton = new TextView(
                    CommentsActivity.this
            );
            likeButton.setText("♡");
            likeButton.setTextColor(Color.WHITE);
            likeButton.setTextSize(27);
            likeButton.setGravity(Gravity.CENTER);
            likeButton.setIncludeFontPadding(false);
            likeButton.setBackgroundColor(Color.TRANSPARENT);

            TextView likeCount = new TextView(
                    CommentsActivity.this
            );
            likeCount.setTextColor(0xFF9E9EA3);
            likeCount.setTextSize(11);
            likeCount.setGravity(Gravity.CENTER);

            likeColumn.addView(
                    likeButton,
                    new LinearLayout.LayoutParams(dp(42), dp(34))
            );

            likeColumn.addView(
                    likeCount,
                    new LinearLayout.LayoutParams(dp(42), dp(18))
            );

            root.addView(
                    likeColumn,
                    new LinearLayout.LayoutParams(
                            dp(46),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            TextView menu = new TextView(
                    CommentsActivity.this
            );
            menu.setText("⋮");
            menu.setTextColor(Color.WHITE);
            menu.setTextSize(23);
            menu.setGravity(Gravity.CENTER);
            menu.setIncludeFontPadding(false);

            root.addView(
                    menu,
                    new LinearLayout.LayoutParams(dp(30), dp(42))
            );

            return new CommentViewHolder(
                    root,
                    avatar,
                    username,
                    commentText,
                    reply,
                    time,
                    likeButton,
                    likeCount,
                    menu
            );
        }

        @Override
        public void onBindViewHolder(
                @NonNull CommentViewHolder holder,
                int position
        ) {
            CommentModel model = list.get(position);

            holder.menu.setOnClickListener(null);
            holder.reply.setOnClickListener(null);

            String text = model.comment == null ? "" : model.comment;

            if (model.parentCommentId != null &&
                    !model.parentCommentId.isEmpty()) {

                String parent =
                        model.parentUsername == null ||
                                model.parentUsername.isEmpty()
                                ? "user"
                                : model.parentUsername;

                holder.commentText.setText(
                        "↳ Replying to @" + parent + "\n" + text
                );

                holder.itemView.setPadding(
                        dp(38), dp(9), dp(14), dp(8)
                );

            } else {
                holder.commentText.setText(text);

                holder.itemView.setPadding(
                        dp(16), dp(12), dp(14), dp(10)
                );
            }

            holder.time.setText(formatTime(model.timestamp));

            boolean mine =
                    auth.getCurrentUser() != null &&
                            auth.getCurrentUser()
                                    .getUid()
                                    .equals(model.userId);

            holder.menu.setVisibility(
                    mine ? View.VISIBLE : View.INVISIBLE
            );

            if (mine) {
                holder.menu.setOnClickListener(
                        v -> deleteComment(model)
                );
            }

            loadUserForComment(holder, model);

            loadCommentLikeState(holder, model);

            holder.likeButton.setOnClickListener(
                    v -> toggleCommentLike(model, holder)
            );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private void loadUserForComment(
                CommentViewHolder holder,
                CommentModel model
        ) {
            holder.username.setText("@User");
            holder.avatar.setText("U");

            if (model.userId == null || model.userId.isEmpty()) return;

            db.collection("users")
                    .document(model.userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        String username =
                                safeString(document.getString("username"));

                        String name =
                                safeString(document.getString("name"));

                        if (username.isEmpty()) username = name;
                        if (username.isEmpty()) username = "User";

                        String finalUsername = username;

                        holder.username.setText(
                                "@" + finalUsername
                        );

                        holder.avatar.setText(
                                finalUsername
                                        .substring(0, 1)
                                        .toUpperCase()
                        );

                        holder.reply.setOnClickListener(
                                v -> startReply(
                                        model,
                                        finalUsername
                                )
                        );
                    })
                    .addOnFailureListener(e ->
                            holder.reply.setOnClickListener(
                                    v -> startReply(model, "User")
                            )
                    );
        }

        private void loadCommentLikeState(
                CommentViewHolder holder,
                CommentModel model
        ) {
            if (model.commentId == null ||
                    model.commentId.isEmpty()) {
                return;
            }

            db.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(model.commentId)
                    .collection("likes")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        holder.likeCount.setText(
                                String.valueOf(snapshot.size())
                        );

                        boolean liked = false;

                        if (auth.getCurrentUser() != null) {
                            String uid =
                                    auth.getCurrentUser().getUid();

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                if (document.getId().equals(uid)) {
                                    liked = true;
                                    break;
                                }
                            }
                        }

                        holder.likeButton.setText(
                                liked ? "♥" : "♡"
                        );

                        holder.likeButton.setTextColor(
                                liked
                                        ? 0xFFFF3040
                                        : Color.WHITE
                        );
                    });
        }

        private void toggleCommentLike(
                CommentModel model,
                CommentViewHolder holder
        ) {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(
                        CommentsActivity.this,
                        "Please login first",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();

            db.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(model.commentId)
                    .collection("likes")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(likeDocument -> {

                        if (likeDocument.exists()) {

                            db.collection("posts")
                                    .document(postId)
                                    .collection("comments")
                                    .document(model.commentId)
                                    .collection("likes")
                                    .document(uid)
                                    .delete()
                                    .addOnSuccessListener(v ->
                                            loadCommentLikeState(
                                                    holder,
                                                    model
                                            )
                                    );

                        } else {

                            Map<String, Object> data =
                                    new HashMap<>();

                            data.put("userId", uid);
                            data.put(
                                    "timestamp",
                                    System.currentTimeMillis()
                            );

                            db.collection("posts")
                                    .document(postId)
                                    .collection("comments")
                                    .document(model.commentId)
                                    .collection("likes")
                                    .document(uid)
                                    .set(data)
                                    .addOnSuccessListener(v ->
                                            loadCommentLikeState(
                                                    holder,
                                                    model
                                            )
                                    );
                        }
                    });
        }

        private class CommentViewHolder
                extends RecyclerView.ViewHolder {

            TextView avatar;
            TextView username;
            TextView commentText;
            TextView reply;
            TextView time;
            TextView likeButton;
            TextView likeCount;
            TextView menu;

            CommentViewHolder(
                    @NonNull View itemView,
                    TextView avatar,
                    TextView username,
                    TextView commentText,
                    TextView reply,
                    TextView time,
                    TextView likeButton,
                    TextView likeCount,
                    TextView menu
            ) {
                super(itemView);

                this.avatar = avatar;
                this.username = username;
                this.commentText = commentText;
                this.reply = reply;
                this.time = time;
                this.likeButton = likeButton;
                this.likeCount = likeCount;
                this.menu = menu;
            }
        }
    }

    private TextView actionText(String text) {
        TextView view = new TextView(this);

        view.setText(text);
        view.setTextColor(0xFF9B9B9F);
        view.setTextSize(12);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setPadding(0, 0, dp(14), 0);

        return view;
    }

    private GradientDrawable circleBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String formatTime(Long timestamp) {
        if (timestamp == null) return "";

        long diff = Math.max(
                0,
                System.currentTimeMillis() - timestamp
        );

        long minutes = diff / 60000L;

        if (minutes < 1) return "now";
        if (minutes < 60) return minutes + "m";

        long hours = minutes / 60L;

        if (hours < 24) return hours + "h";

        long days = hours / 24L;
        return days + "d";
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    editComment.getWindowToken(),
                    0
            );
        }

        editComment.clearFocus();
    }

    private int dp(int value) {
        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}

