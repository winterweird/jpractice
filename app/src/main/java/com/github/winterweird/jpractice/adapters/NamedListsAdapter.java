package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.content.Context;

import android.util.Log;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.FeedReaderContract;

public class NamedListsAdapter extends RecyclerView.Adapter<NamedListsAdapter.ItemViewHolder> {
    private Context context;
    private Cursor cursor;
    private OnItemClickListener listener;
    
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView listname;
        public View root;
        public ItemViewHolder(View view) {
            super(view);
            root = view;
            listname = view.findViewById(R.id.listItemListname);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(ItemViewHolder holder, int position);
    }

    public NamedListsAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    public void changeCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.cursor.getCount();
    }

    public void onBindViewHolder(ItemViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String nm = cursor.getString(cursor.getColumnIndexOrThrow(
                    FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME));
        holder.listname.setText(nm);
        
        final ItemViewHolder h = holder;
        final int pos = position;
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(h, pos);
            }
        });
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lists_list_item, parent, false);
        return new ItemViewHolder(view);
    }
}
