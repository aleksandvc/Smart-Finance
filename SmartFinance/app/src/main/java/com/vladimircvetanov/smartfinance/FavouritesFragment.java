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

//import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class FavouritesFragment extends Fragment {

    private RecyclerView favouritesList;
    private RecyclerView additionalIconsList;
    private TextView moreIconsTitle;
    private DBAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter=DBAdapter.getInstance(getActivity());
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);

        favouritesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        HashSet<CategoryExpense> categories= new HashSet<>();
        categories.addAll(adapter.getCachedFavCategories().values());
        favouritesList.setAdapter(new FavouritesListAdapter(categories, getActivity()));
        favouritesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(new AdditionalIconsAdapter(Manager.getInstance().getAllExpenseIcons(), getActivity()));
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        return root;
    }
}
