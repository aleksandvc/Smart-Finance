package com.vladimircvetanov.smartfinance.favourites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.R;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;

/**
 * Created by vladimircvetanov on 01.05.17.
 */

public class AddToFavDeleteDialogFragment extends DialogFragment {

    private TextView dialogTitle;
    private Button deleteCategory;
    private Button addToFav;
    private Button cancel;

    DBAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_to_fav, container, false);

        dialogTitle = (TextView) view.findViewById(R.id.add_to_fav_title);
        cancel = (Button) view.findViewById(R.id.cancel_category_button);
        deleteCategory = (Button) view.findViewById(R.id.delete_category_button);
        addToFav = (Button) view.findViewById(R.id.add_to_fav_button);


        adapter = DBAdapter.getInstance(getActivity());

        Bundle b = getArguments();
        String iconKey = getText(R.string.EXTRA_ICON).toString();
        String listKey = "ROW_DISPLAYABLE_TYPE";
        final int iconId = b.getInt(iconKey);
        final CategoryExpense  cat = (CategoryExpense) b.getSerializable(listKey);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adapter.getCachedFavCategories().containsValue(cat) && adapter.getCachedFavCategories().size() < 10) {
                    if(adapter.getCachedExpenseCategories().size() > 1) {
                        adapter.moveToFav(cat);
                        Toast.makeText(getActivity(), "Category added to favorites!", Toast.LENGTH_SHORT).show();
                    } else{
                        Message.message(getActivity(),"You can`t be without categories!");
                    }
                } else{
                    Message.message(getActivity(),"This category is already in your favorites, or there is no more place!");
                }
                dismiss();
            }
        });

        deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(adapter.getCachedExpenseCategories().size() > 1) {
                   adapter.deleteExpenseCategory(cat);

                   Message.message(getActivity(),"Category deleted!");
               }else{
                   Message.message(getActivity(),"You can`t be without categories!");
               }

                dismiss();
            }
        });
        return view;
    }

}


