package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.DetailViewHolder> {

    private final List<Post> postList = new ArrayList<>();
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public PostDetailAdapter(List<Post> posts) {
        if (posts != null) {
            postList.addAll(posts);
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post == null) return;

        Context context = holder.itemView.getContext();
        String currentUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        String postOwnerId = post.getUserId();

        // ============================================================
        // 1. USER PROFILE & USERNAME
        // ============================================================
        if (postOwnerId != null && !postOwnerId.trim().isEmpty()) {
            db.collection("users").document(postOwnerId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("userId");
                            if (username == null || username.trim().isEmpty()) {
                                username = document.getString("username");
                            }
                            if (username == null || username.trim().isEmpty()) {
                                username = "user";
                            }
                            holder.tvPostUsername.setText(username);

                            String profileUrl = document.getString("profileImage");
                            if (profileUrl != null && !profileUrl.trim().isEmpty()) {
                                Glide.with(context)
                                        .load(profileUrl)
                                        .circleCrop()
                                        .placeholder(android.R.drawable.ic_menu_myplaces)
                                        .error(android.R.drawable.ic_menu_myplaces)
                                        .into(holder.imgPostProfile);
                            } else {
                                holder.imgPostProfile.setImageResource(android.R.drawable.ic_menu_myplaces);
                            }
                        }
                    });

            View.OnClickListener toProfile = v -> {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId", postOwnerId);
                context.startActivity(intent);
            };

            holder.tvPostUsername.setOnClickListener(toProfile);
            holder.imgPostProfile.setOnClickListener(toProfile);

            // ============================================================
            // 2. FOLLOW BUTTON
            // ============================================================
            if (currentUid != null && !currentUid.equals(postOwnerId)) {
                holder.btnPostFollow.setVisibility(View.VISIBLE);
                loadFollowStatus(holder, postOwnerId, currentUid);
                holder.btnPostFollow.setOnClickListener(v -> toggleFollow(postOwnerId, currentUid, holder));
            } else {
                holder.btnPostFollow.setVisibility(View.GONE);
            }

            // ============================================================
            // 3. OWNER DELETE OPTIONS
            // ============================================================
            if (currentUid != null && currentUid.equals(postOwnerId)) {
                holder.btnMorePost.setVisibility(View.VISIBLE);
                holder.btnMorePost.setOnClickListener(v -> showDeletePostDialog(context, post, holder.getAdapterPosition()));
            } else {
                holder.btnMorePost.setVisibility(View.GONE);
            }
        }

        // ============================================================
        // 4. MEDIA (PHOTO OR REEL VIDEO)
        // ============================================================
        String mediaType = post.getMediaType();
        if (mediaType == null || mediaType.trim().isEmpty()) {
            mediaType = "image";
        }

        String mediaUrl = post.getImageUrl();

        if ("video".equalsIgnoreCase(mediaType)) {
            holder.postImage.setVisibility(View.GONE);
            holder.postVideo.setVisibility(View.VISIBLE);
            holder.playIcon.setVisibility(View.GONE);

            if (mediaUrl != null && !mediaUrl.trim().isEmpty()) {
                Uri videoUri = Uri.parse(mediaUrl);
                holder.postVideo.setVideoURI(videoUri);

                MediaController controller = new MediaController(context);
                controller.setAnchorView(holder.postVideo);
                holder.postVideo.setMediaController(controller);

                holder.postVideo.setOnPreparedListener(mp -> {
                    mp.setLooping(false);
                });

                holder.postVideo.setOnClickListener(v -> {
                    if (holder.postVideo.isPlaying()) {
                        holder.postVideo.pause();
                        holder.playIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.postVideo.start();
                        holder.playIcon.setVisibility(View.GONE);
                    }
                });
            }
        } else {
            holder.postVideo.stopPlayback();
            holder.postVideo.setVisibility(View.GONE);
            holder.playIcon.setVisibility(View.GONE);
            holder.postImage.setVisibility(View.VISIBLE);

            if (mediaUrl != null && !mediaUrl.trim().isEmpty()) {
                Glide.with(context)
                        .load(mediaUrl)
                        .fitCenter()
                        .into(holder.postImage);
            }
        }

        // ============================================================
        // 5. CAPTION
        // ============================================================
        String caption = post.getCaption();
        if (caption != null && !caption.trim().isEmpty()) {
            holder.tvCaption.setText(caption);
            holder.tvCaption.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaption.setVisibility(View.GONE);
        }

        // ============================================================
        // 6. LIKES & COMMENTS STATE
        // ============================================================
        loadLikeState(holder, post, currentUid);
        loadCommentCount(holder, post);

        holder.btnLike.setOnClickListener(v -> likePost(context, post, currentUid, holder));
        holder.btnComment.setOnClickListener(v -> openComments(context, post.getPostId()));
        holder.btnShare.setOnClickListener(v -> showShareDialog(context, post));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updateList(List<Post> newList) {
        postList.clear();
        if (newList != null) {
            postList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // ============================================================
    // FOLLOW LOGIC
    // ============================================================
    private void loadFollowStatus(DetailViewHolder holder, String targetUserId, String currentUid) {
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

    private void toggleFollow(String targetUserId, String currentUid, DetailViewHolder holder) {
        String relationshipId = currentUid + "_" + targetUserId;
        db.collection("relationships").document(relationshipId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        db.collection("relationships").document(relationshipId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("users").document(currentUid)
                                            .update("following", FieldValue.arrayRemove(targetUserId));
                                    loadFollowStatus(holder, targetUserId, currentUid);
                                });
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("followerId", currentUid);
                        data.put("followingId", targetUserId);
                        data.put("status", "accepted");
                        data.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("relationships").document(relationshipId).set(data)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("users").document(currentUid)
                                            .update("following", FieldValue.arrayUnion(targetUserId));
                                    loadFollowStatus(holder, targetUserId, currentUid);
                                });
                    }
                });
    }

    // ============================================================
    // LIKE LOGIC
    // ============================================================
    private void loadLikeState(DetailViewHolder holder, Post post, String currentUid) {
        if (post.getPostId() == null || post.getPostId().isEmpty()) {
            holder.tvLikeCount.setText("0 Likes");
            return;
        }

        db.collection("posts").document(post.getPostId())
                .collection("likes")
                .get()
                .addOnSuccessListener(snapshot -> {
                    holder.tvLikeCount.setText(snapshot.size() + " Likes");
                    boolean liked = false;
                    if (currentUid != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            if (currentUid.equals(doc.getId())) {
                                liked = true;
                                break;
                            }
                        }
                    }
                    holder.btnLike.setColorFilter(liked ? Color.RED : Color.WHITE);
                });
    }

    private void likePost(Context context, Post post, String currentUid, DetailViewHolder holder) {
        if (currentUid == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (post.getPostId() == null) return;

        String postId = post.getPostId();
        db.collection("posts").document(postId)
                .collection("likes").document(currentUid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        db.collection("posts").document(postId)
                                .collection("likes").document(currentUid)
                                .delete()
                                .addOnSuccessListener(v -> loadLikeState(holder, post, currentUid));
                    } else {
                        Map<String, Object> like = new HashMap<>();
                        like.put("userId", currentUid);
                        like.put("timestamp", System.currentTimeMillis());

                        db.collection("posts").document(postId)
                                .collection("likes").document(currentUid)
                                .set(like)
                                .addOnSuccessListener(v -> {
                                    loadLikeState(holder, post, currentUid);

                                    boolean isVideo = "video".equalsIgnoreCase(post.getMediaType());
                                    NotificationHelper.createNotification(
                                            post.getUserId(),
                                            currentUid,
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
    // COMMENTS & SHARE
    // ============================================================
    private void loadCommentCount(DetailViewHolder holder, Post post) {
        if (post.getPostId() == null) return;
        db.collection("posts").document(post.getPostId())
                .collection("comments")
                .get()
                .addOnSuccessListener(snapshot -> {
                    holder.tvCommentCount.setText(snapshot.size() + " Comments");
                });
    }

    private void openComments(Context context, String postId) {
        if (postId == null) return;
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }

    private void showShareDialog(Context context, Post post) {
        String[] options = {"Add to Story", "Send in Chat"};
        new AlertDialog.Builder(context)
                .setTitle("Share Post")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(context, StoryEditorActivity.class);
                        intent.putExtra("mediaUrl", post.getImageUrl());
                        intent.putExtra("mediaType", "video".equalsIgnoreCase(post.getMediaType()) ? "video" : "image");
                        intent.putExtra("postId", post.getPostId());
                        intent.putExtra("initialCaption", post.getCaption() == null ? "" : post.getCaption());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ChatListActivity.class);
                        intent.putExtra("sharePost", true);
                        intent.putExtra("sharedPostId", post.getPostId());
                        intent.putExtra("sharedPostImage", post.getImageUrl());
                        intent.putExtra("sharedPostCaption", post.getCaption());
                        intent.putExtra("sharedPostMediaType", post.getMediaType());
                        context.startActivity(intent);
                    }
                })
                .show();
    }

    // ============================================================
    // DELETE POST
    // ============================================================
    private void showDeletePostDialog(Context context, Post post, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post?")
                .setMessage("This post will be permanently deleted.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (post.getPostId() != null) {
                        db.collection("posts").document(post.getPostId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                    if (position >= 0 && position < postList.size()) {
                                        postList.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                });
                    }
                })
                .show();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================
    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPostProfile;
        TextView tvPostUsername;
        TextView btnPostFollow;
        ImageButton btnMorePost;

        ImageView postImage;
        VideoView postVideo;
        ImageView playIcon;

        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnShare;

        TextView tvLikeCount;
        TextView tvCommentCount;
        TextView tvCaption;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPostProfile = itemView.findViewById(R.id.imgPostProfile);
            tvPostUsername = itemView.findViewById(R.id.tvPostUsername);
            btnPostFollow = itemView.findViewById(R.id.btnPostFollow);
            btnMorePost = itemView.findViewById(R.id.btnMorePost);

            postImage = itemView.findViewById(R.id.postImage);
            postVideo = itemView.findViewById(R.id.postVideo);
            playIcon = itemView.findViewById(R.id.playIcon);

            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);

            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvCaption = itemView.findViewById(R.id.tvCaption);
        }
    }
}
