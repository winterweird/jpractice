package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.content.Context;
import android.widget.Toast;

import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;

public class ViewListAdapter extends RecyclerView.Adapter<ViewListAdapter.ItemViewHolder>
        implements ViewListItemTouchHelperCallback.ItemTouchHelperAdapter {
    private Context context;
    private ArrayList<Entry> entries;
    private String listName;
    private boolean filterOn;
    
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
        Entry obj = entries.get(position);
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
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_list_item,
                parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (filterOn) {
            Toast.makeText(context, "Reordering disabled while filters active",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Entry tmp = entries.get(fromPosition);
            entries.set(fromPosition, entries.get(toPosition));
            entries.set(toPosition, tmp);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Toast.makeText(context, "Not yet implemented", Toast.LENGTH_LONG).show();
    }
}

