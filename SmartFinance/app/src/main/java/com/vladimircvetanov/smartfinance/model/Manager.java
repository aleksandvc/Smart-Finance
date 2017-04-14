package com.vladimircvetanov.smartfinance.model;

import java.util.HashMap;
import java.util.HashSet;

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
        }
        return instance;
    }

    public static void setLoggedUser(User user){
        if(user !=null){
            loggedUser = user;
        }
    }
    /**
     * Adds an entry, corresponding to a User expense or income, into the financial {@link Manager#masterLog}.
     *
     * @param type    income or expense.
     * @param section appropriate ISection enum value.
     * @param entry   LogEntry which to insert.
     * @return <i>true</i> only if entry is successfully added.
     */
    public static boolean addLogEntry(Type type, Section section, LogEntry entry) {
        if (type == null || section == null || entry == null || !getInstance().masterLog.get(type).contains(section))
            return false;

        HashSet<Section> sections = getInstance().masterLog.get(type);
        for (Section s : sections)
            if (s.equals(section))
                return s.addLogEntry(entry);
        return false;
    }

    /**
     * Wraps {@link Manager#addLogEntry(com.vladimircvetanov.smartfinance.model.Manager.Type, com.vladimircvetanov.smartfinance.model.Section, com.vladimircvetanov.smartfinance.model.LogEntry)}
     * Takes necessary data from the entry itself and passes it to addLogEntry(Type type, Section section, LogEntry entry), simplifying method calls.
     * @param entry   LogEntry which to insert.
     * @return
     */
    public static boolean addLogEntry(LogEntry entry) {
        if (entry == null)
            return false;
        Type t = entry.getType();
        Section s = entry.getSection();

        return addLogEntry(t,s,entry);
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

    public static boolean addSection(Section section){
        if(section == null) return false;
        return getInstance().masterLog.get(section.getType()).add(section);
    }

    public static boolean containsSection(Section section){
       return getInstance().masterLog.get(section.getType()).contains(section);
    }

}
