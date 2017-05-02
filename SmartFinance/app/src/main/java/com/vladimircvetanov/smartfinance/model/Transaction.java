package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class representing a single entry in an account or an expense category.
 */
public class Transaction implements Serializable {

    private static ArrayList<TransactionComparator> sorters;

    private DateTime date;
    private double sum;
    private String note;
    private long id;
    private long userFk;

    private Account account;
    private Category category;

    public Transaction(DateTime date, double sum, String note, Account account, Category category) {

        this.date = date;
        this.sum = sum;
        this.note = note;

        this.account = account;
        this.category = category;

        account.addTransaction(this);
    }

    public DateTime getDate() {
        return date;
    }

    public double getSum() {
        return sum;
    }

    public String getNote() {
        return note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserFk() {
        return userFk;
    }

    public void setUserFk(long userFk) {
        this.userFk = userFk;
    }

    public Account getAccount() {
        return account;
    }

    public
    @Nullable
    Category getCategory() {
        return category;
    }

    /**
     * A chronological comparator for the {@link Transaction} data type.
     * It sorts by the LogEntry's date in ascending order
     * and returns -1 if the LocalDate property of the 2 dates is identical within a millisecond resolution.
     */
    public static class TransactionDateComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.date.equals(o2.date)) return ((Double)o2.sum).compareTo((Double)o1.sum);
            return o2.date.compareTo(o1.date);
        }

        @Override
        public String toString() {
            return "SORT BY DATE";
        }
    }

    public static class TransactionSumComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.sum == o2.sum) return o2.date.compareTo(o1.date);
            return ((Double)o2.sum).compareTo((Double)o1.sum);
        }

        @Override
        public String toString() {
            return "SORT BY SUM";
        }
    }

    public static class TransactionCategoryComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.category.equals(o2.category)){
                if (o1.date.equals(o2.date))
                    return ((Double)o2.sum).compareTo((Double)o1.sum);
                return o2.date.compareTo(o1.date);
            }

            return o2.category.getName().compareTo(o1.category.getName());
        }

        @Override
        public String toString() {
            return "SORT BY CATEGORY";
        }
    }

    public static ArrayList<TransactionComparator> getComparators(){
        if (sorters == null || sorters.isEmpty()) {
            sorters = new ArrayList<>();
            sorters.add(new TransactionSumComparator());
            sorters.add(new TransactionCategoryComparator());
            sorters.add(new TransactionDateComparator());
        }
        return sorters;
    }

    public interface TransactionComparator extends Comparator<Transaction>{}
}


