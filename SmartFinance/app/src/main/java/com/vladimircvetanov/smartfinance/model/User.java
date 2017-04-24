package com.vladimircvetanov.smartfinance.model;

import android.accounts.Account;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;

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

        addDefaultCategories();

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

    private void addDefaultCategories() {
        DBAdapter.addFavCategory(new CategoryExpense("Vehicle", true, R.mipmap.car),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Clothes", true, R.mipmap.clothes),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Health", true, R.mipmap.heart),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Travel", true, R.mipmap.plane),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("House", true, R.mipmap.home),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Sport", true, R.mipmap.swimming),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Food", true, R.mipmap.restaurant),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Transport", true, R.mipmap.train),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Entertainment", true, R.mipmap.cocktail),this.id);
        DBAdapter.addFavCategory(new CategoryExpense("Phone", true, R.mipmap.phone),this.id);

    }
}
