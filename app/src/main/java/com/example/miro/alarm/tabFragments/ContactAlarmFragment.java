package com.example.miro.alarm.tabFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.ContactAlarmAdapter;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.ContactAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.main.ContactAlarmSettingActivity;

/**
 * Created by Miro on 11/22/2016.
 */

public class ContactAlarmFragment extends PlaceholderFragment implements FragmentSetter {

    private Context context;
    private View rootView;
    private Contact setUpContact = null;
    private static final int REQ_CODE = 79;
    private static final int REQ_CODE_WAKE_UP = 70;
    private static final int CONTACT_PICKER_RESULT = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = getContext();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initiateButtons();

        return rootView;
    }


    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContactAlarmSettingActivity.class);
                intent.putExtra("contactSetting", contactSettings.get(position));
                startActivityForResult(intent, REQ_CODE);
            }
        });
        if (!contactSettings.isEmpty()) {
            final ContactAlarmAdapter adapter = new ContactAlarmAdapter(context, R.layout.contact_buttons,
                    contactSettings.toArray(new ContactAlarmSettingsImpl[contactSettings.size()]));
            listView.setAdapter(adapter);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 2);
        }
        final Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addButton() {
        if (setUpContact != null) {
            final ContactAlarmSettingsImpl contactAlarmSettings = new ContactAlarmSettingsImpl(context,
                    contactSettings.size(), setUpContact);
            contactSettings.add(contactAlarmSettings);

            refresh();
        }
    }

    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void removeButton(int id) {
        contactSettings.remove(id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    final Uri dataUri = data.getData();
                    if (dataUri != null) {
                        final String[] split = data.getData().toString().split("/");
                        final String id = String.valueOf(Long.parseLong(split[split.length - 1]));

                        final String[] whereArgs = new String[]{id,
                                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};
                        final Cursor cursor = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and "
                                        + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                                whereArgs, null);
                        if (cursor != null) {
                            int columnIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            cursor.moveToFirst();
                            final String phoneNumber = cursor.getString(columnIndex);
                            columnIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            final String displayName = cursor.getString(columnIndex);
                            cursor.close();
                            RequestQueue queue = Volley.newRequestQueue(context);
                            String url = String.format("http://127.0.0.1:8000/numberHasInstalled/%s", phoneNumber);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onResponse(String response) {
                                    final boolean hasApp = true;
                                    setUpContact = new Contact(displayName, phoneNumber, hasApp);
                                    addButton();
                                }
                            }, new Response.ErrorListener() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    final boolean hasApp = true;
                                    setUpContact = new Contact(displayName, phoneNumber, hasApp);
                                    //TODO send text message with link but only if owner agrees to send message
                                    addButton();
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }
                    break;
            }
        }
    }
}
