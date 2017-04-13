package com.vladimircvetanov.smartfinance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LogoutDialogFragment extends DialogFragment {

    Context context;

    public LogoutDialogFragment() {
        context = getActivity();
    }

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Log out!");
        alertDialogBuilder.setMessage("Are you sure you want to log out?");

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return alertDialogBuilder.create();
    }
}
