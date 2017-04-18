package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class representing a single entry in an account or an expense category.
 */
public class LogEntry implements Serializable {

    private DateTime date;
    private double sum;
    private String note;

    private Manager.Type type;
    private Section account;
    private Section category;

    public LogEntry(DateTime date, double sum, String note, Manager.Type type, Section account, @Nullable Section category) {
        if (date == null || note == null || type == null || account == null)
            throw new IllegalArgumentException("Arguments can not be null!");
        if (type == Manager.Type.EXPENSE && category == null)
            throw new IllegalArgumentException("The category of an expense entry can not be null!");
        if (type.equals(Manager.Type.INCOMING) && sum < 0.0)
            throw new IllegalArgumentException("Negative values are not allowed for Income account entries!");

        this.date = date;
        this.sum = sum;
        this.note = note;
        this.type = type;
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

    public Manager.Type getType() {
        return type;
    }

    public Section getAccount() {
        return account;
    }

    public
    @Nullable
    Section getCategory() {
        return category;
    }

    /**
     * A chronological comparator for the {@link LogEntry} data type.
     * It sorts by the LogEntry's date in ascending order
     * and returns -1 if the LocalDate property of the 2 dates is identical within a millisecond resolution.
     */
    public static class logEntryDateComparator implements Comparator<LogEntry> {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            return o1.date.equals(o2.date) ? -1 : o1.date.compareTo(o2.date);
        }
    }

}
