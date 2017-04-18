package com.vladimircvetanov.smartfinance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.LogEntry;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.Section;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Manager.Type selectedType;

    private Spinner accountSelection;
    private Section selectedAccount;

    private ListView categorySelection;
    private Section selectedCategory;

    private TextView dateDisplay;
    private DateTime date;

    private EditText noteInput;

    //Selection between income and expense;
    private RadioGroup directionRadio;

    private TextView numDisplay;
    private ImageButton backspace;

    private View numpad;
    private View numDisplayBase;

    private TextView[] numButtons;

    private TextView equals;
    private TextView divide;
    private TextView multiply;
    private TextView plus;
    private TextView minus;
    private TextView decimal;

    private TextView submitButton;

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
     * If <i>true</i>, pressing an operation button will execute the standing arithmetic operation and reset this to false.
     * If <i>false</i>, standing operation can be changed at will until a number is pressed.
     */
    private boolean operationPrimed = false;

    //Previous input. Stored for executing currentOperation.
    private Double storedNumber = 0.0;

    private boolean startedWithSection = false;
    private boolean isNumpadDown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        //Set Toolbar, because our overlords at Google are taking <b>forever</b> to compat-ize the Appbar properly...
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentManager fm = getSupportFragmentManager();
        if(fm.getFragments() != null || !fm.getFragments().isEmpty()) {
            fm.beginTransaction()
                    .add(R.id.transaction_fragment, new TransactionFragment(), "Transaction")
                    .commit();
        }

        //==============================Initializations============================================================//

        accountSelection = (Spinner) findViewById(R.id.transaction_account_spinner);
        accountSelection.setAdapter(new AccountSpinnerAdapter(Manager.getSections(Manager.Type.INCOMING)));

        categorySelection = (ListView) findViewById(R.id.transaction_account_selection);
        categorySelection.setAdapter(new AccountSpinnerAdapter(Manager.getSections(Manager.Type.EXPENSE)));

        noteInput = (EditText) findViewById(R.id.transaction_note_input);

        numDisplayBase = findViewById(R.id.transaction_number_display);
        numpad = findViewById(R.id.transaction_numpad);

        submitButton = (TextView) findViewById(R.id.transaction_submit_btn);

        directionRadio = (RadioGroup) findViewById(R.id.transaction_radio);
        directionRadio.check(R.id.transaction_radio_expense);
        selectedType = Manager.Type.EXPENSE;

        dateDisplay = (TextView) findViewById(R.id.transaction_date_display);
        //Show the current date in a "d MMMM, YYYY" format.
        date = DateTime.now();
        final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        dateDisplay.setText(date.toString(dateFormat));

        numDisplay = (TextView) findViewById(R.id.transaction_number_display_text);
        backspace = (ImageButton) findViewById(R.id.transaction_number_display_backspace);

        //Seeing as they act almost identically, put all numeric buttons in an array for easier manipulation.
        numButtons = new TextView[10];
        for (int i = 0; i <= 9; i++)
            numButtons[i] = (TextView) findViewById(getResources().getIdentifier("transaction_numpad_" + i, "id", getPackageName()));

        equals = (TextView) findViewById(R.id.transaction_numpad_equals);
        divide = (TextView) findViewById(R.id.transaction_numpad_divide);
        multiply = (TextView) findViewById(R.id.transaction_numpad_multiply);
        plus = (TextView) findViewById(R.id.transaction_numpad_plus);
        minus = (TextView) findViewById(R.id.transaction_numpad_minus);
        decimal = (TextView) findViewById(R.id.transaction_numpad_decimal);
        //=========================================================================================================//

        Intent intent = getIntent();
        checkForCategoryExtra(intent);

        //============onClickListeners=============================================================================//


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

        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();

                Bundle args = new Bundle();
                args.putSerializable(getString(R.string.EXTRA_DATE), date);
                datePicker.setArguments(args);

                datePicker.show(getSupportFragmentManager(), "testTag");
            }
        });

        directionRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.transaction_radio_expense:
                        colorizeUI(TransactionActivity.this, R.color.colorOrange, R.drawable.orange_button_9);
                        selectedType = Manager.Type.EXPENSE;
                        submitButton.setText(getString(R.string.transaction_select_section));
                        break;
                    case R.id.transaction_radio_income:
                        colorizeUI(TransactionActivity.this, R.color.colorGreen, R.drawable.green_button_9);

                        selectedType = Manager.Type.INCOMING;
                        selectedAccount = (Section) accountSelection.getSelectedItem();
                        submitButton.setText(getString(R.string.transaction_add_to) + " " + selectedAccount.getName());
                        break;
                }
                if (startedWithSection) {
                    startedWithSection = false;
                    submitButton.setText(getString(R.string.transaction_select_section));
                }
            }
        });
        accountSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Section s = (Section) accountSelection.getItemAtPosition(position);
                selectedAccount = s;
                if (selectedType == Manager.Type.INCOMING)
                    submitButton.setText(getString(R.string.transaction_add_to) + " " + s.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        categorySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Section) categorySelection.getAdapter().getItem(position);
                createEntry();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startedWithSection) {
                    createEntry();
                    return;
                }
                if (selectedType == Manager.Type.INCOMING) {
                    selectedAccount = (Section) accountSelection.getSelectedItem();
                    if (selectedAccount == null) {
                        accountSelection.requestFocus();
                        Message.message(TransactionActivity.this, getString(R.string.transaction_select_account));
                        return;
                    }
                    createEntry();
                } else {
                    numpad.animate().setDuration(600).alpha(0.0F).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            numpad.setVisibility(View.GONE);
                            findViewById(R.id.transaction_section_selection_layout).setAlpha(1F);
                            findViewById(R.id.transaction_section_selection_layout).setVisibility(View.VISIBLE);
                            isNumpadDown = true;
                        }
                    });
                }
            }
        });
        //=========================================================================================================//

    }

    private void checkForCategoryExtra(Intent intent) {
        if (intent != null && intent.hasExtra(getString(R.string.EXTRA_SECTION))) {
            Section s = (Section) intent.getSerializableExtra(getString(R.string.EXTRA_SECTION));
            if (s == null || s.getType() == Manager.Type.INCOMING) return;

            selectedType = s.getType();
            selectedCategory = s;
            directionRadio.check(R.id.transaction_radio_expense);

            submitButton.setText(getString(R.string.transaction_add_to) + " " + s.getName());
            startedWithSection = true;
        }
    }

    /**
     * Changes the colours and background of UI elements for the TransactionActivity.
     * @param c Activity Context.
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
     * Creates a LogEntry with the note, sum, type and date selected in the Activity and a passed Section.
     */
    private void createEntry() {
        double sum = Double.parseDouble(numDisplay.getText().toString());
        String note = noteInput.getText().toString();

        Section category = selectedType == Manager.Type.INCOMING ? null : selectedCategory;

        LogEntry entry = new LogEntry(date, sum, note, selectedType, selectedAccount, category);

        if (Manager.addLogEntry(entry)) {
            Message.message(TransactionActivity.this, getString(R.string.transaction_success));
            startActivity(new Intent(TransactionActivity.this, MainActivity.class));
        } else {
            Message.message(TransactionActivity.this, getString(R.string.transaction_failure));
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_currency:
                return true;
            case R.id.item_settings:
                return true;
            case R.id.item_logout:
                startActivity(new Intent(TransactionActivity.this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Animates transition between CategorySelector and number-pad, if number-pad is hidden.
     */
    public void onBackPressed() {
        if (isNumpadDown)
            findViewById(R.id.transaction_section_selection_layout).animate().setDuration(600).alpha(0.0F).withEndAction(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.transaction_section_selection_layout).setVisibility(View.GONE);
                    numpad.setAlpha(1F);
                    numpad.setVisibility(View.VISIBLE);
                    isNumpadDown = false;
                }
            });
        else
            super.onBackPressed();
    }

    class AccountSpinnerAdapter extends BaseAdapter {

        private Section[] dataSet;

        public AccountSpinnerAdapter(Section[] dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public int getCount() {
            return dataSet.length;
        }

        @Override
        public Object getItem(int position) {
            return dataSet[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.spinner_transaction_category, parent, false);

            Section s = dataSet[position];

            TextView t = (TextView) convertView.findViewById(R.id.account_spinner_text);
            t.setText(s.getName());

            ImageView i = (ImageView) convertView.findViewById(R.id.account_spinner_icon);
            i.setImageResource(s.getIconID());

            return convertView;
        }
    }
}

