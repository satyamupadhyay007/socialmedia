package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UserViewHolder> {

    private List<User> userList;

    public SearchAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User currentUser = userList.get(position);

        // ==============================
        // USERNAME
        // ==============================

        String username = currentUser.getUsername();

        if (username == null || username.trim().isEmpty()) {
            username = "Unknown User";
        }

        holder.userUsername.setText(username);


        // ==============================
        // REAL NAME
        // ==============================

        String name = currentUser.getName();

        if (name == null || name.trim().isEmpty()) {

            holder.userName.setVisibility(View.GONE);

        } else {

            holder.userName.setVisibility(View.VISIBLE);
            holder.userName.setText(name);
        }


        // ==============================
        // PROFILE IMAGE
        // ==============================

        String profileImage = currentUser.getProfileImage();

        if (profileImage != null && !profileImage.trim().isEmpty()) {

            Glide.with(holder.userImage.getContext())
                    .load(profileImage)
                    .circleCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.userImage);

        } else {

            holder.userImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }


        // =================================================
        // OPEN USER PROFILE
        // =================================================

        final String userUid = currentUser.getUid();

        holder.itemView.setOnClickListener(v -> {

            if (userUid == null || userUid.trim().isEmpty()) {
                return;
            }

            Context context = v.getContext();

            Intent intent = new Intent(
                    context,
                    UserProfileActivity.class
            );

            intent.putExtra("userId", userUid);

            context.startActivity(intent);
        });
    }


    // =================================================
    // UPDATE SEARCH RESULTS
    // =================================================

    public void updateList(List<User> newList) {

        this.userList = newList;

        notifyDataSetChanged();
    }


    // =================================================
    // ITEM COUNT
    // =================================================

    @Override
    public int getItemCount() {

        return userList != null
                ? userList.size()
                : 0;
    }


    // =================================================
    // VIEW HOLDER
    // =================================================

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userUsername;
        TextView userName;

        public UserViewHolder(@NonNull View itemView) {

            super(itemView);

            userImage =
                    itemView.findViewById(R.id.userImage);

            userUsername =
                    itemView.findViewById(R.id.userUsername);

            userName =
                    itemView.findViewById(R.id.userName);
        }
    }
}