package com.github.winterweird.jpractice.util;

import android.os.Looper;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    static Toast toast = null;
    public static void show(Context context, String text) {
        try {
            if (toast != null) {
                toast.setText(text);
            }
            else {
                toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            }
            toast.show();
        }
        catch (Exception e) {
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }
}
