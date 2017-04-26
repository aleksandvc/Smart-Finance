package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.RowDisplayable;
import com.vladimircvetanov.smartfinance.db.DBAdapter;

/**
 * Created by vladimircvetanov on 18.04.17.
 */

public abstract class Category implements RowDisplayable {

    public enum Type {INCOME, EXPENSE}

    private Type type;
    private String name;
    private long id;
    private int iconId;
    private double sum;
    private long userFk;

    public Category(Type type, String name,int iconId) {
        this.type = type;
        this.name = name;
        this.iconId = iconId;
        this.sum = 0.0d;
    }

    public long getUserFk() {
        return userFk;
    }

    public void setUserFk(long userFk) {
        this.userFk = userFk;
    }

    public Type getType() {
        return type;
    }

    public double getSum() { return sum; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }
}
