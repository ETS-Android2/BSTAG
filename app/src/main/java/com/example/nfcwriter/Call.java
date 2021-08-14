package com.example.nfcwriter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Call extends AppCompatActivity {

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        EditText main_edit = (EditText)findViewById(R.id.telNum);
        final Button main_btn = (Button)findViewById(R.id.button);

        main_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    main_btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mNfcAdapter = NfcAdapter.getDefaultAdapter(Call.this);
                            mNfcPendingIntent = PendingIntent.getActivity(Call.this, 0,
                                    new Intent(Call.this, Call.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                            enableTagWriteMode();

                            new AlertDialog.Builder(Call.this).setTitle("Touch tag to write").setMessage("NFC 태그를 핸드폰 뒷면에 가까이하세요.")
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            disableTagWriteMode();
                                        }

                                    }).create().show();
                        }
                    });

                    main_btn.setBackground(ContextCompat.getDrawable(Call.this, R.drawable.button_radius_dark));
                } else {
                    main_btn.setClickable(false);
                    main_btn.setBackground(ContextCompat.getDrawable(Call.this, R.drawable.button_radius));
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
            NdefRecord record = NdefRecord.createUri("tel:"+((TextView)findViewById(R.id.telNum)).getText().toString());
            //NdefRecord record = NdefRecord.createUri("https://poplme.co/"+((TextView)findViewById(R.id.uri)).getText().toString());
            //NdefRecord record = NdefRecord.createMime( ((TextView)findViewById(R.id.mime)).getText().toString(), ((TextView)findViewById(R.id.value)).getText().toString().getBytes());
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });
            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, "기록 성공! : 전화번호가 기록되었습니다. ", Toast.LENGTH_LONG)
                        .show();
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

    public void goBack(View view){
        finish();
    }
}