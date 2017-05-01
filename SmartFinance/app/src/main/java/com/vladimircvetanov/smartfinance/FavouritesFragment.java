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
import com.vladimircvetanov.smartfinance.model.Manager;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment {

    private RecyclerView favouritesList;
    private RecyclerView additionalIconsList;
    private RecyclerView allCategoriesList;

    private TextView moreIconsTitle;
    private TextView favouritesTitle;
    private TextView allCategoriesTitle;
    private DBAdapter adapter;

    private RowDisplayableAdapter rowDisplayableAdapter;
    private IconsAdapter iconsAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        favouritesTitle = (TextView) root.findViewById(R.id.fav_icons_title);
        allCategoriesTitle = (TextView) root.findViewById(R.id.all_categories_title);
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);
        context = getActivity();

        ArrayList<RowDisplayable> categories = new ArrayList<>();
        categories.addAll(adapter.getCachedFavCategories().values());

        rowDisplayableAdapter = new RowDisplayableAdapter(categories, getActivity());
        favouritesList = (RecyclerView) root.findViewById(R.id.favourites_list);
        favouritesList.setAdapter(rowDisplayableAdapter);
        favouritesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        allCategoriesList = (RecyclerView) root.findViewById(R.id.all_categories_list);
        final ArrayList<RowDisplayable> allCategories = new ArrayList<>();
        allCategories.addAll(adapter.getCachedExpenseCategories().values());
        allCategoriesList.setAdapter(new RowDisplayableAdapter(allCategories, getActivity()));
        allCategoriesList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

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
                })
        );

        iconsAdapter = new IconsAdapter(Manager.getInstance().getAllExpenseIcons(), getActivity());
        additionalIconsList = (RecyclerView) root.findViewById(R.id.additional_icons_list);
        additionalIconsList.setAdapter(iconsAdapter);
        additionalIconsList.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        additionalIconsList.addOnItemTouchListener(
            new RecyclerItemClickListener(context, additionalIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {

                    AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                    Bundle arguments = new Bundle();
                    int iconId = Manager.getInstance().getAllExpenseIcons().get(position);

                    arguments.putInt(getString(R.string.EXTRA_ICON), iconId);
                    arguments.putInt("POSITION", position);
                    arguments.putString(String.valueOf(R.string.ROW_DISPLAYABLE_TYPE), String.valueOf(R.string.EXTRA_CATEGORY));

                    dialog.setArguments(arguments);
                    dialog.show(getFragmentManager(), String.valueOf(R.string.add_category_dialog));
                }

            }));
        return root;
    }
}
