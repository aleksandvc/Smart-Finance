package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private AdditionalIconsAdapter.IconViewHolder holder;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        adapter = DBAdapter.getInstance(getActivity());
        moreIconsTitle = (TextView) root.findViewById(R.id.more_icons_title);
        context = getActivity();

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

        additionalIconsList.addOnItemTouchListener(
            new RecyclerItemClickListener(context, additionalIconsList, new RecyclerItemClickListener.OnItemClickListener() {

                @Override public void onItemClick(View view, int position) {

                    //holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_icon_background));
                    //holder.addButton.setVisibility(View.VISIBLE);

                    AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
                    Bundle arguments = new Bundle();

                    long id = additionalIconsAdapter.getItemId(position);
                    arguments.putLong(getString(R.string.EXTRA_ICON), id);

                    //dialog.setArguments(arguments);
                    dialog.show(getFragmentManager(), getString(R.string.logout_button));

                    //addCategoryIfRoom((ImageView) view, holder);
                    Toast.makeText(context, "Item clicked!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongItemClick(View view, int position) {
                }
            })
        );
        return root;
    }

    private void addCategoryIfRoom(ImageView image, AdditionalIconsAdapter.IconViewHolder holder) {
        if (adapter.getCachedFavCategories().size() < 9) {

        } else if (adapter.getCachedFavCategories().size() == 9) {
            //holder.addButton.setVisibility(View.GONE);
            //holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.unselected_icon_background));

            Toast.makeText(context, "There are no free slots.\nPlease remove an existing category first!", Toast.LENGTH_SHORT).show();
        }
    }
}
