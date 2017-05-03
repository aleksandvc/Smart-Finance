package com.vladimircvetanov.smartfinance.reports;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Transaction;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class FilteredReportFragment extends Fragment implements AccountSelectionDialog.Communicator, Serializable {
    private static final DateTimeFormatter LONG_DATE_FORMAT = DateTimeFormat.forPattern("d MMMM, YYYY");
    private static final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat.forPattern("d MMM, YYYY");

    private Button filterButton;
    private Spinner sortSpinner;
    private TextView startDateView, endDateView;
    private DateTime startDate, endDate;
    private ExpandableListView list;
    private ExpandableAccountAdapter adapter;
    private HashSet<Account> selectedAccounts;

    private EndDateSetListener endDateListener;
    private StartDateSetListener startDateListener;


    public static FilteredReportFragment newInstance(Account... selectedAccounts) {
        HashSet<Account> accounts = new HashSet<>(Arrays.asList(selectedAccounts));
        return newInstance(accounts);
    }

    public static FilteredReportFragment newInstance(HashSet<Account> selectedAccounts) {
        FilteredReportFragment fragment = new FilteredReportFragment();
        fragment.selectedAccounts = new HashSet<>(selectedAccounts);
        fragment.startDate = new DateTime(1970, 1, 1, 0, 0);
        fragment.endDate = DateTime.now();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filtered_report, container, false);

        Log.e("ASDDDDDDDDDDDD", "onCreateView: " + (selectedAccounts == null));
        adapter = new ExpandableAccountAdapter(getActivity(), selectedAccounts);

        endDateListener = new EndDateSetListener();
        startDateListener = new StartDateSetListener();

        filterButton = (Button) rootView.findViewById(R.id.filtered_report_filters_button);

        startDateView = (TextView) rootView.findViewById(R.id.filtered_report_date_start);
        startDateView.setText(startDate.toString(LONG_DATE_FORMAT));
        startDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = DatePickerFragment.newInstance(startDateListener, startDate);
                datePicker.show(getFragmentManager(), getString(R.string.calendar_fragment_tag));
            }
        });
        endDateView = (TextView) rootView.findViewById(R.id.filtered_report_date_end);
        endDateView.setText(endDate.toString(LONG_DATE_FORMAT));
        endDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = DatePickerFragment.newInstance(endDateListener, endDate);
                datePicker.show(getFragmentManager(), getString(R.string.calendar_fragment_tag));
            }
        });

        list = (ExpandableListView) rootView.findViewById(R.id.filtered_report_list);
        list.setAdapter(adapter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSelectionDialog dialog = AccountSelectionDialog.newInstance(selectedAccounts, FilteredReportFragment.this);
                dialog.show(getFragmentManager(), "Account Selection Tag");
            }
        });

        sortSpinner = (Spinner) rootView.findViewById(R.id.filtered_report_sort_spinner);
        sortSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Transaction.getComparators()));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSorter((Transaction.TransactionComparator) sortSpinner.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void setAccounts(HashSet<Account> newSelection) {
        selectedAccounts = newSelection;
        adapter.setFilters(startDate, endDate, newSelection);
    }

    class ExpandableAccountAdapter extends BaseExpandableListAdapter {

        Comparator<Transaction> sorter;

        private ArrayMap<Account, ArrayList<Transaction>> dataSet;
        private DateTime startDate, endDate;

        private LayoutInflater inflater;

        private HashSet<Account> selectedAccounts;


        ExpandableAccountAdapter(Context context, HashSet<Account> selectedAccounts) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.selectedAccounts = selectedAccounts;

            this.startDate = new DateTime(1970, 1, 1, 0, 0);
            this.endDate = DateTime.now();

            sorter = new Transaction.TransactionSumComparator();

            this.dataSet = loadFromCache(selectedAccounts);
        }


        @Override
        public int getGroupCount() {
            return dataSet.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dataSet.valueAt(groupPosition).size();
        }

        @Override
        public Account getGroup(int groupPosition) {
            return dataSet.keyAt(groupPosition);
        }

        @Override
        public Transaction getChild(int groupPosition, int childPosition) {
            return dataSet.valueAt(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return getGroup(groupPosition).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_group, parent, false);

            Account acc = getGroup(groupPosition);

            ImageView icon = (ImageView) convertView.findViewById(R.id.report_group_icon);
            TextView title = (TextView) convertView.findViewById(R.id.report_group_title);
            TextView sum = (TextView) convertView.findViewById(R.id.report_group_sum);

            icon.setImageResource(acc.getIconId());
            title.setText(acc.getName());

            double accSum = 0.0;
            for (Transaction t : acc.getTransactions())
                if (t.getDate().isAfter(startDate) && t.getDate().isBefore(endDate))
                    accSum += t.getSum() * (t.getCategory().getType() == Category.Type.EXPENSE ? -1 : 1);
            sum.setText("$ " + accSum);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            final Transaction trans = getChild(groupPosition, childPosition);

            ImageView icon = (ImageView) convertView.findViewById(R.id.report_item_icon);
            TextView title = (TextView) convertView.findViewById(R.id.report_item_title);
            TextView sum = (TextView) convertView.findViewById(R.id.report_item_sum);
            TextView date = (TextView) convertView.findViewById(R.id.report_item_date);

            icon.setImageResource(trans.getCategory().getIconId());
            title.setText(trans.getCategory().getName());
            sum.setText("$ " + trans.getSum());
            date.setText(trans.getDate().toString(SHORT_DATE_FORMAT));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionDetailsFragment fragment = TransactionDetailsFragment.newInstance(trans);
                    fragment.show(getFragmentManager(), "TransactionDetails");
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void setFilters(DateTime start, DateTime end, HashSet<Account> newSelection) {
            if (start.isAfter(end)) {
                DateTime temp = start;
                start = end;
                end = temp;
            }

            this.startDate = start;
            this.endDate = end;
            this.selectedAccounts = newSelection;

            notifyDataSetChanged();
        }

        private ArrayMap<Account, ArrayList<Transaction>> loadFromCache(HashSet<Account> selectedAccounts) {
            dataSet = new ArrayMap<>();

            for (Account acc : selectedAccounts) {
                ArrayList<Transaction> transactions = new ArrayList<>();

                for (Transaction t : acc.getTransactions())
                    if (t.getDate().isAfter(startDate) && t.getDate().isBefore(endDate))
                        transactions.add(t);

                Collections.sort(transactions, sorter);
                dataSet.put(acc, transactions);
            }

            return dataSet;
        }

        @Override
        public void notifyDataSetChanged() {
            this.dataSet = loadFromCache(selectedAccounts);
            super.notifyDataSetChanged();
        }

        public void setSorter(Transaction.TransactionComparator sorter) {
            this.sorter = sorter;
            notifyDataSetChanged();
        }
    }


    class StartDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            DateTime newDate = new DateTime(year, month + 1, dayOfMonth, 0, 0);
            if (newDate.isAfter(endDate)) {
                startDate = endDate;
                endDate = newDate;
            } else {
                startDate = newDate;
            }
            startDateView.setText(startDate.toString(LONG_DATE_FORMAT));
            endDateView.setText(endDate.toString(LONG_DATE_FORMAT));
            adapter.setFilters(startDate, endDate, selectedAccounts);
        }
    }

    class EndDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            DateTime newDate = new DateTime(year, month + 1, dayOfMonth, 0, 0);
            if (newDate.isBefore(startDate)) {
                endDate = startDate;
                startDate = newDate;
            } else {
                endDate = newDate;
            }
            startDateView.setText(startDate.toString(LONG_DATE_FORMAT));
            endDateView.setText(endDate.toString(LONG_DATE_FORMAT));
            adapter.setFilters(startDate, endDate, selectedAccounts);
        }
    }
}
