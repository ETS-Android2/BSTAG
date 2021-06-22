package com.example.nfcwriter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ImageButton)findViewById(R.id.btn1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyApp.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Web.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Contact.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Call.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Map.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Etiquette.class);
                startActivity(intent);
            }
        });

        ((ImageButton)findViewById(R.id.btn8)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.allthatmobilenfc");
                startActivity( launchIntent );
            }
        });



    }


}