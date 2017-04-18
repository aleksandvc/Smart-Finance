package com.vladimircvetanov.smartfinance.model;

import android.accounts.Account;

import com.vladimircvetanov.smartfinance.R;

import java.io.Serializable;
import java.util.HashSet;

public class User implements Serializable {

    private String email;
    private String password;
    private long id;
    private int imageId;
    public float totalSum;
    public static HashSet<Account> accounts;
    public static HashSet<CategoryExpense> favouriteCategories;

    public User(String email, String password) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        totalSum = 0f;
        accounts = new HashSet<>();
        favouriteCategories = new HashSet<>();

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

    /*private void addDefaultCategories() {
        favouriteCategories.add(new CategoryExpense("Vehicle", Manager.Type.EXPENSE, R.mipmap.car, true));
        favouriteCategories.add(new Section("Clothes", Manager.Type.EXPENSE, R.mipmap.clothes, true));
        favouriteCategories.add(new Section("Health", Manager.Type.EXPENSE, R.mipmap.heart, true));
        favouriteCategories.add(new Section("Travel", Manager.Type.EXPENSE, R.mipmap.plane, true));
        favouriteCategories.add(new Section("House", Manager.Type.EXPENSE, R.mipmap.home, true));
        favouriteCategories.add(new Section("Sport", Manager.Type.EXPENSE, R.mipmap.swimming, true));
        favouriteCategories.add(new Section("Food", Manager.Type.EXPENSE, R.mipmap.restaurant, true));
        favouriteCategories.add(new Section("Transport", Manager.Type.EXPENSE, R.mipmap.train, true));
        favouriteCategories.add(new Section("Entertainment", Manager.Type.EXPENSE, R.mipmap.cocktail, true));
        favouriteCategories.add(new Section("Phone", Manager.Type.EXPENSE, R.mipmap.phone, true));
        for (Section s : favouriteCategories)
            Manager.addSection(s);
    }*/
}
