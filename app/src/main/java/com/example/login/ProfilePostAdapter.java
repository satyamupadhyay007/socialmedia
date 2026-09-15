package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfilePostAdapter
        extends RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder> {

    private final List<Integer> images;

    public ProfilePostAdapter(List<Integer> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PostViewHolder holder,
            int position) {

        holder.imgPost.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.imgPost);
        }
    }
}