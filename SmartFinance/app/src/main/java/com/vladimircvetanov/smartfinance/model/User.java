package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.R;

import java.util.HashSet;

public class User {

    private String email;
    private String password;
    private int imageId;
    public static HashSet<Account> accounts;
    public static HashSet<Category> favouriteCategories;

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
        favouriteCategories.add(new Category("Vehicle", R.mipmap.car));
        favouriteCategories.add(new Category("Clothes", R.mipmap.clothes));
        favouriteCategories.add(new Category("Health", R.mipmap.heart));
        favouriteCategories.add(new Category("Travel", R.mipmap.plane));
        favouriteCategories.add(new Category("House", R.mipmap.home));
        favouriteCategories.add(new Category("Sport", R.mipmap.swimming));
        favouriteCategories.add(new Category("Food", R.mipmap.restaurant));
        favouriteCategories.add(new Category("Transport", R.mipmap.train));
        favouriteCategories.add(new Category("Entertainment", R.mipmap.cocktail));
        favouriteCategories.add(new Category("Phone", R.mipmap.phone));
    }
}
