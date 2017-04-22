package com.vladimircvetanov.smartfinance.model;

import java.util.ArrayList;
import java.util.Collections;
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

    public static HashSet<Integer> allExpenseIcons;
    private ArrayList<Transaction> transactions;

    private Manager() {

        transactions = new ArrayList<>();
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
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
     * Get the balance of all active Accounts (INCOMING type Sections) in the {@link Manager#}
     *
     * @return balance of all Accounts in masterLog
     */
   /* public static double getSum() {
        double sum = 0;
        for (Account a : getInstance().accounts) {
            sum += a.getSum();
        }
        return sum;
    }*/

    public static boolean addTransaction(Transaction transaction) {
        return transaction != null && getInstance().transactions.add(transaction);
    }
}
