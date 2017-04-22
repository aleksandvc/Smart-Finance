package com.vladimircvetanov.smartfinance;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class AdditionalIconsListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.IconViewHolder>{

    private ArrayList<Integer> additionalIcons;
    private Activity activity;

    AdditionalIconsListAdapter(HashSet<Integer> allExpenseIcons, Activity activity) {
        this.activity = activity;
        additionalIcons = new ArrayList<Integer> (allExpenseIcons);
    }

    @Override
    public FavouritesListAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new FavouritesListAdapter.IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavouritesListAdapter.IconViewHolder holder, final int position) {
        final Integer icon = additionalIcons.get(position);
        holder.image.setImageResource(icon);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.image.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGrey));
                holder.addButton.setVisibility(View.VISIBLE);

                holder.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCategoryIfRoom(holder.image, holder);
                    }
                });
            }
        });
    }

    private void addCategoryIfRoom(ImageView image, FavouritesListAdapter.IconViewHolder holder) {
        if (favouriteCategories.size() < 9) {
            // popup dialog fragment
            //with icon image
            //and edit text for name
            //cancel and add btns
        } else {
            holder.addButton.setVisibility(View.GONE);
            image.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorTransparent));

            Toast.makeText(activity, "There are no free slots.\nPlease remove an existing category first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return additionalIcons.size();
    }
}
