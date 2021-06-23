package com.example.nfcwriter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

public class AudioMode2 extends AppCompatActivity {

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_mode);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // 무음모드
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL || audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            // 무음모드로 변경
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //벨소리모드로 변경
    //audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); //진동모드로 변경
    //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); //무음모드로 변경

}