package com.github.winterweird.jpractice.util;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

/**
 * An activity which may be started by the FilePicker util class in order to
 * retrieve a result file from a file dialog and call the callback on the given
 * result file.
 */
public class FilePickerActivity extends Activity {
    // NOTE: These do not refer to the same category of things, so they can have
    // identical numbers
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 1;
    public static final int READ_REQUEST_CODE = 1;
    
    /**
     * Requests permissions to read external storage upon creation.
     *
     * @param savedInstanceState The previously saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get permission to read external storage, or get previous answer to
        // request
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                PERMISSION_READ_EXTERNAL_STORAGE); 
        
    }

    /**
     * After permissions have been granted, open a file picker dialog that
     * allows the user to select a document.
     *
     * NOTE: Does not check actual permissions(?) TODO: Test
     *
     * @param rc The request code for the type of permissions we are requesting
     * @param permissions The requested permissions
     * @param res The results of the permission requests
     * (PERMISSION_GRANTED|PERMISSION_DENIED) (not checked)
     */
    @Override
    public void onRequestPermissionsResult(int rc, String permissions[], int[] res) {
        // open any document; we're not picky
        // NOTE: In practice this will be CSV data
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        // request code in return result identifies the kind of request the
        // result is an answer to
        this.startActivityForResult(intent, READ_REQUEST_CODE);
    }
    
    /**
     * Once the result of the open document action has been determined, if it is
     * a success, execute the callback specified when calling
     * FilePicker.findFile().
     *
     * @param requestCode The request code for the result we wanted to get
     * @param resultCode The result code returned by the child activity using
     * setResult
     * @param data The result data (specified by the child activity)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == READ_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                FilePicker.Callback callback = FilePicker.getMostRecentCallback();
                callback.callback(data.getData());
            }
            finish(); // finish anyway
        }
    }
}
