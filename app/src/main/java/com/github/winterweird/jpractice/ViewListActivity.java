package com.github.winterweird.jpractice;

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
import com.github.winterweird.jpractice.dialogs.ConfirmationDialog;
import com.github.winterweird.jpractice.adapters.ViewListAdapter;
import com.github.winterweird.jpractice.adapters.ViewListItemTouchHelperCallback;

public class ViewListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ViewListAdapter adapter;
    private String listName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.genericToolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.viewListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
        
        
        listName = getIntent().getExtras().getString(
                getResources().getString(R.string.intentViewListListName));
        setTitle(listName);
}

    @Override
    public void onResume() {
        super.onResume();
        getListContent();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewListActionDeleteList:
                showDeleteConfirmationDialog();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_list_menu, menu);
        return true;
    }

    public void getListContent() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        ArrayList<Entry> entries = dbhelper.getEntries(listName);
        if (adapter == null) {
            adapter = new ViewListAdapter(this, entries, listName, false);
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new ViewListItemTouchHelperCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }
        else {
            // TODO
        }
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

    public void deleteList() {
        SQLiteDatabase db = DatabaseHelper.getHelper(this).getWritableDatabase();
        String table = FeedReaderContract.FeedLists.TABLE_NAME;
        String where = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME + " = ?";
        String[] whereArgs = new String[] {listName};
        db.delete(table, where, whereArgs);
        Toast.makeText(this, "List deleted", Toast.LENGTH_LONG).show();
    }
}
