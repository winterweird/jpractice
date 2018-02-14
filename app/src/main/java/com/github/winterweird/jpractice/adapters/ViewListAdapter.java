package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.content.Context;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.os.Handler;

import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.layout.SwipeToRevealButtonItemLayout;

public class ViewListAdapter extends RecyclerView.Adapter<ViewListAdapter.ItemViewHolder>
        implements ViewListItemTouchHelperCallback.ItemTouchHelperAdapter {
    private Context context;
    private ArrayList<Entry> entries;
    private String listName;
    private boolean filterOn;
    Handler handler = new Handler();
    
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView kanji, reading;
        public View root;
        public ItemViewHolder(View view) {
            super(view);
            kanji = view.findViewById(R.id.listItemKanji);
            reading = view.findViewById(R.id.listItemReading);
            root = view;
        }
    }

    public ViewListAdapter(Context context, ArrayList<Entry> entries,
            String listName, boolean filterOn) {
        this.context = context;
        this.entries = entries;
        this.filterOn = filterOn;
        this.listName = listName;
    }

    @Override
    public int getItemCount() {
        return this.entries.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Entry obj = entries.get(position);
        String kanji = obj.getKanji();
        String reading = obj.getReading();
        holder.kanji.setText(kanji);
        holder.reading.setText(reading);
        
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        
        final int pos = position;
        View delButton = holder.root.findViewById(R.id.buttonDelete);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iof = entries.indexOf(obj);
                if (iof == -1) {
                    Toast.makeText(context, "Entry not found: " + obj, Toast.LENGTH_LONG).show();
                }
                else {
                    deleteItemAtPosition(entries.indexOf(obj));
                }
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_list_list_item, parent, false);
        
        SwipeToRevealButtonItemLayout bil = (SwipeToRevealButtonItemLayout)view;
        LinearLayout buttonsLayout = bil.findViewById(R.id.buttonsLayout);
        
        ImageButton b = (ImageButton)inflater.inflate(R.layout.delete_button, buttonsLayout, true)
            .findViewById(R.id.buttonDelete); // THIS. FUCKING THIS.
        bil.addButton(b);
        
        return new ItemViewHolder(bil);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (filterOn) {
            Toast.makeText(context, "Reordering disabled while filters active",
                    Toast.LENGTH_LONG).show();
        }
        else {
            final Entry e1 = entries.get(fromPosition);
            final Entry e2 = entries.get(toPosition);
            entries.set(fromPosition, e2.setPosition(fromPosition));
            entries.set(toPosition, e1.setPosition(toPosition));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DatabaseHelper dbhelper = DatabaseHelper.getHelper(context);
                    dbhelper.swapEntries(e1, e2);
                }
            });
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Toast.makeText(context, "Not yet implemented", Toast.LENGTH_LONG).show();
    }

    public void insertItem(Entry e) {
        int pos = getItemCount();
        entries.add(e);
        notifyItemInserted(pos);
    }

    public void deleteItemAtPosition(int position) {
        notifyItemRemoved(position);
        final int pos = position;
        final Entry e = entries.get(pos);
        for (int i = pos + 1; i < entries.size(); i++) {
            entries.set(i, entries.get(i).setPosition(i-1));
        }
        entries.remove(pos);
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized(entries) {
                    DatabaseHelper.getHelper(context).delete(e);
                }
            }
        });
    }
    
    public void setContent(ArrayList<Entry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }
}

