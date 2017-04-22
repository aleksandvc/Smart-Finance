package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Section class represents an Income Account or an Expense Category.
 * Sections should be unique by name + type.
 */
public class Account implements Serializable, Comparable<Account> {

    private String name;
    private int id;
    private int iconID;
    private double sum;
    private HashMap<Category.Type,ArrayList<Transaction>> transactions;

    /**
     * Section constructor.
     * @param name Section name : a non-null and not-empty String.
     * @param iconID Resource ID for icon image.
     */
    public Account(@NonNull String name, int iconID) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The Section name must be a non-null and not-empty string!");
        if (iconID == -1)
            throw new IllegalArgumentException();

        this.sum = 0.0;
        this.name = name;
        this.iconID = iconID;

        transactions = new HashMap<>();
    }


    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NonNull Account o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account section = (Account) o;

        return (name.equals(section.name));

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public double getSum(){ return sum; }

    public List<Transaction> getTransactions() {
        ArrayList<Transaction> tempTransactions = new ArrayList<>();
        tempTransactions.addAll(transactions.get(Category.Type.EXPENSE));
        tempTransactions.addAll(transactions.get(Category.Type.INCOME));

        return Collections.unmodifiableList(tempTransactions);
    }

    public boolean addTransaction(Transaction entry) {
      return true;
    }
}
