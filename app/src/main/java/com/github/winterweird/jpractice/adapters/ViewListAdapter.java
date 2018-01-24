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

public class ViewListAdapter extends RecyclerView.Adapter<ViewListAdapter.ItemViewHolder> {
    private Context context;
    private Cursor cursor;
    
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView kanji, reading;
        public ItemViewHolder(View view) {
            super(view);
            kanji = view.findViewById(R.id.listItemKanji);
            reading = view.findViewById(R.id.listItemReading);
        }
    }

    public ViewListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
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
        String kanji = cursor.getString(cursor.getColumnIndexOrThrow(
                    FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI));
        String reading = cursor.getString(cursor.getColumnIndexOrThrow(
                    FeedReaderContract.FeedEntries.COLUMN_NAME_READING));
        holder.kanji.setText(kanji);
        holder.reading.setText(reading);
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_list_item,
                parent, false);
        return new ItemViewHolder(view);
    }
}

