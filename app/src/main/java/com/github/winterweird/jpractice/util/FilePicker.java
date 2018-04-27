package com.github.winterweird.jpractice.util;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;

public class FilePicker {
    private Activity context;
    private static FilePicker fp;
    private static Callback mostRecentCallback;

    public static FilePicker getInstance() {
        if (fp == null) {
            throw new IllegalStateException("FilePicker instance context not set");
        }
        return fp;
    }
    
    public static FilePicker getInstance(Activity context) {
        if (fp == null) {
            fp = new FilePicker(context);
        }
        return fp;
    }
    
    private FilePicker(Activity context) {
        this.context = context;
    }

    public void findFile(Callback callback) {
        mostRecentCallback = callback;
        Intent intent = new Intent(context, FilePickerActivity.class);
        context.startActivity(intent);
    }

    public static interface Callback {
        void callback(Uri filename);
    }


    /**
     * Helper method: make callback accessible from newly started intent.
     *
     * @return The most recently callback as set by FilePicker.findFile()
     */
    static Callback getMostRecentCallback() {
        return mostRecentCallback;
    }
}

