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
import java.util.TreeMap;

public class ReportFragment extends Fragment {

    private DBAdapter dbAdapter;
    private ExpandableListView expandableListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        dbAdapter = DBAdapter.getInstance(getActivity());

        expandableListView = (ExpandableListView) v.findViewById(R.id.inquiry_expandable_list);

        ArrayList<Category> sections = new ArrayList<>();
        sections.addAll(dbAdapter.getCachedFavCategories().values());
        sections.addAll(dbAdapter.getCachedExpenseCategories().values());

        expandableListView.setAdapter(new ExpandableListAdapter(getContext()));

        return v;
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Category> groups;
        private HashMap<Category, ArrayList<Transaction>> children;
        private LayoutInflater inflater;
        private Context context;

        ExpandableListAdapter(Context context) {
            groups = new ArrayList<>();
            groups.addAll(dbAdapter.getCachedExpenseCategories().values());
            groups.addAll(dbAdapter.getCachedFavCategories().values());

            this.context = context;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            children = new HashMap<>();

            HashMap<Long, ArrayList<Transaction>> temp = new HashMap<>(dbAdapter.getCachedTransactions());
            for (Category c : groups) {
                ArrayList<Transaction> tempTrans = temp.containsKey(c.getId()) ? temp.get(c.getId()) : new ArrayList<Transaction>();
                children.put(c, tempTrans);
            }
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(getGroup(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(getGroup(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groups.get(groupPosition).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return ((Transaction) getChild(groupPosition, childPosition)).getId();
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
            i.setImageResource(cat.getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_group_name);
            t1.setText(cat.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_group_sum);
            t2.setText("$" + cat.getSum());

            if (cat.getSum() < 0)
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
