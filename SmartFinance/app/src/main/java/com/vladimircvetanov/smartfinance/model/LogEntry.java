package com.vladimircvetanov.smartfinance.model;

import org.joda.time.LocalDate;

import java.util.Comparator;

/**
 * Created by simeon on 4/8/17.
 */

/**
 * Class representing a single entry in an account or an expense category.
 */
public class LogEntry {

    private LocalDate date;
    private double sum;
    private String note;

    private Manager.Type type;
    private Section section;

    public LogEntry(LocalDate date, double sum, String note, Manager.Type type, Section section) {
        if (date == null || note == null || type == null || section == null)
            throw new IllegalArgumentException("Arguments can not be null!");
        if (type.equals(Manager.Type.INCOMING) && sum < 0.0)
            throw new IllegalArgumentException("Negative values are not allowed for Income account entries!");

        this.date = date;
        this.sum = sum;
        this.note = note;
        this.type = type;
        this.section = section;
    }

    public LocalDate getDate() {return date;}
    public double getSum() {return sum;}
    public String getNote() {return note;}
    public Manager.Type getType() {return type;}
    public Section getSection() {return section;}

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
