package com.vladimircvetanov.smartfinance;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Manager;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private Manager.Type direction;
    private Manager.IType expenseCategory;
    private Manager.IType incomeAccount;

    private TextView dateDisplay;
    private LocalDate date;

    //Selection between income and expense;
    private RadioGroup directionRadio;

    private TextView numDisplay;
    private ImageButton backspace;
    private Spinner categorySpinner;

    private View numDisplayBase;

    private Button[] numButtons;

    private Button equals;
    private Button divide;
    private Button multiply;
    private Button plus;
    private Button minus;
    private Button decimal;

    private Button submitButton;

    //Counts how far past the decimal point the input number is. Used to block input past 2 decimal positions
    private int decimalPosition = BEFORE_DECIMAL;
    private static final int BEFORE_DECIMAL = -1;
    private static final int MAX_DECIMAL_DEPTH = 2;

    //Tracks what arithmetic operation is currently selected.
    private char currentOperation = OPERATION_NONE;

    private static final char OPERATION_NONE = '=';
    private static final char OPERATION_PLUS = '+';
    private static final char OPERATION_MINUS = '-';
    private static final char OPERATION_MULTIPLY = 'x';
    private static final char OPERATION_DIVIDE = '÷';


    /**
     * Flag tracking if there is an operation waiting for execution. Basically: true if there has been numeric input after setting an operation.
     * If <i>true</i>, pressing an operation button will execute standing arithmetic operation and reset to false.
     * If <i>false</i>, standing operation can be changed at will until a number is pressed.
     */
    private boolean operationPrimed = false;

    //Previous input. Stored for executing currentOperation.
    private Double storedNumber = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        //Set Toolbar, because our overlords at Google are taking <b>forever</b> to compat-ize the Appbar properly...
        Toolbar tb = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(tb);

        //------Initializations------------------//
        numDisplayBase = findViewById(R.id.transaction_number_display);

        submitButton = (Button) findViewById(R.id.transaction_submit_btn);

        directionRadio = (RadioGroup) findViewById(R.id.transaction_radio);
        directionRadio.check(R.id.transaction_radio_expense);

        dateDisplay = (TextView) findViewById(R.id.transaction_date_display);
        numDisplay = (TextView) findViewById(R.id.transaction_number_display_text);
        backspace = (ImageButton) findViewById(R.id.transaction_number_display_backspace);
        categorySpinner = (Spinner) findViewById(R.id.transaction_category_spinner);

        //Seeing as they act almost identically, put all numeric buttons in an array for easier manipulation.
        numButtons = new Button[10];
        for (int i = 0; i <= 9; i++)
            numButtons[i] = (Button) findViewById(getResources().getIdentifier("transaction_numpad_" + i, "id", getPackageName()));

        equals = (Button) findViewById(R.id.transaction_numpad_equals);
        divide = (Button) findViewById(R.id.transaction_numpad_divide);
        multiply = (Button) findViewById(R.id.transaction_numpad_multiply);
        plus = (Button) findViewById(R.id.transaction_numpad_plus);
        minus = (Button) findViewById(R.id.transaction_numpad_minus);
        decimal = (Button) findViewById(R.id.transaction_numpad_decimal);
        //---------------------------------------//


        //Made separate adapters for Expense and Income items, as it should be cheaper switching adapters, rather than switching the data each time if the user changes mode often.
        final ArrayAdapter<Manager.IType> categoryAdapter = new ArrayAdapter<Manager.IType>(this,R.layout.spinner_transaction_category,Manager.Category.values());
        final ArrayAdapter<Manager.IType> accountAdapter = new ArrayAdapter<Manager.IType>(this,R.layout.spinner_transaction_category,Account.Type.values());
        categorySpinner.setAdapter(categoryAdapter);

        direction = Manager.Type.EXPENSE;
        expenseCategory = (Manager.Category) categorySpinner.getSelectedItem();

        //Show the current date in a "d MMMM, YYYY" format.
        date = LocalDate.now();
        final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        dateDisplay.setText(date.toString(dateFormat));


        /**
         * On arithmetic button pressed -> execute stored {@link TransactionActivity#currentOperation}
         *                              -> save new currentOperation
         */
        Button.OnClickListener arithmeticListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operationPrimed && currentOperation != OPERATION_NONE) calculate();
                currentOperation = ((Button) v).getText().charAt(0);
                if (currentOperation != OPERATION_NONE)operationPrimed = false;
            }
        };
        equals.setOnClickListener(arithmeticListener);
        divide.setOnClickListener(arithmeticListener);
        multiply.setOnClickListener(arithmeticListener);
        plus.setOnClickListener(arithmeticListener);
        minus.setOnClickListener(arithmeticListener);

        /**
         * Handles input from the numeric buttons and the decimal-point button.
         */
        Button.OnClickListener numberListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Block input if maximal number of digits after decimal point has been reached.
                if (operationPrimed && decimalPosition >= MAX_DECIMAL_DEPTH) return;

                if(!operationPrimed) {
                    operationPrimed = true;
                    storedNumber = Double.valueOf(numDisplay.getText().toString());
                    numDisplay.setText("0");
                    decimalPosition = BEFORE_DECIMAL;
                }

                if (v.getId() == R.id.transaction_numpad_decimal && decimalPosition == BEFORE_DECIMAL) {
                    numDisplay.append(".");
                    decimalPosition++;
                    return;
                }

                if (numDisplay.getText().toString().equals("0")) numDisplay.setText("");

                numDisplay.append(((Button) v).getText().toString());
                if (decimalPosition != BEFORE_DECIMAL) decimalPosition++;
            }
        };
        for (int i = 0; i < numButtons.length; i++)
            numButtons[i].setOnClickListener(numberListener);
        decimal.setOnClickListener(numberListener);

        /**
         * Deletes a single digit (or the dec. point)
         */
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayText = numDisplay.getText().toString();
                int length = displayText.length();

                if (decimalPosition != BEFORE_DECIMAL) decimalPosition--;

                //if the 'display' is a single digit long, replaces it with a 0.
                //else removes last number
                numDisplay.setText(length <= 1 ? "0" : displayText.substring(0, --length));
            }
        });



        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();

                Bundle args = new Bundle();
                args.putSerializable("date",date);
                datePicker.setArguments(args);

                datePicker.show(getSupportFragmentManager(),"testTag");
            }
        });
        directionRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                //TODO !IMPORTANT! - this block is brimming with possible exceptions. Might need TOTAL re-vamping.
                int pos;
                switch (checkedId){
                    case R.id.transaction_radio_expense:
                        numDisplay.setBackgroundResource(R.color.colorOrange);
                        backspace.setBackgroundResource(R.color.colorOrange);
                        numDisplayBase.setBackgroundResource(R.color.colorOrange);

                        direction = Manager.Type.EXPENSE;
                        categorySpinner.setAdapter(categoryAdapter);
                        pos = categoryAdapter.getPosition(expenseCategory);
                        categorySpinner.setSelection(pos);
                        break;
                    case R.id.transaction_radio_income:
                        numDisplay.setBackgroundResource(R.color.colorGreen);
                        backspace.setBackgroundResource(R.color.colorGreen);
                        numDisplayBase.setBackgroundResource(R.color.colorGreen);

                        direction = Manager.Type.INCOMING;
                        categorySpinner.setAdapter(accountAdapter);
                        pos = accountAdapter.getPosition(incomeAccount);
                        categorySpinner.setSelection(pos);
                        break;
                }
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Manager.IType categoryOrAccount = (Manager.IType) categorySpinner.getItemAtPosition(position);
                switch (direction){
                    case EXPENSE:
                        expenseCategory = categoryOrAccount;
                        break;
                    case INCOMING:
                        incomeAccount = categoryOrAccount;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {Log.wtf("onNothingSelected","This is a VERY temporary log. Seriously, if you are reading this, I probably forgot to remove it. Woops. Sorry :) ~Simo");}
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - validation
                //TODO - user-friendly feedback.
                Manager.IType cat = direction.equals(Manager.Type.EXPENSE) ? expenseCategory : incomeAccount;
                if(Manager.addLogEntry(direction,cat,date,Double.valueOf(numDisplay.getText().toString()))){
                    Message.message(TransactionActivity.this,getString(R.string.transaction_success));
                    startActivity(new Intent(TransactionActivity.this,MainActivity.class));
                } else {
                    Message.message(TransactionActivity.this,getString(R.string.transaction_failure));
                }
            }
        });
    }

    /**
     * Executes stored arithmetic operation.
     */
    private void calculate() {
        double currNumber = Double.valueOf(numDisplay.getText().toString());

        switch (currentOperation){
            case OPERATION_PLUS:
                storedNumber += currNumber;
                break;
            case OPERATION_MINUS:
                storedNumber -= currNumber;
                break;
            case OPERATION_MULTIPLY:
                storedNumber *= currNumber;
                break;
            case OPERATION_DIVIDE:
                storedNumber /= currNumber;
                break;
        }

        //Round result to 2 decimal places
        storedNumber = Math.round(storedNumber * 100.0)/100.0;

        String numText = storedNumber.toString();
        numText = numText.replaceAll("[\\.](00|0$)","");
        numDisplay.setText(numText);

        decimalPosition = numText.contains(".") ? (numText.length() - 1) - numText.lastIndexOf(".") : BEFORE_DECIMAL;
    }


    @Override
    /**
     * Handles selection from the DatePickerFragment.
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = new LocalDate(year,month+1,dayOfMonth);
        final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        dateDisplay.setText(date.toString(dateFormat));
    }
}

