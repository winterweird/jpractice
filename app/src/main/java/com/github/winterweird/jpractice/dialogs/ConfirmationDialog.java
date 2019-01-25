package com.github.winterweird.jpractice.dialogs;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.annotation.SuppressLint;

import com.github.winterweird.jpractice.R;

@SuppressLint("ValidFragment")
public class ConfirmationDialog extends DialogFragment {
    String title;
    String msg;
    String confirmText;
    String cancelText;
    DialogInterface.OnClickListener positiveClickListener;

    @SuppressLint("ValidFragment")
    public ConfirmationDialog(String title, String msg, String confirmText, String cancelText,
            DialogInterface.OnClickListener positiveClickListener) {
        this.title = title;
        this.msg = msg;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.positiveClickListener = positiveClickListener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(confirmText, positiveClickListener)
            .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {}
            }).create();
    }
}
