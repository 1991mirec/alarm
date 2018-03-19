package com.example.miro.alarm.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Process;
import android.renderscript.RSInvalidStateException;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.example.miro.alarm.tabFragments.AlarmFragment;
import com.example.miro.alarm.tabFragments.ContactAlarmFragment;
import com.example.miro.alarm.tabFragments.GPSAlarmFragment;
import com.example.miro.alarm.tabFragments.POIAlarmFragment;
import com.example.miro.alarm.tabFragments.PlaceholderFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private SharedPreferences sharedPreferences;

    private String name = null;
    private static final String PREFS_NAME = "MyPrefsFile";

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final boolean isFirstTime = sharedPreferences.getBoolean("FirstTimeContact", true);
        if (isFirstTime) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = null;
                if (tMgr != null) {
                    Utils.MY_PHONE_NUMBER = tMgr.getLine1Number();
                    deviceId = tMgr.getDeviceId();
                }

                String name = null;
                if (null == Utils.MY_PHONE_NUMBER || "".equals(Utils.MY_PHONE_NUMBER)) {
                    getInfoWithoutNumber(deviceId);
                } else {
                    getInfoWithNumber(deviceId);
                }
            }
        } else {
            Utils.MY_PHONE_NUMBER = sharedPreferences.getString("myPhoneNumber", "");
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            boolean sms_granted = false;
            boolean phone_state_granted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.READ_SMS.equals(permissions[i])) {
                    if (grantResults[i] == 0) {
                        sms_granted = true;
                    }
                } else if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                    if (grantResults[i] == 0) {
                        phone_state_granted = true;
                    }
                }
            }
            if (sms_granted && phone_state_granted) {
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = null;
                if (tMgr != null) {
                    Utils.MY_PHONE_NUMBER = tMgr.getLine1Number();
                    deviceId = tMgr.getDeviceId();
                }

                String name = null;
                if (null == Utils.MY_PHONE_NUMBER || "".equals(Utils.MY_PHONE_NUMBER)) {
                    getInfoWithoutNumber(deviceId);
                } else {
                    getInfoWithNumber(deviceId);
                }
            } else {
                Process.killProcess(Process.myPid());
            }
        } else if (requestCode == 2) {
            for (int i = 0; i > permissions.length; i++) {
                if (Manifest.permission.READ_CONTACTS.equals(permissions[i])) {
                    if (grantResults[i] == -1) {

                        //TODO report problem
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(new AlarmFragment());
                case 1:
                    return PlaceholderFragment.newInstance(new GPSAlarmFragment());
                case 2:
                    return PlaceholderFragment.newInstance(new ContactAlarmFragment());
                case 3:
                    return PlaceholderFragment.newInstance(new POIAlarmFragment());
                default:
            }
            throw new RSInvalidStateException("tab position out of bounds (0-2)!" +
                    " Current position " + position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Alarm";
                case 1:
                    return "GPS Alarm";
                case 2:
                    return "Contact Alarm";
                case 3:
                    return "POI Alarm";
            }
            return null;
        }
    }

    public void getInfoWithNumber(final String deviceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.MY_PHONE_NUMBER = input.getText().toString();
                sendToAPI(deviceId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void getInfoWithoutNumber(final String deviceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome, please provide initial information");

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText phoneNum = new EditText(this);
        final TextView nameView = new TextView(this);
        final TextView phoneView = new TextView(this);
        phoneNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        phoneNum.setLayoutParams(lp2);
        final EditText nameText = new EditText(this);
        nameText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        nameText.setLayoutParams(lp2);
        nameView.setLayoutParams(lp2);
        phoneView.setLayoutParams(lp2);
        nameView.setText("Display name");
        phoneView.setText("Your phone number");
        ll.setLayoutParams(lp);
        ll.addView(phoneView);
        ll.addView(phoneNum);
        ll.addView(nameView);
        ll.addView(nameText);
        builder.setView(ll);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.MY_PHONE_NUMBER = phoneNum.getText().toString();
                sharedPreferences.edit().putString("myPhoneNumber", Utils.MY_PHONE_NUMBER).apply();
                name = nameText.getText().toString();
                sendToAPI(deviceId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Process.killProcess(Process.myPid());
            }
        });

        builder.show();
    }

    private void sendToAPI(final String deviceId) {
        final FusedLocationProviderClient f = new FusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        f.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                double lat = task.getResult().getLatitude();
                double ltd = task.getResult().getLongitude();
                final String latLng = lat + "," + ltd;

                try {
                    JSONObject input = new JSONObject();
                    JSONObject item = new JSONObject();
                    item.put("location", latLng);
                    item.put("number", Utils.MY_PHONE_NUMBER);
                    item.put("name", name);
                    input.put("input", item);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = String.format("%s/createUser", Utils.API_PREFIX);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, input, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getApplicationContext(), "User created successfully",
                                            Toast.LENGTH_SHORT).show();
                                    sharedPreferences.edit()
                                            .putBoolean("FirstTimeContact", false).apply();
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error.networkResponse.statusCode == 409) {
                                        Toast.makeText(getApplicationContext(), "User creation failed because user exist",
                                                Toast.LENGTH_SHORT).show();
                                        sharedPreferences.edit()
                                                .putBoolean("FirstTimeContact", false).apply();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "User creation failed",
                                                Toast.LENGTH_SHORT).show();
                                        Process.killProcess(Process.myPid());
                                    }
                                }
                            });
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    System.out.print("problem");
                }

            }
        });

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE}, 0);
    }
}
