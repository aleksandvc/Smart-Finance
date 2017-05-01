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
import com.vladimircvetanov.smartfinance.message.Message;
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
        String iconKey = getText(R.string.EXTRA_ICON).toString();
        String listKey = String.valueOf(R.string.ROW_DISPLAYABLE_TYPE);
        String list = "";
        int iconId = 0;

        if (b != null && !b.isEmpty() && b.containsKey(iconKey) && b.containsKey(listKey)) {
            iconId = b.getInt(iconKey);
            list = b.getString(listKey);

            assert list != null;
            switch (list) {
                case "ACCOUNT":
                    dialogTitle.setText(R.string.add_new_account);
                    break;
                case "CATEGORY":
                    dialogTitle.setText(R.string.add_new_category);
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
        final int finalIconId = iconId;
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = categoryName.getText().toString();
                if (nameStr.isEmpty()) {
                    categoryName.setError("Name cannot be empty!");
                    categoryName.requestFocus();

                } else {
                    switch (tempList) {
                        case "CATEGORY":
                            CategoryExpense cat = new CategoryExpense(nameStr, false, finalIconId);
                            if(!adapter.getCachedExpenseCategories().containsValue(cat) && !adapter.getCachedFavCategories().containsValue(cat)) {
                                adapter.addExpenseCategory(cat, Manager.getLoggedUser().getId());
                                Toast.makeText(getActivity(), "Category created!", Toast.LENGTH_SHORT).show();
                            }else{
                                Message.message(getActivity(),"This category already exists,please choose another name!");
                            }
                            break;
                        case "ACCOUNT":
                            Account ac = new Account(nameStr, finalIconId);
                            if(!adapter.getCachedAccounts().containsValue(ac)) {
                                adapter.addAccount(ac, Manager.getLoggedUser().getId());
                                Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                            }else{
                                Message.message(getActivity(),"This account already exists,please choose another name!");
                            }
                            break;
                    }
                    dismiss();
                }
            }
        });
        return view;
    }
}