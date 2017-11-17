package com.example.miro.alarm.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.helper.TypeImpl;

/**
 * Created by Miro on 11/27/2016.
 */

public class TypeDialogFragment extends DialogFragment {

    private TypesDialogListener listener;
    private TypeImpl type;

    public TypeDialogFragment(final TypeImpl type) {
        this.type = type;
    }

    public TypeImpl getType() {
        return type;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TypesDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final TypeImpl tempType = new TypeImpl(type);
        // mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        CharSequence cs[] = {"Sound", "Vibration", "Sound and Vibration"};
        builder.setTitle("Title")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(cs, type.getType().ordinal(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempType.setType(which);

                            }
                        })
                // Set the action buttons
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(TypeDialogFragment.this);
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                type = tempType;
                listener.onDialogPositiveClick(TypeDialogFragment.this);
            }
        });

        return builder.create();
    }

     public interface TypesDialogListener {
        void onDialogPositiveClick(final DialogFragment dialog);

        void onDialogNegativeClick(final DialogFragment dialog);
    }
}
