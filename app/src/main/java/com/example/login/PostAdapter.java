package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PostViewHolder holder,
            int position) {

        Post post = postList.get(position);

        // Firebase Storage ki image/video URL
        // ko ImageView mein load karna
        Glide.with(holder.itemView.getContext())
                .load(post.getMediaUrl())
                .centerCrop()
                .into(holder.postImage);

        // Agar post video/reel hai
        if ("video".equalsIgnoreCase(post.getMediaType())) {

            holder.playIcon.setVisibility(View.VISIBLE);

        } else {

            holder.playIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Firebase se new posts aane par list update
    public void updateList(List<Post> newList) {

        postList = newList;

        notifyDataSetChanged();
    }

    public static class PostViewHolder
            extends RecyclerView.ViewHolder {

        ImageView postImage;
        ImageView playIcon;

        public PostViewHolder(
                @NonNull View itemView) {

            super(itemView);

            postImage = itemView.findViewById(
                    R.id.postImage
            );

            playIcon = itemView.findViewById(
                    R.id.playIcon
            );
        }
    }
}