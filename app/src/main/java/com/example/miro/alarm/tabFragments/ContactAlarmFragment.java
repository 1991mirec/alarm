package com.example.miro.alarm.tabFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.enums.Type;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Miro on 11/22/2016.
 */

public class ContactAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/

        ((ViewGroup) rootView).addView(initiateButtons());

        return rootView;
    }





    @Override
    public LinearLayout initiateButtons() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Set the layout full width, full height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        if (contactSettings.isEmpty()) {

            final ContactAlarmSettingsImpl contactAlarmSettings = new ContactAlarmSettingsImpl(1, "my song", "miro",
                    Type.BOTH, new Postpone(1, 1, false), new Contact("miro", 1, true), 1002, false, new Repeat());

            linearLayout.addView(createButton(contactAlarmSettings.getName() + "  " + contactAlarmSettings.getRadius()));
            contactSettings.add(contactAlarmSettings);
        } else {
            for (final ContactAlarmSettingsImpl settings : contactSettings) {
                linearLayout.addView(createButton(settings.getName() + "  " + settings.getRadius()));
            }
        }
        return linearLayout;
    }

    @Override
    public void addButton() {

    }


    public Button createButton(final String name) {
        final Button button = new Button(getActivity());
        button.setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT));

        button.setText(name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return button;
    }

    @Override
    public void removeButton(int id) {
        contactSettings.remove(id);
    }


}
