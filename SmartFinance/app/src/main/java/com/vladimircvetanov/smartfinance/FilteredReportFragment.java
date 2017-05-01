package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Transaction;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class FilteredReportFragment extends Fragment implements AccountSelectionDialog.Communicator, Serializable {

    private Button filterButton, sortButton;
    private TextView startDate, endDate;
    private DateTime start, end;
    private ExpandableListView list;
    private ExpandableAccountAdapter adapter;
    private HashSet<Account> selectedAccounts;

    public static FilteredReportFragment newInstance(Account... selectedAccounts) {
        FilteredReportFragment fragment = new FilteredReportFragment();
        fragment.selectedAccounts = new HashSet<>(Arrays.asList(selectedAccounts));
        fragment.start = new DateTime(1970, 1, 1, 0, 0);
        fragment.end = DateTime.now();
        return fragment;
    }

    public static FilteredReportFragment newInstance(HashSet<Account> selectedAccounts) {
        FilteredReportFragment fragment = new FilteredReportFragment();
        fragment.selectedAccounts = new HashSet<>(selectedAccounts);
        fragment.start = new DateTime(1970, 1, 1, 0, 0);
        fragment.end = DateTime.now();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filtered_report, container, false);


        filterButton = (Button) rootView.findViewById(R.id.filtered_report_filters_button);
        sortButton = (Button) rootView.findViewById(R.id.filtered_report_sort_button);

        startDate = (TextView) rootView.findViewById(R.id.filtered_report_date_start);
        endDate = (TextView) rootView.findViewById(R.id.filtered_report_date_end);
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("d MMMM, YYYY");
        startDate.setText(start.toString(dateFormat));
        endDate.setText(end.toString(dateFormat));


        list = (ExpandableListView) rootView.findViewById(R.id.filtered_report_list);

        adapter = new ExpandableAccountAdapter(getActivity(), selectedAccounts);

        list.setAdapter(adapter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSelectionDialog dialog = AccountSelectionDialog.newInstance(selectedAccounts, FilteredReportFragment.this);
                dialog.show(getFragmentManager(), "Account Selection Tag");
            }
        });

        /** On date click -> pop-up a DateDialogFragment and let user select different date. */
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();

                Bundle args = new Bundle();
                args.putSerializable(getString(R.string.EXTRA_DATE), start);
                datePicker.setArguments(args);

                datePicker.show(getFragmentManager(), "testTag");
            }
        });
        return rootView;
    }

    @Override
    public void setAccounts(HashSet<Account> newSelection) {
        selectedAccounts = newSelection;
        adapter.setFilters(start, end, newSelection);
    }


    class ExpandableAccountAdapter extends BaseExpandableListAdapter {

        Comparator<RowDisplayable> sorting;
        private HashSet<Account> selectedAccounts;
        private DateTime start, end;
        private LinkedHashMap<Account, LinkedHashMap<Category, ArrayList<Transaction>>> dataSet;
        private LayoutInflater inflater;
        private Context context;

        private DBAdapter dbAdapter;

        ExpandableAccountAdapter(Context context, HashSet<Account> selectedAccounts) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.start = new DateTime(1970, 1, 1, 0, 0);
            this.end = DateTime.now();

            this.selectedAccounts = new HashSet();
            this.selectedAccounts.addAll(selectedAccounts);
            dbAdapter = DBAdapter.getInstance(context);
            loadFromCache();
        }

        @Override
        public Account getGroup(int groupPosition) {
            return dataSet.keySet().toArray(new Account[]{})[groupPosition];
        }

        @Override
        public Category getChild(int groupPosition, int childPosition) {
            return (Category) dataSet.get(getGroup(groupPosition)).keySet().toArray()[childPosition];
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_group, parent, false);

            Account acc = getGroup(groupPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.inquiry_group_icon);
            i.setImageResource(acc.getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_group_name);
            t1.setText(acc.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_group_sum);
            double sum = 0.0;
            for (Transaction t : acc.getTransactions())
                if (t.getDate().isAfter(start) && t.getDate().isBefore(end))
                    sum += t.getSum() * (t.getCategory().getType() == Category.Type.EXPENSE ? -1 : 1);

            t2.setText("$" + sum);

            if (sum < 0)
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            else
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.fragment_report, parent, false);

            ((ExpandableListView) convertView).setAdapter(new SecondLevelAdapter(getGroup(groupPosition), getChild(groupPosition, childPosition), this, context));

            return convertView;
        }

        private void loadFromCache() {
            dataSet = new LinkedHashMap<>();

            Message.message(context, selectedAccounts.size()+"");
            for (Account a : dbAdapter.getCachedAccounts().values())
                if (selectedAccounts.contains(a))
                    dataSet.put(a, new LinkedHashMap<Category, ArrayList<Transaction>>());

            for (LinkedList<Transaction> list : dbAdapter.getCachedTransactions().values()) {
                for (Transaction t : list) {
                    if (dataSet.containsKey(t.getAccount()) && t.getDate().isAfter(start) && t.getDate().isBefore(end)) {
                        if (!dataSet.get(t.getAccount()).containsKey(t.getCategory()))
                            dataSet.get(t.getAccount()).put(t.getCategory(), new ArrayList<Transaction>());
                        dataSet.get(t.getAccount()).get(t.getCategory()).add(t);
                    }
                }
            }
        }

        public void setFilters(DateTime startDate, DateTime endDate, HashSet<Account> selectedAccounts) {
            this.start = startDate;
            this.end = endDate;
            this.selectedAccounts = selectedAccounts;

            loadFromCache();
            this.notifyDataSetChanged();
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
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public int getGroupCount() {
            return dataSet.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dataSet.get(getGroup(groupPosition)).size();
        }
    }

    class SecondLevelAdapter extends BaseExpandableListAdapter {

        private ExpandableAccountAdapter parent;
        private Category cat;
        private Account acc;
        private ArrayList<Transaction> dataSet;
        private LayoutInflater inflater;
        private Context context;

        SecondLevelAdapter(Account acc, Category cat, ExpandableAccountAdapter parent, Context context) {
            this.acc = acc;
            this.cat = cat;
            this.parent = parent;
            this.dataSet = parent.dataSet.get(acc).get(cat);
            this.context = context;
            this.parent = parent;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dataSet.size();
        }

        @Override
        public Category getGroup(int groupPosition) {
            return cat;
        }

        @Override
        public Transaction getChild(int groupPosition, int childPosition) {
            return dataSet.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return cat.getId();
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
                convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            Category cat = getGroup(groupPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.report_item_icon);
            i.setImageResource(cat.getIconId());

            TextView t0 = (TextView) convertView.findViewById(R.id.report_item_account);
            t0.setText(cat.getName());

            TextView t1 = (TextView) convertView.findViewById(R.id.report_item_sum);
            t1.setText(" ");

            TextView t2 = (TextView) convertView.findViewById(R.id.report_item_date);
            double sum = 0.0;
            for (Transaction t : dataSet)
                sum += t.getSum();

            t2.setText("$" + sum);

            if (cat.getType() == Category.Type.EXPENSE)
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            else
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            Transaction trans = getChild(groupPosition, childPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.report_item_icon);
            i.setImageResource(trans.getAccount().getIconId());

            TextView t0 = (TextView) convertView.findViewById(R.id.report_item_account);
            t0.setText(trans.getAccount().getName());

            TextView t1 = (TextView) convertView.findViewById(R.id.report_item_sum);
            t1.setText("$" + trans.getSum());

            TextView t2 = (TextView) convertView.findViewById(R.id.report_item_date);
            t2.setText(trans.getDate().toString("dd/MM/YY"));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
