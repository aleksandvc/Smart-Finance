package com.vladimircvetanov.smartfinance.transactionRelated;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.model.RowDisplayable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Takes a RowDisplayable (meant for Category and Account) instance and
 * creates a view with a corresponding icon and title.
 */
public class RowViewAdapter<T extends RowDisplayable> extends BaseAdapter {

    private ArrayList<T> dataSet;
    private LayoutInflater inflater;

    public RowViewAdapter(LayoutInflater inflater, Collection<T> dataSet) {
        this.dataSet = new ArrayList<>(dataSet);
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_transaction_category, parent, false);

        T item = dataSet.get(position);

        TextView t = (TextView) convertView.findViewById(R.id.account_spinner_text);
        t.setText(item.getName());

        ImageView i = (ImageView) convertView.findViewById(R.id.account_spinner_icon);
        i.setImageResource(item.getIconId());

        return convertView;
    }
}


