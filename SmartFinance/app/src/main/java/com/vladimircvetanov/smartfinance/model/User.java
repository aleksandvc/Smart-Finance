package com.vladimircvetanov.smartfinance.model;

import android.accounts.Account;

import com.vladimircvetanov.smartfinance.R;

import java.io.Serializable;
import java.util.HashSet;

import static com.vladimircvetanov.smartfinance.db.DBAdapter.addExpenseCategory;
import static com.vladimircvetanov.smartfinance.db.DBAdapter.addFavCategory;

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
        favouriteCategories.add(new CategoryExpense("Vehicle", true, R.mipmap.car));
        favouriteCategories.add(new CategoryExpense("Clothes", true, R.mipmap.clothes));
        favouriteCategories.add(new CategoryExpense("Health", true, R.mipmap.heart));
        favouriteCategories.add(new CategoryExpense("Travel", true, R.mipmap.plane));
        favouriteCategories.add(new CategoryExpense("House", true, R.mipmap.home));
        favouriteCategories.add(new CategoryExpense("Sport", true, R.mipmap.swimming));
        favouriteCategories.add(new CategoryExpense("Food", true, R.mipmap.restaurant));
        favouriteCategories.add(new CategoryExpense("Transport", true, R.mipmap.train));
        favouriteCategories.add(new CategoryExpense("Entertainment", true, R.mipmap.cocktail));
        favouriteCategories.add(new CategoryExpense("Phone", true, R.mipmap.phone));

        for (CategoryExpense category : favouriteCategories) {
            addExpenseCategory(category, this.getId());
            addFavCategory(category, this.getId());
            Manager.allExpenseIcons.add(category.getIconId());
        }
    }
}
