package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddCategoryDialogFragment extends DialogFragment {

    private TextView dialogTitle;
    private ImageView icon;
    private EditText categoryName;
    private Button addCategory;
    private Button cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_category, container, false);

        dialogTitle = (TextView) view.findViewById(R.id.add_category_title);
        icon = (ImageView) view.findViewById(R.id.add_category_icon);
        categoryName = (EditText) view.findViewById(R.id.add_category_name);
        cancel = (Button) view.findViewById(R.id.cancel_addition);
        addCategory = (Button) view.findViewById(R.id.start_addition);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DBAdapter.addFavCategory(new CategoryExpense(name, iconId, true));
            }
        });

        return view;
    }
}
