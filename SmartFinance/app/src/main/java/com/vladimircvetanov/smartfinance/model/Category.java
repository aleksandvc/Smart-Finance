package com.vladimircvetanov.smartfinance.model;

/**
 * Created by vladimircvetanov on 18.04.17.
 */

public abstract class Category {



    public enum Type {
        INCOME, EXPENSE
    }

    private Type type;
    private String name;
    private int id;
    private int iconId;


    public Category(Type type, String name,int iconId) {
        this.type = type;
        this.name = name;
        this.iconId = iconId;

    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public int getIconId() {
        return iconId;
    }
}
