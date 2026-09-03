package com.example.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter
        extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {


    private List<ChatItem> chatList;

    private final OnChatClickListener listener;


    // ==================================================
    // CLICK LISTENER
    // ==================================================

    public interface OnChatClickListener {

        void onChatClick(
                ChatItem chatItem
        );
    }


    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public ChatListAdapter(
            List<ChatItem> chatList,
            OnChatClickListener listener) {

        this.chatList =
                chatList != null
                        ? chatList
                        : new ArrayList<>();

        this.listener =
                listener;
    }


    // ==================================================
    // UPDATE
    // ==================================================

    public void updateList(
            List<ChatItem> newList) {

        if (newList == null) {

            chatList =
                    new ArrayList<>();

        } else {

            chatList =
                    newList;
        }

        notifyDataSetChanged();
    }


    // ==================================================
    // DP CONVERSION
    // ==================================================

    private int dp(
            Context context,
            int value) {

        float density =
                context.getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                value * density
        );
    }


    // ==================================================
    // CREATE ROW
    // ==================================================

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        Context context =
                parent.getContext();


        // ==================================================
        // ROW
        // ==================================================

        LinearLayout row =
                new LinearLayout(
                        context
                );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setBackgroundColor(
                Color.BLACK
        );

        row.setPadding(
                dp(context, 14),
                dp(context, 8),
                dp(context, 12),
                dp(context, 8)
        );

        row.setMinimumHeight(
                dp(context, 76)
        );


        // ==================================================
        // AVATAR
        // ==================================================

        TextView avatar =
                new TextView(
                        context
                );

        avatar.setGravity(
                Gravity.CENTER
        );

        avatar.setTextColor(
                Color.WHITE
        );

        avatar.setTextSize(
                17
        );

        avatar.setTypeface(
                null,
                Typeface.BOLD
        );

        avatar.setIncludeFontPadding(
                false
        );


        GradientDrawable avatarDrawable =
                new GradientDrawable();

        avatarDrawable.setShape(
                GradientDrawable.OVAL
        );

        avatarDrawable.setColor(
                Color.rgb(
                        32,
                        32,
                        32
                )
        );

        avatarDrawable.setStroke(
                dp(context, 1),
                Color.rgb(
                        68,
                        68,
                        68
                )
        );

        avatar.setBackground(
                avatarDrawable
        );


        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        dp(context, 52),
                        dp(context, 52)
                );

        avatarParams.setMargins(
                0,
                0,
                dp(context, 16),
                0
        );


        row.addView(
                avatar,
                avatarParams
        );


        // ==================================================
        // CENTER
        // ==================================================

        LinearLayout center =
                new LinearLayout(
                        context
                );

        center.setOrientation(
                LinearLayout.VERTICAL
        );

        center.setGravity(
                Gravity.CENTER_VERTICAL
        );


        LinearLayout.LayoutParams centerParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );


        row.addView(
                center,
                centerParams
        );


        // ==================================================
        // USERNAME
        // ==================================================

        TextView username =
                new TextView(
                        context
                );

        username.setTextColor(
                Color.WHITE
        );

        username.setTextSize(
                17
        );

        username.setTypeface(
                null,
                Typeface.BOLD
        );

        username.setIncludeFontPadding(
                false
        );

        username.setMaxLines(
                1
        );

        username.setEllipsize(
                TextUtils.TruncateAt.END
        );


        center.addView(
                username,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );


        // ==================================================
        // LAST MESSAGE
        // ==================================================

        TextView lastMessage =
                new TextView(
                        context
                );

        lastMessage.setTextColor(
                Color.rgb(
                        165,
                        165,
                        165
                )
        );

        lastMessage.setTextSize(
                13
        );

        lastMessage.setIncludeFontPadding(
                false
        );

        lastMessage.setMaxLines(
                1
        );

        lastMessage.setEllipsize(
                TextUtils.TruncateAt.END
        );


        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        messageParams.setMargins(
                0,
                dp(context, 5),
                0,
                0
        );


        center.addView(
                lastMessage,
                messageParams
        );


        // ==================================================
        // TIME
        // ==================================================

        TextView time =
                new TextView(
                        context
                );

        time.setTextColor(
                Color.rgb(
                        145,
                        145,
                        145
                )
        );

        time.setTextSize(
                11
        );

        time.setGravity(
                Gravity.CENTER
        );

        time.setSingleLine(
                true
        );


        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        dp(context, 56),
                        dp(context, 52)
                );

        timeParams.setMargins(
                dp(context, 10),
                0,
                0,
                0
        );


        row.addView(
                time,
                timeParams
        );


        return new ChatViewHolder(
                row,
                avatar,
                username,
                lastMessage,
                time
        );
    }


    // ==================================================
    // BIND
    // ==================================================

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder,
            int position) {

        if (
                chatList == null ||
                        position < 0 ||
                        position >= chatList.size()
        ) {

            return;
        }


        ChatItem item =
                chatList.get(
                        position
                );


        if (
                item == null
        ) {

            return;
        }


        // ==================================================
        // NAME
        // ==================================================

        String username =
                item.getUsername();


        if (
                username == null ||
                        username.trim().isEmpty()
        ) {

            username =
                    "Unknown User";
        }


        username =
                username.trim();


        holder.username.setText(
                username
        );


        // ==================================================
        // AVATAR
        // ==================================================

        holder.avatar.setText(
                username
                        .substring(
                                0,
                                1
                        )
                        .toUpperCase(
                                Locale.getDefault()
                        )
        );


        // ==================================================
        // MESSAGE
        // ==================================================

        String message =
                item.getLastMessage();


        if (
                message == null ||
                        message.trim().isEmpty()
        ) {

            message =
                    "No messages yet";
        }


        holder.lastMessage.setText(
                message
        );


        // ==================================================
        // TIME
        // ==================================================

        long timestamp =
                item.getLastMessageTime();


        if (
                timestamp > 0
        ) {

            holder.time.setText(
                    formatTime(
                            timestamp
                    )
            );

        } else {

            holder.time.setText(
                    ""
            );
        }


        // ==================================================
        // CLICK
        // ==================================================

        holder.itemView.setOnClickListener(
                v -> {

                    if (
                            listener != null
                    ) {

                        listener.onChatClick(
                                item
                        );
                    }
                }
        );
    }


    // ==================================================
    // FORMAT TIME
    // ==================================================

    private String formatTime(
            long timestamp) {

        Date date =
                new Date(
                        timestamp
                );

        Date now =
                new Date();


        SimpleDateFormat dayFormat =
                new SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.getDefault()
                );


        if (
                dayFormat.format(date)
                        .equals(
                                dayFormat.format(now)
                        )
        ) {

            return new SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
            ).format(
                    date
            );
        }


        return new SimpleDateFormat(
                "dd/MM",
                Locale.getDefault()
        ).format(
                date
        );
    }


    // ==================================================
    // COUNT
    // ==================================================

    @Override
    public int getItemCount() {

        return chatList == null
                ? 0
                : chatList.size();
    }


    // ==================================================
    // VIEW HOLDER
    // ==================================================

    static class ChatViewHolder
            extends RecyclerView.ViewHolder {

        TextView avatar;
        TextView username;
        TextView lastMessage;
        TextView time;


        ChatViewHolder(
                @NonNull View itemView,
                TextView avatar,
                TextView username,
                TextView lastMessage,
                TextView time) {

            super(
                    itemView
            );

            this.avatar =
                    avatar;

            this.username =
                    username;

            this.lastMessage =
                    lastMessage;

            this.time =
                    time;
        }
    }
}