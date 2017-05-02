package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 */
public class Account implements Serializable, Comparable<Account>, RowDisplayable {

    private String name;
    private long id;
    private int iconId;
    private double sum;
    private long userFk;
    private HashMap<Category.Type,ArrayList<Transaction>> transactions;

    /**
     * Account constructor.
     * @param name Account name : a non-null and not-empty String.
     * @param iconId Resource ID for icon image.
     */
    public Account(@NonNull String name, int iconId) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The Section name must be a non-null and not-empty string!");
        if (iconId == -1)
            throw new IllegalArgumentException();

        this.sum = 0.0;
        this.name = name;
        this.iconId = iconId;

        transactions = new HashMap<>();
        transactions.put(Category.Type.EXPENSE, new ArrayList<Transaction>());
        transactions.put(Category.Type.INCOME, new ArrayList<Transaction>());
    }


    public long getId() {
        return id;
    }

    public long getUserFk() {
        return userFk;
    }

    public void setUserFk(long userFk) {
        this.userFk = userFk;
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

    public int getIconId() {
        return iconId;
    }

    @Override
    public boolean getIsFavourite() {
        return false;
    }

    public double getSum(){
        double sum = 0.0;
        for ( Transaction t : transactions.get(Category.Type.INCOME))
            sum += t.getSum();
        for ( Transaction t : transactions.get(Category.Type.EXPENSE))
            sum -= t.getSum();
        return sum;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<Transaction> getTransactions() {
        ArrayList<Transaction> tempTransactions = new ArrayList<>();
        tempTransactions.addAll(transactions.get(Category.Type.EXPENSE));
        tempTransactions.addAll(transactions.get(Category.Type.INCOME));

        return Collections.unmodifiableList(tempTransactions);
    }

    public boolean addTransaction(Transaction entry) {
        if (entry == null || entry.getCategory() == null) return false;
        Category.Type type = entry.getCategory().getType();
        return transactions.get(type).add(entry);
    }
}
