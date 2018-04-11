package com.example.smartfix.smartfix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Form extends Activity {

    static EditText name, number, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        Button next= (Button) findViewById(R.id.button);
        name= (EditText) findViewById(R.id.name);
        number= (EditText) findViewById(R.id.number);
        email= (EditText) findViewById(R.id.email);
        if(Global.name!=null) name.setText(Global.name.toString());
        if(Global.phone!=null) number.setText(Global.phone.toString());
        if(Global.email!=null) email.setText(Global.email);
        next.setOnClickListener(nextListener);

    }
    View.OnClickListener nextListener=new View.OnClickListener() {

        public void onClick(View view) {
            Intent i= new Intent(Form.this,SecondForm.class);
            if(name!=null) Global.name=name.getText().toString();
            if(email!=null)Global.email=email.getText().toString();
            if(number!=null)Global.phone=number.getText().toString();
            startActivity(i);
            finish();

        }
    };
    @NonNull
    public static String getName()
    {
        return name.getText().toString();
    }
    public String getNumber()
    {
        return number.getText().toString();
    }

}
