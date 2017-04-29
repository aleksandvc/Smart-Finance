package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>{

    private ArrayList<RowDisplayable> categories;
    private Context context;

    AccountsAdapter(HashSet<RowDisplayable> favouriteCategories, Context context) {
        this.context = context;
        categories = new ArrayList<RowDisplayable> (favouriteCategories);
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.accounts_list_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccountViewHolder holder, final int position) {
        final RowDisplayable account = categories.get(position);
        holder.accountImage.setImageResource(account.getIconId());
        holder.accountName.setText(account.getName());
        holder.accountSum.setText(account.getSum() + "");
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder{

        ImageView accountImage;
        TextView accountName;
        TextView accountSum;

        public AccountViewHolder(View itemView) {
            super(itemView);
            accountImage = (ImageView) itemView.findViewById(R.id.account_image);
            accountName = (TextView) itemView.findViewById(R.id.account_name);
            accountSum = (TextView) itemView.findViewById(R.id.account_sum);
        }
    }
}
