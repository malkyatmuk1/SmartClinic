package com.example.smartfix.smartfix;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SecondForm extends AppCompatActivity implements LocationListener {

    Boolean isAndroid = false;
    String text;
    EditText problem, model;
    RadioGroup radioGroup;
    RadioButton radioButtonAndroid, radioButtonIos;
    LocationManager locationManager;

    Double lat, lng;
    String cityName, countryName, stateName;
    Geocoder geocoder;
    private LocationListener locationListener;

    Intent intentThatCalled;
    public double latitude;
    public double longitude;

    public Criteria criteria;
    public String bestProvider;

    String voice2text; //added
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_form);
        Button next = (Button) findViewById(R.id.button);
        Button back= (Button) findViewById(R.id.buttonBack);
        radioButtonAndroid = (RadioButton) findViewById(R.id.radio_android);
        radioButtonIos = (RadioButton) findViewById(R.id.radio_ios);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        problem = (EditText) findViewById(R.id.problem);
        model = (EditText) findViewById(R.id.model);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        next.setOnClickListener(emailListener);
        back.setOnClickListener(backListener);
        radioGroup.setOnCheckedChangeListener(radioListener);
        getLocation();


    }
    View.OnClickListener backListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(SecondForm.this, Form.class);
            startActivity(i);
            finish();
        }
    };
    View.OnClickListener emailListener = new View.OnClickListener() {

        public void onClick(View view) {

            if (isAndroid) text = "Android - " + model.getText().toString() + "\n";
            else text = "IOS  - " + model.getText().toString() + "\n";
            text = text + problem.getText().toString() + "\n";
            text = text + cityName + " " + countryName + " " + stateName + "\n";
            String address = "tanya.naidenova@abv.bg";
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            composeEmail(address, Form.getName(), text);


        }
    };


    public void composeEmail(String address, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/refc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Choose email app"));
        }
    }

    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            onRadioButtonClicked(radioButtonAndroid);

        }
    };

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_android:
                if (checked)
                    isAndroid = true;
                break;
            case R.id.radio_ios:
                if (checked)
                    isAndroid = false;
                break;
        }
    }
    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    protected void getLocation() {
        if (isLocationEnabled(SecondForm.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                lat = location.getLatitude();
                lng = location.getLongitude();
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    if (addresses.size() > 0) {
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        //Toast.makeText(getApplicationContext(),stateName , 1).show();
                        countryName = addresses.get(0).getAddressLine(2);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else {
                //This is what you need:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(SecondForm.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        searchNearestPlace(voice2text);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void searchNearestPlace(String v2txt) {
        //.....
    }

}
