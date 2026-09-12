package com.example.login;

public class User {

    // ==================================================
    // USER DATA
    // ==================================================

    private String uid;
    private String username;
    private String name;
    private String profileImage;


    // ==================================================
    // EMPTY CONSTRUCTOR
    // ==================================================

    public User() {
    }


    // ==================================================
    // OLD CONSTRUCTOR
    // ==================================================

    public User(
            String uid,
            String username
    ) {

        this.uid = uid;
        this.username = username;
        this.name = "";
        this.profileImage = "";
    }


    // ==================================================
    // NAME CONSTRUCTOR
    // ==================================================

    public User(
            String uid,
            String username,
            String name
    ) {

        this.uid = uid;
        this.username = username;
        this.name = name;
        this.profileImage = "";
    }


    // ==================================================
    // FULL CONSTRUCTOR
    // ==================================================

    public User(
            String uid,
            String username,
            String name,
            String profileImage
    ) {

        this.uid = uid;
        this.username = username;
        this.name = name;
        this.profileImage = profileImage;
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
    // USERNAME / USER ID
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


    // ==================================================
    // PROFILE IMAGE
    // ==================================================

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(
            String profileImage
    ) {

        this.profileImage = profileImage;
    }
}