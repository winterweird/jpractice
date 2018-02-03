package com.github.winterweird.jpractice.dialogs;

// stuff I need for all dialogs
import android.support.v4.app.DialogFragment;  // base class
import android.app.Dialog;                     // return of onCreateDialog
import android.content.DialogInterface;        // DialogInterface.OnClickListener
import android.os.Bundle;                      // parameter of onCreateDialog
import android.app.AlertDialog;                // AlertDialog.Builder

// stuff I need for this dialog
import android.view.View;                      // For custom dialog view layouts
import android.widget.TextView;                // For custom view TextView/EditText manipulation
import android.widget.Toast;                   // In case I wanna show messages/feedback
import android.app.Activity;                   // Store activity for later use
import android.widget.Spinner;                 // Select one of the options
import android.view.LayoutInflater;            // IDFK
import android.widget.SimpleCursorAdapter;     // AAAAA
import android.widget.ArrayAdapter;            // AAAAAAAAAAAAAAAAAAAAAAAAAAAAA
import android.content.Context;

// database stuff
import android.database.SQLException;          // in case of problems
import android.database.Cursor;                // for query results
import android.database.sqlite.SQLiteDatabase; // obviously
import android.content.ContentValues;          // for any db operation

// own classes
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.japanese.JapaneseTextProcessingUtilities;
import com.github.winterweird.jpractice.R;

// java API
import java.util.ArrayList;

public class CreateDatabaseEntryDialog extends DialogFragment {
    protected Entry result;
    protected View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity act = getActivity();
        view = act.getLayoutInflater().inflate(R.layout.create_new_entry_dialog_layout, null);
        
        // EditText entries in the thing
        final TextView kanji   = view.findViewById(R.id.createEntryKanji);
        final TextView reading = view.findViewById(R.id.createEntryReading);
        final Spinner spinner = view.findViewById(R.id.createEntrySpinner);

        DatabaseHelper dbhelper = DatabaseHelper.getHelper(act);
        String[] from = new String[] {FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME};
        int   [] to   = new int[]    {R.id.createEntrySpinnerItem};
        ArrayList<List> lists = dbhelper.getLists();
        ArrayAdapter<List> adapter = new ArrayAdapter<>(act,
                R.layout.create_new_entry_spinner_layout, lists);
        adapter.setDropDownViewResource(R.layout.create_new_entry_spinner_layout);
        
        spinner.setAdapter(adapter);
        
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
            .setView(view)
            .setTitle(R.string.createEntryTitle)
            .setPositiveButton(R.string.createEntryOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // ignore this, as we will program in conditional dismissal
                    // and other behavior in the View.OnClickListener below
                }
            }).setNegativeButton(R.string.createEntryCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // dismiss
                }
            }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ktxt = kanji.getText().toString();
                String rtxt = reading.getText().toString();
                if (ktxt.isEmpty()) {
                    Toast.makeText(act,
                            act.getResources()
                               .getString(R.string.createEntryOnOKEmptyKanjiMessage),
                            Toast.LENGTH_LONG)
                        .show();
                }
                else if (!JapaneseTextProcessingUtilities.isValidWordKanji(ktxt)) {
                    Toast.makeText(act,
                            act.getResources()
                               .getString(R.string.createEntryOnOKNotValidKanjiMessage),
                            Toast.LENGTH_LONG)
                        .show();
                }
                else if (rtxt.isEmpty()) {
                    Toast.makeText(act,
                            act.getResources()
                               .getString(R.string.createEntryOnOKEmptyReadingMessage),
                            Toast.LENGTH_LONG)
                        .show();
                }
                else if (!JapaneseTextProcessingUtilities.isValidWordReading(rtxt)) {
                    Toast.makeText(act,
                            act.getResources()
                               .getString(R.string.createEntryOnOKNotValidReadingMessage),
                            Toast.LENGTH_LONG)
                        .show();
                }
                else {
                    DatabaseHelper dbhelper = DatabaseHelper.getHelper(act);

                    List c = (List)spinner.getSelectedItem();
                    String listName = c.getListname();
                    
                    int tid = dbhelper.idOf(FeedReaderContract.FeedLists.TABLE_NAME, listName);
                    int tier = 5; // TODO: put in integer resources
                    
                    result = new Entry(tid, ktxt, rtxt, dbhelper.entryCount(listName), tier);
                    
                    try {
                        dbhelper.insert(result);
                        Toast.makeText(act, "Added entry to " + listName, Toast.LENGTH_LONG).show();
                    } catch (SQLException ex) {
                        Toast.makeText(act, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        dialog.dismiss();
                    }
                }
            }
        });
        
        return dialog;
    }
}

