package com.vladimircvetanov.smartfinance.reports;

import android.support.v4.app.DialogFragment;

import com.vladimircvetanov.smartfinance.model.Transaction;

import java.util.Comparator;



public class ReportSorterSelectionDialog extends DialogFragment {

    //TODO ::IMPLEMENT::

    interface Communicator {
        void setSorter(Comparator<Transaction> sorter);
    }
}