package com.vladimircvetanov.smartfinance.model;

public class Category implements Manager.IType{

    private String name;
    private int icon;
    private double totalAmount;

    public Category(String name, int icon) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (icon != 0) {
            this.icon = icon;
        }
        this.totalAmount = 0;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void increaseAmount(Double amount) {
        totalAmount += amount;
    }

    public void decreaseAmount(Double amount) {
        totalAmount -= amount;
    }
}
