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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class Contact extends AppCompatActivity {

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    boolean isName = false;
    boolean isCom = false;
    boolean isPhone = false;
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Button main_btn = (Button)findViewById(R.id.button);
        EditText edit_name = (EditText)findViewById(R.id.name);
        EditText edit_com = (EditText)findViewById(R.id.comName);
        EditText edit_phone = (EditText)findViewById(R.id.phoneNum);
        EditText edit_email = (EditText)findViewById(R.id.mailAddress);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        //모든 form 작성 시에만 버튼이 활성화되도록 설정
        clickableBtn(edit_name, edit_com, edit_phone, edit_email, main_btn);


    }

    public void actionBtn(Button btn){
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(Contact.this);
                mNfcPendingIntent = PendingIntent.getActivity(Contact.this, 0,
                        new Intent(Contact.this, Contact.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                new AlertDialog.Builder(Contact.this).setTitle("Touch tag to write").setMessage("NFC 태그를 핸드폰 뒷면에 가까이하세요.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create().show();
            }
        });
    }


    public void clickableBtn(EditText name, EditText com, EditText phone, EditText email, final Button btn){


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    isName = true;
                    if(isName && isCom && isPhone && isEmail){
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius_dark));
                        actionBtn(btn);
                    }
                }else{
                    isName = false;
                    btn.setClickable(false);
                    btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                }
            }

        });

        com.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    isCom = true;
                    if(isName && isCom && isPhone && isEmail){
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius_dark));
                        actionBtn(btn);
                    }else{
                        btn.setClickable(false);
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                    }
                }else{
                    isCom = false;
                    btn.setClickable(false);
                    btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                }
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    isPhone = true;
                    if(isName && isCom && isPhone && isEmail){
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius_dark));
                        actionBtn(btn);
                    }else{
                        btn.setClickable(false);
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                    }
                }else{
                    isPhone = false;
                    btn.setClickable(false);
                    btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                }

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    isEmail = true;
                    if(isName && isCom && isPhone && isEmail){
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius_dark));
                        actionBtn(btn);
                    }else{
                        btn.setClickable(false);
                        btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
                    }
                }else{
                    isEmail = false;
                    btn.setClickable(false);
                    btn.setBackground(ContextCompat.getDrawable(Contact.this, R.drawable.button_radius));
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

            String nameVcard = "BEGIN:VCARD\nVERSION:3.0\nFN:"+((TextView)findViewById(R.id.name)).getText().toString()+"\nORG:"+((TextView)findViewById(R.id.comName)).getText().toString()+"\nTEL:"+((TextView)findViewById(R.id.phoneNum)).getText().toString()+"\nEMAIL:"+((TextView)findViewById(R.id.mailAddress)).getText().toString()+"\nEND:VCARD";
            byte[] uriField = nameVcard.getBytes(Charset.forName("UTF-8"));
            byte[] payload = new byte[uriField.length+1];

            System.arraycopy(uriField, 0, payload, 1, uriField.length);


            NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });
            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, "기록 성공! : 연락처가 기록되었습니다. ", Toast.LENGTH_LONG)
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