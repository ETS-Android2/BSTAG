package com.example.nfcwriter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class Etiquette extends AppCompatActivity {

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etiquette);

        final ImageButton vibe = (ImageButton)findViewById(R.id.vibeBtn);
        final ImageButton mute = (ImageButton)findViewById(R.id.muteBtn);
        final RadioButton vibrate_check = (RadioButton) findViewById(R.id.vibrate_checked);
        final RadioButton silent_check = (RadioButton) findViewById(R.id.silent_checked);

        vibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.setImageResource(R.drawable.vibe_on);
                mute.setImageResource(R.drawable.mute_off);
                vibrate_check.setChecked(true);
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.setImageResource(R.drawable.vibe_off);
                mute.setImageResource(R.drawable.mute_on);
                silent_check.setChecked(true);
            }
        });


        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(Etiquette.this);
                mNfcPendingIntent = PendingIntent.getActivity(Etiquette.this, 0,
                        new Intent(Etiquette.this, Etiquette.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                new AlertDialog.Builder(Etiquette.this).setTitle("Touch tag to write").setMessage("NFC 태그를 센서에 가까이 대 주세요.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create().show();
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

            RadioButton vibrate_check = (RadioButton) findViewById(R.id.vibrate_checked);
            RadioButton silent_check = (RadioButton) findViewById(R.id.silent_checked);

            if(vibrate_check.isChecked()){
                NdefRecord record = NdefRecord.createUri("totalknfc://vibrate");

                NdefMessage message = new NdefMessage(new NdefRecord[] { record });

                if (writeTag(message, detectedTag)) {
                    Toast.makeText(this, "기록 성공! : 진동 모드 설정이 기록되었습니다. ", Toast.LENGTH_LONG)
                            .show();
                }
            }else if (silent_check.isChecked()){
                setting();

                NdefRecord record = NdefRecord.createUri("totalknfc://silent");

                NdefMessage message = new NdefMessage(new NdefRecord[] { record });

                if (writeTag(message, detectedTag)) {
                    Toast.makeText(this, "기록 성공! : 무음 모드 설정이 기록되었습니다. ", Toast.LENGTH_LONG)
                            .show();
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setting(){

        NotificationManager notificationManager;
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!notificationManager.isNotificationPolicyAccessGranted()) {

            Toast.makeText(getApplicationContext(), "nfcWriter에 권한을 허용해주세요", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
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

    public void goBack(View view){
        finish();
    }
}