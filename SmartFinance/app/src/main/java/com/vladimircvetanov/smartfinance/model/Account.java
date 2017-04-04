package com.vladimircvetanov.smartfinance.model;

/**
 * Created by vladimircvetanov on 04.04.17.
 */

public class Account {
    public enum Type implements Manager.IType{CREDIT_CARD,DEBIT_CARD};
    private Type type;
    private String name;
    private double sum;


    public Account(Type type, String name, double sum) {
        this.type = type;
        this.name = name;
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (Double.compare(account.sum, sum) != 0) return false;
        if (type != account.type) return false;
        return name != null ? name.equals(account.name) : account.name == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(sum);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
