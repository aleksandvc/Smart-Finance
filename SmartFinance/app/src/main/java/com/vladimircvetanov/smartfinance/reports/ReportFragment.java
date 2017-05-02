package com.vladimircvetanov.smartfinance.reports;

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

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Transaction;

import java.util.ArrayList;

public class ReportFragment extends Fragment {

    private DBAdapter dbAdapter;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;

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

        listAdapter = new ExpandableListAdapter(getContext());
        expandableListView.setAdapter(listAdapter);
        listAdapter.loadFromCache();


        return v;
    }

    @Override
    public void onPause() {
        for (int i = 0; i < listAdapter.getGroupCount(); i++)
            if (expandableListView.isGroupExpanded(i))
                expandableListView.collapseGroup(i);
        super.onPause();
    }

    @Override
    public void onResume() {
        listAdapter.notifyDataSetChanged();
        super.onResume();
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Category> groups;
        private LayoutInflater inflater;
        private Context context;

        ExpandableListAdapter(Context context) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            loadFromCache();
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

            ImageView i = (ImageView) convertView.findViewById(R.id.report_group_icon);
            i.setImageResource(cat.getIconId());

            TextView t1 = (TextView) convertView.findViewById(R.id.report_group_title);
            t1.setText(cat.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.report_group_sum);
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

            final Transaction trans = (Transaction) getChild(groupPosition, childPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.report_item_icon);
            i.setImageResource(trans.getAccount().getIconId());

            TextView t0 = (TextView) convertView.findViewById(R.id.report_item_title);
            t0.setText(trans.getAccount().getName());

            TextView t1 = (TextView) convertView.findViewById(R.id.report_item_sum);
            t1.setText("$" + trans.getSum());

            TextView t2 = (TextView) convertView.findViewById(R.id.report_item_date);
            t2.setText(trans.getDate().toString("dd/MM/YY"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionDetailsFragment fragment = TransactionDetailsFragment.newInstance(trans);
                    fragment.show(getFragmentManager(),"TransactionDetails");
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        protected void loadFromCache(){
            groups = new ArrayList<>();

            groups.addAll(dbAdapter.getCachedExpenseCategories().values());
            groups.addAll(dbAdapter.getCachedFavCategories().values());
            groups.addAll(dbAdapter.getCachedIncomeCategories().values());
        }

        @Override
        public void notifyDataSetChanged() {
            loadFromCache();
            super.notifyDataSetChanged();
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
            notifyDataSetChanged();
            super.onGroupExpanded(groupPosition);
        }
    }
}
