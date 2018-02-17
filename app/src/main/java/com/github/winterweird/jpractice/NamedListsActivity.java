package com.github.winterweird.jpractice;

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

import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.List;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.dialogs.CreateNewListDialog;
import com.github.winterweird.jpractice.adapters.NamedListsAdapter;

import java.util.ArrayList;

public class NamedListsActivity extends ToolbarBackButtonActivity {
    private RecyclerView recyclerView;
    private NamedListsAdapter adapter;
    private NamedListsAdapter.OnItemClickListener listener;
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

    @Override
    public void onResume() {
        super.onResume();
        getListContent();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listsActionCreateNewList:
                showCreateNewListDialog();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.named_lists_menu, menu);
        return true;
    }

    public void showCreateNewListDialog() {
        DialogFragment dialog = new CreateNewListAndRefreshDialog(adapter);
        dialog.show(getSupportFragmentManager(), "CreateNewListDialog");
    }

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
