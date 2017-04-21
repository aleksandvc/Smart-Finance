package com.vladimircvetanov.smartfinance.model;

/**
 * Created by vladimircvetanov on 18.04.17.
 */

public class CategoryExpense extends Category {


    private boolean isFavourite;


    public CategoryExpense(String name,boolean isFavourite,int iconId) {
        super(Type.EXPENSE, name,iconId);
        this.isFavourite = isFavourite;

    }
}
