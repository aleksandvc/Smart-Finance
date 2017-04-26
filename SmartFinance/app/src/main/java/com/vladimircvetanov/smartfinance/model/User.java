package com.vladimircvetanov.smartfinance.model;

import java.io.Serializable;
import java.util.HashSet;

public class User implements Serializable {

    private String email;
    private String password;
    private long id;
    public float totalSum;
    public static HashSet<Account> accounts;



    public User(String email, String password) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        totalSum = 0f;
        accounts = new HashSet<>();



    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTotalSum(float totalSum) {
        this.totalSum = totalSum;
    }

    public long getId() {
        return id;
    }


}
