package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladimircvetanov.smartfinance.db.DBAdapter;

import java.util.HashSet;

public class AccountsFragment extends Fragment {

    private RecyclerView accountsList;
    private RecyclerView moreAccountIconsList;

    private RowDisplayableAdapter rowDisplayableAdapter;
    private IconsAdapter iconsAdapter;

    private DBAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        HashSet<RowDisplayable> accounts = new HashSet<>();
        accounts.addAll(adapter.getCachedAccounts().values());
        rowDisplayableAdapter = new RowDisplayableAdapter(accounts, getActivity());
        accountsList.setAdapter(rowDisplayableAdapter);

        return view;
    }
}
