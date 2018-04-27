package com.github.winterweird.jpractice.dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.github.winterweird.jpractice.R;

public class AddFromCSVDialog extends DialogFragment {
    private String title = "Add data from CSV";
    private String confirmText = "Add data";
    private String cancelText = "Cancel";
    private boolean pasteMode = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity act = getActivity();
        LayoutInflater inflater = act.getLayoutInflater();
        
        final View v = inflater.inflate(R.layout.add_from_csv_dialog, null);
        
        // View children
        final EditText pathSelection = v.findViewById(R.id.addFromCSVPathSelection);
        final EditText pasteBox = v.findViewById(R.id.addFromCSVPasteBox);
        final Button changeMode = v.findViewById(R.id.addFromCSVChangeModeButton);
        
        return new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setView(v)
            .setPositiveButton(confirmText, (dialog, id) -> {
                String s;
                if (pasteMode) s = pasteBox.getText().toString();
                else           s = pathSelection.getText().toString();
                // TODO: use s
                Log.d("Test", "Text is: " + s);
            })
            .setNegativeButton(cancelText, (dialog, id) -> {})
            .create();
    }

    public void addDataToDatabase() {
        Log.d("Test", "confirm button clicked");
    }
}
