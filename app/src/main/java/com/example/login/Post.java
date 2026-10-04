package com.example.login;

import com.google.firebase.Timestamp;

public class Post {
    private String postId;
    private String userId;
    private String userName;
    private String imageUrl;
    private String mediaType;
    private String caption;
    private Timestamp createdAt;

    public Post() {}

    public Post(String postId, String userId, String userName,
                String imageUrl, String mediaType, String caption,
                Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.mediaType = mediaType;
        this.caption = caption;
        this.createdAt = createdAt;
    }

    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getImageUrl() { return imageUrl; }
    public String getMediaType() { return mediaType; }
    public String getCaption() { return caption; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setPostId(String postId) { this.postId = postId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
