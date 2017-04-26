package com.vladimircvetanov.smartfinance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.vladimircvetanov.smartfinance.db.DBAdapter;

public class LogoutDialogFragment extends DialogFragment {

    private Context context;
    private DBAdapter adapter;

    public LogoutDialogFragment() {
        context = getActivity();
    }

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = DBAdapter.getInstance(context);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.logout_title));
        alertDialogBuilder.setMessage(getString(R.string.logout_message));

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton(getString(R.string.logout_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.clearCache();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return alertDialogBuilder.create();
    }
}
