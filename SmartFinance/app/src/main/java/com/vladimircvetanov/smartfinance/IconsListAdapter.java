package com.vladimircvetanov.smartfinance;

import android.app.Activity;
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

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class IconsListAdapter extends RecyclerView.Adapter<IconsListAdapter.IconViewHolder>{

    private ArrayList<CategoryExpense> categories;
    private Activity activity;

    IconsListAdapter(HashSet<CategoryExpense> favouriteCategories, Activity activity) {
        this.activity = activity;
        categories = new ArrayList<CategoryExpense> (favouriteCategories);
    }

    @Override
    public IconsListAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IconsListAdapter.IconViewHolder holder, final int position) {
        final CategoryExpense categoryExpense = categories.get(position);
        holder.image.setImageResource(categoryExpense.getIconId());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.image.setBackgroundColor(activity.getResources().getColor(R.color.colorGrey));
                holder.removeButton.setVisibility(View.VISIBLE);

                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.viewGroup.setVisibility(View.GONE);
                        delete(position);
                        categories.remove(categoryExpense);
                        favouriteCategories.remove(categoryExpense);
                        DBAdapter.deleteFavCategory(categoryExpense);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public void delete(int position) {
        categories.remove(position);
        notifyItemRemoved(position);
    }

    public class IconViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton removeButton;
        View viewGroup;

        public IconViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_btn);
            this.viewGroup = itemView.findViewById(R.id.viewGroup);
        }
    }
}
