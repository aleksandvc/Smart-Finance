package com.vladimircvetanov.smartfinance.model;

import android.util.Log;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class Manager {

    private static Manager instance = null;
    /**
     * Marker interface for (e) Manager.Category and (e) Account.Type
     */
    public interface IType {}

    /**
     * Expense categories;
     */
    public enum Category implements IType {VEHICLE, CLOTHES, HEALTH, TRAVEL, SPORT, FOOD, TRANSPORT, PHONE, HOUSE, ENTERTAIMENT}

    /**
     * Transaction types.
     */
    public enum Type {INCOMING, EXPENSE}

    private HashMap<Type, HashMap<IType, TreeMap<LocalDate, Double>>> logs;


    private Manager() {
        logs = new HashMap<>();
        logs.put(Type.INCOMING, new HashMap<IType, TreeMap<LocalDate, Double>>());
        logs.put(Type.EXPENSE, new HashMap<IType, TreeMap<LocalDate, Double>>());
        populateITypes();
    }




    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }


    /**
     * Adds an entry, corresponding to a User expense or income, into the financial {@link Manager#logs}.
     *
     * @param direction income or expense.
     * @param section   appropriate IType enum value.
     * @param date      date on which transaction should happen.
     * @param sum       amount of money. If Type is Type.INCOMING, sum <u>must</u> be non-negative!
     * @return <i>true</i> if entry is successfully added.
     */
    public static boolean addLogEntry(Type direction, IType section, LocalDate date, Double sum) {

        if (direction == Type.INCOMING && sum < 0) return false;

        try {
            getInstance().logs.get(direction).get(section).put(date, sum);
        } catch (NullPointerException e) {
            // HashMap.get(Key) returns null if key doesn't exist
            // => if 'direction' || 'section' aren't present in 'logs' a NullPointer will pop.
            Log.e(getInstance().getClass().getName(), e.getMessage());
            return false;
        }
        return true;
    }

    ;

    /**
     * Populates the logs' second depth, adding all {@link com.vladimircvetanov.smartfinance.model.Manager.IType} values
     * under the appropriate {@link com.vladimircvetanov.smartfinance.model.Manager.Type} key in the logs collection.
     */
    private void populateITypes() {
        IType[] incomeSections = Category.values();
        HashMap<IType, TreeMap<LocalDate, Double>> incomeLog = logs.get(Type.INCOMING);
        for (int i = 0; i < incomeSections.length; i++)
            incomeLog.put(incomeSections[i], new TreeMap<LocalDate, Double>());

        IType[] expenseSections = Account.Type.values();
        HashMap<IType, TreeMap<LocalDate, Double>> expenseLog = logs.get(Type.EXPENSE);
        for (int i = 0; i < expenseSections.length; i++)
            expenseLog.put(expenseSections[i], new TreeMap<LocalDate, Double>());
    }

}
