package com.vladimircvetanov.smartfinance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.Account;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;

public class AddCategoryDialogFragment extends DialogFragment {

    private TextView dialogTitle;
    private ImageView icon;
    private EditText categoryName;
    private Button addCategory;
    private Button cancel;

    private DBAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_category, container, false);

        dialogTitle = (TextView) view.findViewById(R.id.add_category_title);
        icon = (ImageView) view.findViewById(R.id.add_category_icon);
        categoryName = (EditText) view.findViewById(R.id.add_category_name);
        cancel = (Button) view.findViewById(R.id.cancel_addition);
        addCategory = (Button) view.findViewById(R.id.start_addition);

        adapter = DBAdapter.getInstance(getActivity());
        Bundle b = getArguments();
        final String iconKey = getText(R.string.EXTRA_ICON).toString();
        String listKey = getText(R.string.EXTRA_LIST).toString();
        String list = "";

        if (b != null && !b.isEmpty() && b.containsKey(iconKey) && b.containsKey(listKey)) {
            byte[] byteArray = b.getByteArray(iconKey);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            icon.setImageBitmap(bmp);

            list = b.getString(listKey);
            if (list.equals("ACCOUNT")) {
                dialogTitle.setText("Add new account");
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final String tempList = list;
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = categoryName.getText().toString();
                if (nameStr.isEmpty()) {
                    categoryName.setError("Name cannot be empty!");
                    categoryName.requestFocus();

                } else {
                    switch(tempList) {
                        case "CATEGORY":
                            adapter.addExpenseCategory(new CategoryExpense(nameStr, false, icon.getId()), Manager.getLoggedUser().getId());
                            Toast.makeText(getActivity(), "Category created!", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                        case "ACCOUNT":
                            adapter.addAccount(new Account(nameStr, icon.getId()), Manager.getLoggedUser().getId());
                            Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                    }
                }
            }
        });
        return view;
    }
}
