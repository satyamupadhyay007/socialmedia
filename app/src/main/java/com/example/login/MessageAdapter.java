package com.example.login;

import android.content.Context;
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
        LinearLayout row = createRootRow(parent);
        FrameLayout contentFrame = createContentFrame(parent);
        row.addView(contentFrame);

        contentFrame.addView(createTextMessage(parent));
        contentFrame.addView(createMediaImage(parent));
        contentFrame.addView(createVideoPlayIcon(parent));
        contentFrame.addView(createUploadProgressBar(parent));
        contentFrame.addView(createUploadProgressText(parent));
        row.addView(createStatusText(parent));

        return new MessageViewHolder(row);
    }

    private LinearLayout createRootRow(ViewGroup parent) {
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(Gravity.START);
        row.setPadding(dp(row.getContext(), 2), dp(row.getContext(), 4), dp(row.getContext(), 2), dp(row.getContext(), 4));
        row.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return row;
    }

    private FrameLayout createContentFrame(ViewGroup parent) {
        FrameLayout contentFrame = new FrameLayout(parent.getContext());
        contentFrame.setTag("contentFrame");
        contentFrame.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return contentFrame;
    }

    private TextView createTextMessage(ViewGroup parent) {
        TextView textMessage = new TextView(parent.getContext());
        textMessage.setTag("textMessage");
        textMessage.setTextSize(16);
        textMessage.setTextColor(Color.WHITE);
        textMessage.setGravity(Gravity.CENTER_VERTICAL);
        textMessage.setPadding(dp(textMessage.getContext(), 18), dp(textMessage.getContext(), 12), dp(textMessage.getContext(), 18), dp(textMessage.getContext(), 12));
        textMessage.setMaxWidth((int) (parent.getResources().getDisplayMetrics().widthPixels * 0.75f));
        return textMessage;
    }

    private ImageView createMediaImage(ViewGroup parent) {
        ImageView mediaImage = new ImageView(parent.getContext());
        mediaImage.setTag("mediaImage");
        mediaImage.setLayoutParams(new FrameLayout.LayoutParams(dp(mediaImage.getContext(), 216), dp(mediaImage.getContext(), 264)));
        mediaImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mediaImage.setVisibility(View.GONE);
        mediaImage.setBackgroundColor(Color.rgb(24, 24, 24));
        return mediaImage;
    }

    private TextView createVideoPlayIcon(ViewGroup parent) {
        TextView videoPlay = new TextView(parent.getContext());
        videoPlay.setTag("videoPlay");
        videoPlay.setText("▶");
        videoPlay.setTextSize(30);
        videoPlay.setTextColor(Color.WHITE);
        videoPlay.setGravity(Gravity.CENTER);
        videoPlay.setBackground(createCircleBackground(Color.argb(170, 0, 0, 0)));
        FrameLayout.LayoutParams playParams = new FrameLayout.LayoutParams(dp(videoPlay.getContext(), 60), dp(videoPlay.getContext(), 60));
        playParams.gravity = Gravity.CENTER;
        videoPlay.setLayoutParams(playParams);
        videoPlay.setVisibility(View.GONE);
        return videoPlay;
    }

    private ProgressBar createUploadProgressBar(ViewGroup parent) {
        ProgressBar uploadProgressBar = new ProgressBar(parent.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        uploadProgressBar.setTag("uploadProgressBar");
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(dp(uploadProgressBar.getContext(), 170), dp(uploadProgressBar.getContext(), 8));
        progressParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        progressParams.bottomMargin = dp(uploadProgressBar.getContext(), 12);
        uploadProgressBar.setLayoutParams(progressParams);
        uploadProgressBar.setMax(100);
        uploadProgressBar.setVisibility(View.GONE);
        return uploadProgressBar;
    }

    private TextView createUploadProgressText(ViewGroup parent) {
        TextView uploadProgressText = new TextView(parent.getContext());
        uploadProgressText.setTag("uploadProgressText");
        uploadProgressText.setTextColor(Color.WHITE);
        uploadProgressText.setTextSize(13);
        uploadProgressText.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams progressTextParams = new FrameLayout.LayoutParams(dp(uploadProgressText.getContext(), 60), dp(uploadProgressText.getContext(), 30));
        progressTextParams.gravity = Gravity.CENTER;
        uploadProgressText.setLayoutParams(progressTextParams);
        uploadProgressText.setVisibility(View.GONE);
        return uploadProgressText;
    }

    private TextView createStatusText(ViewGroup parent) {
        TextView statusText = new TextView(parent.getContext());
        statusText.setTag("statusText");
        statusText.setTextSize(11);
        statusText.setTextColor(Color.LTGRAY);
        statusText.setPadding(dp(statusText.getContext(), 8), dp(statusText.getContext(), 2), dp(statusText.getContext(), 8), 0);
        statusText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return statusText;
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
                    message
            );

        } else if (
                "video".equalsIgnoreCase(
                        messageType
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
                        holder.itemView.getContext(),
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
            Message message
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
                            createVideoPlaceholder(holder.itemView.getContext())
                    )
                    .error(
                            createVideoPlaceholder(holder.itemView.getContext())
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


            String finalRemaining = getFinalRemaining(videoUrl.substring(start));


            return prefix +
                    "so_0/" +
                    finalRemaining +
                    ".jpg";

        } catch (Exception e) {

            return videoUrl;
        }
    }

    private String getFinalRemaining(String rawRemaining) {
        String remainingWithoutVersion =
                (rawRemaining.startsWith("v") && rawRemaining.contains("/"))
                        ? rawRemaining.substring(rawRemaining.indexOf("/") + 1)
                        : rawRemaining;


        int queryIndex =
                remainingWithoutVersion.indexOf(
                        "?"
                );


        String remainingWithoutQuery =
                (queryIndex != -1)
                        ? remainingWithoutVersion.substring(0, queryIndex)
                        : remainingWithoutVersion;


        int dotIndex =
                remainingWithoutQuery.lastIndexOf(
                        "."
                );


        return (dotIndex != -1)
                ? remainingWithoutQuery.substring(0, dotIndex)
                : remainingWithoutQuery;
    }


    // ==================================================
    // VIDEO PLACEHOLDER
    // ==================================================

    private GradientDrawable createVideoPlaceholder(Context context) {

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
                dp(context, 18)
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

    private int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    private GradientDrawable createCircleBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }
}