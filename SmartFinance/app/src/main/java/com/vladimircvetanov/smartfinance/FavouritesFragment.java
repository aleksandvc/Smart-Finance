package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);
      //  recyclerView = (RecyclerView) root.findViewById(R.id.icons_list);

       // recyclerView.setAdapter(new IconsListAdapter(icons, getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return root;
    }
}
