package com.vladimircvetanov.smartfinance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.Transaction;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TransactionFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private DBAdapter dbAdapter;

    //Selection radio between income and expense;
    private RadioGroup catTypeRadio;
    private Spinner accountSelection;
    private ListView categorySelection;

    private Category.Type selectedType;
    private Account selectedAccount;
    private Category selectedCategory;

    private TextView dateDisplay;
    private DateTime date;

    private EditText noteInput;

    private TextView submitButton;

    private boolean startedWithSection = false;
    private boolean isNumpadDown = false;

    private View rootView;

    //=======CALCULATOR==============//
    private View numDisplayBase;
    private TextView numDisplay;
    private ImageButton backspace;

    private View numpad;

    private TextView[] numButtons;
    private TextView decimal;

    private TextView equals;
    private TextView divide;
    private TextView multiply;
    private TextView plus;
    private TextView minus;


    //Counts how far past the decimal point the input number is. Used to block input past 2 decimal positions
    private int decimalPosition = BEFORE_DECIMAL;
    private static final int BEFORE_DECIMAL = -1;
    private static final int MAX_DECIMAL_DEPTH = 2;

    /**
     * Flag tracking if there is an operation waiting for execution. Basically: true if there has been numeric input after setting an operation.
     * If <i>true</i>, pressing an operation button will execute the standing arithmetic operation and reset this to false.
     * If <i>false</i>, standing operation can be changed at will until a number is pressed.
     */
    private boolean operationPrimed = false;
    //Tracks what arithmetic operation is currently selected.
    private char currentOperation = OPERATION_NONE;
    private static final char OPERATION_NONE = '=';
    private static final char OPERATION_PLUS = '+';
    private static final char OPERATION_MINUS = '-';
    private static final char OPERATION_MULTIPLY = 'x';
    private static final char OPERATION_DIVIDE = '÷';

    //Previous input. Stored for executing currentOperation.
    private Double storedNumber = 0.0;

    //=======CALCULATOR==============//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction, container, false);
        initializeUiObjects();

        int uId = (int) Manager.getLoggedUser().getId();

        dbAdapter = DBAdapter.getInstance(this.getActivity());

        dbAdapter.addAccount(new Account("Cash", R.mipmap.smartfinance_icon), uId);
        dbAdapter.addAccount(new Account("Bash", R.mipmap.smartfinance_icon), uId);
        dbAdapter.addAccount(new Account("Rash", R.mipmap.smartfinance_icon), uId);

//        dbAdapter.addFavCategory(new CategoryExpense("Vehicle", true, R.mipmap.car),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Clothes", true, R.mipmap.clothes),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Health", true, R.mipmap.heart),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Travel", true, R.mipmap.plane),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("House", true, R.mipmap.home),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Sport", true, R.mipmap.swimming),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Food", true, R.mipmap.restaurant),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Transport", true, R.mipmap.train),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Entertainment", true, R.mipmap.cocktail),uId);
//        dbAdapter.addFavCategory(new CategoryExpense("Phone", true, R.mipmap.phone),uId);

        dbAdapter.getAllAccounts();
        dbAdapter.getAllExpenseCategories();
        dbAdapter.getAllIncomeCategories();
        dbAdapter.getAllFavCategories();

        catTypeRadio.check(R.id.transaction_radio_expense);

        accountSelection.setAdapter(new RowViewAdapter<Account>(inflater, dbAdapter.getCachedAccounts().values()));

        final RowViewAdapter<Category> expenseAdapter = new RowViewAdapter<>(inflater, dbAdapter.getCachedExpenseCategories().values());
        final RowViewAdapter<Category> incomeAdapter = new RowViewAdapter<>(inflater, dbAdapter.getCachedIncomeCategories().values());
        categorySelection.setAdapter(expenseAdapter);

        //Show the current date in a "d MMMM, YYYY" format.
        date = DateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        dateDisplay.setText(date.toString(dateFormat));


//        checkForCategoryExtra();

        //============onClickListeners=============================================================================//

        /**
         * On date click -> pop-up a DateDialogFragment and let user select different date.
         */
        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();

                Bundle args = new Bundle();
                args.putSerializable(getString(R.string.EXTRA_DATE), date);
                datePicker.setArguments(args);

                datePicker.show(getFragmentManager(), "testTag");
            }
        });

        catTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.transaction_radio_expense:
                        colorizeUI(getActivity(), R.color.colorOrange, R.drawable.orange_button_9);
                        selectedType = Category.Type.EXPENSE;
                        categorySelection.setAdapter(expenseAdapter);
                        break;
                    case R.id.transaction_radio_income:
                        colorizeUI(getActivity(), R.color.colorGreen, R.drawable.green_button_9);
                        selectedType = Category.Type.INCOME;
                        categorySelection.setAdapter(incomeAdapter);
                        break;
                }
            }
        });
        accountSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Account acc = (Account) accountSelection.getItemAtPosition(position);
                selectedAccount = acc;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        categorySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Category) categorySelection.getItemAtPosition(position);
                selectedAccount = (Account) accountSelection.getSelectedItem();
                createTransaction();
            }
        });

        //TODO - REDO listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numpad.animate().setDuration(600).alpha(0.0F).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        numpad.setVisibility(View.GONE);
                        rootView.findViewById(R.id.transaction_section_selection_layout).setAlpha(1F);
                        rootView.findViewById(R.id.transaction_section_selection_layout).setVisibility(View.VISIBLE);
                        isNumpadDown = true;
                    }
                });
            }
        });

        //=========================================================================================================//
        //------------CALCULATOR_LISTENERS------------------------------------------------------------------//
        /**
         * On arithmetic button pressed -> execute stored {@link TransactionActivity#currentOperation}
         *                              -> save new currentOperation
         */
        View.OnClickListener arithmeticListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operationPrimed && currentOperation != OPERATION_NONE) calculate();
                currentOperation = ((TextView) v).getText().charAt(0);
                if (currentOperation != OPERATION_NONE) operationPrimed = false;
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
        View.OnClickListener numberListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Block input if maximal number of digits after decimal point has been reached.
                if (operationPrimed && decimalPosition >= MAX_DECIMAL_DEPTH) return;

                if (!operationPrimed) {
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

                numDisplay.append(((TextView) v).getText().toString());
                if (decimalPosition != BEFORE_DECIMAL) decimalPosition++;
            }
        };

        equals.setOnClickListener(arithmeticListener);
        divide.setOnClickListener(arithmeticListener);
        multiply.setOnClickListener(arithmeticListener);
        plus.setOnClickListener(arithmeticListener);
        minus.setOnClickListener(arithmeticListener);


        for (TextView btn : numButtons)
            btn.setOnClickListener(numberListener);
        decimal.setOnClickListener(numberListener);

        /**
         * Deletes a single digit (or the dec. point)
         */
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get currently displayed String representation and its length
                String displayText = numDisplay.getText().toString();
                int length = displayText.length();

                //if the 'display' is a single digit long, replaces it with a 0.
                //else removes last number
                numDisplay.setText(displayText.matches("^-?[0-9]$") ? "0" : displayText.substring(0, --length));
                //If las digit was after the decimal point, move tracker one step back.
                if (decimalPosition != BEFORE_DECIMAL) decimalPosition--;
            }
        });

        //-------END--CALCULATOR_LISTENERS------------------------------------------------------------------//

        return rootView;
    }

    private void checkForCategoryExtra(Intent intent) {
        if (intent != null && intent.hasExtra(getString(R.string.EXTRA_SECTION))) {
            CategoryExpense s = (CategoryExpense) intent.getSerializableExtra(getString(R.string.EXTRA_SECTION));
            if (s == null) return;

            //selectedType = s.getType();
            selectedCategory = s;
            catTypeRadio.check(R.id.transaction_radio_expense);

            submitButton.setText(getString(R.string.transaction_add_to) + " " + s.getName());
            startedWithSection = true;
        }
    }

    /**
     * Changes the colours and background of UI elements for the TransactionActivity.
     *
     * @param c           Activity Context.
     * @param colourID
     * @param ninePatchID
     */
    private void colorizeUI(Context c, int colourID, int ninePatchID) {
        numDisplay.setBackgroundResource(colourID);
        backspace.setBackgroundResource(colourID);
        numDisplayBase.setBackgroundResource(colourID);
        dateDisplay.setTextColor(ContextCompat.getColor(c, colourID));

        for (TextView btn : numButtons)
            btn.setBackgroundResource(ninePatchID);

        decimal.setBackgroundResource(ninePatchID);
        equals.setBackgroundResource(ninePatchID);
        plus.setBackgroundResource(ninePatchID);
        minus.setBackgroundResource(ninePatchID);
        multiply.setBackgroundResource(ninePatchID);
        divide.setBackgroundResource(ninePatchID);

        submitButton.setBackgroundResource(ninePatchID);
    }

    /**
     * Creates a Transaction with the note, sum, type and date selected in the Activity and a passed Section.
     */
    private void createTransaction() {
        double sum = Double.parseDouble(numDisplay.getText().toString());
        String note = noteInput.getText().toString();

        Account account = selectedAccount;
        Category category = selectedCategory;

        Transaction transaction = new Transaction(date, sum, note, account, category);

        dbAdapter.addTransaction(transaction, Manager.getLoggedUser().getId());
        account.addTransaction(transaction);

        startActivity(new Intent(this.getActivity(), MainActivity.class));

    }

    /**
     * Executes stored arithmetic operation.
     */
    private void calculate() {
        double currNumber = Double.valueOf(numDisplay.getText().toString());

        switch (currentOperation) {
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
        storedNumber = Math.round(storedNumber * 100.0) / 100.0;

        String numText = storedNumber.toString();
        numText = numText.replaceAll("\\.(00$|0$)", "");
        numDisplay.setText(numText);

        decimalPosition = numText.contains(".") ? (numText.length() - 1) - numText.lastIndexOf(".") : BEFORE_DECIMAL;
    }

    @Override
    /**
     * Handles selection from the DatePickerFragment.
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = new DateTime(year, month + 1, dayOfMonth, 0, 0);
        final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        dateDisplay.setText(date.toString(dateFormat));
    }

    /**
     * Animates transition between CategorySelector and number-pad, if number-pad is hidden.
     */
    public void onBackPressed() {
        if (isNumpadDown)
            rootView.findViewById(R.id.transaction_section_selection_layout).animate().setDuration(600).alpha(0.0F).withEndAction(new Runnable() {
                @Override
                public void run() {
                    rootView.findViewById(R.id.transaction_section_selection_layout).setVisibility(View.GONE);
                    numpad.setAlpha(1F);
                    numpad.setVisibility(View.VISIBLE);
                    isNumpadDown = false;
                }
            });
        else
            super.getActivity().onBackPressed();
    }

    /**
     * Moved all the .findViewById([...]) methods here, because the onCreateView was getting a bit cluttered.
     */
    private void initializeUiObjects() {
        catTypeRadio = (RadioGroup) rootView.findViewById(R.id.transaction_radio);
        dateDisplay = (TextView) rootView.findViewById(R.id.transaction_date_display);

        noteInput = (EditText) rootView.findViewById(R.id.transaction_note_input);
        accountSelection = (Spinner) rootView.findViewById(R.id.transaction_account_spinner);

        numDisplayBase = rootView.findViewById(R.id.transaction_number_display);
        numDisplay = (TextView) rootView.findViewById(R.id.transaction_number_display_text);
        backspace = (ImageButton) rootView.findViewById(R.id.transaction_number_display_backspace);

        numpad = rootView.findViewById(R.id.transaction_numpad);

        //Seeing as they act almost identically, put all numeric buttons in an array for easier manipulation.
        numButtons = new TextView[10];
        for (int i = 0; i <= 9; i++)
            numButtons[i] = (TextView) rootView.findViewById(getResources().getIdentifier("transaction_numpad_" + i, "id", getActivity().getPackageName()));
        decimal = (TextView) rootView.findViewById(R.id.transaction_numpad_decimal);
        equals = (TextView) rootView.findViewById(R.id.transaction_numpad_equals);
        divide = (TextView) rootView.findViewById(R.id.transaction_numpad_divide);
        multiply = (TextView) rootView.findViewById(R.id.transaction_numpad_multiply);
        plus = (TextView) rootView.findViewById(R.id.transaction_numpad_plus);
        minus = (TextView) rootView.findViewById(R.id.transaction_numpad_minus);

        categorySelection = (ListView) rootView.findViewById(R.id.transaction_account_selection);
        submitButton = (TextView) rootView.findViewById(R.id.transaction_submit_btn);
    }
}
