package com.example.miro.alarm.tabFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.POIAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Contact;
import com.example.miro.alarm.inteligentAlarm.helper.InteligentAlarm;
import com.example.miro.alarm.inteligentAlarm.helper.Postpone;
import com.example.miro.alarm.inteligentAlarm.helper.Repeat;
import com.example.miro.alarm.main.ContactAlarmSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

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
        if (contactSettings.size() == 0) {
            try {
                loadSharedPreferancesWithAlarmSettings();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initiateButtons();

        return rootView;
    }


    @Override
    public LinearLayout initiateButtons() {
        final ListView listView = (ListView) rootView.findViewById(R.id.listViewAlarm);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(contactSettings.get(position).gotPermission()) {
                    Intent intent = new Intent(getActivity(), ContactAlarmSettingActivity.class);
                    intent.putExtra("contactSettings", contactSettings.get(position));
                    startActivityForResult(intent, REQ_CODE);
                }
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
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void removeButton(int id) {
        contactSettings.remove(id);
        try {
            updateAndSaveSharedPreferancesWithAlarmSettings(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE:
                    final ContactAlarmSettingsImpl poiSettingsReturned = (ContactAlarmSettingsImpl) data.getExtras()
                            .getSerializable("contactSettings");

                    final int contactId = poiSettingsReturned.getId();
                    contactSettings.get(contactId).setAlarm(poiSettingsReturned);
                    refresh();
                    break;
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

    public static void updateAndSaveSharedPreferancesWithAlarmSettings(final Context context)
            throws JSONException {
        final JSONArray listOfSettings = new JSONArray();
        for(final ContactAlarmSettingsImpl settings: contactSettings){
            final JSONObject obj = new JSONObject();
            obj.put("name", settings.getName());
            obj.put("songName", settings.getSong().getName());
            obj.put("type", settings.getType().getType().ordinal());
            obj.put("radius", settings.getRadius());
            obj.put("distanceType", settings.getDistanceType());
            final JSONObject contact = new JSONObject();
            contact.put("hasApp", settings.getContact().getHasApp());
            contact.put("phoneNum", settings.getContact().getId());
            contact.put("name", settings.getContact().getName());
            obj.put("contact", contact);
            obj.put("volume", settings.getVolume());
            final JSONObject postpone = new JSONObject();
            postpone.put("isOn", settings.getPostpone().isOn());
            postpone.put("minutes", settings.getPostpone().getMinutes());
            postpone.put("timesOfRepeat", settings.getPostpone().getTimesOfRepeat());
            obj.put("postpone", postpone);
            obj.put("isOn", settings.isOn());
            listOfSettings.put(settings.getId(), obj);
        }
        final JSONObject mainObj = new JSONObject();
        mainObj.put("contactAlarmSettings", listOfSettings);
        final String stringToSave = mainObj.toString();
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("contactSettings", stringToSave).apply();
    }

    private void loadSharedPreferancesWithAlarmSettings() throws JSONException {
        final SharedPreferences sharedPreferences = getContext().getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String main = sharedPreferences.getString("contactSettings", null);
        if (main != null) {
            final JSONObject mainObject = new JSONObject(main);
            final JSONArray array = mainObject.getJSONArray("contactAlarmSettings");
            for (int i = 0, size = array.length(); i < size; i++) {
                final String name = ((JSONObject) array.get(i)).getString("name");
                final String songName = ((JSONObject) array.get(i)).getString("songName");
                final int type = ((JSONObject) array.get(i)).getInt("type");
                final int volume = ((JSONObject) array.get(i)).getInt("volume");
                final int radius = ((JSONObject) array.get(i)).getInt("radius");
                final boolean isOn = ((JSONObject) array.get(i)).getBoolean("isOn");

                final JSONObject contact = ((JSONObject) array.get(i)).getJSONObject("contact");
                final boolean hasApp = contact.getBoolean("hasApp");
                final String contactName = contact.getString("name");
                final String contactNum = contact.getString("phoneNum");
                final Contact contactObj = new Contact(contactName, contactNum, hasApp);
                final JSONObject postpone = ((JSONObject) array.get(i)).getJSONObject("postpone");
                final boolean postponeIsOn = postpone.getBoolean("isOn");
                final int postponeMinutes = postpone.getInt("minutes");
                final int postponeTimes = postpone.getInt("timesOfRepeat");
                final String distanceType = ((JSONObject) array.get(i)).getString("distanceType");
                final Postpone postponeObj = new Postpone(postponeTimes, postponeMinutes,
                        postponeIsOn);
                final ContactAlarmSettingsImpl contactSettingsReturned =
                        new ContactAlarmSettingsImpl(context, i, name, volume, isOn, type, songName,
                                postponeObj, contactObj, radius, distanceType);
                contactSettings.add(contactSettingsReturned);
            }
        }
    }
}
