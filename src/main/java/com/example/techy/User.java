package com.example.techy;

public class User {

    public String email;
    public Boolean isAdmin;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(Boolean isAdmin, String email) {
        this.isAdmin = isAdmin;
        this.email = email;
    }

}
