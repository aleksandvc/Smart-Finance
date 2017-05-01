package com.vladimircvetanov.smartfinance.model;

import com.vladimircvetanov.smartfinance.R;

import java.util.ArrayList;

public class Manager {

    /* Transaction types. */
    private static User loggedUser;
    private static Manager instance = null;

    /* A collection that maintains a list of all Sections (both Income and Expense) and distributes input accordingly. */
    private ArrayList<Integer> allExpenseIcons;
    private ArrayList<Integer> allAccountIcons;


    private Manager() {
        allAccountIcons = new ArrayList<>();
        allExpenseIcons = new ArrayList<>();
        addMoreExpenseIcons();
        addMoreAccountIcons();
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

    public ArrayList<Integer> getAllExpenseIcons() {
        return allExpenseIcons;
    }

    public ArrayList<Integer> getAllAccountIcons() {
        return allAccountIcons;
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

    public static void addExpenseIcon(int icon) {
        getInstance().allExpenseIcons.add(icon);
    }

    public static void removeExpenseIcon(int icon) {
        getInstance().allExpenseIcons.remove(icon);
    }

    private void addMoreExpenseIcons() {

        allExpenseIcons.add(R.mipmap.car);
        allExpenseIcons.add(R.mipmap.clothes);
        allExpenseIcons.add(R.mipmap.heart);
        allExpenseIcons.add(R.mipmap.plane);
        allExpenseIcons.add(R.mipmap.home);
        allExpenseIcons.add(R.mipmap.swimming);
        allExpenseIcons.add(R.mipmap.restaurant);
        allExpenseIcons.add(R.mipmap.train);
        allExpenseIcons.add(R.mipmap.cocktail);
        allExpenseIcons.add(R.mipmap.phone);
        allExpenseIcons.add(R.mipmap.books);
        allExpenseIcons.add(R.mipmap.cafe);
        allExpenseIcons.add(R.mipmap.cats);
        allExpenseIcons.add(R.mipmap.household);
        allExpenseIcons.add(R.mipmap.food_and_wine);
        allExpenseIcons.add(R.mipmap.wifi);
        allExpenseIcons.add(R.mipmap.flowers);
        allExpenseIcons.add(R.mipmap.gas);
        allExpenseIcons.add(R.mipmap.cleaning);
        allExpenseIcons.add(R.mipmap.gifts);
        allExpenseIcons.add(R.mipmap.kids);
        allExpenseIcons.add(R.mipmap.makeup);
        allExpenseIcons.add(R.mipmap.music);
        allExpenseIcons.add(R.mipmap.gamming);
        allExpenseIcons.add(R.mipmap.hair);
        allExpenseIcons.add(R.mipmap.car_service);
        allExpenseIcons.add(R.mipmap.doctor);
        allExpenseIcons.add(R.mipmap.art);
        allExpenseIcons.add(R.mipmap.beach);
        allExpenseIcons.add(R.mipmap.bicycle);
        allExpenseIcons.add(R.mipmap.bowling);
        allExpenseIcons.add(R.mipmap.football);
        allExpenseIcons.add(R.mipmap.bus);
        allExpenseIcons.add(R.mipmap.taxi);
        allExpenseIcons.add(R.mipmap.games);
        allExpenseIcons.add(R.mipmap.fitness);
        allExpenseIcons.add(R.mipmap.shoes);
        allExpenseIcons.add(R.mipmap.dancing);
        allExpenseIcons.add(R.mipmap.shopping_bag);
        allExpenseIcons.add(R.mipmap.shopping_cart);
        allExpenseIcons.add(R.mipmap.tennis);
        allExpenseIcons.add(R.mipmap.tent);
        allExpenseIcons.add(R.mipmap.hotel);
        allExpenseIcons.add(R.mipmap.ping_pong);
        allExpenseIcons.add(R.mipmap.rollerblade);
    }

    private void addMoreAccountIcons() {
        allAccountIcons.add(R.mipmap.money_box);
        allAccountIcons.add(R.mipmap.gift_card);
        allAccountIcons.add(R.drawable.accounts);
        allAccountIcons.add(R.mipmap.funding);
        allAccountIcons.add(R.mipmap.mattress);
        allAccountIcons.add(R.mipmap.paypal);
        allAccountIcons.add(R.drawable.calculator);
        allAccountIcons.add(R.mipmap.safe);
        allAccountIcons.add(R.mipmap.coins);
        allAccountIcons.add(R.drawable.income);
    }
}
