package com.example.miro.alarm.inteligentAlarm.adapters.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.alarmSettings.impl.GPSAlarmSettingsImpl;
import com.example.miro.alarm.inteligentAlarm.helper.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener {

    private GoogleMap mMap;
    private Marker marker;
    private String lengthType = "km";
    private Circle circle;
    private GPSAlarmSettingsImpl settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Intent intent = getIntent();
        settings = (GPSAlarmSettingsImpl) intent.getExtras().get("setting");
        final TextView textRadius = (TextView) findViewById(R.id.mapEditText);
        findViewById(R.id.mapButtonOK).setOnClickListener(this);
        findViewById(R.id.mapButtonCancel).setOnClickListener(this);


        textRadius.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (null != marker) {
                    setCircle(marker.getPosition());
                }
                return true;

            }
        });
        final Spinner lengthTypeSpinner = (Spinner) findViewById(R.id.mapSpinner);
        String[] items = new String[]{"km", "m"};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lengthTypeSpinner.setAdapter(adapter);
        lengthTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lengthType = parent.getItemAtPosition(position).toString();
                if (null != marker) {
                    setCircle(marker.getPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mapButtonCancel:
                setResult(RESULT_CANCELED);
                super.finish();
                break;
            case R.id.mapButtonOK:
                if (null == marker) {
                    setResult(RESULT_CANCELED);
                } else {
                    Intent output = new Intent();
                    output.putExtra("radius", getRadiusInMeters());
                    output.putExtra("latitude", marker.getPosition().latitude);
                    output.putExtra("longitude", marker.getPosition().longitude);
                    setResult(RESULT_OK, output);
                }
                super.finish();
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.requestAccessFinePermissions(this);
            return;
        }
        mMap.setMyLocationEnabled(true);
        final FusedLocationProviderClient f = new FusedLocationProviderClient(this);

        f.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                double lat = task.getResult().getLatitude();
                double ltd = task.getResult().getLongitude();
                final LatLng latLng = new LatLng(lat, ltd);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setButton(latLng);
            }
        });
        final TextView textRadius = (TextView) findViewById(R.id.mapEditText);
        textRadius.setText(String.valueOf((double) settings.getRadius() / 1000));
        setButton(settings.getCoordinates().get(0));
    }

    private void setButton(final LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }

        marker = mMap.addMarker(new MarkerOptions().position(latLng));
        setCircle(latLng);
    }

    private void setCircle(final LatLng latLng) {
        if (null != circle) {
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(getRadiusInMeters())
                    .strokeWidth(0.5f)
                    .fillColor(0x550000FF));
    }

    private int getRadiusInMeters() {
        final TextView textRadius = (TextView) findViewById(R.id.mapEditText);
        int radius = (int) Double.parseDouble(textRadius.getText().toString());
        if ("km".equals(lengthType)) {
            return radius * 1000;
        }

        return radius;
    }

    public LatLng getLatLng() {
        return marker.getPosition();
    }

}
