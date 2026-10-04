package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

        notifyItemRangeChanged(0, userList.size());
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
                String.format("@%s", username)
        );


        // ==================================================
        // AVATAR LETTER
        // ==================================================

        String firstLetter =
                username.substring(0, 1)
                        .toUpperCase(Locale.getDefault());


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

        return Objects.requireNonNullElse(userList, Collections.emptyList()).size();
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