package com.github.winterweird.jpractice;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;

import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;

import java.util.ArrayList;

public class NamedListsActivity extends AppCompatActivity {
    private ListView listview;
    private ListNameCursorAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.named_lists);

        Toolbar toolbar = (Toolbar)findViewById(R.id.genericToolbar);
        setSupportActionBar(toolbar);

        listview = (ListView)findViewById(R.id.listsListView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView listnameView = view.findViewById(R.id.listItemListname);
                String listname = listnameView.getText().toString();
                Intent intent = new Intent(NamedListsActivity.this, ViewListActivity.class);
                intent.putExtra(getResources().getString(R.string.intentViewListListName), listname);
                startActivity(intent);
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
        DialogFragment dialog = new CreateNewListAndRefreshDialog(this);
        dialog.show(getSupportFragmentManager(), "CreateNewListDialog");
    }

    public static class CreateNewListAndRefreshDialog extends CreateNewListDialog {
        private NamedListsActivity context;
        public CreateNewListAndRefreshDialog(NamedListsActivity context) {
            this.context = context;
        }
        
        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            context.getListContent();
        }
    }

    public void getListContent() {
        SQLiteDatabase db = DatabaseHelper.getHelper(this).getReadableDatabase();

        String[] projection = {
            FeedReaderContract.FeedLists._ID,
            FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME
        };
        
        Cursor cursor = db.query(
                FeedReaderContract.FeedLists.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        if (adapter == null) {
            adapter = new ListNameCursorAdapter(this, cursor, 0);
            listview.setAdapter(adapter);
        }
        else {
            adapter.changeCursor(cursor);
        }
    }

    private class ListNameCursorAdapter extends CursorAdapter {
        public ListNameCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }
        
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.lists_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView listname = view.findViewById(R.id.listItemListname);
            String listnameStr = cursor.getString(cursor.getColumnIndexOrThrow(
                        FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME));
            listname.setText(listnameStr);
        }
    }
}
