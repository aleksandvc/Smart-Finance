package com.vladimircvetanov.smartfinance;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;

import java.util.ArrayList;
import java.util.HashSet;



public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.IconViewHolder>{

    private ArrayList<CategoryExpense> categories;
    private Activity activity;

    FavouritesListAdapter(HashSet<CategoryExpense> favouriteCategories, Activity activity) {
        this.activity = activity;
        categories = new ArrayList<CategoryExpense> (favouriteCategories);
    }

    @Override
    public FavouritesListAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavouritesListAdapter.IconViewHolder holder, final int position) {
        final CategoryExpense categoryExpense = categories.get(position);
        holder.image.setImageResource(categoryExpense.getIconId());
        holder.image.setBackground(ContextCompat.getDrawable(activity, R.drawable.fav_icon_backgroud));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.image.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGrey));
                holder.removeButton.setVisibility(View.VISIBLE);

                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        categories.remove(holder.getAdapterPosition());
                        notifyItemRemoved(position);
                        DBAdapter.deleteFavCategory(categoryExpense);

                        //Manager.getInstance();
                        //Manager.addExpenseIcon(categoryExpense.getIconId());

                        //fragment.updateLists();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class IconViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton removeButton;
        ImageButton addButton;
        View viewGroup;

        public IconViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_icon_btn);
            addButton = (ImageButton) itemView.findViewById(R.id.add_icon_btn);
            this.viewGroup = itemView.findViewById(R.id.viewGroup);
        }
    }
}
