package com.vladimircvetanov.smartfinance.model;

import java.io.Serializable;

/**
 * Created by vladimircvetanov on 18.04.17.
 */

public class CategoryIncome extends Category implements Serializable{

    public CategoryIncome(String name, int iconId) {
        super(Type.INCOME, name, iconId);
    }

    @Override
    public boolean getIsFavourite() {
        return false;
    }
}
