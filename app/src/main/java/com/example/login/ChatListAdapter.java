package com.example.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatListAdapter
        extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatItem chatItem);
    }

    public ChatListAdapter(
            List<ChatItem> chatList,
            OnChatClickListener listener
    ) {
        this.chatList =
                Objects.requireNonNullElseGet(chatList, ArrayList::new);

        this.listener = listener;
    }

    public void updateList(List<ChatItem> newList) {
        chatList = Objects.requireNonNullElseGet(newList, ArrayList::new);

        notifyDataSetChanged();
    }

    private int dp(Context context, int value) {

        return Math.round(
                value *
                        context.getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        Context context = parent.getContext();

        // ==================================================
        // ROW
        // ==================================================

        LinearLayout row =
                new LinearLayout(context);

        row.setLayoutParams(
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
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
                dp(context, 7),
                dp(context, 12),
                dp(context, 7)
        );

        row.setMinimumHeight(
                dp(context, 82)
        );

        // ==================================================
        // PROFILE IMAGE CONTAINER
        // ==================================================

        FrameLayoutFix avatarContainer =
                new FrameLayoutFix(context);

        LinearLayout.LayoutParams avatarContainerParams =
                new LinearLayout.LayoutParams(
                        dp(context, 54),
                        dp(context, 54)
                );

        avatarContainerParams.setMargins(
                0,
                0,
                dp(context, 14),
                0
        );

        row.addView(
                avatarContainer,
                avatarContainerParams
        );

        // ==================================================
        // PROFILE IMAGE
        // ==================================================

        ImageView profileImage =
                new ImageView(context);

        profileImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        GradientDrawable imageBackground =
                new GradientDrawable();

        imageBackground.setShape(
                GradientDrawable.OVAL
        );

        imageBackground.setColor(
                Color.rgb(32, 32, 32)
        );

        imageBackground.setStroke(
                dp(context, 1),
                Color.rgb(75, 75, 75)
        );

        profileImage.setBackground(
                imageBackground
        );

        profileImage.setClipToOutline(true);

        avatarContainer.addView(
                profileImage,
                new FrameLayoutFix.LayoutParams(
                        dp(context, 54),
                        dp(context, 54),
                        Gravity.CENTER
                )
        );

        // ==================================================
        // INITIAL
        // ==================================================

        TextView avatarInitial =
                new TextView(context);

        avatarInitial.setGravity(
                Gravity.CENTER
        );

        avatarInitial.setTextColor(
                Color.WHITE
        );

        avatarInitial.setTextSize(
                17
        );

        avatarInitial.setTypeface(
                null,
                Typeface.BOLD
        );

        avatarInitial.setVisibility(
                View.VISIBLE
        );

        avatarContainer.addView(
                avatarInitial,
                new FrameLayoutFix.LayoutParams(
                        dp(context, 54),
                        dp(context, 54),
                        Gravity.CENTER
                )
        );

        // ==================================================
        // CENTER
        // ==================================================

        LinearLayout center =
                new LinearLayout(context);

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
        // NAME
        // ==================================================

        TextView name =
                new TextView(context);

        name.setTextColor(
                Color.WHITE
        );

        name.setTextSize(
                16
        );

        name.setTypeface(
                null,
                Typeface.BOLD
        );

        name.setIncludeFontPadding(
                false
        );

        name.setMaxLines(
                1
        );

        name.setEllipsize(
                TextUtils.TruncateAt.END
        );

        center.addView(
                name,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // ==================================================
        // USERNAME
        // ==================================================

        TextView username =
                new TextView(context);

        username.setTextColor(
                Color.rgb(175, 175, 175)
        );

        username.setTextSize(
                12
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

        LinearLayout.LayoutParams usernameParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        usernameParams.setMargins(
                0,
                dp(context, 2),
                0,
                0
        );

        center.addView(
                username,
                usernameParams
        );

        // ==================================================
        // LAST MESSAGE
        // ==================================================

        TextView lastMessage =
                new TextView(context);

        lastMessage.setTextColor(
                Color.rgb(150, 150, 150)
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
                dp(context, 4),
                0,
                0
        );

        center.addView(
                lastMessage,
                messageParams
        );

        // ==================================================
        // RIGHT SIDE
        // ==================================================

        LinearLayout right =
                new LinearLayout(context);

        right.setOrientation(
                LinearLayout.VERTICAL
        );

        right.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        LinearLayout.LayoutParams rightParams =
                new LinearLayout.LayoutParams(
                        dp(context, 48),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        rightParams.setMargins(
                dp(context, 8),
                0,
                0,
                0
        );

        row.addView(
                right,
                rightParams
        );

        // ==================================================
        // TIME
        // ==================================================

        TextView time =
                new TextView(context);

        time.setTextColor(
                Color.rgb(145, 145, 145)
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

        right.addView(
                time,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // ==================================================
        // UNREAD BADGE
        // ==================================================

        TextView unreadBadge =
                new TextView(context);

        unreadBadge.setGravity(
                Gravity.CENTER
        );

        unreadBadge.setTextColor(
                Color.WHITE
        );

        unreadBadge.setTextSize(
                10
        );

        unreadBadge.setTypeface(
                null,
                Typeface.BOLD
        );

        unreadBadge.setPadding(
                dp(context, 5),
                0,
                dp(context, 5),
                0
        );

        unreadBadge.setMinWidth(
                dp(context, 20)
        );

        unreadBadge.setMinHeight(
                dp(context, 20)
        );

        GradientDrawable badgeBackground =
                new GradientDrawable();

        badgeBackground.setShape(
                GradientDrawable.OVAL
        );

        badgeBackground.setColor(
                Color.rgb(255, 45, 85)
        );

        unreadBadge.setBackground(
                badgeBackground
        );

        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        dp(context, 20)
                );

        badgeParams.setMargins(
                0,
                dp(context, 6),
                0,
                0
        );

        right.addView(
                unreadBadge,
                badgeParams
        );

        return new ChatViewHolder(
                row,
                profileImage,
                avatarInitial,
                name,
                username,
                lastMessage,
                time,
                unreadBadge
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder,
            int position
    ) {

        if (
                chatList == null ||
                        position < 0 ||
                        position >= chatList.size()
        ) {
            return;
        }

        ChatItem item =
                chatList.get(position);

        if (item == null) {
            return;
        }

        // ==================================================
        // SAFE USERNAME
        // ==================================================

        String username =
                safe(
                        item.getUsername()
                );

        if (username.isEmpty()) {
            username = "Unknown User";
        }

        // ==================================================
        // SAFE NAME
        // ==================================================

        String name =
                safe(
                        item.getName()
                );

        if (name.isEmpty()) {
            name = username;
        }

        // ==================================================
        // NAME
        // ==================================================

        holder.name.setText(
                name
        );

        // ==================================================
        // USERNAME
        // ==================================================

        if (
                username.equalsIgnoreCase(name)
        ) {
            holder.username.setVisibility(
                    View.GONE
            );
        } else {
            holder.username.setVisibility(
                    View.VISIBLE
            );

            holder.username.setText(
                    String.format("@%s", username)
            );
        }

        // ==================================================
        // MESSAGE
        // ==================================================

        String message =
                safe(
                        item.getLastMessage()
                );

        if (message.isEmpty()) {
            message = "No messages yet";
        }

        holder.lastMessage.setText(
                message
        );

        // ==================================================
        // UNREAD
        // ==================================================

        int unread =
                Math.max(
                        item.getUnreadCount(),
                        0
                );

        if (unread > 0) {

            holder.unreadBadge.setVisibility(
                    View.VISIBLE
            );

            if (unread > 9) {
                holder.unreadBadge.setText(
                        "9+"
                );
            } else {
                holder.unreadBadge.setText(
                        String.valueOf(unread)
                );
            }

            holder.lastMessage.setTextColor(
                    Color.WHITE
            );

            holder.lastMessage.setTypeface(
                    null,
                    Typeface.BOLD
            );

        } else {

            holder.unreadBadge.setVisibility(
                    View.GONE
            );

            holder.lastMessage.setTextColor(
                    Color.rgb(150, 150, 150)
            );

            holder.lastMessage.setTypeface(
                    null,
                    Typeface.NORMAL
            );
        }

        // ==================================================
        // TIME
        // ==================================================

        long timestamp =
                item.getLastMessageTime();

        if (timestamp > 0) {
            holder.time.setText(
                    formatTime(timestamp)
            );
        } else {
            holder.time.setText("");
        }

        // ==================================================
        // PROFILE PHOTO
        // ==================================================

        String imageUrl =
                safe(
                        item.getProfileImage()
                );

        holder.avatarInitial.setText(
                getInitial(
                        name
                )
        );

        holder.avatarInitial.setVisibility(
                View.VISIBLE
        );

        holder.profileImage.setImageDrawable(
                null
        );

        if (!imageUrl.isEmpty()) {

            Glide.with(
                            holder.itemView.getContext()
                    )
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(
                            holder.profileImage.getBackground()
                    )
                    .error(
                            holder.profileImage.getBackground()
                    )
                    .listener(
                            new RequestListener<>() {

                                @Override
                                public boolean onLoadFailed(
                                        GlideException e,
                                        Object model,
                                        Target<Drawable> target,
                                        boolean isFirstResource
                                ) {
                                    holder.avatarInitial.setVisibility(
                                            View.VISIBLE
                                    );

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(
                                        Drawable resource,
                                        Object model,
                                        Target<Drawable> target,
                                        DataSource dataSource,
                                        boolean isFirstResource
                                ) {
                                    holder.avatarInitial.setVisibility(
                                            View.GONE
                                    );

                                    return false;
                                }
                            }
                    )
                    .into(
                            holder.profileImage
                    );
        }

        // ==================================================
        // CLICK
        // ==================================================

        holder.itemView.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onChatClick(
                                item
                        );
                    }
                }
        );
    }

    private String safe(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String getInitial(String text) {

        String safeText =
                safe(text);

        if (safeText.isEmpty()) {
            return "?";
        }

        return safeText
                .substring(0, 1)
                .toUpperCase(
                        Locale.getDefault()
                );
    }

    private String formatTime(long timestamp) {

        Date date =
                new Date(timestamp);

        Date now =
                new Date();

        SimpleDateFormat dayFormat =
                new SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.getDefault()
                );

        if (
                Objects.equals(dayFormat.format(date), dayFormat.format(now))
        ) {

            return new SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
            ).format(date);
        }

        return new SimpleDateFormat(
                "dd/MM",
                Locale.getDefault()
        ).format(date);
    }

    @Override
    public int getItemCount() {

        return chatList == null
                ? 0
                : chatList.size();
    }

    static class ChatViewHolder
            extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView avatarInitial;
        TextView name;
        TextView username;
        TextView lastMessage;
        TextView time;
        TextView unreadBadge;

        ChatViewHolder(
                @NonNull View itemView,
                ImageView profileImage,
                TextView avatarInitial,
                TextView name,
                TextView username,
                TextView lastMessage,
                TextView time,
                TextView unreadBadge
        ) {

            super(itemView);

            this.profileImage =
                    profileImage;

            this.avatarInitial =
                    avatarInitial;

            this.name =
                    name;

            this.username =
                    username;

            this.lastMessage =
                    lastMessage;

            this.time =
                    time;

            this.unreadBadge =
                    unreadBadge;
        }
    }

    // ==================================================
    // SIMPLE FRAME LAYOUT
    // ==================================================

    private static class FrameLayoutFix
            extends android.widget.FrameLayout {

        public FrameLayoutFix(Context context) {
            super(context);
        }

        static class LayoutParams
                extends android.widget.FrameLayout.LayoutParams {

            LayoutParams(
                    int width,
                    int height,
                    int gravity
            ) {
                super(
                        width,
                        height
                );

                this.gravity =
                        gravity;
            }
        }
    }
}