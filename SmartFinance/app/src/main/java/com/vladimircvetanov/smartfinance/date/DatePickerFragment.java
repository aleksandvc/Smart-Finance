package com.vladimircvetanov.smartfinance.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.joda.time.LocalDate;

/**
 * Created by simeon on 4/5/17.
 */

public class DatePickerFragment extends DialogFragment{

    private LocalDate date;

    @Override
    public void setArguments(Bundle args) {
        LocalDate date = (LocalDate) args.getSerializable("date");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (date == null) date = LocalDate.now();

        int year = date.getYear();
        int month = date.getMonthOfYear() - 1;
        int day = date.getDayOfMonth();

        return new DatePickerDialog(getContext(),(DatePickerDialog.OnDateSetListener) getActivity(), year,month,day);
    }


}
