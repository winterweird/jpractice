package com.github.winterweird.jpractice.dialogs;

import android.content.ContentResolver;
import android.os.Handler;
import android.content.Context;
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
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;

import java.io.IOException;
import java.io.File;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.util.FilePicker;
import com.github.winterweird.jpractice.util.FileUtils;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.adapters.NamedListsAdapter;

public class AddFromCSVDialog extends DialogFragment {
    private String title = "Add data from CSV";
    private String confirmText = "Add data";
    private String cancelText = "Cancel";
    private boolean pasteMode = true;
    private Callback callback;
    private Uri uri;
    private EditText pathSelection;
    private Handler handler = new Handler();

    public AddFromCSVDialog(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity act = getActivity();
        LayoutInflater inflater = act.getLayoutInflater();
        
        final View v = inflater.inflate(R.layout.add_from_csv_dialog, null);
        
        // View children
        pathSelection = v.findViewById(R.id.addFromCSVPathSelection);
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

        pathSelection.setOnClickListener(view ->
                FilePicker.getInstance().findFile(s -> selectPath(s)));
        
        return new AlertDialog.Builder(act, R.style.AlertDialogTheme)
            .setTitle(title)
            .setView(v)
            .setPositiveButton(confirmText, (dialog, id) -> {
                String s;
                if (pasteMode) {
                    s = pasteBox.getText().toString();
                    if (s.replaceAll("\\s+", "").isEmpty()) {
                        s = null;
                    }
                }
                else {
                    try {
                        if (uri == null) {
                            Toast.makeText(act, "No resource selected", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ContentResolver resolver = act.getContentResolver();
                        s = FileUtils.getContents(resolver.openInputStream(uri));
                        Log.d("Test", s);
                    } catch (IOException e) {
                        Log.e("Test", e.getMessage());
                        s = null;
                    }
                }
                
                if (s == null) {
                    Toast.makeText(act, "Couldn't add data: invalid resource",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                final String csvText = s;
                
                final Context appctx = act.getApplicationContext();
                Toast.makeText(appctx, "Adding entries to database...", Toast.LENGTH_LONG).show();
                new Thread (() -> {
                    DatabaseHelper dbhelper = DatabaseHelper.getHelper(appctx);
                    DatabaseHelper.DbUpdateResult res = dbhelper.insertFromCSVText(csvText);
                    
                    callback.callback(res);

                    handler.post(() -> {
                        Toast.makeText(appctx, res.toString(), Toast.LENGTH_LONG).show();
                    });
                }).start();
            })
            .setNegativeButton(cancelText, (dialog, id) -> {})
            .create();
    }

    private void selectPath(Uri uri) {
        this.uri = uri;
        this.pathSelection.setText(uri.getPath());
    }

    /**
     * Called after updating the database.
     */
    public static interface Callback {
        void callback(DatabaseHelper.DbUpdateResult res);
    }
}
