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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

        this.messageList =
                messageList;

        this.currentUserId =
                currentUserId;

        this.onUnsendListener =
                onUnsendListener;
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
        // ROOT ROW
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


        contentFrame.setLayoutParams(
                contentParams
        );


        row.addView(
                contentFrame
        );


        // ==================================================
        // TEXT MESSAGE
        // ==================================================

        TextView textMessage =
                new TextView(
                        parent.getContext()
                );

        textMessage.setTag(
                "textMessage"
        );


        textMessage.setTextSize(
                16
        );


        textMessage.setTextColor(
                Color.WHITE
        );


        textMessage.setGravity(
                Gravity.CENTER_VERTICAL
        );


        textMessage.setPadding(
                dp(parent, 18),
                dp(parent, 12),
                dp(parent, 18),
                dp(parent, 12)
        );


        textMessage.setMaxWidth(
                (int) (
                        parent.getResources()
                                .getDisplayMetrics()
                                .widthPixels
                                * 0.75f
                )
        );


        contentFrame.addView(
                textMessage
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


        int mediaWidth =
                dp(parent, 216);

        int mediaHeight =
                dp(parent, 264);


        FrameLayout.LayoutParams mediaParams =
                new FrameLayout.LayoutParams(
                        mediaWidth,
                        mediaHeight
                );


        mediaImage.setLayoutParams(
                mediaParams
        );


        mediaImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );


        mediaImage.setVisibility(
                View.GONE
        );


        mediaImage.setBackgroundColor(
                Color.rgb(
                        24,
                        24,
                        24
                )
        );


        contentFrame.addView(
                mediaImage
        );


        // ==================================================
        // VIDEO PLAY ICON
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


        videoPlay.setTextSize(
                30
        );


        videoPlay.setTextColor(
                Color.WHITE
        );


        videoPlay.setGravity(
                Gravity.CENTER
        );


        videoPlay.setBackground(
                createCircleBackground(
                        Color.argb(
                                170,
                                0,
                                0,
                                0
                        )
                )
        );


        FrameLayout.LayoutParams playParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 60),
                        dp(parent, 60)
                );


        playParams.gravity =
                Gravity.CENTER;


        videoPlay.setLayoutParams(
                playParams
        );


        videoPlay.setVisibility(
                View.GONE
        );


        contentFrame.addView(
                videoPlay
        );


        // ==================================================
        // PROGRESS BAR
        // ==================================================

        ProgressBar uploadProgressBar =
                new ProgressBar(
                        parent.getContext(),
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );


        uploadProgressBar.setTag(
                "uploadProgressBar"
        );


        FrameLayout.LayoutParams progressParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 170),
                        dp(parent, 8)
                );


        progressParams.gravity =
                Gravity.BOTTOM |
                        Gravity.CENTER_HORIZONTAL;


        progressParams.bottomMargin =
                dp(parent, 12);


        uploadProgressBar.setLayoutParams(
                progressParams
        );


        uploadProgressBar.setMax(
                100
        );


        uploadProgressBar.setVisibility(
                View.GONE
        );


        contentFrame.addView(
                uploadProgressBar
        );


        // ==================================================
        // PROGRESS TEXT
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


        FrameLayout.LayoutParams progressTextParams =
                new FrameLayout.LayoutParams(
                        dp(parent, 60),
                        dp(parent, 30)
                );


        progressTextParams.gravity =
                Gravity.CENTER;


        uploadProgressText.setLayoutParams(
                progressTextParams
        );


        uploadProgressText.setVisibility(
                View.GONE
        );


        contentFrame.addView(
                uploadProgressText
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


        statusText.setTextSize(
                11
        );


        statusText.setTextColor(
                Color.LTGRAY
        );


        statusText.setPadding(
                dp(parent, 8),
                dp(parent, 2),
                dp(parent, 8),
                0
        );


        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        statusText.setLayoutParams(
                statusParams
        );


        row.addView(
                statusText
        );


        return new MessageViewHolder(
                row
        );
    }


    // ==================================================
    // BIND VIEW
    // ==================================================

    @Override
    public void onBindViewHolder(
            @NonNull MessageViewHolder holder,
            int position
    ) {

        Message message =
                messageList.get(
                        position
                );


        if (message == null) {

            clearHolder(
                    holder
            );

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


        holder.textMessage.setText("");

        holder.statusText.setText("");


        holder.mediaImage.setImageDrawable(
                null
        );


        holder.mediaImage.setOnClickListener(
                null
        );


        holder.videoPlay.setOnClickListener(
                null
        );


        holder.textMessage.setOnLongClickListener(
                null
        );


        holder.mediaImage.setOnLongClickListener(
                null
        );


        holder.videoPlay.setOnLongClickListener(
                null
        );


        holder.contentFrame.setOnLongClickListener(
                null
        );


        // ==================================================
        // CHECK OWNER
        // ==================================================

        boolean isOwnMessage =
                Objects.equals(currentUserId, message.getSenderId());


        // ==================================================
        // UPLOADING MESSAGE
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
                    String.format(Locale.getDefault(), "%d%%", message.getUploadProgress())
            );


            applyMessagePosition(
                    holder,
                    isOwnMessage
            );


            // Uploading photo/video is not actual sent message.
            return;
        }


        // ==================================================
        // MESSAGE TYPE
        // ==================================================

        String messageType =
                message.getMessageType();


        if (
                "image".equalsIgnoreCase(
                        messageType
                )
        ) {

            bindImage(
                    holder,
                    message,
                    isOwnMessage
            );

        } else if (
                "video".equalsIgnoreCase(
                        messageType
                )
        ) {

            bindVideo(
                    holder,
                    message,
                    isOwnMessage
            );

        } else {

            bindText(
                    holder,
                    message,
                    isOwnMessage
            );
        }


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
        // POSITION
        // ==================================================

        applyMessagePosition(
                holder,
                isOwnMessage
        );


        // ==================================================
        // LONG PRESS - CONTENT
        // ==================================================

        if (
                isOwnMessage &&
                        onUnsendListener != null
        ) {

            holder.contentFrame.setOnLongClickListener(
                    v -> {

                        onUnsendListener.onUnsend(
                                message
                        );

                        return true;
                    }
            );


            holder.textMessage.setOnLongClickListener(
                    v -> {

                        onUnsendListener.onUnsend(
                                message
                        );

                        return true;
                    }
            );


            holder.mediaImage.setOnLongClickListener(
                    v -> {

                        onUnsendListener.onUnsend(
                                message
                        );

                        return true;
                    }
            );


            holder.videoPlay.setOnLongClickListener(
                    v -> {

                        onUnsendListener.onUnsend(
                                message
                        );

                        return true;
                    }
            );
        }
    }


    // ==================================================
    // TEXT
    // ==================================================

    private void bindText(
            MessageViewHolder holder,
            Message message,
            boolean isOwnMessage
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


        if (isOwnMessage) {

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
            Message message,
            boolean isOwnMessage
    ) {

        holder.mediaImage.setVisibility(
                View.VISIBLE
        );


        String imageUrl =
                message.getImageUrl();


        if (
                imageUrl != null &&
                        !imageUrl.trim().isEmpty()
        ) {

            Glide.with(
                            holder.itemView.getContext()
                    )
                    .load(
                            imageUrl
                    )
                    .centerCrop()
                    .into(
                            holder.mediaImage
                    );


            // ==========================================
            // TAP PHOTO
            // ==========================================

            holder.mediaImage.setOnClickListener(
                    v ->
                            openMedia(
                                    holder,
                                    imageUrl,
                                    false
                            )
            );
        }


        // Video icon hidden for photo
        holder.videoPlay.setVisibility(
                View.GONE
        );
    }


    // ==================================================
    // VIDEO
    // ==================================================

    private void bindVideo(
            MessageViewHolder holder,
            Message message,
            boolean isOwnMessage
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
                videoUrl != null &&
                        !videoUrl.trim().isEmpty()
        ) {

            // ==========================================
            // CLOUDINARY VIDEO THUMBNAIL
            // ==========================================

            String thumbnailUrl =
                    createCloudinaryVideoThumbnail(
                            videoUrl
                    );


            Glide.with(
                            holder.itemView.getContext()
                    )
                    .load(
                            thumbnailUrl
                    )
                    .centerCrop()
                    .placeholder(
                            createVideoPlaceholder()
                    )
                    .error(
                            createVideoPlaceholder()
                    )
                    .into(
                            holder.mediaImage
                    );


            // ==========================================
            // TAP THUMBNAIL
            // ==========================================

            holder.mediaImage.setOnClickListener(
                    v ->
                            openMedia(
                                    holder,
                                    videoUrl,
                                    true
                            )
            );


            // ==========================================
            // TAP PLAY
            // ==========================================

            holder.videoPlay.setOnClickListener(
                    v ->
                            openMedia(
                                    holder,
                                    videoUrl,
                                    true
                            )
            );
        }
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
                    isVideo
                            ? "Video unavailable"
                            : "Photo unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        try {

            // ==========================================
            // VIDEO
            // ==========================================

            if (isVideo) {

                Intent intent =
                        new Intent(
                                holder.itemView.getContext(),
                                VideoPlayerActivity.class
                        );


                intent.putExtra(
                        "videoUrl",
                        url
                );


                holder.itemView
                        .getContext()
                        .startActivity(
                                intent
                        );

                return;
            }


            // ==========================================
            // PHOTO
            // ==========================================

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW
                    );


            intent.setDataAndType(
                    Uri.parse(url),
                    "image/*"
            );


            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );


            holder.itemView
                    .getContext()
                    .startActivity(
                            intent
                    );

        } catch (Exception e) {

            Toast.makeText(
                    holder.itemView.getContext(),
                    isVideo
                            ? "Unable to play video"
                            : "Unable to open photo",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ==================================================
    // CLOUDINARY VIDEO THUMBNAIL
    // ==================================================

    private String createCloudinaryVideoThumbnail(
            String videoUrl
    ) {

        if (
                videoUrl == null ||
                        videoUrl.trim().isEmpty()
        ) {

            return "";
        }


        try {

            // Example:
            // https://res.cloudinary.com/cloud/video/upload/v123/file.mp4
            //
            // becomes:
            // https://res.cloudinary.com/cloud/video/upload/so_0/file.jpg

            String cloudinaryPrefix = "/video/upload/";
            int uploadIndex = videoUrl.indexOf(cloudinaryPrefix);


            if (uploadIndex == -1) {

                return videoUrl;
            }


            int start =
                    uploadIndex +
                            cloudinaryPrefix.length();


            String prefix =
                    videoUrl.substring(
                            0,
                            start
                    );


            String remaining =
                    videoUrl.substring(
                            start
                    );


            if (
                    remaining.startsWith(
                            "v"
                    )
            ) {

                int slashIndex =
                        remaining.indexOf(
                                "/"
                        );


                if (slashIndex != -1) {

                    remaining =
                            remaining.substring(
                                    slashIndex + 1
                            );
                }
            }


            int queryIndex =
                    remaining.indexOf(
                            "?"
                    );


            if (queryIndex != -1) {

                remaining =
                        remaining.substring(
                                0,
                                queryIndex
                        );
            }


            int dotIndex =
                    remaining.lastIndexOf(
                            "."
                    );


            if (dotIndex != -1) {

                remaining =
                        remaining.substring(
                                0,
                                dotIndex
                        );
            }


            return prefix +
                    "so_0/" +
                    remaining +
                    ".jpg";

        } catch (Exception e) {

            return videoUrl;
        }
    }


    // ==================================================
    // VIDEO PLACEHOLDER
    // ==================================================

    private GradientDrawable createVideoPlaceholder() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.rgb(
                        30,
                        30,
                        30
                )
        );

        drawable.setCornerRadius(
                18
        );

        return drawable;
    }


    // ==================================================
    // STATUS FORMAT
    // ==================================================

    private String formatStatus(
            String status
    ) {

        if (status == null) {

            return "";
        }

        switch (status.toLowerCase()) {
            case "sent":
                return "Sent";
            case "delivered":
                return "Delivered";
            case "seen":
                return "Seen";
            default:
                return status;
        }
    }


    // ==================================================
    // MESSAGE POSITION
    // ==================================================

    private void applyMessagePosition(
            MessageViewHolder holder,
            boolean isOwnMessage
    ) {

        LinearLayout.LayoutParams contentParams =
                (LinearLayout.LayoutParams)
                        holder.contentFrame
                                .getLayoutParams();


        if (isOwnMessage) {

            contentParams.gravity =
                    Gravity.END;

        } else {

            contentParams.gravity =
                    Gravity.START;
        }


        holder.contentFrame.setLayoutParams(
                contentParams
        );


        LinearLayout.LayoutParams statusParams =
                (LinearLayout.LayoutParams)
                        holder.statusText
                                .getLayoutParams();


        if (isOwnMessage) {

            statusParams.gravity =
                    Gravity.END;

        } else {

            statusParams.gravity =
                    Gravity.START;
        }


        holder.statusText.setLayoutParams(
                statusParams
        );
    }


    // ==================================================
    // CLEAR HOLDER
    // ==================================================

    private void clearHolder(
            MessageViewHolder holder
    ) {

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
    }


    // ==================================================
    // ITEM COUNT
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
    // CIRCLE BACKGROUND
    // ==================================================

    private GradientDrawable createCircleBackground(
            int color
    ) {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setShape(
                GradientDrawable.OVAL
        );


        drawable.setColor(
                color
        );


        return drawable;
    }
}