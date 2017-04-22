package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Transaction;

import java.util.ArrayList;

public class ReportFragment extends Fragment {

    private ExpandableListView expandableListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        expandableListView = (ExpandableListView) v.findViewById(R.id.inquiry_expandable_list);
        ArrayList<Account> sections = new ArrayList<>();
        sections.addAll(DBAdapter.getInstance(getActivity()).getCachedAccounts().values());

        expandableListView.setAdapter(new ExpandableListAdapter(getActivity(), sections));

        return v;
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<Account> dataSet;
        private LayoutInflater inflater;


        public ExpandableListAdapter(Context context, ArrayList<Account> dataSet) {
            if (context == null || dataSet == null)
                throw new IllegalArgumentException("Parameter CAN NOT be null!");

            this.context = context;
            this.dataSet = dataSet;

            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return dataSet.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dataSet.get(groupPosition).getTransactions().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return dataSet.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return dataSet.get(groupPosition).getTransactions().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_group, parent, false);

            Account acc = dataSet.get(groupPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.inquiry_group_icon);
            i.setImageResource(acc.getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_group_name);
            t1.setText(acc.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_group_sum);
            t2.setText("$" + acc.getSum());

            if (acc.getSum() <= 0)
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            else
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            Transaction t = dataSet.get(groupPosition).getTransactions().get(childPosition);

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_item_sum);
            t1.setText("$" + t.getSum());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_item_date);
            t2.setText(t.getDate().toString("dd/MM/YY"));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
