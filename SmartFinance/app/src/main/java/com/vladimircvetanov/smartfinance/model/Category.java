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


    public Category(Type type, String name) {
        this.type = type;
        this.name = name;

    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }



    public int getId() {
        return id;
    }


}
