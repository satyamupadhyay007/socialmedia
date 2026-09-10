package com.example.login;

public class User {

    // ==================================================
    // USER DATA
    // ==================================================

    private String uid;
    private String username;
    private String name;

    // ==================================================
    // EMPTY CONSTRUCTOR
    // Firestore / Firebase requires this
    // ==================================================

    public User() {
    }

    // ==================================================
    // OLD CONSTRUCTOR
    // Keep for existing code compatibility
    // ==================================================

    public User(
            String uid,
            String username
    ) {

        this.uid = uid;
        this.username = username;
        this.name = "";
    }

    // ==================================================
    // FULL CONSTRUCTOR
    // ==================================================

    public User(
            String uid,
            String username,
            String name
    ) {

        this.uid = uid;
        this.username = username;
        this.name = name;
    }

    // ==================================================
    // UID
    // ==================================================

    public String getUid() {
        return uid;
    }

    public void setUid(
            String uid
    ) {

        this.uid = uid;
    }

    // ==================================================
    // USERNAME
    // ==================================================

    public String getUsername() {
        return username;
    }

    public void setUsername(
            String username
    ) {

        this.username = username;
    }

    // ==================================================
    // NAME
    // ==================================================

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {

        this.name = name;
    }
}