package com.vladimircvetanov.smartfinance.reports;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Transaction;

public class TransactionDetailsFragment extends DialogFragment {

    private Transaction t;

    public static TransactionDetailsFragment newInstance(Transaction transaction) {
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        fragment.t = transaction;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        ImageView accountIcon = (ImageView) rootView.findViewById(R.id.transaction_dialog_source_icon);
        ImageView categoryIcon = (ImageView) rootView.findViewById(R.id.transaction_dialog_destination_icon);

        TextView accountName = (TextView) rootView.findViewById(R.id.transaction_dialog_source_title);
        TextView categoryName = (TextView) rootView.findViewById(R.id.transaction_dialog_destination_title);

        ImageView directionIndicator = (ImageView) rootView.findViewById(R.id.transaction_dialog_direction_indicator);

        TextView noteView = (TextView) rootView.findViewById(R.id.transaction_dialog_note);

        TextView dateView = (TextView) rootView.findViewById(R.id.transaction_dialog_date);
        TextView sumView = (TextView) rootView.findViewById(R.id.transaction_dialog_sum);

        Button returnButton = (Button) rootView.findViewById(R.id.transaction_dialog_return_button);


        accountIcon.setImageResource(t.getAccount().getIconId());
        categoryIcon.setImageResource(t.getCategory().getIconId());

        accountName.setText(t.getAccount().getName());
        categoryName.setText(t.getCategory().getName());

        directionIndicator.setImageResource(t.getCategory().getType() == Category.Type.EXPENSE ? R.mipmap.arrow_out : R.mipmap.arrow_in);

        String note = t.getNote();
        if (note == null || note.isEmpty()) note = "No note entered for this transaction.";
        noteView.setText(note);

        dateView.setText("Date: " + t.getDate().toString("dd MM/YYYY"));

        sumView.setText("Sum: " + t.getSum() + " $");

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

}
