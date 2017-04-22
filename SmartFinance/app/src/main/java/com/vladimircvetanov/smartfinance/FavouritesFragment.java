package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class FavouritesFragment extends Fragment {

    private RecyclerView favoutitesList;
    private RecyclerView additionalIconsList;
    private TextView moreIconsTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);

        favoutitesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        favoutitesList.setAdapter(new IconsListAdapter(favouriteCategories, getActivity()));
        favoutitesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        /*
        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(new IconsListAdapter(allExpenseIcons, getActivity()));
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        */

        return root;
    }
}
