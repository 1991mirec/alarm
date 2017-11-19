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

import com.example.miro.alarm.R;
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

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String phoneNumber = null;
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
        final SharedPreferences.Editor editor = sharedPreferences.edit()
                .putBoolean("FirstTimeContact", true);
        editor.apply();
        final boolean isFirstTime = sharedPreferences.getBoolean("FirstTimeContact", true);
        if (isFirstTime) {

            String deviceId = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,
                                Manifest.permission.READ_PHONE_STATE}, 0);
            }
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (tMgr != null) {
                phoneNumber = tMgr.getLine1Number();
                deviceId = tMgr.getDeviceId();
            }

            String name = null;
            if (null == phoneNumber || "".equals(phoneNumber)) {
                getInfoWithoutNumber(deviceId);
            } else {
                getInfoWithNumber(deviceId);
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
                    " Current position "+ position);
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
                phoneNumber = input.getText().toString();
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
                phoneNumber = phoneNum.getText().toString();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        final FusedLocationProviderClient f = new FusedLocationProviderClient(this);

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
                    item.put("phone-number", phoneNumber);
                    item.put("name", name);
                    item.put("device-id", deviceId);
                    input.put("input", item);
                    final String message = input.toString();
                    // TODO http request with message
                } catch (JSONException e) {
                    System.out.print("problem");
                }
                final SharedPreferences.Editor editor = sharedPreferences.edit()
                        .putBoolean("FirstTimeContact", false);
                editor.apply();
            }
        });

    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0);
    }
}
