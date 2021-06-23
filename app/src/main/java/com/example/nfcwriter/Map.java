package com.example.nfcwriter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Map extends AppCompatActivity {

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setLocation();

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(Map.this);
                mNfcPendingIntent = PendingIntent.getActivity(Map.this, 0,
                        new Intent(Map.this, Map.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                new AlertDialog.Builder(Map.this).setTitle("Touch tag to write").setMessage("NFC 태그를 핸드폰 뒷면에 가까이하세요.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create().show();
            }
        });


        ((Button) findViewById(R.id.btn_location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linear_loc = (LinearLayout)findViewById(R.id.linear_location);
                Button button = (Button)findViewById(R.id.go_map);

                LinearLayout linear_addr = (LinearLayout)findViewById(R.id.linear_address);


                if(linear_loc.getVisibility() == View.GONE){
                    linear_loc.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    linear_addr.setVisibility(View.GONE);
                }else{
                    linear_loc.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }


            }
        });

        ((Button) findViewById(R.id.btn_address)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linear_addr = (LinearLayout)findViewById(R.id.linear_address);

                LinearLayout linear_loc = (LinearLayout)findViewById(R.id.linear_location);
                Button button = (Button)findViewById(R.id.go_map);

                if(linear_addr.getVisibility() == View.GONE){
                    linear_addr.setVisibility(View.VISIBLE);
                    linear_loc.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }else{
                    linear_addr.setVisibility(View.GONE);
                }

            }
        });



    }


    //tagwrite 모드 활성화
    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[] { tagDetected };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }
    //tagwrite 모드 비활성화
    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            LinearLayout linear_location = (LinearLayout)findViewById(R.id.linear_location);
            LinearLayout linear_address = (LinearLayout)findViewById(R.id.linear_address);

            if(linear_location.getVisibility() == View.VISIBLE){
                NdefRecord record = NdefRecord.createUri("geo:0,0?q="+((TextView)findViewById(R.id.edit_lat)).getText().toString()+","+((TextView)findViewById(R.id.edit_lng)).getText().toString());
                NdefMessage message = new NdefMessage(new NdefRecord[] { record });
                if (writeTag(message, detectedTag)) {
                    Toast.makeText(this, "기록 성공! : 지도 정보(좌표)가 기록되었습니다. ", Toast.LENGTH_LONG)
                            .show();
                }
            }else if(linear_address.getVisibility() == View.VISIBLE){
                NdefRecord record = NdefRecord.createUri("geo:0,0?q="+((TextView)findViewById(R.id.edit_addr)).getText().toString());
                NdefMessage message = new NdefMessage(new NdefRecord[] { record });
                if (writeTag(message, detectedTag)) {
                    Toast.makeText(this, "기록 성공! : 지도 정보(주소)가 기록되었습니다. ", Toast.LENGTH_LONG)
                            .show();
                }
            }else{
                Toast.makeText(this, "ERR : 지도 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
     * Writes an NdefMessage to a NFC tag
     */
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag not writable",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag too small",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void goGoogleMap(View view) {
        Intent intent = new Intent(Map.this, GoogleMapView.class);
        startActivity(intent);
    }

    public void setLocation(){
        Intent intent = getIntent();
        String lat = intent.getStringExtra("lat");
        String lng = intent.getStringExtra("lng");

        if(lat != null){
            LinearLayout linear_loc = (LinearLayout)findViewById(R.id.linear_location);
            Button button = (Button)findViewById(R.id.go_map);

            linear_loc.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        EditText editLat = (EditText)findViewById(R.id.edit_lat);
        EditText editLng = (EditText)findViewById(R.id.edit_lng);
        editLat.setText(lat);
        editLng.setText(lng);
    }

}