package com.github.winterweird.jpractice.util;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

public class FilePickerActivity extends Activity {
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 1;
    public static final int READ_REQUEST_CODE = 1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                PERMISSION_READ_EXTERNAL_STORAGE); 
        
    }

    @Override
    public void onRequestPermissionsResult(int rc, String permissions[], int[] res) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        this.startActivityForResult(intent, READ_REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == READ_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                FilePicker.Callback callback = FilePicker.getMostRecentCallback();
                callback.callback(data.getData());
                finish();
            }
        }
    }
}
