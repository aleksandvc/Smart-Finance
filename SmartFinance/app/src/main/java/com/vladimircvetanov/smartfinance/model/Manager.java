package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.R;

import java.util.HashMap;
import java.util.HashSet;

public class Manager {

    /**
     * Transaction types.
     */


    private static User loggedUser;
    private static Manager instance = null;

    /**
     * A collection that maintains a list of all Sections (both Income and Expense) and distributes input accordingly.
     */
    private HashSet<Account>accounts;

    private Manager() {
        accounts = new HashSet<>();

    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
            //TODO - temporary : for test purposes
            addSection(new Account("Cash", R.mipmap.letter));
            addSection(new Account("Debit",R.mipmap.lock));
            addSection(new Account("Credit", R.mipmap.lockche));
        }
        return instance;
    }

    public static void setLoggedUser(User user) {
        if (user != null) {
            loggedUser = user;
        }
    }

    public static User getLoggedUser() {
        return loggedUser;
    }



    /**
     * Get the balance of all active Accounts (INCOMING type Sections) in the {@link Manager#accounts}
     *
     * @return balance of all Accounts in masterLog
     */
    public static double getSum() {
        double sum = 0;
        for (Account a : getInstance().accounts) {
            sum += a.getSum();
        }
        return sum;
    }


    /**
     * Wraps HashSet.add(Object) for the {@link Manager#accounts}
     *
     * @param account
     * @return <code>true</code> if addition completed successfully
     */
    public static boolean addSection(Account account) {
        return account != null && getInstance().accounts.add(account);
    }



}
