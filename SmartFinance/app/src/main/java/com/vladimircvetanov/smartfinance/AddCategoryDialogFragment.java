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
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.db.DBAdapter;

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
        String iconKey = getText(R.string.EXTRA_ICON).toString();
        String listKey = "ROW_DISPLAYABLE_TYPE";
        String list = "";
        int iconId = 0;

        if (b != null && !b.isEmpty() && b.containsKey(iconKey) && b.containsKey(listKey)) {
            iconId = b.getInt(iconKey);
            list = b.getString(listKey);

            assert list != null;
            switch (list) {
                case "ACCOUNT":
                    dialogTitle.setText("Add new account");
                    break;
                case "CATEGORY":
                    dialogTitle.setText("Add new category");
                    break;
            }
        }
        icon.setImageResource(iconId);

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
                    switch (tempList) {
                        case "C":
                            //adapter.addExpenseCategory(new CategoryExpense(nameStr, false, icon.getId()), Manager.getLoggedUser().getId());
                            Toast.makeText(getActivity(), "Category created!", Toast.LENGTH_SHORT).show();
                            break;
                        case "A":
                            //adapter.addAccount(new Account(nameStr, icon.getId()), Manager.getLoggedUser().getId());
                            Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    dismiss();
                }
            }
        });
        return view;
    }
}
