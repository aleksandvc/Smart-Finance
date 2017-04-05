package com.vladimircvetanov.smartfinance.model;

import java.util.HashSet;

public class User {

    private String email;
    private String password;
    private int imageId;
    private HashSet<Account> accounts;

    public User(String email, String password) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        accounts = new HashSet<>();
    }
}