package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter
        extends RecyclerView.Adapter<SearchAdapter.UserViewHolder> {

    private List<User> userList;

    public SearchAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_user,
                parent,
                false
        );

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position) {

        User currentUser = userList.get(position);

        // Sirf username display hoga
        holder.userUsername.setText(
                currentUser.getUsername()
        );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Search results update karna
    public void updateList(List<User> newList) {

        userList = newList;

        notifyDataSetChanged();
    }

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userUsername;

        public UserViewHolder(
                @NonNull View itemView) {

            super(itemView);

            userImage = itemView.findViewById(
                    R.id.userImage
            );

            userUsername = itemView.findViewById(
                    R.id.userUsername
            );
        }
    }
}