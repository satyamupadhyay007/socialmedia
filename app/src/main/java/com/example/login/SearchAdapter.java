package com.example.login;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter
        extends RecyclerView.Adapter<SearchAdapter.UserViewHolder> {

    private List<User> userList;


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public SearchAdapter(
            List<User> userList
    ) {

        this.userList =
                userList;
    }


    // ==================================================
    // CREATE VIEW
    // ==================================================

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_user,
                                parent,
                                false
                        );

        return new UserViewHolder(
                view
        );
    }


    // ==================================================
    // BIND
    // ==================================================

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position
    ) {

        User currentUser =
                userList.get(position);


        if (currentUser == null) {
            return;
        }


        // ==================================================
        // USERNAME / USER ID
        // ==================================================

        String username =
                currentUser.getUsername();


        if (
                username == null ||
                        username.trim().isEmpty()
        ) {

            username =
                    "Unknown User";
        }


        holder.userUsername.setText(
                username
        );


        // ==================================================
        // NAME
        // ==================================================

        String name =
                currentUser.getName();


        if (
                name == null ||
                        name.trim().isEmpty()
        ) {

            holder.userName.setText(
                    ""
            );

            holder.userName.setVisibility(
                    View.GONE
            );

        } else {

            holder.userName.setText(
                    name
            );

            holder.userName.setVisibility(
                    View.VISIBLE
            );
        }


        // ==================================================
        // PROFILE IMAGE
        // ==================================================

        String profileImage =
                currentUser.getProfileImage();


        if (
                profileImage != null &&
                        !profileImage.trim().isEmpty()
        ) {

            Glide.with(
                            holder.itemView.getContext()
                    )
                    .load(profileImage)
                    .centerCrop()
                    .placeholder(
                            android.R.drawable.ic_menu_gallery
                    )
                    .error(
                            android.R.drawable.ic_menu_gallery
                    )
                    .into(
                            holder.userImage
                    );

        } else {

            // Default image
            holder.userImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }
    }


    // ==================================================
    // COUNT
    // ==================================================

    @Override
    public int getItemCount() {

        return userList == null
                ? 0
                : userList.size();
    }


    // ==================================================
    // UPDATE LIST
    // ==================================================

    public void updateList(
            List<User> newList
    ) {

        userList =
                newList;

        notifyDataSetChanged();
    }


    // ==================================================
    // VIEW HOLDER
    // ==================================================

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        ImageView userImage;

        TextView userUsername;

        TextView userName;


        public UserViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            userImage =
                    itemView.findViewById(
                            R.id.userImage
                    );


            userUsername =
                    itemView.findViewById(
                            R.id.userUsername
                    );


            userName =
                    itemView.findViewById(
                            R.id.userName
                    );
        }
    }
}