package com.example.miro.alarm.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;

/**
 * Created by Miro on 11/27/2016.
 */

public class DaysDialogFragment extends DialogFragment {

    private DaysDialogListener listener;
    private Repeat repeat;

    public DaysDialogFragment(Repeat repeat) {
        this.repeat = repeat;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DaysDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Repeat tempRepeat = new Repeat(repeat);
        // mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Title")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.days, repeat.getDays(),
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                tempRepeat.setDays(which, isChecked);
                            }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        repeat = tempRepeat;
                        listener.onDialogPositiveClick(DaysDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(DaysDialogFragment.this);
                    }
                });

        return builder.create();
    }

    public interface DaysDialogListener {
        void onDialogPositiveClick(final DialogFragment dialog);

        void onDialogNegativeClick(final DialogFragment dialog);
    }
}
