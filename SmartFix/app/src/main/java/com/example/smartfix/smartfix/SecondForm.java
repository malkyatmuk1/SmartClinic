package com.example.smartfix.smartfix;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SecondForm extends Activity {

    Boolean isAndroid=false;
    String text;
    EditText problem,model;
    RadioGroup radioGroup;
    RadioButton radioButtonAndroid,radioButtonIos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_form);
        ImageButton next= (ImageButton) findViewById(R.id.buttonEmail);
         radioButtonAndroid= (RadioButton) findViewById(R.id.radio_android);
         radioButtonIos = (RadioButton) findViewById(R.id.radio_ios);
         radioGroup=(RadioGroup) findViewById(R.id.radio);
         problem= (EditText) findViewById(R.id.problem);
         model= (EditText) findViewById(R.id.model);

        next.setOnClickListener(emailListener);
        radioGroup.setOnCheckedChangeListener(radioListener);
        problem.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() ==R.id.problem) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

    }

    View.OnClickListener emailListener=new View.OnClickListener() {

        public void onClick(View view) {

            if(isAndroid) text = "Android - " + model.getText().toString()+"\n" ;
            else text="IOS  - "+ model.getText().toString() + "\n";
            text= text+ problem.getText().toString()+ "\n";
            String address = "tanya.naidenova@abv.bg";
            composeEmail(address, Form.getName(),text);



        }
    };
    public void composeEmail(String address, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/refc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        intent.putExtra(Intent.EXTRA_TEXT,text);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent,"Choose email app"));
        }
    }
    RadioGroup.OnCheckedChangeListener radioListener= new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            onRadioButtonClicked(radioButtonAndroid);

        }
    };
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_android:
                if (checked)
                   isAndroid=true;
                    break;
            case R.id.radio_ios:
                if (checked)
                    isAndroid=false;
                    break;
        }
    }
}