package com.github.winterweird.jpractice;

import android.text.TextWatcher;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.v7.app.ActionBar;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.design.widget.FloatingActionButton;
import android.widget.ArrayAdapter;
import android.os.Handler;
import android.content.SharedPreferences;
import android.content.Intent;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Canvas;
import android.view.DragEvent;
import android.widget.AdapterView;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.dialogs.ConfirmationDialog;
import com.github.winterweird.jpractice.dialogs.CreateDatabaseEntryDialog;
import com.github.winterweird.jpractice.adapters.ViewListAdapter;
import com.github.winterweird.jpractice.adapters.ViewListItemTouchHelperCallback;
import com.github.winterweird.jpractice.japanese.JapaneseTextProcessingUtilities;
import com.github.winterweird.jpractice.util.SoftKeyboard;

public class ViewListActivity extends ToolbarBackButtonActivity {
    private RecyclerView recyclerView;
    private ViewListAdapter adapter;
    private String listName;
    private Handler handler = new Handler();

    private boolean searchOpened = false;
    private MenuItem searchAction;
    private EditText searchBox;
    private String searched = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
        
        
        listName = getIntent().getExtras().getString(
                getResources().getString(R.string.intentViewListListName));
        setTitle(listName);

        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateEntryDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getListContent();
        
        if (JapaneseTextProcessingUtilities.isJapanese(searched))
            adapter.filter(searched);
    }
    
    /**
     * Store search box info.
     *
     * I need to do this because if I don't hide the search box before going to
     * another activity, there will just be a dangling search box somewhere that
     * I can't access, but which will show up on the toolbar no matter what I
     * do.
     */
    @Override
    public void onPause() {
        super.onPause();
        String storeSearch = searched;
        boolean restoreSearch = searchOpened;
        setShowFilterSearchBox(false);
        searched = storeSearch;
        searchOpened = restoreSearch;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewListActionFilterWords:
                toggleShowFilterSearchBox();
                return true;
            case R.id.viewListActionDeleteList:
                showDeleteConfirmationDialog();
                return true;
            case R.id.viewListActionExportListAsCSV:
                exportListAsCSV();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchAction = menu.findItem(R.id.viewListActionFilterWords);
        setShowFilterSearchBox(searchOpened);
        return super.onPrepareOptionsMenu(menu);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_list_menu, menu);
        return true;
    }

    public void getListContent() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        ArrayList<Entry> entries = dbhelper.getEntries(listName);
        adapter = new ViewListAdapter(this, entries, listName, false);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ViewListItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void showDeleteConfirmationDialog() {
        DialogFragment dialog = new ConfirmationDialog("Delete list",
                "Are you sure you want to delete list '" + listName + "'?",
                "Delete", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ViewListActivity.this.deleteList();
                        ViewListActivity.this.finish();
                    }
                });
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    public void showCreateEntryDialog() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        int count = dbhelper.getLists().size();
        
        if (count == 0) {
            Toast.makeText(this, "You haven't created any lists", Toast.LENGTH_LONG).show();
        }
        else {
            DialogFragment dialog = new CreateDatabaseEntryAndRefreshDialog(adapter, listName);
            dialog.show(getSupportFragmentManager(), "CreateDatabaseEntryDialog");
        }
    }

    public static class CreateDatabaseEntryAndRefreshDialog extends CreateDatabaseEntryDialog {
        private ViewListAdapter adapter;
        private String listname;
        public CreateDatabaseEntryAndRefreshDialog(ViewListAdapter adapter, String listname) {
            this.adapter = adapter;
            this.listname = listname;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog d = super.onCreateDialog(savedInstanceState);
            List l = new List(listname);
            Spinner s = view.findViewById(R.id.createEntrySpinner);
            View txtView = view.findViewById(R.id.createEntrySpinnerLabelText);

            ArrayAdapter<List> adapter = getArrayAdapter(s);
            s.setSelection(getArrayAdapter(s).getPosition(l));
            s.setVisibility(View.GONE);
            txtView.setVisibility(View.GONE);
            return d;
        }

        /**
         * Helper method: Get array adapter from spinner while suppressing
         * unchecked warnings as minutely as possible.
         *
         * @param s The spinner to get the adapter from
         * @return The adapter of the spinner cast as an ArrayAdapter&lt;List&gt;
         */
        @SuppressWarnings("unchecked")
        private ArrayAdapter<List> getArrayAdapter(Spinner s) {
            return (ArrayAdapter<List>)s.getAdapter();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (this.result != null) {
                adapter.insertItem(this.result);
            }
        }
    }

    public void exportListAsCSV() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        
        // Put in separate thread to avoid making the UI hang
        new Thread(() -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, dbhelper.createEntryCSVText(new String[]{listName}));
            intent.setType("text/plain");

            // create a chooser every time
            String title = getResources().getString(R.string.listExportChooserTitle);
            Intent chooser = Intent.createChooser(intent, title);
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
            else {
                Toast.makeText(this, "Error: could not export to any application", 
                        Toast.LENGTH_LONG).show();
            }

        }).start();
    }

    public void deleteList() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencesFile),
                Context.MODE_PRIVATE);
        final String prefList = prefs.getString(getString(R.string.preferencesLastUsedList), null);
        final SharedPreferences.Editor edit = prefs.edit();
        final SQLiteDatabase db = DatabaseHelper.getHelper(this).getWritableDatabase();
        final String table = FeedReaderContract.FeedLists.TABLE_NAME;
        final String where = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME + " = ?";
        final String[] whereArgs = new String[] {listName};
        handler.post(new Runnable() {
            @Override
            public void run() {
                db.delete(table, where, whereArgs);
                Toast.makeText(ViewListActivity.this, "List deleted", Toast.LENGTH_LONG).show();
                if (listName.equals(prefList)) {
                    edit.remove(getString(R.string.preferencesLastUsedList));
                    edit.commit();
                }
            }
        });
    }

    public void toggleShowFilterSearchBox() {
        setShowFilterSearchBox(!searchOpened);
    }

    private void setShowFilterSearchBox(boolean visible) {
        searchOpened = visible;
        ActionBar action = getSupportActionBar();
        Log.d("Test", "searchbox visibility is " + visible);
        action.setDisplayShowCustomEnabled(visible);
        action.setDisplayShowTitleEnabled(!visible);
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!visible) {
            if (searchBox != null) {
                searchBox.setText("");
                searchBox.clearFocus();
                SoftKeyboard.hide(this);
                //imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            }

            action.setCustomView(null);
            
            searchAction.setIcon(getDrawable(R.drawable.ic_search_white_24dp));
        }
        else {
            action.setCustomView(R.layout.search_box);
            searchBox = (EditText)action.getCustomView().findViewById(R.id.searchBox);
            searchBox.setText(searched);
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) { }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searched = s.toString();
                    if (JapaneseTextProcessingUtilities.isJapanese(searched)) {
                        adapter.filter(searched);
                    }
                }
            });
            searchBox.requestFocus();
            
            imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
            
            searchAction.setIcon(getDrawable(R.drawable.ic_cancel_white_24dp));
        }
    }
}
