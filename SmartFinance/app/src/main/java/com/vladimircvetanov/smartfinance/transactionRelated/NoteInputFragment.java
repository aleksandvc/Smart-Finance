package com.vladimircvetanov.smartfinance.transactionRelated;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vladimircvetanov.smartfinance.R;

public class NoteInputFragment extends DialogFragment{

    public interface NoteCommunicator{
        void setNote(String note);
    }

    private EditText input;
    private Button cancel, submit;
    private NoteCommunicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            communicator = (NoteCommunicator) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activities containing a NoteInputFragment MUST implement the NoteCommunicator interface!!!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_input, null);

        input = (EditText) rootView.findViewById(R.id.note_input_text);

        Bundle args = getArguments();
        if (args != null) input.setText(args.getString(getString(R.string.EXTRA_NOTE),""));

        cancel = (Button) rootView.findViewById(R.id.note_input_cancel);
        submit = (Button) rootView.findViewById(R.id.note_input_submit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.setNote(input.getText().toString());
                dismiss();
            }
        });
        
        return rootView;
    }
}

