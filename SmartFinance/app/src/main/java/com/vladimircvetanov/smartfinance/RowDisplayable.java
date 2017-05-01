package com.vladimircvetanov.smartfinance;

public interface RowDisplayable {

    long getId();

    String getName();

    int getIconId();

    double getSum();

    boolean getIsFavourite();

    void setUserFk(long userId);

    void setId(long l);

    void setName(String newName);

    void setIconId(int newIconId);
}
