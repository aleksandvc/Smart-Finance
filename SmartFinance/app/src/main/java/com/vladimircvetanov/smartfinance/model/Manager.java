package com.vladimircvetanov.smartfinance.model;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class Manager {

    private static Manager instance = null;
    public interface IType {}

    public enum Category implements IType {VEHICLE, CLOTHES, HEALTH, TRAVEL, SPORT, FOOD, TRANSPORT, PHONE, HOUSE, ENTERTAINMENT}
    public enum Type {INCOMING, EXPENSE}
    private HashMap<Type, HashMap<IType, TreeMap<Date, Double>>> logs;

    private Manager() {
        logs = new HashMap<>();
        logs.put(Type.INCOMING, new HashMap<IType, TreeMap<Date, Double>>());
        logs.put(Type.EXPENSE, new HashMap<IType, TreeMap<Date, Double>>());
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }
}
