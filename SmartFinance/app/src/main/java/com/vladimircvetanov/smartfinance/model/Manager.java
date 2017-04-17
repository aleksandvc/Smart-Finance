package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Manager {

    /**
     * Transaction types.
     */
    public enum Type {
        INCOMING, EXPENSE
    }

    private static User loggedUser;
    private static Manager instance = null;

    /**
     * A collection that maintains a list of all Sections (both Income and Expense) and distributes input accordingly.
     */
    private HashMap<Type, HashSet<Section>> masterLog;

    private Manager() {
        masterLog = new HashMap<>();
        masterLog.put(Type.EXPENSE, new HashSet<Section>());
        masterLog.put(Type.INCOMING, new HashSet<Section>());
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
            //TODO - temporary : for test purposes
            addSection(new Section("Cash",Type.INCOMING, R.mipmap.letter,false));
            addSection(new Section("Debit",Type.INCOMING, R.mipmap.lock,false));
            addSection(new Section("Credit",Type.INCOMING, R.mipmap.lockche,false));
        }
        return instance;
    }

    public static void setLoggedUser(User user){
        if(user !=null){
            loggedUser = user;
        }
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Adds a
     * @param entry
     * @return
     */
    public static boolean addLogEntry(LogEntry entry) {
        if (entry == null)
            return false;
        Type t = entry.getType();
        if (t == null) return false;
        if (!entry.getAccount().addLogEntry(entry)) return false;
        if (t == Type.EXPENSE && !entry.getCategory().addLogEntry(entry))
            return false;
        //TODO - if adding to ExpenseCategory fails -> remove from account! Or find a way to make it Atomic;
        return true;
    }


    /**
     * Get the balance of all active Accounts (INCOMING type Sections) in the {@link Manager#masterLog}
     * @return balance of all Accounts in masterLog
     */
    public static double getSum() {
        double sum = 0;
        for (Section s : getSections(Type.INCOMING))
            sum += s.getSum();
        return sum;
    }

    /**
     * Returns an array of all sections of a passed type in an array. Useful for ArrayAdapters, etc.
     * @param type INCOMING or EXPENSE
     * @return array of Sections.
     */
    public static Section[] getSections(Type type) {
        Manager m = getInstance();
        Section[] sections = new Section[m.masterLog.get(type).size()];
        return getInstance().masterLog.get(type).toArray(sections);
    }

    /**
     * Wraps HashSet.add(Object) for the {@link Manager#masterLog}
     * @param section
     * @return <code>true</code> if addition completed successfully
     */
    public static boolean addSection(Section section){
        if(section == null) return false;
        return getInstance().masterLog.get(section.getType()).add(section);
    }

    public static boolean containsSection(Section section){
        if (section.getType() == null) return false;
        return getInstance().masterLog.get(section.getType()).contains(section);
    }

}
