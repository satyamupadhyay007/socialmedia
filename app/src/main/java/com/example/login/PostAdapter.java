package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter
        extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public PostAdapter(List<Post> postList) {

        this.postList = postList;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }


    // ============================================================
    // CREATE VIEW
    // ============================================================

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }


    // ============================================================
    // BIND POST
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull PostViewHolder holder,
            int position
    ) {

        Post post = postList.get(position);

        Context context = holder.itemView.getContext();


        // ========================================================
        // POST HEADER (USERNAME & PROFILE)
        // ========================================================

        holder.tvPostUsername.setText(
                post.getUserName() == null || post.getUserName().isEmpty()
                ? "user" : post.getUserName()
        );

        // Load profile photo of post owner
        if (post.getUserId() != null) {
            db.collection("users").document(post.getUserId())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String profileUrl = document.getString("profileImage");
                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(context)
                                .load(profileUrl)
                                .circleCrop()
                                .into(holder.imgPostProfile);
                        } else {
                            holder.imgPostProfile.setImageResource(android.R.drawable.ic_menu_myplaces);
                        }
                    }
                });

            // Follow logic
            loadFollowStatus(holder, post.getUserId());

            holder.btnPostFollow.setOnClickListener(v -> toggleFollow(post.getUserId(), holder));

            // Go to profile
            View.OnClickListener toProfile = v -> {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId", post.getUserId());
                context.startActivity(intent);
            };

            holder.tvPostUsername.setOnClickListener(toProfile);
            holder.imgPostProfile.setOnClickListener(toProfile);
        }

        // ========================================================
        // IMAGE
        // ========================================================

        Glide.with(context)
                .load(post.getImageUrl())
                .centerCrop()
                .into(holder.postImage);

        holder.playIcon.setVisibility(View.GONE);


        // ========================================================
        // CAPTION
        // ========================================================

        String caption = post.getCaption();

        if (caption != null && !caption.trim().isEmpty()) {

            holder.tvCaption.setText(caption);
            holder.tvCaption.setVisibility(View.VISIBLE);

        } else {

            holder.tvCaption.setVisibility(View.GONE);
        }


        // ========================================================
        // LIKE COUNT
        // ========================================================

        loadLikeState(holder, post);


        // ========================================================
        // OPEN POST DETAIL
        // ========================================================

        holder.postImage.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    PostDetailActivity.class
            );

            intent.putExtra(
                    "postId",
                    post.getPostId()
            );

            intent.putExtra(
                    "imageUrl",
                    post.getImageUrl()
            );

            intent.putExtra(
                    "caption",
                    post.getCaption()
            );

            context.startActivity(intent);
        });


        // Also allow tapping anywhere on image/card

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    PostDetailActivity.class
            );

            intent.putExtra(
                    "postId",
                    post.getPostId()
            );

            intent.putExtra(
                    "imageUrl",
                    post.getImageUrl()
            );

            intent.putExtra(
                    "caption",
                    post.getCaption()
            );

            context.startActivity(intent);
        });


        // ========================================================
        // LIKE BUTTON
        // ========================================================

        holder.btnLike.setOnClickListener(v -> {

            likePost(
                    context,
                    post,
                    holder
            );

        });


        // ========================================================
        // COMMENT BUTTON
        // ========================================================

        holder.btnComment.setOnClickListener(v -> {

            showCommentDialog(
                    context,
                    post
            );

        });


        // ========================================================
        // SHARE BUTTON
        // ========================================================

        holder.btnShare.setOnClickListener(v -> {

            showShareDialog(
                    context,
                    post
            );

        });
    }


    // ============================================================
    // LOAD LIKE STATE + COUNT
    // ============================================================

    private void loadLikeState(
            PostViewHolder holder,
            Post post
    ) {

        if (post.getPostId() == null ||
                post.getPostId().isEmpty()) {

            setLikeUI(holder, false, 0);
            return;
        }

        String postId = post.getPostId();

        // Count likes

        db.collection("posts")
                .document(postId)
                .collection("likes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int likeCount =
                            snapshot.size();

                    boolean liked = false;

                    if (auth.getCurrentUser() != null) {

                        String userId =
                                auth.getCurrentUser().getUid();

                        liked =
                                snapshot.getDocuments()
                                        .stream()
                                        .anyMatch(
                                                document ->
                                                        document.getId()
                                                                .equals(userId)
                                        );
                    }

                    setLikeUI(
                            holder,
                            liked,
                            likeCount
                    );

                })
                .addOnFailureListener(e -> {

                    setLikeUI(
                            holder,
                            false,
                            0
                    );
                });
    }


    // ============================================================
    // LIKE / UNLIKE
    // ============================================================

    private void likePost(
            Context context,
            Post post,
            PostViewHolder holder
    ) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    context,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String postId = post.getPostId();

        if (postId == null ||
                postId.isEmpty()) {

            Toast.makeText(
                    context,
                    "Post ID missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        // ==========================
                        // UNLIKE
                        // ==========================

                        db.collection("posts")
                                .document(postId)
                                .collection("likes")
                                .document(userId)
                                .delete()
                                .addOnSuccessListener(v -> {

                                    loadLikeState(
                                            holder,
                                            post
                                    );

                                });

                    } else {

                        // ==========================
                        // LIKE
                        // ==========================

                        Map<String, Object> like =
                                new HashMap<>();

                        like.put(
                                "userId",
                                userId
                        );

                        like.put(
                                "timestamp",
                                System.currentTimeMillis()
                        );

                        db.collection("posts")
                                .document(postId)
                                .collection("likes")
                                .document(userId)
                                .set(like)
                                .addOnSuccessListener(v -> {

                                    loadLikeState(
                                            holder,
                                            post
                                    );

                                    boolean isVideo = "video".equalsIgnoreCase(post.getMediaType());
                                    NotificationHelper.createNotification(
                                            post.getUserId(),
                                            userId,
                                            null,
                                            isVideo ? "like_reel" : "like_post",
                                            "liked your " + (isVideo ? "reel" : "post"),
                                            post.getPostId()
                                    );

                                });
                    }

                });
    }


    // ============================================================
    // LIKE UI
    // ============================================================

    private void setLikeUI(
            PostViewHolder holder,
            boolean liked,
            int count
    ) {

        if (liked) {

            holder.btnLike.setColorFilter(
                    Color.RED
            );

        } else {

            holder.btnLike.setColorFilter(
                    Color.WHITE
            );
        }

        holder.tvLikeCount.setText(
                count + " Likes"
        );
    }


    // ============================================================
    // COMMENT DIALOG
    // ============================================================

    private void showCommentDialog(
            Context context,
            Post post
    ) {

        EditText input =
                new EditText(context);

        input.setHint(
                "Write a comment..."
        );

        input.setTextColor(
                Color.WHITE
        );

        input.setHintTextColor(
                Color.LTGRAY
        );

        input.setSingleLine(false);

        input.setMinLines(2);

        input.setPadding(
                20,
                15,
                20,
                15
        );


        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle("Comment")
                        .setView(input)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Post",
                                null
                        )
                        .create();


        dialog.setOnShowListener(
                d -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String comment =
                                input.getText()
                                        .toString()
                                        .trim();

                        if (comment.isEmpty()) {

                            input.setError(
                                    "Enter a comment"
                            );

                            return;
                        }

                        addComment(
                                context,
                                post,
                                comment
                        );

                        dialog.dismiss();
                    });
                }
        );

        dialog.show();
    }


    // ============================================================
    // ADD COMMENT
    // ============================================================

    private void addComment(
            Context context,
            Post post,
            String comment
    ) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    context,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (post.getPostId() == null ||
                post.getPostId().isEmpty()) {

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();


        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "userId",
                userId
        );

        data.put(
                "comment",
                comment
        );

        data.put(
                "timestamp",
                System.currentTimeMillis()
        );


        db.collection("posts")
                .document(post.getPostId())
                .collection("comments")
                .add(data)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    context,
                                    "Comment added",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    context,
                                    "Comment failed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                );
    }


    // ============================================================
    // SHARE MENU
    // ============================================================

    private void showShareDialog(
            Context context,
            Post post
    ) {

        String[] options = {
                "Add to Story",
                "Send in Chat"
        };


        new AlertDialog.Builder(context)
                .setTitle("Share Post")
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                shareToStory(
                                        context,
                                        post
                                );

                            } else {

                                shareToChat(
                                        context,
                                        post
                                );
                            }

                        }
                )
                .show();
    }


    // ============================================================
    // ADD TO STORY
    // ============================================================
    // NEW:
    // Instead of directly creating the Story in Firestore,
    // open StoryEditorActivity first.
    // ============================================================

    private void shareToStory(
            Context context,
            Post post
    ) {

        // DEBUG: confirm that this PostAdapter is being used
        Toast.makeText(
                context,
                "STORY EDITOR CLICKED",
                Toast.LENGTH_SHORT
        ).show();

        // Check login

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    context,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Check post

        if (post == null) {

            Toast.makeText(
                    context,
                    "Post missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Check image URL

        if (post.getImageUrl() == null ||
                post.getImageUrl().trim().isEmpty()) {

            Toast.makeText(
                    context,
                    "Post image missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ========================================================
        // OPEN STORY EDITOR
        // ========================================================

        Intent intent =
                new Intent(
                        context,
                        StoryEditorActivity.class
                );


        // Post image

        intent.putExtra(
                "mediaUrl",
                post.getImageUrl()
        );


        // This is a photo post

        intent.putExtra(
                "mediaType",
                "image"
        );


        // Original post ID

        intent.putExtra(
                "postId",
                post.getPostId()
        );


        // Original caption
        // StoryEditor can use this as initial caption

        intent.putExtra(
                "initialCaption",
                post.getCaption() == null
                        ? ""
                        : post.getCaption()
        );


        context.startActivity(intent);
    }


    // ============================================================
    // SEND TO CHAT
    // ============================================================

    private void shareToChat(
            Context context,
            Post post
    ) {

        Intent intent =
                new Intent(
                        context,
                        ChatListActivity.class
                );


        intent.putExtra(
                "sharePost",
                true
        );


        intent.putExtra(
                "sharedPostId",
                post.getPostId()
        );


        intent.putExtra(
                "sharedPostImage",
                post.getImageUrl()
        );


        intent.putExtra(
                "sharedPostCaption",
                post.getCaption()
        );


        context.startActivity(intent);
    }


    // ============================================================
    // SAFE STRING
    // ============================================================

    private String getString(
            DocumentSnapshot document,
            String field
    ) {

        if (document == null) {

            return "";
        }


        String value =
                document.getString(field);


        return value != null
                ? value
                : "";
    }


    // ============================================================
    // UPDATE LIST
    // ============================================================

    public void updateList(
            List<Post> newList
    ) {

        postList.clear();


        if (newList != null) {

            postList.addAll(
                    newList
            );
        }


        notifyDataSetChanged();
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return postList.size();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    // ============================================================
    // FOLLOW LOGIC
    // ============================================================

    private void loadFollowStatus(PostViewHolder holder, String targetUserId) {
        if (auth.getCurrentUser() == null || targetUserId.equals(auth.getCurrentUser().getUid())) {
            holder.btnPostFollow.setVisibility(View.GONE);
            return;
        }

        holder.btnPostFollow.setVisibility(View.VISIBLE);
        String currentUid = auth.getCurrentUser().getUid();
        String relationshipId = currentUid + "_" + targetUserId;

        db.collection("relationships").document(relationshipId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists() && "accepted".equals(document.getString("status"))) {
                    holder.btnPostFollow.setText("Following");
                    holder.btnPostFollow.setTextColor(Color.LTGRAY);
                } else if (document.exists() && "pending".equals(document.getString("status"))) {
                    holder.btnPostFollow.setText("Requested");
                    holder.btnPostFollow.setTextColor(Color.LTGRAY);
                } else {
                    holder.btnPostFollow.setText("Follow");
                    holder.btnPostFollow.setTextColor(0xFF0095F6);
                }
            });
    }

    private void toggleFollow(String targetUserId, PostViewHolder holder) {
        if (auth.getCurrentUser() == null) return;
        String currentUid = auth.getCurrentUser().getUid();
        String relationshipId = currentUid + "_" + targetUserId;

        db.collection("relationships").document(relationshipId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    // Unfollow / Cancel
                    db.collection("relationships").document(relationshipId).delete()
                        .addOnSuccessListener(aVoid -> {
                            db.collection("users").document(currentUid)
                                .update("following", FieldValue.arrayRemove(targetUserId));
                            loadFollowStatus(holder, targetUserId);
                        });
                } else {
                    // Follow
                    Map<String, Object> data = new HashMap<>();
                    data.put("followerId", currentUid);
                    data.put("followingId", targetUserId);
                    data.put("status", "accepted"); // Assuming public for simplicity, or check private status
                    data.put("createdAt", FieldValue.serverTimestamp());

                    db.collection("relationships").document(relationshipId).set(data)
                        .addOnSuccessListener(aVoid -> {
                            db.collection("users").document(currentUid)
                                .update("following", FieldValue.arrayUnion(targetUserId));
                            loadFollowStatus(holder, targetUserId);
                        });
                }
            });
    }

    public static class PostViewHolder
            extends RecyclerView.ViewHolder {

        ImageView postImage;
        ImageView imgPostProfile;
        ImageView playIcon;

        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnShare;

        TextView tvPostUsername;
        TextView tvCaption;
        TextView tvLikeCount;

        TextView btnPostFollow;


        public PostViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            postImage =
                    itemView.findViewById(
                            R.id.postImage
                    );

            imgPostProfile =
                    itemView.findViewById(
                            R.id.imgPostProfile
                    );


            playIcon =
                    itemView.findViewById(
                            R.id.playIcon
                    );


            btnLike =
                    itemView.findViewById(
                            R.id.btnLike
                    );


            btnComment =
                    itemView.findViewById(
                            R.id.btnComment
                    );


            btnShare =
                    itemView.findViewById(
                            R.id.btnShare
                    );


            tvPostUsername =
                    itemView.findViewById(
                            R.id.tvPostUsername
                    );


            tvCaption =
                    itemView.findViewById(
                            R.id.tvCaption
                    );


            tvLikeCount =
                    itemView.findViewById(
                            R.id.tvLikeCount
                    );

            btnPostFollow =
                    itemView.findViewById(
                            R.id.btnPostFollow
                    );
        }
    }
}
