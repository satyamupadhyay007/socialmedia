package com.example.login;

public class ChatItem {

    private String uid;
    private String username;
    private String name;
    private String profileImage;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;

    public ChatItem() {
    }

    // Old constructor - compatibility
    public ChatItem(
            String uid,
            String username,
            String lastMessage,
            long lastMessageTime,
            int unreadCount
    ) {
        this.uid = uid;
        this.username = username;
        this.name = "";
        this.profileImage = "";
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    // New constructor
    public ChatItem(
            String uid,
            String username,
            String name,
            String profileImage,
            String lastMessage,
            long lastMessageTime,
            int unreadCount
    ) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}