package com.vladimircvetanov.smartfinance.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.LoginActivity;
import com.vladimircvetanov.smartfinance.MainActivity;
import com.vladimircvetanov.smartfinance.RegisterActivity;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.User;

import java.util.HashMap;

/**
 * Created by vladimircvetanov on 04.04.17.
 */

public class DBAdapter {

    Context context;
    /**
     * Declaration of fields of the adapter class. A reference to innerclass will executes queries.
     */
    static DbHelper helper ;

    private static HashMap<String,User> registeredUsers;
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
            registeredUsers = new HashMap<>();
            loadUsers();

        }
        return instance;
    }

    /**
     * A method used to insert data in the database.
     * @param username
     * @param password
     * @return long id if the method was successful and -1 if it fails
     */
    public long insertData(final String username,final String password){

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

                    values.put(DbHelper.COLUMN_USERNAME, username);
                    values.put(DbHelper.COLUMN_PASSWORD, password);

                    /**
                     * The insert method with three parameters(String TableName,String NullColumnHack,ContentValues values)
                     * is called on the SQL object of the class.
                     * It returns the ID of the inserted row or -1 if the operation fails.
                     */
                    id[0] = db.insert(DbHelper.TABLE_NAME, null, values);



                }

                return null;
            }

        }.execute();
            return id[0];
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
        Cursor cursor = db.query(DbHelper.TABLE_NAME,columns,DbHelper.COLUMN_USERNAME + " = '" + username + "'",null,null,null,null);


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
                Cursor cursor = helper.getWritableDatabase().rawQuery("SELECT _id,username,password FROM SmartFinance;",null);
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
                db.update("SmartFinance", values, "username = ?", new String[]{oldEmail});

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(context, " user edited successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute(oldEmail);

    }

    public void  close(){
        helper.close();
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
         * Definition of the database's table name. Specify a String constant.
         */
        private static final String TABLE_NAME = "SmartFinance";

        /**
         * Constant String SQL statement for erasing old version of table.
         */
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        /**
         * Constant integer of database`s version.
         */
        private static final int DB_VERSION = 1;

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

        /**
         * Constant String SQL statement for creating new database table.
         */
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " VARCHAR(255), " +
                COLUMN_PASSWORD + " VARCHAR(255));";


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
                db.execSQL(CREATE_TABLE);

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
                db.execSQL(DROP_TABLE);

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
