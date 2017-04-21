package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.R;

import java.util.ArrayList;
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
    private ArrayList<Account> accounts;
    private ArrayList<CategoryExpense> expenseCategories;
    private ArrayList<CategoryIncome> incomeCategories;
    private ArrayList<Transaction> transactions;


    private Manager() {
        accounts = new ArrayList<>();
        expenseCategories = new ArrayList<>();
        incomeCategories = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
            //TODO - temporary : for test purposes
            addAccount(new Account("Cash", R.mipmap.letter));
            addAccount(new Account("Debit",R.mipmap.lock));
            addAccount(new Account("Credit", R.mipmap.lockche));
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


    public static boolean containsAccount(Account account){
        if(account == null) return false;
        return getInstance().accounts.contains(account);
    }
    public static boolean containsExpenseCat(CategoryExpense category){
        if(category == null) return false;
        return getInstance().expenseCategories.contains(category);
    }
    public static boolean containsIncomeCat(CategoryIncome category){
        if(category == null) return false;
        return getInstance().incomeCategories.contains(category);
    }
    public static boolean containsTransaction(Transaction transaction){
        if(transaction == null) return false;
        return getInstance().incomeCategories.contains(transaction);
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
    public static boolean addAccount(Account account) {
        return account != null && getInstance().accounts.add(account);
    }
    public static boolean addExpenseCategory(CategoryExpense category) {
        return category != null && getInstance().expenseCategories.add(category);
    }
    public static boolean addIncomeCategory(CategoryIncome category) {
        return category != null && getInstance().incomeCategories.add(category);
    }
    public static boolean addTransaction(Transaction transaction) {
        return transaction != null && getInstance().transactions.add(transaction);
    }



}
