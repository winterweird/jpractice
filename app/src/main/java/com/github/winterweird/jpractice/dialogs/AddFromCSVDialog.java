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
import android.widget.Toast;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.adapters.NamedListsAdapter;

public class AddFromCSVDialog extends DialogFragment {
    private String title = "Add data from CSV";
    private String confirmText = "Add data";
    private String cancelText = "Cancel";
    private boolean pasteMode = true;
    private Callback callback;

    public AddFromCSVDialog(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity act = getActivity();
        LayoutInflater inflater = act.getLayoutInflater();
        
        final View v = inflater.inflate(R.layout.add_from_csv_dialog, null);
        
        // View children
        final EditText pathSelection = v.findViewById(R.id.addFromCSVPathSelection);
        final EditText pasteBox = v.findViewById(R.id.addFromCSVPasteBox);
        final Button changeMode = v.findViewById(R.id.addFromCSVChangeModeButton);
        changeMode.setOnClickListener(view -> {
            pasteMode = !pasteMode;
            if (pasteMode) {
                changeMode.setText(act.getString(
                            R.string.addFromCSVChangeModeToFileSelectionButtonText));
                pasteBox.setVisibility(View.VISIBLE);
                pathSelection.setVisibility(View.GONE);
            }
            else {
                changeMode.setText(act.getString(
                            R.string.addFromCSVChangeModeToPasteBoxButtonText));
                pasteBox.setVisibility(View.GONE);
                pathSelection.setVisibility(View.VISIBLE);
            }
        });
        
        return new AlertDialog.Builder(act, R.style.AlertDialogTheme)
            .setTitle(title)
            .setView(v)
            .setPositiveButton(confirmText, (dialog, id) -> {
                String s;
                if (pasteMode) {
                    s = pasteBox.getText().toString();
                }
                else {
                    s = pathSelection.getText().toString();
                    throw new IllegalStateException("Path selection not implemented");
                }
                
                DatabaseHelper dbhelper = DatabaseHelper.getHelper(act);
                DatabaseHelper.DbUpdateResult res = dbhelper.insertFromCSVText(s);
                
                callback.callback(res);

                Toast.makeText(act, res.toString(), Toast.LENGTH_LONG).show();
            })
            .setNegativeButton(cancelText, (dialog, id) -> {})
            .create();
    }

    public void addDataToDatabase() {
        Log.d("Test", "confirm button clicked");
    }

    /**
     * Called after updating the database.
     */
    public static interface Callback {
        void callback(DatabaseHelper.DbUpdateResult res);
        
    }
}
