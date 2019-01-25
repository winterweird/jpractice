package com.github.winterweird.jpractice.util;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;

/**
 * Utility class which can be used to find a file with a given callback once the
 * file is found.
 */
public class FilePicker {
    private Activity context; // the context for this specific file picker
    private static FilePicker fp; // a static file picker that may be trouble
    
    // this allows us to keep track of the continuation we intend to execute
    // once the file is found, and call it in the actual activity which handles
    // the result
    private static Callback mostRecentCallback;

    /**
     * Get a previously instantiated FilePicker instance.
     *
     * @return the previously constructed instance FilePicker instance
     * @throws IllegalStateException if called without the FilePicker being
     * instantiated
     */
    public static FilePicker getInstance() {
        if (fp == null) {
            throw new IllegalStateException("FilePicker instance context not set");
        }
        return fp;
    }
    
    /**
     * Get a FilePicker instance.
     *
     * This method must be called once with arguments to "prime" the FilePicker
     * instance.
     *
     * NOTE: Potential problem I see just from reading this is that there may be a
     * situation where the filepicker is instantiated with a context that has
     * expired. Should probably fix that.
     *
     * @param context The context to instantiate the FilePicker with (only
     * applicable on first call)
     * @return a new instance if none exists, otherwise return the previously
     * constructed instance.
     */
    public static FilePicker getInstance(Activity context) {
        if (fp == null) {
            fp = new FilePicker(context);
        }
        return fp;
    }
    
    /**
     * Private constructor.
     */
    private FilePicker(Activity context) {
        this.context = context;
    }

    /**
     * Find a file and specify the callback that should be executed given the
     * data once the file is found.
     *
     * @param callback The callback to be executed once the resource has been
     * procured
     */
    public void findFile(Callback callback) {
        mostRecentCallback = callback;
        Intent intent = new Intent(context, FilePickerActivity.class);
        context.startActivity(intent);
    }

    /**
     * Interface specifying a callback given a file Uri.
     */
    public static interface Callback {
        /**
         * The callback to be executed.
         * @param filename The Uri of the file resource to open
         */
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

