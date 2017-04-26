package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private EditText confirmPass;
    private Button signUp;
    private Button cancel;
    private DBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        adapter = DBAdapter.getInstance(this);

        userEmail = (EditText)findViewById(R.id.register_email_insert);
        userPass = (EditText)findViewById(R.id.register_pass_insert);
        confirmPass = (EditText)findViewById(R.id.register_confirm_insert);

        signUp = (Button)findViewById(R.id.register_reg_button);
        cancel = (Button)findViewById(R.id.register_cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(signUp()){
                   startActivity(new Intent(RegisterActivity.this,MainActivity.class));
               }
            }
        });
    }

    private boolean signUp() {
        final String username = userEmail.getText().toString();
        final String pass = userPass.getText().toString();
        final String confirm = confirmPass.getText().toString();
        boolean flag;
        if (username.isEmpty()) {
            userEmail.setError("Empty email");
            userEmail.requestFocus();
            return false;
        }
        //if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()){
        if (!username.matches("^(.+)@(.+)$")) {
            userEmail.setError("enter a valid email address");
            userEmail.setText("");
            userEmail.requestFocus();
            return false;
        }
        if (pass.isEmpty()) {
            userPass.setError("Empty password");
            userPass.requestFocus();
            return false;

        }
        if (!pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,30}$")) {
            userPass.setError("Password should contain at least one digit," +
                    "one special symbol,one small letter,and should be between 8 and 30 symbols. ");
            return false;
        }
        if (confirm.isEmpty()) {
            confirmPass.setError("Empty confirmation");
            confirmPass.requestFocus();
            return false;

        }
        if (!pass.equals(confirm)) {

            confirmPass.setError("Different passwords");
            confirmPass.setText("");
            confirmPass.requestFocus();
            return false;

        }
        if(adapter.existsUser(username)){
            Message.message(this,"User already exists");
            return false;
        }

        flag = true;
        new AsyncTask<Boolean, Void, Boolean>() {


            @Override
            protected Boolean doInBackground(Boolean... params) {

                User u = new User(username,pass);
                long id = adapter.insertData(username, pass);
                Manager.setLoggedUser(u);
                Manager.getLoggedUser().setId(id);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                Message.message(RegisterActivity.this, "User registered!" + Manager.getLoggedUser().getId());
                addDefaultCategories();
               Message.message(RegisterActivity.this,adapter.existsFavCat("Food")+""); //NQMA TAKAVA KATEGORIQ VOBSHTE

            }
        }.execute();

        return flag;
    }
    private void addDefaultCategories() {
        adapter.addFavCategory(new CategoryExpense("Vehicle", true, R.mipmap.car),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Clothes", true, R.mipmap.clothes),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Health", true, R.mipmap.heart),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Travel", true, R.mipmap.plane),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("House", true, R.mipmap.home),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Sport", true, R.mipmap.swimming),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Food", true, R.mipmap.restaurant),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Transport", true, R.mipmap.train),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Entertainment", true, R.mipmap.cocktail),Manager.getLoggedUser().getId());
        adapter.addFavCategory(new CategoryExpense("Phone", true, R.mipmap.phone),Manager.getLoggedUser().getId());

    }
}
