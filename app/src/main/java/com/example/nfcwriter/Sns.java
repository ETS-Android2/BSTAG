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

import org.w3c.dom.Text;

import java.io.IOException;

public class Sns extends AppCompatActivity {

    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    int snsNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(Sns.this);
                mNfcPendingIntent = PendingIntent.getActivity(Sns.this, 0,
                        new Intent(Sns.this, Sns.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                new AlertDialog.Builder(Sns.this).setTitle("Touch tag to write")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create().show();
            }
        });

        findViewById(R.id.instagram).setOnClickListener(myClick);
        findViewById(R.id.facebook).setOnClickListener(myClick);
        findViewById(R.id.youtube).setOnClickListener(myClick);
        findViewById(R.id.tiktok).setOnClickListener(myClick);
        findViewById(R.id.twitter).setOnClickListener(myClick);
        findViewById(R.id.github).setOnClickListener(myClick);

    }
    View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.urlLayout);
            TextView textView = (TextView)findViewById(R.id.urlLink);

            switch (view.getId()) {
                case R.id.instagram:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 1){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("인스타그램 사용자명");
                        snsNumber = 1;
                    }
                    Toast.makeText(getApplicationContext(), "인스타", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.facebook:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 2){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("페이스북 프로필 링크 붙여넣기");
                        snsNumber = 2;
                    }
                    Toast.makeText(getApplicationContext(), "페북", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.youtube:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 3){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("유튜브 링크 붙여넣기");
                        snsNumber = 3;
                    }
                    Toast.makeText(getApplicationContext(), "유튭", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tiktok:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 4){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("틱톡 사용자명");
                        snsNumber = 4;
                    }
                    Toast.makeText(getApplicationContext(), "틱톡", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.twitter:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 5){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("트위터 사용자명");
                        snsNumber = 5;
                    }
                    Toast.makeText(getApplicationContext(), "트윝", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.github:
                    if(linearLayout.getVisibility() == View.VISIBLE && snsNumber == 6){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                        textView.setHint("깃허브 사용자명");
                        snsNumber = 6;
                    }
                    Toast.makeText(getApplicationContext(), "깃헙", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
            TextView url = (TextView)findViewById(R.id.urlLink);
            String snsName = "";

            // instagram
            if(snsNumber == 1){
                snsName = "https://instagram.com/"+url.getText().toString();
            }
            // facebook
            else if(snsNumber == 2){
                if(url.getText().toString().contains("https://www.facebok.com/profile.php?id=")){
                    snsName = url.getText().toString();
                }
                else {
//                    String str = url.getText().toString();
//                    String restr = str.replaceAll("[^0-9]","");
                    snsName = "https://www.facebok.com/profile.php?id=" + url.getText().toString();
                }
            }
            // youtube
            else if(snsNumber == 3){
                snsName = "https://youtu.be.com/"+url.getText().toString();
            }
            // tiktok
            else if(snsNumber == 4){
                snsName = "https://tiktok.com/@"+url.getText().toString();
            }
            // twitter
            else if(snsNumber == 5){
                snsName = "https://www.twitter.com/"+url.getText().toString();
            }
            // github
            else if(snsNumber == 6){
                snsName = "https://github.com/"+url.getText().toString();
            }

            NdefRecord record = NdefRecord.createUri(snsName);
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });
            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, "기록 성공! : SNS 프로필 링크가 기록되었습니다. ", Toast.LENGTH_LONG)
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
}