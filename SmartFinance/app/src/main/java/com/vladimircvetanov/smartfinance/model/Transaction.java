package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class representing a single entry in an account or an expense category.
 */
public class Transaction implements Serializable {

    private DateTime date;
    private double sum;
    private String note;

    private Account account;
    private Category category;

    public Transaction(DateTime date, double sum, String note, Account account, Category category) {

        this.date = date;
        this.sum = sum;
        this.note = note;

        this.account = account;
        this.category = category;
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
    public static class logEntryDateComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o1.date.equals(o2.date) ? -1 : o1.date.compareTo(o2.date);
        }
    }

}
