package com.vladimircvetanov.smartfinance.model;

import android.accounts.Account;

import com.vladimircvetanov.smartfinance.R;

import java.util.HashSet;

public class User {

    private String email;
    private String password;
    private int imageId;
    public static HashSet<Account> accounts;
    public static HashSet<Section> favouriteCategories;

    public User(String email, String password) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        accounts = new HashSet<>();
        favouriteCategories = new HashSet<>();
        addDefaultCategories();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    private void addDefaultCategories() {
        favouriteCategories.add(new Section("Vehicle", Manager.Type.EXPENSE, R.mipmap.car, true));
        favouriteCategories.add(new Section("Clothes", Manager.Type.EXPENSE, R.mipmap.clothes, true));
        favouriteCategories.add(new Section("Health", Manager.Type.EXPENSE, R.mipmap.heart, true));
        favouriteCategories.add(new Section("Travel", Manager.Type.EXPENSE, R.mipmap.plane, true));
        favouriteCategories.add(new Section("House", Manager.Type.EXPENSE, R.mipmap.home, true));
        favouriteCategories.add(new Section("Sport", Manager.Type.EXPENSE, R.mipmap.swimming, true));
        favouriteCategories.add(new Section("Food", Manager.Type.EXPENSE, R.mipmap.restaurant, true));
        favouriteCategories.add(new Section("Transport", Manager.Type.EXPENSE, R.mipmap.train, true));
        favouriteCategories.add(new Section("Entertainment", Manager.Type.EXPENSE, R.mipmap.cocktail, true));
        favouriteCategories.add(new Section("Phone", Manager.Type.EXPENSE, R.mipmap.phone, true));
    }
}
