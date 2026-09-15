package com.example.login;

@SuppressWarnings("unused")
public class Message {

    // ==================================================
    // MESSAGE DATA
    // ==================================================

    private String messageId;
    private String senderId;
    private String receiverId;

    // Text
    private String message;

    // Cloudinary URL
    private String imageUrl;

    // text / image / video
    private String messageType;

    // sent / delivered / seen
    private String status;

    // Message time
    private long timestamp;

    // true = unsent
    private boolean isUnsent;


    // ==================================================
    // TEMPORARY UPLOAD DATA
    // Local UI only
    // ==================================================

    // true while photo/video is uploading
    private boolean uploading;

    // 0 - 100
    private int uploadProgress;


    // ==================================================
    // EMPTY CONSTRUCTOR
    // Firestore requires this
    // ==================================================

    public Message() {
    }


    // ==================================================
    // TEXT MESSAGE CONSTRUCTOR
    // ==================================================

    public Message(
            String messageId,
            String senderId,
            String receiverId,
            String message,
            long timestamp,
            String status) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;

        this.message = message;

        this.imageUrl = "";

        this.messageType = "text";

        this.status = status;

        this.timestamp = timestamp;

        this.isUnsent = false;

        this.uploading = false;
        this.uploadProgress = 0;
    }


    // ==================================================
    // MEDIA MESSAGE CONSTRUCTOR
    // ==================================================

    public Message(
            String messageId,
            String senderId,
            String receiverId,
            String message,
            String imageUrl,
            String messageType,
            long timestamp,
            String status) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;

        this.message = message;

        this.imageUrl = imageUrl;

        this.messageType = messageType;

        this.status = status;

        this.timestamp = timestamp;

        this.isUnsent = false;

        this.uploading = false;
        this.uploadProgress = 0;
    }


    // ==================================================
    // FULL CONSTRUCTOR
    // ==================================================

    public Message(
            String messageId,
            String senderId,
            String receiverId,
            String message,
            String imageUrl,
            String messageType,
            long timestamp,
            String status,
            boolean isUnsent) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;

        this.message = message;
        this.imageUrl = imageUrl;
        this.messageType = messageType;

        this.timestamp = timestamp;
        this.status = status;

        this.isUnsent = isUnsent;

        this.uploading = false;
        this.uploadProgress = 0;
    }


    // ==================================================
    // MESSAGE ID
    // ==================================================

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(
            String messageId) {

        this.messageId = messageId;
    }


    // ==================================================
    // SENDER ID
    // ==================================================

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(
            String senderId) {

        this.senderId = senderId;
    }


    // ==================================================
    // RECEIVER ID
    // ==================================================

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(
            String receiverId) {

        this.receiverId = receiverId;
    }


    // ==================================================
    // MESSAGE
    // ==================================================

    public String getMessage() {
        return message;
    }

    public void setMessage(
            String message) {

        this.message = message;
    }


    // ==================================================
    // IMAGE / VIDEO URL
    // ==================================================

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl) {

        this.imageUrl = imageUrl;
    }


    // ==================================================
    // MESSAGE TYPE
    // ==================================================

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(
            String messageType) {

        this.messageType = messageType;
    }


    // ==================================================
    // STATUS
    // ==================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status = status;
    }


    // ==================================================
    // TIMESTAMP
    // ==================================================

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(
            long timestamp) {

        this.timestamp = timestamp;
    }


    // ==================================================
    // UNSENT
    // ==================================================

    public boolean isUnsent() {
        return isUnsent;
    }

    public void setUnsent(
            boolean unsent) {

        isUnsent = unsent;
    }


    // ==================================================
    // UPLOADING
    // ==================================================

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(
            boolean uploading) {

        this.uploading = uploading;
    }


    // ==================================================
    // UPLOAD PROGRESS
    // ==================================================

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(
            int uploadProgress) {

        this.uploadProgress = uploadProgress;
    }
}