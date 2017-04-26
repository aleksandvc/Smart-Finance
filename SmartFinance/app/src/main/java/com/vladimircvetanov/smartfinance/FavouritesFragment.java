package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;

import java.util.HashSet;

public class FavouritesFragment extends Fragment {

    private RecyclerView favouritesList;
    private RecyclerView additionalIconsList;
    private RecyclerView allCategoriesList;

    private TextView moreIconsTitle;
    private TextView favouritesTitle;
    private TextView allCategoriesTitle;
    private DBAdapter adapter;

    private FavouritesListAdapter favouritesListAdapter;
    private AdditionalIconsAdapter additionalIconsAdapter;

    private AdditionalIconsAdapter.IconViewHolder holder;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        favouritesTitle = (TextView) root.findViewById(R.id.fav_icons_title);
        allCategoriesTitle = (TextView) root.findViewById(R.id.all_categories_title);
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);
        context = getActivity();

        HashSet<CategoryExpense> categories = new HashSet<>();
        categories.addAll(adapter.getCachedFavCategories().values());

        favouritesListAdapter = new FavouritesListAdapter(categories, getActivity());
        favouritesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        favouritesList.setAdapter(favouritesListAdapter);
        favouritesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        allCategoriesList = (RecyclerView) root.findViewById(R.id.all_categories_list);
        final HashSet<CategoryExpense> allCategories = new HashSet<>();
        allCategories.addAll(adapter.getCachedExpenseCategories().values());
        allCategoriesList.setAdapter(new FavouritesListAdapter(allCategories, getActivity()));

        allCategoriesList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, allCategoriesList, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (adapter.getCachedFavCategories().size() < 9) {
                            //DBAdapter.addFavCategory();

                        } else if (adapter.getCachedFavCategories().size() == 9) {
                            Toast.makeText(context, "There are no free slots.\nPlease remove an existing category first!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        additionalIconsAdapter = new AdditionalIconsAdapter(Manager.getInstance().getAllExpenseIcons(), getActivity());
        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(additionalIconsAdapter);
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        additionalIconsList.addOnItemTouchListener(
            new RecyclerItemClickListener(context, additionalIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                @Override public void onItemClick(View view, int position) {
                        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                        Bundle arguments = new Bundle();

                        int id = view.getId();
                        //arguments.putInt(getString(R.string.EXTRA_ICON), id);

                        //dialog.setArguments(arguments);
                        dialog.show(getFragmentManager(), getString(R.string.logout_button));
                }

                @Override
                public void onLongItemClick(View view, int position) {
                }
            })
        );
        return root;
    }
}
