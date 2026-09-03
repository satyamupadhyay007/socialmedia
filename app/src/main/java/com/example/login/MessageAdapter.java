package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class MessageAdapter
        extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    // ==================================================
    // DATA
    // ==================================================

    private final List<Message> messageList;
    private final String currentUserId;
    private final OnUnsendListener onUnsendListener;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public MessageAdapter(
            List<Message> messageList,
            String currentUserId,
            OnUnsendListener onUnsendListener
    ) {

        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.onUnsendListener = onUnsendListener;
    }

    // ==================================================
    // UNSEND LISTENER
    // ==================================================

    public interface OnUnsendListener {

        void onUnsend(Message message);
    }

    // ==================================================
    // VIEW HOLDER
    // ==================================================

    public static class MessageViewHolder
            extends RecyclerView.ViewHolder {

        LinearLayout rowContainer;

        FrameLayout contentFrame;

        TextView textMessage;
        TextView statusText;

        ImageView mediaImage;

        TextView videoPlay;

        ProgressBar uploadProgressBar;

        TextView uploadProgressText;

        public MessageViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            rowContainer =
                    (LinearLayout) itemView;

            contentFrame =
                    itemView.findViewWithTag(
                            "contentFrame"
                    );

            textMessage =
                    itemView.findViewWithTag(
                            "textMessage"
                    );

            statusText =
                    itemView.findViewWithTag(
                            "statusText"
                    );

            mediaImage =
                    itemView.findViewWithTag(
                            "mediaImage"
                    );

            videoPlay =
                    itemView.findViewWithTag(
                            "videoPlay"
                    );

            uploadProgressBar =
                    itemView.findViewWithTag(
                            "uploadProgressBar"
                    );

            uploadProgressText =
                    itemView.findViewWithTag(
                            "uploadProgressText"
                    );
        }
    }

    // ==================================================
    // CREATE VIEW
    // ==================================================

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        // ==================================================
        // ROOT
        // ==================================================

        LinearLayout row =
                new LinearLayout(
                        parent.getContext()
                );

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setGravity(
                Gravity.START
        );

        row.setPadding(
                dp(parent, 2),
                dp(parent, 4),
                dp(parent, 2),
                dp(parent, 4)
        );

        RecyclerView.LayoutParams rowParams =
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        row.setLayoutParams(
                rowParams
        );

        // ==================================================
        // CONTENT FRAME
        // ==================================================

        FrameLayout contentFrame =
                new FrameLayout(
                        parent.getContext()
                );

        contentFrame.setTag(
                "contentFrame"
        );

        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        row.addView(
                contentFrame,
                contentParams
        );

        // ==================================================
        // TEXT
        // ==================================================

        TextView textMessage =
                new TextView(
                        parent.getContext()
                );

        textMessage.setTag(
                "textMessage"
        );

        textMessage.setTextColor(
                Color.WHITE
        );

        textMessage.setTextSize(
                15
        );

        textMessage.setGravity(
                Gravity.CENTER_VERTICAL
        );

        textMessage.setPadding(
                dp(parent, 14),
                dp(parent, 9),
                dp(parent, 14),
                dp(parent, 9)
        );

        textMessage.setMaxWidth(
                dp(parent, 280)
        );

        textMessage.setVisibility(
                View.GONE
        );

        FrameLayout.LayoutParams textParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        contentFrame.addView(
                textMessage,
                textParams
        );

        // ==================================================
        // MEDIA IMAGE
        // ==================================================

        ImageView mediaImage =
                new ImageView(
                        parent.getContext()
                );

        mediaImage.setTag(
                "mediaImage"
        );

        mediaImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        mediaImage.setAdjustViewBounds(
                false
        );

        mediaImage.setVisibility(
                View.GONE
        );

        mediaImage.setBackground(
                roundedBackground(
                        Color.rgb(35, 35, 35),
                        16
                )
        );

        mediaImage.setClipToOutline(
                true
        );

        FrameLayout.LayoutParams mediaParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 230),
                        dp(parent, 280)
                );

        contentFrame.addView(
                mediaImage,
                mediaParams
        );

        // ==================================================
        // VIDEO PLAY
        // ==================================================

        TextView videoPlay =
                new TextView(
                        parent.getContext()
                );

        videoPlay.setTag(
                "videoPlay"
        );

        videoPlay.setText(
                "▶"
        );

        videoPlay.setTextColor(
                Color.WHITE
        );

        videoPlay.setTextSize(
                26
        );

        videoPlay.setGravity(
                Gravity.CENTER
        );

        videoPlay.setVisibility(
                View.GONE
        );

        videoPlay.setBackground(
                roundedBackground(
                        Color.argb(
                                190,
                                0,
                                0,
                                0
                        ),
                        50
                )
        );

        FrameLayout.LayoutParams playParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 58),
                        dp(parent, 58)
                );

        playParams.gravity =
                Gravity.CENTER;

        contentFrame.addView(
                videoPlay,
                playParams
        );

        // ==================================================
        // UPLOAD PROGRESS
        // ==================================================

        ProgressBar uploadProgressBar =
                new ProgressBar(
                        parent.getContext()
                );

        uploadProgressBar.setTag(
                "uploadProgressBar"
        );

        uploadProgressBar.setIndeterminate(
                false
        );

        uploadProgressBar.setMax(
                100
        );

        uploadProgressBar.setVisibility(
                View.GONE
        );

        FrameLayout.LayoutParams progressParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 42),
                        dp(parent, 42)
                );

        progressParams.gravity =
                Gravity.CENTER;

        contentFrame.addView(
                uploadProgressBar,
                progressParams
        );

        // ==================================================
        // UPLOAD %
        // ==================================================

        TextView uploadProgressText =
                new TextView(
                        parent.getContext()
                );

        uploadProgressText.setTag(
                "uploadProgressText"
        );

        uploadProgressText.setTextColor(
                Color.WHITE
        );

        uploadProgressText.setTextSize(
                13
        );

        uploadProgressText.setGravity(
                Gravity.CENTER
        );

        uploadProgressText.setVisibility(
                View.GONE
        );

        uploadProgressText.setBackground(
                roundedBackground(
                        Color.argb(
                                170,
                                0,
                                0,
                                0
                        ),
                        20
                )
        );

        FrameLayout.LayoutParams percentParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 54),
                        dp(parent, 30)
                );

        percentParams.gravity =
                Gravity.CENTER_HORIZONTAL |
                        Gravity.BOTTOM;

        percentParams.bottomMargin =
                dp(parent, 12);

        contentFrame.addView(
                uploadProgressText,
                percentParams
        );

        // ==================================================
        // STATUS
        // ==================================================

        TextView statusText =
                new TextView(
                        parent.getContext()
                );

        statusText.setTag(
                "statusText"
        );

        statusText.setTextColor(
                Color.LTGRAY
        );

        statusText.setTextSize(
                11
        );

        statusText.setVisibility(
                View.GONE
        );

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        statusParams.topMargin =
                dp(parent, 3);

        row.addView(
                statusText,
                statusParams
        );

        return new MessageViewHolder(
                row
        );
    }

    // ==================================================
    // BIND
    // ==================================================

    @Override
    public void onBindViewHolder(
            @NonNull MessageViewHolder holder,
            int position
    ) {

        Message message =
                messageList.get(position);

        if (message == null) {
            return;
        }

        // ==================================================
        // RESET
        // ==================================================

        holder.textMessage.setVisibility(
                View.GONE
        );

        holder.mediaImage.setVisibility(
                View.GONE
        );

        holder.videoPlay.setVisibility(
                View.GONE
        );

        holder.uploadProgressBar.setVisibility(
                View.GONE
        );

        holder.uploadProgressText.setVisibility(
                View.GONE
        );

        holder.statusText.setVisibility(
                View.GONE
        );

        holder.textMessage.setText(
                ""
        );

        holder.statusText.setText(
                ""
        );

        holder.mediaImage.setImageDrawable(
                null
        );

        holder.mediaImage.setOnClickListener(
                null
        );

        holder.contentFrame.setOnLongClickListener(
                null
        );

        // ==================================================
        // OWNER
        // ==================================================

        boolean isOwnMessage =
                currentUserId != null &&
                        currentUserId.equals(
                                message.getSenderId()
                        );

        // ==================================================
        // UPLOADING MEDIA
        // ==================================================

        if (message.isUploading()) {

            holder.mediaImage.setVisibility(
                    View.VISIBLE
            );

            String localUri =
                    message.getImageUrl();

            if (
                    localUri != null &&
                            !localUri.isEmpty()
            ) {

                Glide.with(
                                holder.itemView.getContext()
                        )
                        .load(
                                Uri.parse(localUri)
                        )
                        .centerCrop()
                        .into(
                                holder.mediaImage
                        );
            }

            holder.uploadProgressBar.setVisibility(
                    View.VISIBLE
            );

            holder.uploadProgressBar.setProgress(
                    message.getUploadProgress()
            );

            holder.uploadProgressText.setVisibility(
                    View.VISIBLE
            );

            holder.uploadProgressText.setText(
                    message.getUploadProgress() +
                            "%"
            );

            applyPosition(
                    holder,
                    true
            );

            return;
        }

        // ==================================================
        // TYPE
        // ==================================================

        String type =
                message.getMessageType();

        if (
                "image".equalsIgnoreCase(
                        type
                )
        ) {

            bindImage(
                    holder,
                    message
            );

        } else if (
                "video".equalsIgnoreCase(
                        type
                )
        ) {

            bindVideo(
                    holder,
                    message
            );

        } else {

            bindText(
                    holder,
                    message,
                    isOwnMessage
            );
        }

        // ==================================================
        // POSITION
        // ==================================================

        applyPosition(
                holder,
                isOwnMessage
        );

        // ==================================================
        // STATUS
        // ==================================================

        if (isOwnMessage) {

            String status =
                    message.getStatus();

            if (
                    status != null &&
                            !status.trim().isEmpty()
            ) {

                holder.statusText.setText(
                        formatStatus(status)
                );

                holder.statusText.setVisibility(
                        View.VISIBLE
                );
            }
        }

        // ==================================================
        // LONG PRESS = UNSEND
        // ==================================================

        holder.contentFrame.setOnLongClickListener(
                v -> {

                    if (
                            isOwnMessage &&
                                    onUnsendListener != null
                    ) {

                        onUnsendListener.onUnsend(
                                message
                        );

                        return true;
                    }

                    return false;
                }
        );
    }

    // ==================================================
    // TEXT
    // ==================================================

    private void bindText(
            MessageViewHolder holder,
            Message message,
            boolean isOwn
    ) {

        holder.textMessage.setVisibility(
                View.VISIBLE
        );

        holder.textMessage.setText(
                message.getMessage()
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setCornerRadius(
                dp(
                        holder.itemView,
                        18
                )
        );

        if (isOwn) {

            background.setColor(
                    Color.rgb(
                            0,
                            122,
                            255
                    )
            );

        } else {

            background.setColor(
                    Color.rgb(
                            45,
                            45,
                            45
                    )
            );
        }

        holder.textMessage.setBackground(
                background
        );
    }

    // ==================================================
    // IMAGE
    // ==================================================

    private void bindImage(
            MessageViewHolder holder,
            Message message
    ) {

        holder.mediaImage.setVisibility(
                View.VISIBLE
        );

        String imageUrl =
                message.getImageUrl();

        if (
                imageUrl == null ||
                        imageUrl.trim().isEmpty()
        ) {

            showMediaError(
                    holder,
                    "Image URL is empty"
            );

            return;
        }

        Glide.with(
                        holder.itemView.getContext()
                )
                .load(imageUrl)
                .centerCrop()
                .listener(
                        new RequestListener<android.graphics.drawable.Drawable>() {

                            @Override
                            public boolean onLoadFailed(
                                    GlideException e,
                                    Object model,
                                    Target<android.graphics.drawable.Drawable> target,
                                    boolean isFirstResource
                            ) {

                                String errorMessage =
                                        "Image load failed";

                                if (e != null &&
                                        e.getRootCauses() != null &&
                                        !e.getRootCauses().isEmpty()) {

                                    errorMessage =
                                            "Image load failed: " +
                                                    e.getRootCauses()
                                                            .get(0)
                                                            .getClass()
                                                            .getSimpleName();
                                }

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                ).show();

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    android.graphics.drawable.Drawable resource,
                                    Object model,
                                    Target<android.graphics.drawable.Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource
                            ) {

                                return false;
                            }
                        }
                )
                .into(
                        holder.mediaImage
                );

        // ==================================================
        // TAP TO OPEN IMAGE
        // ==================================================

        holder.mediaImage.setOnClickListener(
                v -> openMedia(
                        holder,
                        imageUrl,
                        false
                )
        );
    }

    // ==================================================
    // VIDEO
    // ==================================================

    private void bindVideo(
            MessageViewHolder holder,
            Message message
    ) {

        holder.mediaImage.setVisibility(
                View.VISIBLE
        );

        holder.videoPlay.setVisibility(
                View.VISIBLE
        );

        String videoUrl =
                message.getImageUrl();

        if (
                videoUrl == null ||
                        videoUrl.trim().isEmpty()
        ) {

            showMediaError(
                    holder,
                    "Video URL is empty"
            );

            return;
        }

        Glide.with(
                        holder.itemView.getContext()
                )
                .load(videoUrl)
                .centerCrop()
                .listener(
                        new RequestListener<android.graphics.drawable.Drawable>() {

                            @Override
                            public boolean onLoadFailed(
                                    GlideException e,
                                    Object model,
                                    Target<android.graphics.drawable.Drawable> target,
                                    boolean isFirstResource
                            ) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    android.graphics.drawable.Drawable resource,
                                    Object model,
                                    Target<android.graphics.drawable.Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource
                            ) {

                                return false;
                            }
                        }
                )
                .into(
                        holder.mediaImage
                );

        // ==================================================
        // TAP TO OPEN VIDEO
        // ==================================================

        holder.mediaImage.setOnClickListener(
                v -> openMedia(
                        holder,
                        videoUrl,
                        true
                )
        );

        holder.videoPlay.setOnClickListener(
                v -> openMedia(
                        holder,
                        videoUrl,
                        true
                )
        );
    }

    // ==================================================
    // OPEN MEDIA
    // ==================================================

    private void openMedia(
            MessageViewHolder holder,
            String url,
            boolean isVideo
    ) {

        if (
                url == null ||
                        url.trim().isEmpty()
        ) {
            Toast.makeText(
                    holder.itemView.getContext(),
                    "Media unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW
                    );

            Uri uri =
                    Uri.parse(url);

            intent.setDataAndType(
                    uri,
                    isVideo
                            ? "video/*"
                            : "image/*"
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            holder.itemView.getContext()
                    .startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    isVideo
                            ? "No video app found"
                            : "No image app found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ==================================================
    // MEDIA ERROR
    // ==================================================

    private void showMediaError(
            MessageViewHolder holder,
            String message
    ) {

        Toast.makeText(
                holder.itemView.getContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // ==================================================
    // POSITION
    // ==================================================

    private void applyPosition(
            MessageViewHolder holder,
            boolean isOwn
    ) {

        LinearLayout.LayoutParams
                contentParams =
                (LinearLayout.LayoutParams)
                        holder.contentFrame
                                .getLayoutParams();

        contentParams.gravity =
                isOwn
                        ? Gravity.END
                        : Gravity.START;

        holder.contentFrame.setLayoutParams(
                contentParams
        );

        LinearLayout.LayoutParams
                statusParams =
                (LinearLayout.LayoutParams)
                        holder.statusText
                                .getLayoutParams();

        statusParams.gravity =
                isOwn
                        ? Gravity.END
                        : Gravity.START;

        holder.statusText.setLayoutParams(
                statusParams
        );
    }

    // ==================================================
    // STATUS
    // ==================================================

    private String formatStatus(
            String status
    ) {

        if (
                "sent".equalsIgnoreCase(
                        status
                )
        ) {

            return "Sent";
        }

        if (
                "delivered".equalsIgnoreCase(
                        status
                )
        ) {

            return "Delivered";
        }

        if (
                "seen".equalsIgnoreCase(
                        status
                )
        ) {

            return "Seen";
        }

        return status;
    }

    // ==================================================
    // COUNT
    // ==================================================

    @Override
    public int getItemCount() {

        return messageList == null
                ? 0
                : messageList.size();
    }

    // ==================================================
    // DP
    // ==================================================

    private int dp(
            View view,
            int value
    ) {

        return (int)
                (
                        value *
                                view.getResources()
                                        .getDisplayMetrics()
                                        .density
                );
    }

    private int dp(
            ViewGroup parent,
            int value
    ) {

        return (int)
                (
                        value *
                                parent.getResources()
                                        .getDisplayMetrics()
                                        .density
                );
    }

    // ==================================================
    // ROUNDED BACKGROUND
    // ==================================================

    private GradientDrawable roundedBackground(
            int color,
            int radiusDp
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                color
        );

        drawable.setCornerRadius(
                dpValue(
                        radiusDp
                )
        );

        return drawable;
    }

    private float dpValue(
            int value
    ) {

        return value *
                1.0f;
    }
}