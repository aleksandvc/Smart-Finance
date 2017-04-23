package com.vladimircvetanov.smartfinance;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.ImageView;

public class AddCategoryDialogFragment extends DialogFragment {

    private ImageView icon;
    private EditText categoryName;
    private Context context;

    public AddCategoryDialogFragment() {
        context = getActivity();
    }

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        return fragment;
    }

    /*
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    }
    */
}
