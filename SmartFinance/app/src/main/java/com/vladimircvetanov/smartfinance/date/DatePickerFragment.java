package com.vladimircvetanov.smartfinance.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.vladimircvetanov.smartfinance.R;

import org.joda.time.DateTime;

public class DatePickerFragment extends DialogFragment {

    private DateTime date;

    @Override
    public void setArguments(Bundle args) {
        date = (DateTime) args.getSerializable(getString(R.string.EXTRA_DATE));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (date == null) date = DateTime.now();

        int year = date.getYear();
        int month = date.getMonthOfYear() - 1;
        int day = date.getDayOfMonth();

        return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }


}
