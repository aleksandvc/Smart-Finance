package com.vladimircvetanov.smartfinance.model;

import java.util.HashSet;

/**
 * Created by vladimircvetanov on 04.04.17.
 */

public class User {

    private String email;
    private String password;
    private int imageId;
    private HashSet<Account>accounts;

    public User(String email, String password) {
       if(email!=null && !email.isEmpty()) {
           this.email = email;
       }
        if(password != null && !password.isEmpty()) {
            this.password = password;
        }
        accounts = new HashSet<>();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
