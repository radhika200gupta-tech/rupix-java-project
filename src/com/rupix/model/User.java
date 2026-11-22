package com.rupix.model;

public class User {

    private final String userId;
    private final String name;
    private final String upiId;

    public User(String userId, String name, String upiId) {
        this.userId = userId;
        this.name = name;
        this.upiId = upiId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUpiId() {
        return upiId;
    }

    @Override
    public String toString() {
        return "User ID: " + userId
                + "\nName: " + name
                + "\nUPI ID: " + upiId;
    }
}
