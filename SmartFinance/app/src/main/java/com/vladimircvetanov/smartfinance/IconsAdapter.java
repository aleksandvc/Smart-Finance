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

import java.util.ArrayList;
import java.util.HashSet;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconViewHolder>{

    private ArrayList<Integer> additionalIcons;
    private Activity activity;
    private DBAdapter adapter;
    private boolean isClicked;

    public View.OnClickListener mItemClickListener;

    IconsAdapter(HashSet<Integer> allExpenseIcons, Activity activity) {
        this.activity = activity;
        additionalIcons = new ArrayList<Integer> (allExpenseIcons);
        adapter = DBAdapter.getInstance(activity);
        isClicked = false;
    }

    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.icons_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IconViewHolder holder, final int position) {
        final Integer icon = additionalIcons.get(position);
        holder.image.setImageResource(icon);
        holder.image.setBackground(ContextCompat.getDrawable(activity, R.drawable.unselected_icon_background));
    }

    @Override
    public int getItemCount() {
        return additionalIcons.size();
    }

    public static class IconViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton addButton;
        View viewGroup;

        public IconViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            addButton = (ImageButton) itemView.findViewById(R.id.add_icon_btn);
            this.viewGroup = itemView.findViewById(R.id.viewGroup);
        }
    }
}
