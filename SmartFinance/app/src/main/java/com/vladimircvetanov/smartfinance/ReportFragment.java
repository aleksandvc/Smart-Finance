package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.vladimircvetanov.smartfinance.model.LogEntry;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.Section;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportFragment extends Fragment {

    private ExpandableListView expandableListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        expandableListView = (ExpandableListView) v.findViewById(R.id.inquiry_expandable_list);
        ArrayList<Section> sections = new ArrayList<>();
        Section[] expenseSections = Manager.getSections(Manager.Type.EXPENSE);
        Section[] incomeSections = Manager.getSections(Manager.Type.INCOMING);
        sections.addAll(Arrays.asList(expenseSections));
        sections.addAll(Arrays.asList(incomeSections));

        expandableListView.setAdapter(new ExpandableListAdapter(getActivity(), sections));

        return v;
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<Section> dataSet;
        private LayoutInflater inflater;


        public ExpandableListAdapter(Context context, ArrayList<Section> dataSet) {
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
            return dataSet.get(groupPosition).getLog().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return dataSet.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return dataSet.get(groupPosition).getLog().get(childPosition);
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

            Section s = dataSet.get(groupPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.inquiry_group_icon);
            i.setImageResource(s.getIconID());

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_group_name);
            t1.setText(s.getName());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_group_sum);
            t2.setText("$" + s.getSum());

            if (s.getType() == Manager.Type.EXPENSE || s.getSum() <= 0)
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
            else
                t2.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            LogEntry e = dataSet.get(groupPosition).getLog().get(childPosition);

            ImageView i = (ImageView) convertView.findViewById(R.id.inquiry_item_icon);
            switch (e.getType()) {
                case EXPENSE:
                    i.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.radio_expense_true));
                    break;
                case INCOMING:
                    i.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.radio_income_true));
                    break;
            }

            TextView t1 = (TextView) convertView.findViewById(R.id.inquiry_item_sum);
            t1.setText("$" + e.getSum());

            TextView t2 = (TextView) convertView.findViewById(R.id.inquiry_item_date);
            t2.setText(e.getDate().toString("dd/MM/YY"));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
