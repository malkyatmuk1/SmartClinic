package com.example.smartfix.smartfix;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.smartfix.smartfix.Global.address2;

public class SecondForm extends AppCompatActivity{

    Boolean isAndroid = false,isWindows=false;
    String text;
    EditText problem, model, userLocation;
    RadioGroup radioGroup;
    RadioButton radioButtonAndroid, radioButtonIos;
    LocationManager locationManager;

    CheckBox checkBox;

    Double lat, lng;
    String cityName;
    Geocoder geocoder;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private LocationListener locationListener;
    private String emailAndroidAndWindows="info@smartclinic.pt", emailIOS="geral@iloja.pt", address;

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
        ImageButton back= (ImageButton) findViewById(R.id.buttonBack);
        radioButtonAndroid = (RadioButton) findViewById(R.id.radio_android);
        radioButtonIos = (RadioButton) findViewById(R.id.radio_ios);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        problem = (EditText) findViewById(R.id.problem);
        model = (EditText) findViewById(R.id.model);
        userLocation = (EditText) findViewById(R.id.custom_address);
        checkBox = (CheckBox) findViewById(R.id.current_location);
        model.setSelected(false);
        if(Global.problem!=null) problem.setText(Global.problem);
        if(Global.model!=null) model.setText(Global.model);
        if(Global.os!=null && Global.os==true) radioButtonAndroid.setChecked(true);
        else radioButtonIos.setChecked(true);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        next.setOnClickListener(emailListener);
        back.setOnClickListener(backListener);
        radioGroup.setOnCheckedChangeListener(radioListener);
        userLocation.setVisibility(View.GONE);

        getAddress();
    }



    View.OnClickListener backListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(SecondForm.this, Form.class);
            Global.model=model.getText().toString();
            Global.problem=problem.getText().toString();
            startActivity(i);
            finish();
        }
    };
    @Override
    public void onBackPressed() {
           Intent i = new Intent(SecondForm.this, Form.class);
           Global.model=model.getText().toString();
           Global.problem=problem.getText().toString();
           startActivity(i);
           finish();
        }
        public boolean customLocation(View view) {
            boolean enabled = ((CheckBox) view).isChecked();
            if (enabled = false) {
                userLocation.setVisibility(View.VISIBLE);
                Global.address2 = userLocation.getText().toString();
                return true;
            } else {
                return false;
            }

        }


    View.OnClickListener emailListener = new View.OnClickListener() {

        public void onClick(View view) {
            if (isAndroid) text = "OS: Android - " + model.getText().toString() + "\n";
            else if(isWindows)text = "OS: Windows phone - " + model.getText().toString() + "\n";
            else text = "OS: IOS - " + model.getText().toString() + "\n";
            text = text +"Problem: " + problem.getText().toString() + "\n";
            if(customLocation(view))
            {
                text = text + "Address: " + Global.address2 +"\n";
            }
            else {
                text = text + "Address: " + cityName + "\n";
            }
            text = text + "Person: " + Global.name + " - "+ Global.phone + "\n";
            if(isAndroid || isWindows)
                 address = emailAndroidAndWindows;
            else address=emailIOS;
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
                Global.os=true;
                break;
            case R.id.radio_ios:
                if (checked)
                    isAndroid = false;
                Global.os=true;
                break;
        }
    }
    protected void getAddress() {


        SharedPreferences prefs = getSharedPreferences("Loc", MODE_PRIVATE);

            Global.lat= Double.parseDouble(prefs.getString("lat",null));
         Global.lng= Double.parseDouble(prefs.getString("lng",null));
         Log.e("l",Global.lat + " " + Global.lng);

        if (Global.lng != null && Global.lat != null) {
                Log.e("TAG", "GPS is on");

                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(Global.lat, Global.lng, 1);
                    if (addresses.size() > 0) {
                        cityName = addresses.get(0).getAddressLine(0);
               //         stateName = addresses.get(0).getAddressLine(1);
                        //Toast.makeText(getApplicationContext(),stateName , 1).show();
                 //       countryName = addresses.get(0).getAddressLine(2);
                    }
                } catch (IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

    }

