package com.vladimircvetanov.smartfinance.reports;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.Account;

import java.util.ArrayList;
import java.util.HashSet;

public class AccountSelectionDialog extends DialogFragment {

    private HashSet<Account> selectedAccounts;
    private ListView listView;
    private Button cancel, submit;
    private Communicator communicator;

    /**
     * Factory method for AccountSelectionDialog
     * @param selectedAccounts HashSet of Accounts that are already selected
     * @param communicator AccountSelectionDialog.Communicator instance that will recive the result from this Dialog.
     * @return a new AccountSelectionDialog instance.
     */
    public static AccountSelectionDialog newInstance(HashSet<Account> selectedAccounts, Communicator communicator) {
        AccountSelectionDialog fragment = new AccountSelectionDialog();
        fragment.selectedAccounts = selectedAccounts;
        fragment.communicator = communicator;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_selection, container, false);

        listView = (ListView) rootView.findViewById(R.id.account_selection_list);
        final CustomListAdapter listAdapter = new CustomListAdapter();
        listAdapter.inflater = inflater;

        listView.setAdapter(listAdapter);

        cancel = (Button) rootView.findViewById(R.id.account_selection_cancel);
        submit = (Button) rootView.findViewById(R.id.account_selection_submit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.setAccounts(selectedAccounts);
                dismiss();
            }
        });


        return rootView;
    }

    interface Communicator {
        void setAccounts(HashSet<Account> newSelection);
    }

    class CustomListAdapter extends BaseAdapter {

        ArrayList<Account> data = new ArrayList<Account>(DBAdapter.getInstance(getActivity()).getCachedAccounts().values());
        private LayoutInflater inflater;

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Account getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.account_selection_item, parent, false);

            final Account acc = getItem(position);

            TextView line = (TextView) convertView.findViewById(R.id.account_selection_name);
            line.setText(acc.getName());

            final CheckBox check = (CheckBox) convertView.findViewById(R.id.account_selection_checkbox);
            check.setChecked(selectedAccounts.contains(acc));

            ImageView i = (ImageView) convertView.findViewById(R.id.account_selection_icon);
            i.setImageResource(acc.getIconId());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check.isChecked())
                        selectedAccounts.remove(acc);
                    else
                        selectedAccounts.add(acc);

                    check.setChecked(!check.isChecked());
                }
            });
            return convertView;
        }
    }
}
