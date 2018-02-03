package com.github.winterweird.jpractice.dialogs;

import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentValues;
import android.widget.Toast;
import android.widget.TextView;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.R;

public class CreateNewListDialog extends DialogFragment {
    protected List result;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity act = getActivity();
        View view = act.getLayoutInflater().inflate(R.layout.create_new_list_dialog_layout, null);
        final TextView listnameTextView = view.findViewById(R.id.createListListName);
        return new AlertDialog.Builder(act).setView(view)
            .setPositiveButton(R.string.createListOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String s = listnameTextView.getText().toString().trim();
                    if (s.isEmpty()) {
                        Toast.makeText(getContext(), "Cannot create a list with no name",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        List l = new List(s);
                        DatabaseHelper dbhelper = DatabaseHelper.getHelper(getContext());
                        try {
                            dbhelper.insert(l);
                            result = l;
                        } catch (SQLException ex) {
                            Toast.makeText(getContext(), "Error: " + ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }).setNegativeButton(R.string.createListCancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // ignore, don't modify db
                    }
            }).create();
    }
}
