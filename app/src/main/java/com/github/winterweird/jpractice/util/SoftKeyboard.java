package com.github.winterweird.jpractice.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.content.Context;

/**
 * Utility class for managing the soft keyboard.
 */
public class SoftKeyboard {
    /**
     * Utility method for hiding the soft keyboard (called from actvitiy).
     *
     * Source: https://stackoverflow.com/a/17789187/4498826 (excellent read)
     */
    public static void hide(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
            // alternative methods: something involving getRootView()
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Utility method for hiding the soft keyboard (called from fragment or smth).
     * 
     * Source: https://stackoverflow.com/a/17789187/4498826 (again)
     *
     * Typical usage (I think): SoftKeyboard.hide(getContext(), someView)
     */
    public static void hide(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
