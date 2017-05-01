package com.vladimircvetanov.smartfinance.model;

import java.io.Serializable;

/**
 * Created by vladimircvetanov on 18.04.17.
 */

public class CategoryExpense extends Category implements Serializable{

    private boolean isFavourite;

    public CategoryExpense(String name, boolean isFavourite, int iconId) {
        super(Type.EXPENSE, name, iconId);
        this.isFavourite = isFavourite;
    }
    public boolean getIsFavourite(){
        return isFavourite;
    }
}
