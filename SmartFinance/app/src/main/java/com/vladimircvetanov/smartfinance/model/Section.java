package com.vladimircvetanov.smartfinance.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by simeon on 4/8/17.
 */

/**
 * Section class represents an Income Account or an Expense Category.
 * Sections should be unique by name + type.
 */
public class Section implements Serializable, Comparable<Section> {

    private String name;
    private int iconID;
    private boolean isFavourite;
    private Manager.Type type;

    private double sum;
    private ArrayList<LogEntry> log;

    /**
     * Section constructor.
     * @param name Section name : a non-null and not-empty String.
     * @param type INCOMING or EXPENSE
     * @param iconID Resource ID for icon image.
     * @param isFavourite <b>true</b> if Section is withing Favourites.
     */
    public Section(@NonNull String name, @NonNull Manager.Type type, int iconID, boolean isFavourite) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The Section name must be a non-null and not-empty string!");
        if (type == null)
            throw new IllegalArgumentException("The Section type must not be null!");

        if (iconID == -1)
            throw new IllegalArgumentException();

        this.sum = 0.0;
        this.name = name;
        this.type = type;
        this.iconID = iconID;
        this.isFavourite = isFavourite;

        log = new ArrayList<>();
    }

    /**
     * Allow for construction without specifying if is 'favourite', assuming it is not.
     * @param name - Section name : a non-null and not-empty String.
     * @param iconID - Resource ID for icon image.
     */
    public Section(String name, Manager.Type type, int iconID) {
        this(name, type, iconID, false);
    }


    public void setIsFavourite(boolean isFavourite){
        this.isFavourite = isFavourite;
    }


    @Override
    public int compareTo(@NonNull Section o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;

        Section section = (Section) o;

        return (type == section.type && name.equals(section.name));

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public Manager.Type getType() {
        return type;
    }

    public double getSum(){ return sum; }

    public List<LogEntry> getLog() {
        return Collections.unmodifiableList(log);
    }

    public boolean addLogEntry(LogEntry entry) {
        sum += entry.getSum();
        return this.log.add(entry);
    }
}
