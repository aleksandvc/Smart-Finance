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
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportFragment extends Fragment {

    private DBAdapter dbAdapter;
    private ExpandableListView expandableListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        dbAdapter = DBAdapter.getInstance(getActivity());
        dbAdapter.loadTransactions();

        expandableListView = (ExpandableListView) v.findViewById(R.id.inquiry_expandable_list);

        ArrayList<Category> sections = new ArrayList<>();
        sections.addAll(dbAdapter.getCachedFavCategories().values());
        sections.addAll(dbAdapter.getCachedExpenseCategories().values());
        sections.addAll(dbAdapter.getCachedIncomeCategories().values());

        expandableListView.setAdapter(new ExpandableListAdapter(getContext()));

        return v;
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Category> groups;
        private LayoutInflater inflater;
        private Context context;

        ExpandableListAdapter(Context context) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            groups = new ArrayList<>();

            groups.addAll(dbAdapter.getCachedExpenseCategories().values());
            groups.addAll(dbAdapter.getCachedFavCategories().values());
            groups.addAll(dbAdapter.getCachedIncomeCategories().values());
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            long groupId = getGroupId(groupPosition);
            return dbAdapter.getCachedTransactions().containsKey(groupId) ? dbAdapter.getCachedTransactions().get(groupId).size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            long groupId = getGroupId(groupPosition);
            return dbAdapter.getCachedTransactions().get(groupId).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return ((Category)getGroup(groupPosition)).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return ((Transaction)getChild(groupPosition, childPosition)).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_group, parent, false);

            Category cat = (Category) getGroup(groupPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.inquiry_group_icon);
//            i.setImageResource(cat.getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_group_name);
            t1.setText(cat.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_group_sum);
            double sum = 0.0;

            if (dbAdapter.getCachedTransactions().containsKey(cat.getId()))
                for (Transaction t : dbAdapter.getCachedTransactions().get(cat.getId()))
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

            Transaction t = (Transaction) getChild(groupPosition, childPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.report_item_icon);
            i.setImageResource(t.getAccount().getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.report_item_account);
            t1.setText(t.getAccount().getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.report_item_date);
            t2.setText(t.getDate().toString("dd/MM/YY"));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
