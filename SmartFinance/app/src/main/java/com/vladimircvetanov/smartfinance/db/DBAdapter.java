package com.vladimircvetanov.smartfinance.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.CategoryIncome;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.Transaction;
import com.vladimircvetanov.smartfinance.model.User;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vladimircvetanov on 04.04.17.
 */

public class DBAdapter {

    static Context context;
    /**
     * Declaration of fields of the adapter class. A reference to innerclass will executes queries.
     */
    static DbHelper helper ;


    private static ConcurrentHashMap<String, User> registeredUsers;
    private static ConcurrentHashMap<Long, Account> accounts;
    private static ConcurrentHashMap<Long, CategoryExpense> expenseCategories;
    private static ConcurrentHashMap<Long, CategoryIncome> incomeCategories;
    private static ConcurrentHashMap<Long, CategoryExpense> favouriteCategories;
    private static ConcurrentHashMap<Long, LinkedList<Transaction>> transactions;
    /**
     * Static reference to the instance of the adapter.Private static because helps to create only one instance of type DbAdapter.
     */
    private static DBAdapter instance = null;
    /**
     * Constructor of the Adapter class. An object with reference to the inner class is instanciated with a context parameter.
     * The adapter creates and performs database management tasks.
     *
     * @param context
     */
    private DBAdapter(Context context){

        helper = new DbHelper(context);
        this.context = context;


    }

    /**
     * Static method which returns single instance of the DbAdapter.
     * @return
     */
    public static DBAdapter getInstance(Context context){
        if(instance == null){
            instance = new DBAdapter(context);
            registeredUsers = new ConcurrentHashMap<>();
            accounts = new ConcurrentHashMap<>();
            expenseCategories = new ConcurrentHashMap<>();
            incomeCategories = new ConcurrentHashMap<>();
            favouriteCategories = new ConcurrentHashMap<>();
            transactions = new ConcurrentHashMap<>();
            loadUsers();
        }
        return instance;
    }

    public void clearCache(){
        accounts.clear();
        expenseCategories.clear();
        incomeCategories.clear();
        favouriteCategories.clear();
        transactions.clear();
    }
    public Map<String, User> getCachedUsers(){
        return Collections.unmodifiableMap(registeredUsers);
    }
    public Map<Long, Category> getCachedIncomeCategories(){
        HashMap<Long, Category> temp = new HashMap<>();
        temp.putAll(incomeCategories);
        return Collections.unmodifiableMap(temp);
    }
    public Map<Long, CategoryExpense> getCachedExpenseCategories(){
        return Collections.unmodifiableMap(expenseCategories);
    }
    public Map<Long, CategoryExpense> getCachedFavCategories(){
        return Collections.unmodifiableMap(favouriteCategories);
    }
    public Map<Long, Account> getCachedAccounts(){
        return Collections.unmodifiableMap(accounts);
    }
    public Map<Long, LinkedList<Transaction>> getCachedTransactions(){
        return Collections.unmodifiableMap(transactions);
    }
    public long getId(String email){
            return registeredUsers.get(email).getId();
    }
    /**
     * A method used to insert data in the database.
    // * @param username
    // * @param password
     * @return long id if the method was successful and -1 if it fails
     */
    public long insertData(final User u){

        final long[] id = new long[1];
        final boolean[] flag = new boolean[1];

        new AsyncTask<Long,Void,Void>() {

            @Override
            protected Void doInBackground(Long... params) {

                if (!flag[0]) {

                    /**
                     *  A reference from inner class is used to create a Database object.
                     */
                    SQLiteDatabase db = helper.getWritableDatabase();

                    /**
                     * An instance of ContentValues class is created. To insert data the reference takes a key and a value.
                     * We specify the key as the column name. The value is the data we want ot put inside.
                     */
                    ContentValues values = new ContentValues();

                    /**
                     * Three columns are inserted;
                     */

                    values.put(DbHelper.COLUMN_USERNAME, u.getEmail());
                    values.put(DbHelper.COLUMN_PASSWORD, u.getPassword());

                    /**
                     * The insert method with three parameters(String TableName,String NullColumnHack,ContentValues values)
                     * is called on the SQL object of the class.
                     * It returns the ID of the inserted row or -1 if the operation fails.
                     */
                    id[0] = db.insert(DbHelper.TABLE_NAME_USERS, null, values);
                    u.setId(id[0]);
                    registeredUsers.put(u.getEmail(),u);
                    Manager.setLoggedUser(u);


                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                addDefaultCategories(u);

            }
        }.execute();
        return id[0];
    }
    private void addDefaultCategories(User u) {
        long id =u.getId();
        addFavCategory(new CategoryExpense("Vehicle", true, R.mipmap.car), id);
        addFavCategory(new CategoryExpense("Clothes", true, R.mipmap.clothes), id);
        addFavCategory(new CategoryExpense("Health", true, R.mipmap.heart), id);
        addFavCategory(new CategoryExpense("Travel", true, R.mipmap.plane), id);
        addFavCategory(new CategoryExpense("House", true, R.mipmap.home), id);
        addFavCategory(new CategoryExpense("Sport", true, R.mipmap.swimming), id);
        addFavCategory(new CategoryExpense("Food", true, R.mipmap.restaurant), id);
        addFavCategory(new CategoryExpense("Transport", true, R.mipmap.train), id);
        addFavCategory(new CategoryExpense("Entertainment", true, R.mipmap.cocktail), id);
        addFavCategory(new CategoryExpense("Phone", true, R.mipmap.phone), id);

        addAccount(new Account("Cash", R.mipmap.cash), id);
        addAccount(new Account("Debit", R.mipmap.visa), id);
        addAccount(new Account("Credit", R.mipmap.mastercard), id);

        addIncomeCategory(new CategoryIncome("Salary", R.mipmap.coins), id);
        addIncomeCategory(new CategoryIncome("Savings", R.mipmap.money_box), id);
        addIncomeCategory(new CategoryIncome("Other", R.mipmap.money_bag), id);
    }
    private  String getData(final String username){

        /**
         *  A reference from inner class is used to create a Database object.
         */
        SQLiteDatabase db = helper.getWritableDatabase();

        /**
         * select username,pass from SmartFinance table.
         */
        String[] columns = {DbHelper.COLUMN_USERNAME,DbHelper.COLUMN_PASSWORD};

        /**
         * object which contains username and password of the user.
         */
        StringBuffer buffer = new StringBuffer();

        /**
         * A call to the query method. Cursor object returned by the query method.
         * The cursor object's reference is the control which let's us move from the top to the bottom
         * of the table's result sets.
         * The method query takes seven parameters:
         * String table, String[] columns (list of columns to process, null returns all);
         * extra conditions on the SQL statement to return rows satisfying certain criteria,
         * String selection, String [] selectionArgs, String groupBy, String having, String orderby
         */
        Cursor cursor = db.query(DbHelper.TABLE_NAME_USERS,columns,DbHelper.COLUMN_USERNAME + " = '" + username + "'",null,null,null,null);


        while(cursor.moveToNext()){
            int index1 = cursor.getColumnIndex(DbHelper.COLUMN_USERNAME);
            int index2 = cursor.getColumnIndex(DbHelper.COLUMN_PASSWORD);

            String personUsername = cursor.getString(index1);
            String personPass = cursor.getString(index2);

            buffer.append(personUsername + " " + personPass + "\n");
        }

        return buffer.toString();

    }


    /**
     * Method to verify user data if user is already in database.
     *
     * @param username
     * @param password
     * @return
     */
    public  boolean getUser(String username,String password){
        String details = username + " " + password + "\n";

        if(details.equals(getData(username))){
            return true;
        }
        return false;
    }

    private static void loadUsers(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,username,password FROM Users;",null);
                while(cursor.moveToNext()){
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String email = cursor.getString(cursor.getColumnIndex("username"));
                    String pass = cursor.getString(cursor.getColumnIndex("password"));
                    User u = new User(email,pass);
                    u.setId(id);
                    registeredUsers.put(email,u);
                }
                return null;
            }
        }.execute();


    }
    public boolean existsUser(String username){
        return registeredUsers.containsKey(username);
    }

    public void updateUser(String oldEmail, String oldPass, final String newEmail, final String newPass) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String oldEmail = strings[0];
                User u = registeredUsers.get(oldEmail);
                //update

                ContentValues values = new ContentValues();
                values.put("username", newEmail);
                values.put("password", newPass);

                u.setEmail(newEmail);
                u.setPassword(newPass);
                registeredUsers.remove(oldEmail);
                registeredUsers.put(newEmail, u);
                db.update("Users", values, "username = ?", new String[]{oldEmail});

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context, " user edited successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(oldEmail);
    }


    public void loadAccounts(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                String[] fk = {Manager.getLoggedUser().getId()+""};
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,account_name,account_icon,account_user FROM " + DbHelper.TABLE_NAME_ACCOUNTS + " WHERE " + DbHelper.ACCOUNTS_COLUMN_USERFK +" = ? ;",fk);

                while(cursor.moveToNext()){
                    long id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String accountName = cursor.getString(cursor.getColumnIndex(DbHelper.ACCOUNTS_COLUMN_ACCOUNTNAME));
                    int iconId = cursor.getInt(cursor.getColumnIndex(DbHelper.ACCOUNTS_COLUMN_ICON));
                    Account account = new Account(accountName,iconId);
                    account.setId(id);
                    accounts.put(account.getId(), account);
                }
                return null;
            }
        }.execute();
    }
    public boolean existsAccount(Account account){
        return accounts.containsKey(account.getName());
    }

    public long addAccount(final Account account,final long userId){
        final long[] id = new long[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(!accounts.containsKey(account.getName())) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues values = new ContentValues();

                    values.put(DbHelper.ACCOUNTS_COLUMN_ACCOUNTNAME,account.getName());
                    values.put(DbHelper.ACCOUNTS_COLUMN_ICON,account.getIconId());
                    values.put(DbHelper.ACCOUNTS_COLUMN_USERFK,userId);

                    id[0] = db.insert(DbHelper.TABLE_NAME_ACCOUNTS,null,values);
                    account.setId(id[0]);
                    account.setUserFk(userId);
                    accounts.put(account.getId(),account);
                }
                return null;
            }
        }.execute();

        return id[0];
    }

    public int deleteAccount(final RowDisplayable account){

        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                for (Transaction t: ((Account)account).getTransactions()){
                    deleteTransaction(t);
                }

                count[0] = db.delete(DbHelper.TABLE_NAME_ACCOUNTS,DbHelper.ACCOUNTS_COLUMN_ACCOUNTNAME + " = ? AND " + DbHelper.ACCOUNTS_COLUMN_USERFK + " = ?",new String[]{account.getName(), Manager.getLoggedUser().getId()+""});
                accounts.remove(account.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
                Message.message(context,"Account deleted!");
            }
        }.execute();
        return count[0];
    }
    public void editAccount(String oldName,int oldIconId , final String newName, final int newIconId) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String oldName = strings[0];

                Account a = accounts.get(oldName);

                ContentValues values = new ContentValues();
                values.put("name", newName);
                values.put("iconId", newIconId);

                a.setName(newName);
                a.setIconId(newIconId);
                accounts.remove(oldName);
                accounts.put(a.getId(), a);
                db.update("Accounts", values, "account_name = ?", new String[]{oldName});

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context, " account edited successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(oldName);

    }
    public void loadExpenseCategories(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                String[] fk = {String.valueOf(Manager.getLoggedUser().getId())};
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,expense_category_name,expense_category_icon,expense_category_user FROM " + DbHelper.TABLE_NAME_EXPENSE_CATEGORIES + " WHERE " + DbHelper.EXPENSE_CATEGORIES_COLUMN_USERFK +" = ? ;",fk);

                while(cursor.moveToNext()){
                    long id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String categoryName = cursor.getString(cursor.getColumnIndex(DbHelper.EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME));
                    int iconId = cursor.getInt(cursor.getColumnIndex(DbHelper.EXPENSE_CATEGORIES_COLUMN_ICON));
                    CategoryExpense category = new CategoryExpense(categoryName, false, iconId);
                    category.setId(id);
                    expenseCategories.put(category.getId(), category);
                }
                return null;
            }
        }.execute();


    }
    public boolean existsExpenseCat(CategoryExpense category){
        return expenseCategories.containsKey(category.getId());
    }


    public long addExpenseCategory(final CategoryExpense category, final long userId){
        final long[] id = new long[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(!expenseCategories.containsKey(category.getId())) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues values = new ContentValues();

                    values.put(DbHelper.EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME,category.getName());
                    values.put(DbHelper.EXPENSE_CATEGORIES_COLUMN_ICON,category.getIconId());
                    values.put(DbHelper.EXPENSE_CATEGORIES_COLUMN_USERFK,userId);

                    id[0] = db.insert(DbHelper.TABLE_NAME_EXPENSE_CATEGORIES,null,values);
                    category.setUserFk(userId);
                    category.setId(id[0]);
                    expenseCategories.put(category.getId(),category);
                }
                return null;
            }
        }.execute();

        return id[0];
    }
    public int deleteExpenseCategory(final RowDisplayable category){
        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                count[0] = db.delete(DbHelper.TABLE_NAME_EXPENSE_CATEGORIES,DbHelper.EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME + " = ? AND " + DbHelper.EXPENSE_CATEGORIES_COLUMN_USERFK + " = ?",new String[]{category.getName(), Manager.getLoggedUser().getId()+""});
                expenseCategories.remove(category.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
                loadExpenseCategories();
                if(transactions.containsKey(category.getId())) {
                    for (Transaction t : transactions.get(category.getId())) {
                        deleteTransaction(t);
                    }
                }


            }
        }.execute();
        return count[0];
    }
    //TODO - ADAPT TO NEW COLLECTION STRUCTURE
    public void editExpenseCategory(String oldName,int oldIconId , final String newName, final int newIconId) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String oldName = strings[0];

                CategoryExpense c = expenseCategories.get(oldName);

                ContentValues values = new ContentValues();
                values.put("name", newName);
                values.put("iconId", newIconId);

                c.setName(newName);
                c.setIconId(newIconId);
                expenseCategories.remove(c.getId());
                expenseCategories.put(c.getId(), c);
                db.update("Expense_Categories", values, "expense_category_name = ?", new String[]{oldName});

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context, " Category edited successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(oldName);

    }
    public void loadIncomeCategories(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                String[] fk = {Manager.getLoggedUser().getId()+""};
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,income_category_name,income_category_icon,income_category_user FROM " + DbHelper.TABLE_NAME_INCOME_CATEGORIES + " WHERE " + DbHelper.INCOME_CATEGORIES_COLUMN_USERFK +" = ? ;",fk);
                while(cursor.moveToNext()){
                    long id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String categoryName = cursor.getString(cursor.getColumnIndex(DbHelper.INCOME_CATEGORIES_COLUMN_CATEGORYNAME));
                    int iconId = cursor.getInt(cursor.getColumnIndex(DbHelper.INCOME_CATEGORIES_COLUMN_ICON));
                    CategoryIncome category = new CategoryIncome(categoryName,iconId);
                    category.setId(id);
                    incomeCategories.put(category.getId(),category);
                }
                return null;
            }
        }.execute();


    }
    public boolean existsIncomeCat(CategoryIncome category){
        return incomeCategories.containsKey(category.getId());
    }

    public long addIncomeCategory(final CategoryIncome category,final long userId){
        final long[] id = new long[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(!incomeCategories.containsKey(category.getId())) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues values = new ContentValues();

                    values.put(DbHelper.INCOME_CATEGORIES_COLUMN_CATEGORYNAME,category.getName());
                    values.put(DbHelper.INCOME_CATEGORIES_COLUMN_ICON,category.getIconId());
                    values.put(DbHelper.INCOME_CATEGORIES_COLUMN_USERFK,userId);

                    id[0] = db.insert(DbHelper.TABLE_NAME_INCOME_CATEGORIES,null,values);
                    category.setUserFk(userId);
                    category.setId(id[0]);
                    incomeCategories.put(category.getId(),category);
                }
                return null;
            }
        }.execute();

        return id[0];
    }
    public int deleteIncomeCategory(final CategoryIncome category){
        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                count[0] = db.delete(DbHelper.TABLE_NAME_INCOME_CATEGORIES,DbHelper.INCOME_CATEGORIES_COLUMN_CATEGORYNAME + " = ? " ,new String[]{category.getName()});
                incomeCategories.remove(category.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
                Message.message(context,"Category deleted!");
            }
        }.execute();
        return count[0];
    }

    //TODO -
    public void editIncomeCategory(String oldName,int oldIconId , final String newName, final int newIconId) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String oldName = strings[0];

                CategoryIncome c = incomeCategories.get(oldName);

                ContentValues values = new ContentValues();
                values.put("name", newName);
                values.put("iconId", newIconId);

                c.setName(newName);
                c.setIconId(newIconId);
                incomeCategories.remove(c.getId());
                incomeCategories.put(c.getId(), c);
                db.update("Income_Categories", values, "income_category_name = ?", new String[]{oldName});

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context, " Category edited successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(oldName);

    }
    public void loadFavouriteCategories(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                String[] fk = {Manager.getLoggedUser().getId()+""};
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,fav_category_name,fav_category_icon,fav_category_user FROM " + DbHelper.TABLE_NAME_FAVCATEGORIES + " WHERE " + DbHelper.FAVCATEGORIES_COLUMN_USERFK +" = ? ;",fk);

                if(favouriteCategories.isEmpty()) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex("_id"));
                        String categoryName = cursor.getString(cursor.getColumnIndex(DbHelper.FAVCATEGORIES_COLUMN_CATEGORYNAME));
                        int iconId = cursor.getInt(cursor.getColumnIndex(DbHelper.FAVCATEGORIES_COLUMN_ICON));
                        CategoryExpense category = new CategoryExpense(categoryName, true, iconId);
                        category.setId(id);
                        favouriteCategories.put(category.getId(), category);
                    }
                }

                return null;
            }

        }.execute();
    }

    public boolean existsFavCat(String categoryName){
        return favouriteCategories.containsKey(categoryName);
    }


    public int moveToFav(final CategoryExpense category){
        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                count[0] = db.delete(DbHelper.TABLE_NAME_EXPENSE_CATEGORIES,DbHelper.EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME + " = ? AND " + DbHelper.EXPENSE_CATEGORIES_COLUMN_USERFK + " = ?",new String[]{category.getName(), Manager.getLoggedUser().getId()+""});
                expenseCategories.remove(category.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
                addFavCategory(category,Manager.getLoggedUser().getId());

            }
        }.execute();
        return count[0];
    }
    public  long addFavCategory(final CategoryExpense category, final long userId){
        final long[] id = new long[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                if(!favouriteCategories.containsKey(category.getId())) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    ContentValues values = new ContentValues();

                    values.put(DbHelper.FAVCATEGORIES_COLUMN_CATEGORYNAME,category.getName());
                    values.put(DbHelper.FAVCATEGORIES_COLUMN_ICON,category.getIconId());
                    values.put(DbHelper.FAVCATEGORIES_COLUMN_USERFK,userId);

                    id[0] = db.insert(DbHelper.TABLE_NAME_FAVCATEGORIES,null,values);
                    category.setUserFk(userId);
                    category.setId(id[0]);
                    favouriteCategories.put(category.getId(),category);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();

        return id[0];
    }
    public int deleteFavCategory(final RowDisplayable category){
        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                count[0] = db.delete(DbHelper.TABLE_NAME_FAVCATEGORIES,DbHelper.FAVCATEGORIES_COLUMN_CATEGORYNAME + " = ? AND " + DbHelper.FAVCATEGORIES_COLUMN_USERFK+ " = ?",new String[]{category.getName(), Manager.getLoggedUser().getId()+""});
                favouriteCategories.remove(category.getId());
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
               if(transactions.containsKey(category.getId())) {
                   for (Transaction t : transactions.get(category.getId())) {
                       deleteTransaction(t);
                   }
               }

                Message.message(context,"Category deleted!");

            }
        }.execute();
        return count[0];
    }

    public long addTransaction(final Transaction transaction, final long userId) {
        final long[] id = new long[1];

        long catId = transaction.getCategory().getId();

        if (!transactions.containsKey(catId)){
            transactions.put(catId, new LinkedList<Transaction>());
        }
        transactions.get(catId).add(transaction);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(DbHelper.TRANSACTIONS_COLUMN_DATE,transaction.getDate().getMillis());
                values.put(DbHelper.TRANSACTIONS_COLUMN_SUM,transaction.getSum());
                values.put(DbHelper.TRANSACTIONS_COLUMN_NOTE,transaction.getNote());
                values.put(DbHelper.TRANSACTIONS_COLUMN_ACCOUNTFK,transaction.getAccount().getId());
                values.put(DbHelper.TRANSACTIONS_COLUMN_CATEGORYFK,transaction.getCategory().getId());
                values.put(DbHelper.TRANSACTIONS_COLUMN_USERFK, userId);

                id[0] = db.insert(DbHelper.TABLE_NAME_TRANSACTIONS,null,values);
                transaction.setId(id[0]);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Message.message(context,"Transaction added successfully.");
                Log.wtf("LOAD TRANSACTIONS:", " LOADED ");
            }
        }.execute();

        return id[0];
    }

    public void loadTransactions(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Cursor cursor = helper.getWritableDatabase().rawQuery(
                                "SELECT " +  helper.COLUMN_ID +
                                " , " + helper.TRANSACTIONS_COLUMN_NOTE +
                                " , " + helper.TRANSACTIONS_COLUMN_DATE +
                                " , " + helper.TRANSACTIONS_COLUMN_SUM +
                                " , " + helper.TRANSACTIONS_COLUMN_ACCOUNTFK +
                                " , " + helper.TRANSACTIONS_COLUMN_CATEGORYFK +
                                " , " + helper.TRANSACTIONS_COLUMN_USERFK +
                                " FROM " + DbHelper.TABLE_NAME_TRANSACTIONS +
                                " WHERE " + DbHelper.TRANSACTIONS_COLUMN_USERFK +" = " + Manager.getLoggedUser().getId(), null);

                if(transactions.isEmpty()) {

                    int noteIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_NOTE);
                    int dateIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_DATE);
                    int sumIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_SUM);
                    int accountIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_ACCOUNTFK);
                    int categoryIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_CATEGORYFK);
                    int userIndex = cursor.getColumnIndex(helper.TRANSACTIONS_COLUMN_USERFK);
                    int idIndex = cursor.getColumnIndex(helper.COLUMN_ID);


                    while (cursor.moveToNext()) {

                        long id = cursor.getLong(idIndex);
                        String note = cursor.getString(noteIndex);
                        long timestamp = cursor.getLong(dateIndex);
                        DateTime date = new DateTime(timestamp, DateTimeZone.UTC);
                        double sum = cursor.getDouble(sumIndex);
                        long accFk = cursor.getLong(accountIndex);
                        long catFk = cursor.getLong(categoryIndex);
                        long userFk = cursor.getLong(userIndex);

                        if (! (expenseCategories.containsKey(catFk) || favouriteCategories.containsKey(catFk) || incomeCategories.containsKey(catFk))){
                            Log.wtf("LOAD TRANSACTIONS:", " NO CATEGORY FOR THIS TRANSACTION!");
                            continue;
                        }
                        if (!accounts.containsKey(accFk)){
                            Log.wtf("LOAD TRANSACTIONS:", " NO ACCOUNT FOR THIS TRANSACTION!");
                            continue;
                        }

                        Account acc = accounts.get(accFk);
                        Category cat = incomeCategories.get(catFk);
                        if (cat == null) cat = expenseCategories.get(catFk);
                        if (cat == null) cat = favouriteCategories.get(catFk);

                        Transaction t = new Transaction(date, sum, note, acc, cat);
                        t.setId(id);
                        t.setUserFk(userFk);

                        if (!transactions.containsKey(catFk)){
                            transactions.put(catFk, new LinkedList<Transaction>());
                        }

                        transactions.get(catFk).add(t);
                        Log.wtf("LOAD TRANSACTIONS:", " LOADED " + cat.getName() + "   :   " + transactions.get(catFk).size());
                    }
                }
                else{

                }
                return null;
            }


        }.execute();
    }
    public int deleteTransaction(final Transaction transaction){

        final int[] count = new int[1];

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = helper.getWritableDatabase();

                count[0] = db.delete(DbHelper.TABLE_NAME_TRANSACTIONS,DbHelper.COLUMN_ID + " = ? AND " + DbHelper.TRANSACTIONS_COLUMN_USERFK + " = ?",new String[]{transaction.getId()+"", Manager.getLoggedUser().getId()+""});
                if (count[0] >= 1){
                    transactions.get(transaction.getCategory().getId()).remove(transaction);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void integer) {
                Message.message(context,"Transaction deleted!");
            }
        }.execute();
        return count[0];
    }

    /**
     * Inner static class which is responsible for the creation of  database.
     * A custom class implementation of SQLiteOpenHelper is created. Database's schema is defined programatically.
     * This class takes care of opening the database if it exists,
     * creating it if it does not exist and upgrading it if necessary.
     */
    static class  DbHelper extends SQLiteOpenHelper {

        /**
         * Definition of unique for the application database name. Specify a String constant.
         */
        private static final String DB_NAME = "smartfinance.db";

        /**
         * Definition of the database's tables name`s. Specify a String constant.
         */
        private static final String TABLE_NAME_USERS = "Users" ;

        private static final String TABLE_NAME_ACCOUNTS = "Accounts";

        private static final String TABLE_NAME_EXPENSE_CATEGORIES = "Expense_Categories";

        private static final String TABLE_NAME_INCOME_CATEGORIES = "Income_Categories";

        private static final String TABLE_NAME_FAVCATEGORIES = "Fav_Categories";

        private static final String TABLE_NAME_TRANSACTIONS = "Transactions";
        /**
         * Constant String SQL statement for erasing old version of table.
         */
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

        /**
         * Constant integer of database`s version.
         */
        private static final int DB_VERSION = 4;

        /**
         * Constant String of the table`s column for id;
         */
        private static final String COLUMN_ID = "_id";

        /**
         * Constant String of the table`s column for usernames;
         */
        private static final String COLUMN_USERNAME = "username";

        /**
         * Constant String of the table`s column for passwords;
         */
        private static final String COLUMN_PASSWORD = "password";

        private static final String ACCOUNTS_COLUMN_ACCOUNTNAME = "account_name";

        private static final String ACCOUNTS_COLUMN_ICON = "account_icon";

        private static final String ACCOUNTS_COLUMN_USERFK = "account_user";

        private static final String EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME = "expense_category_name";

        private static final String EXPENSE_CATEGORIES_COLUMN_USERFK = "expense_category_user";

        private static final String EXPENSE_CATEGORIES_COLUMN_ICON = "expense_category_icon";

        private static final String INCOME_CATEGORIES_COLUMN_CATEGORYNAME = "income_category_name";

        private static final String INCOME_CATEGORIES_COLUMN_USERFK = "income_category_user";

        private static final String INCOME_CATEGORIES_COLUMN_ICON = "income_category_icon";

        private static final String FAVCATEGORIES_COLUMN_CATEGORYNAME = "fav_category_name";

        private static final String FAVCATEGORIES_COLUMN_USERFK = "fav_category_user";

        private static final String FAVCATEGORIES_COLUMN_ICON = "fav_category_icon";

        private static final String TRANSACTIONS_COLUMN_NOTE = "transaction_note";

        private static final String TRANSACTIONS_COLUMN_SUM = "transaction_sum";

        private static final String TRANSACTIONS_COLUMN_DATE = "transaction_date";

        private static final String TRANSACTIONS_COLUMN_USERFK = "transaction_user_fk";

        private static final String TRANSACTIONS_COLUMN_CATEGORYFK = "transaction_category_fk";

        private static final String TRANSACTIONS_COLUMN_ACCOUNTFK = "transaction_account_fk";

        /**
         * Constant String SQL statement for creating new database table.
         */
        public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_NAME_USERS + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " VARCHAR(255), " +
                COLUMN_PASSWORD + " VARCHAR(255));";

        public static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE " + TABLE_NAME_ACCOUNTS + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCOUNTS_COLUMN_ACCOUNTNAME + " VARCHAR(255), " +
                ACCOUNTS_COLUMN_ICON + " INTEGER, " + ACCOUNTS_COLUMN_USERFK + " INTEGER);";

        public static final String CREATE_TABLE_EXPENSE_CATEGORIES = "CREATE TABLE " + TABLE_NAME_EXPENSE_CATEGORIES + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + EXPENSE_CATEGORIES_COLUMN_CATEGORYNAME + " VARCHAR(255), " +
                EXPENSE_CATEGORIES_COLUMN_ICON + " INTEGER, " + EXPENSE_CATEGORIES_COLUMN_USERFK + " INTEGER);";

        public static final String CREATE_TABLE_INCOME_CATEGORIES = "CREATE TABLE " + TABLE_NAME_INCOME_CATEGORIES + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + INCOME_CATEGORIES_COLUMN_CATEGORYNAME + " VARCHAR(255), " +
                INCOME_CATEGORIES_COLUMN_ICON + " INTEGER, " + INCOME_CATEGORIES_COLUMN_USERFK + " INTEGER);";


        public static final String CREATE_TABLE_FAVCATEGORIES = "CREATE TABLE " + TABLE_NAME_FAVCATEGORIES + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + FAVCATEGORIES_COLUMN_CATEGORYNAME + " VARCHAR(255), " +
                FAVCATEGORIES_COLUMN_ICON + " INTEGER, " + FAVCATEGORIES_COLUMN_USERFK + " INTEGER);";

        public static final String CREATE_TABLE_TRANASCTIONS = "CREATE TABLE " + TABLE_NAME_TRANSACTIONS + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + TRANSACTIONS_COLUMN_NOTE + " VARCHAR(255), " +
                TRANSACTIONS_COLUMN_SUM + " REAL, " + TRANSACTIONS_COLUMN_DATE + " INTEGER, " + TRANSACTIONS_COLUMN_CATEGORYFK + " INTEGER," +
                TRANSACTIONS_COLUMN_USERFK + " INTEGER, " + TRANSACTIONS_COLUMN_ACCOUNTFK + " INTEGER);";


        /**
         * Definition of context;
         */
        private static Context context;





        /**
         * Declaration of constructor the supertype class.
         * Object is responsible for the creation of  single instance of database and
         * editing of database. Constructor takes context as parameter.
         * Super constructor takes four parameters(context, database name, custom cursor object and version of database.
         * Since we do not create custom cursor factory, we pass null.
         *
         * @param context
         */
        private DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }




        /**
         * {@inheritDoc}
         * The method is called when the database is first created. Creation of tables and initial data inside tables is put here.
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                /**
                 * Executes a single SQL statement that is NOT a SELECT and does not return data.
                 */
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_ACCOUNTS);
                db.execSQL(CREATE_TABLE_EXPENSE_CATEGORIES);
                db.execSQL(CREATE_TABLE_INCOME_CATEGORIES);
                db.execSQL(CREATE_TABLE_FAVCATEGORIES);
                db.execSQL(CREATE_TABLE_TRANASCTIONS);

            }
            /**
             * If the SQL statement is invalid it throws an exception.
             */
            catch (SQLException e) {
                Message.message(context, "" + e);
            }
        }

        /**
         * {@inheritDoc}
         * Method is called when database needs to be upgraded. It is triggered when updates are made.
         * This method is used to drop tables, add tables, do anything that needs to upgrade to new version of schema.
         * It deletes the old table and creates the new one with new parameters with query.
         *
         * @param db         - the database.
         * @param oldVersion - go from old version
         * @param newVersion - to new version.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                /**
                 * When there are edits, the old table is deleted.
                 */
                db.execSQL(DROP_TABLE + TABLE_NAME_USERS);
                db.execSQL(DROP_TABLE + TABLE_NAME_ACCOUNTS);
                db.execSQL(DROP_TABLE + TABLE_NAME_TRANSACTIONS);
                db.execSQL(DROP_TABLE + TABLE_NAME_EXPENSE_CATEGORIES);
                db.execSQL(DROP_TABLE + TABLE_NAME_INCOME_CATEGORIES);
                db.execSQL(DROP_TABLE + TABLE_NAME_FAVCATEGORIES);

                /**
                 * Once the table is deleted the new database is created once again with new statements.
                 */
                onCreate(db);

            }
            /**
             * If the SQL statement is invalid it throws an exception.
             */ catch (SQLException e) {
                Message.message(context, "" + e);

            }
        }
    }
}
