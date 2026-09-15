package com.example.login;

@SuppressWarnings("unused")
public class Post {

    private String postId;
    private String userId;
    private String username;
    private String mediaUrl;
    private String mediaType;
    private String caption;

    public Post() {
    }

    public Post(
            String postId,
            String userId,
            String username,
            String mediaUrl,
            String mediaType,
            String caption) {

        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}