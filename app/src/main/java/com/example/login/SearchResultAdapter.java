package com.example.login;

import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class SearchResultAdapter
        extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {

    private List<User> userList;

    private final OnUserClickListener listener;


    // ==================================================
    // CLICK LISTENER
    // ==================================================

    public interface OnUserClickListener {

        void onUserClick(
                User user
        );
    }


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public SearchResultAdapter(
            List<User> userList,
            OnUserClickListener listener) {

        this.userList =
                userList;

        this.listener =
                listener;
    }


    // ==================================================
    // UPDATE LIST
    // ==================================================

    public void updateList(
            List<User> newList) {

        userList =
                newList;

        notifyDataSetChanged();
    }


    // ==================================================
    // CREATE VIEW
    // ==================================================

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.search_result_item,
                                parent,
                                false
                        );


        return new ResultViewHolder(
                view
        );
    }


    // ==================================================
    // BIND
    // ==================================================

    @Override
    public void onBindViewHolder(
            @NonNull ResultViewHolder holder,
            int position) {

        User user =
                userList.get(
                        position
                );


        String username =
                user.getUsername();


        if (
                username == null ||
                        username.isEmpty()
        ) {

            username =
                    "Unknown User";
        }


        // ==================================================
        // USERNAME
        // ==================================================

        holder.resultUsername.setText(
                username
        );


        // ==================================================
        // USER ID
        // ==================================================

        holder.resultUserId.setText(
                "@" + username
        );


        // ==================================================
        // AVATAR LETTER
        // ==================================================

        String firstLetter =
                username.substring(
                                0,
                                1
                        )
                        .toUpperCase();


        holder.resultAvatar.setText(
                firstLetter
        );


        // ==================================================
        // CLICK
        // ==================================================

        holder.itemView.setOnClickListener(
                v -> {

                    if (
                            listener != null
                    ) {

                        listener.onUserClick(
                                user
                        );
                    }
                }
        );
    }


    // ==================================================
    // COUNT
    // ==================================================

    @Override
    public int getItemCount() {

        if (
                userList == null
        ) {

            return 0;
        }


        return userList.size();
    }


    // ==================================================
    // VIEW HOLDER
    // ==================================================

    static class ResultViewHolder
            extends RecyclerView.ViewHolder {

        TextView resultAvatar;
        TextView resultUsername;
        TextView resultUserId;


        ResultViewHolder(
                @NonNull View itemView) {

            super(itemView);


            resultAvatar =
                    itemView.findViewById(
                            R.id.resultAvatar
                    );


            resultUsername =
                    itemView.findViewById(
                            R.id.resultUsername
                    );


            resultUserId =
                    itemView.findViewById(
                            R.id.resultUserId
                    );
        }
    }
}