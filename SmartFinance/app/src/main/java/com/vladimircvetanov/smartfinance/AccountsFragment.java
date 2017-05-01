package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.Manager;

import java.util.ArrayList;
import java.util.HashSet;

public class AccountsFragment extends Fragment {

    private RecyclerView accountsList;
    private RecyclerView moreAccountIconsList;

    private AccountsAdapter accountsAdapter;
    private IconsAdapter iconsAdapter;

    private DBAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        context = getActivity();

        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        final ArrayList<RowDisplayable> accounts = new ArrayList<>();
        accounts.addAll(adapter.getCachedAccounts().values());

        accountsAdapter = new AccountsAdapter(accounts, getActivity());
        accountsList = (RecyclerView) view.findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsAdapter);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        accountsList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, accountsList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        //TODO - re-name TAG
                        getFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment_frame, FilteredReportFragment.newInstance((Account) accountsAdapter.getItem(position)), getString(R.string.transaction_fragment_tag))
                                .addToBackStack(getString(R.string.transaction_fragment_tag))
                                .commit();
                    }
                })
        );

        iconsAdapter = new IconsAdapter(Manager.getInstance().getAllAccountIcons(), getActivity());
        moreAccountIconsList = (RecyclerView) view.findViewById(R.id.accounts_icons_list);
        moreAccountIconsList.setAdapter(iconsAdapter);
        moreAccountIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        moreAccountIconsList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, moreAccountIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override public void onItemClick(View view, int position) {

                        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                        Bundle arguments = new Bundle();
                        int iconId = Manager.getInstance().getAllAccountIcons().get(position);

                        arguments.putInt(getString(R.string.EXTRA_ICON), iconId);
                        arguments.putString("ROW_DISPLAYABLE_TYPE", "ACCOUNT");

                        dialog.setArguments(arguments);
                        dialog.show(getFragmentManager(), String.valueOf(R.string.add_category_dialog));

                    }
                })
        );
        return view;
    }
}
