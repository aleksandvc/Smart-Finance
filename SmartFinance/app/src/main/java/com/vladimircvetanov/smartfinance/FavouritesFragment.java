package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;

import java.util.HashSet;

public class FavouritesFragment extends Fragment {

    private RecyclerView favouritesList;
    private RecyclerView additionalIconsList;
    private TextView moreIconsTitle;
    private DBAdapter adapter;

    private FavouritesListAdapter favouritesListAdapter;
    private AdditionalIconsAdapter additionalIconsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter=DBAdapter.getInstance(getActivity());
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);

        HashSet<CategoryExpense> categories = new HashSet<>();
        categories.addAll(adapter.getCachedFavCategories().values());

        favouritesListAdapter = new FavouritesListAdapter(categories, getActivity(), FavouritesFragment.this);
        additionalIconsAdapter = new AdditionalIconsAdapter(Manager.getInstance().getAllExpenseIcons(), getActivity());

        favouritesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        favouritesList.setAdapter(favouritesListAdapter);
        favouritesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(additionalIconsAdapter);
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        return root;
    }

    void updateLists() {

        favouritesListAdapter.notifyDataSetChanged();
        additionalIconsAdapter.notifyDataSetChanged();
    }
}
