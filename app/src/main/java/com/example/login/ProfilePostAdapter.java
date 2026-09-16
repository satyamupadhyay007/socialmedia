package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostAdapter
        extends RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder> {

    private final List<Post> postList = new ArrayList<>();

    public ProfilePostAdapter(List<Post> posts) {
        if (posts != null) {
            postList.addAll(posts);
        }
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_profile_post,
                        parent,
                        false
                );

        return new PostViewHolder(view);
    }

    // ============================================================
    // BIND POST / REEL
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull PostViewHolder holder,
            int position
    ) {

        Post post = postList.get(position);

        if (post == null) {
            return;
        }

        // --------------------------------------------------------
        // MEDIA TYPE
        // --------------------------------------------------------

        String mediaTypeValue = post.getMediaType();

        if (mediaTypeValue == null ||
                mediaTypeValue.trim().isEmpty()) {

            mediaTypeValue = "image";
        }

        // IMPORTANT:
        // Lambda ke andar use karne ke liye final variable.
        final String finalMediaType = mediaTypeValue;

        // --------------------------------------------------------
        // LOAD THUMBNAIL
        // --------------------------------------------------------

        String imageUrl = post.getImageUrl();

        if (imageUrl != null &&
                !imageUrl.trim().isEmpty()) {

            Glide.with(holder.postImage.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.postImage);

        } else {

            holder.postImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        // --------------------------------------------------------
        // REEL PLAY ICON
        // --------------------------------------------------------

        if (holder.playIcon != null) {

            if ("video".equalsIgnoreCase(finalMediaType)) {

                holder.playIcon.setVisibility(
                        View.VISIBLE
                );

            } else {

                holder.playIcon.setVisibility(
                        View.GONE
                );
            }
        }

        // --------------------------------------------------------
        // CLICK POST / REEL
        // --------------------------------------------------------

        holder.itemView.setOnClickListener(
                v -> {

                    Context context =
                            v.getContext();

                    Intent intent =
                            new Intent(
                                    context,
                                    PostDetailActivity.class
                            );

                    // Post ID
                    intent.putExtra(
                            "postId",
                            post.getPostId()
                    );

                    // Photo / Video URL
                    intent.putExtra(
                            "imageUrl",
                            post.getImageUrl()
                    );

                    // Caption
                    intent.putExtra(
                            "caption",
                            post.getCaption()
                    );

                    // image / video
                    intent.putExtra(
                            "mediaType",
                            finalMediaType
                    );

                    context.startActivity(
                            intent
                    );
                }
        );
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return postList.size();
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
    // VIEW HOLDER
    // ============================================================

    static class PostViewHolder
            extends RecyclerView.ViewHolder {

        ImageView postImage;
        ImageView playIcon;

        PostViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            // Thumbnail MUST be ImageView
            postImage =
                    itemView.findViewById(
                            R.id.postImage
                    );

            // Reel play icon
            playIcon =
                    itemView.findViewById(
                            R.id.playIcon
                    );
        }
    }
}