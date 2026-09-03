package com.example.login;

public class ChatItem {

    private String uid;
    private String username;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;


    // Firebase ke liye
    public ChatItem() {
    }


    public ChatItem(
            String uid,
            String username,
            String lastMessage,
            long lastMessageTime,
            int unreadCount) {

        this.uid = uid;
        this.username = username;
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