package com.github.winterweird.jpractice;

import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DividerItemDecoration;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;

import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.dialogs.CreateNewListDialog;
import com.github.winterweird.jpractice.dialogs.AddFromCSVDialog;
import com.github.winterweird.jpractice.adapters.NamedListsAdapter;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Activity displaying a list of the lists the user has created.
 *
 * Clicking on one of the lists will take you to ViewListActivity, which
 * displays the words in the list and their readings.
 */
public class NamedListsActivity extends ToolbarBackButtonActivity {
    private RecyclerView recyclerView;
    private NamedListsAdapter adapter;
    private NamedListsAdapter.OnItemClickListener listener;

    /**
     * Called when the activity is created.
     *
     * Set the layout, and defined the items, their style, and their onclick
     * behavior, as well as the floating action button's behavior.
     *
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.named_lists);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
        listener = new NamedListsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NamedListsAdapter.ItemViewHolder holder, int position) {
                String lnm = holder.listname.getText().toString();
                Intent intent = new Intent(NamedListsActivity.this, ViewListActivity.class);
                intent.putExtra(getResources().getString(R.string.intentViewListListName), lnm);
                startActivity(intent);
            }
        };

        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateNewListDialog();
            }
        });
    }

    /**
     * Update list content on resume.
     *
     * Needs to be called because the list content may have changed since the
     * items can be edited outside of this activity.
     */
    @Override
    public void onResume() {
        super.onResume();
        getListContent();
    }
    
    /**
     * Define the actions performed when the user clicks a toolbar menu item.
     *
     * @param item The menu item clicked
     * @return Whether the click was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listsActionCreateNewList:
                showCreateNewListDialog();
                return true;
            case R.id.listsActionExportListsAsCSV:
                exportListsAsCSV();
                return true;
            case R.id.listsActionAddFromCSV:
                showAddFromCSVDialog();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Inflate the menu.
     *
     * @param menu The menu to inflate the layout into
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.named_lists_menu, menu);
        return true;
    }

    /**
     * Export the entries in the database as plaintext in CSV format, displaying
     * a chooser every time.
     *
     * Create a new thread to do this in.
     */
    public void exportListsAsCSV() {
        // TODO: use application context instead of this as the context
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);

        // Put in separate thread to avoid making the UI hang
        new Thread(() -> {
            // get all lists as array of string
            ArrayList<List> lists = dbhelper.getLists();
            String[] listStrings = new String[lists.size()];
            lists.stream().map(l -> l.toString()).collect(Collectors.toList()).toArray(listStrings);
            
            String csv = dbhelper.createEntryCSVText(listStrings);
            
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, csv);
            intent.setType("text/plain");
            
            // create a chooser every time
            String title = getResources().getString(R.string.listExportChooserTitle);
            Intent chooser = Intent.createChooser(intent, title);

            // Verify the intent will resolve to at least one activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
            else {
                Toast.makeText(this, "Error: could not export to any application", 
                        Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    /**
     * Display the dialog for adding from a CSV.
     *
     * Allows for either adding from a CSV file, or inserting the values to add
     * in a text box.
     *
     * Updates the recyclerview with the newly added lists.
     */
    public void showAddFromCSVDialog() {
        DialogFragment dialog = new AddFromCSVDialog(res -> {
            for (List l : res.createdLists()) {
                adapter.insertItem(l);
            }
        });
        dialog.show(getSupportFragmentManager(), "AddFromCSVDialog");
    }

    /**
     * Display the dialog for creating a new list.
     *
     * Updates the recyclerview with the newly added list.
     */
    public void showCreateNewListDialog() {
        DialogFragment dialog = new CreateNewListAndRefreshDialog(adapter);
        dialog.show(getSupportFragmentManager(), "CreateNewListDialog");
    }

    /**
     * Custom class which refreshes the recyclerview content when closed.
     *
     * Inserts the added item, if there was an added item.
     */
    public static class CreateNewListAndRefreshDialog extends CreateNewListDialog {
        private NamedListsAdapter adapter;
        public CreateNewListAndRefreshDialog(NamedListsAdapter adapter) {
            this.adapter = adapter;
        }
        
        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (this.result != null) {
                adapter.insertItem(this.result);
            }
        }
    }

    /**
     * Helper method: refresh the contents of the recyclerview.
     *
     * If the adapter has not been created before, create a new adapter;
     * otherwise, just set the content.
     */
    public void getListContent() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        ArrayList<List> lists = dbhelper.getLists();
        if (adapter == null) {
            adapter = new NamedListsAdapter(this, lists, listener);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setContent(lists);
        }
    }
}
