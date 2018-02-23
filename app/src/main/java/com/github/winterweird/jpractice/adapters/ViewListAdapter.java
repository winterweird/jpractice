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
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.widget.TextView.BufferType;
import android.text.style.BackgroundColorSpan;
import android.graphics.Color;

import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.ViewEntryActivity;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.layout.SwipeToRevealButtonItemLayout;

public class ViewListAdapter extends RecyclerView.Adapter<ViewListAdapter.ItemViewHolder>
        implements ViewListItemTouchHelperCallback.ItemTouchHelperAdapter {
    private Context context;
    private ArrayList<Entry> entries;
    private ArrayList<Entry> realContent;
    private String listName;
    private boolean filterOn;
    private String filter = "";
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
        this.realContent = entries;
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
        String sKanji = obj.getKanji();
        String sReading = obj.getReading();
        SpannableString kanji = new SpannableString(sKanji);
        SpannableString reading = new SpannableString(sReading);
        BackgroundColorSpan highlightColor = new BackgroundColorSpan(Color.YELLOW);
        if (!filter.isEmpty()) {
            int index = 0;
            while ((index = sKanji.indexOf(filter, index)) != -1) {
                kanji.setSpan(highlightColor, index, index + filter.length(), 0);
                index += filter.length() + 1;
            }
            index = 0;
            while ((index = sReading.indexOf(filter, index)) != -1) {
                reading.setSpan(highlightColor, index, index + filter.length(), 0);
                index += filter.length() + 1;
            }
        }
        holder.kanji.setText(kanji, BufferType.SPANNABLE);
        holder.reading.setText(reading, BufferType.SPANNABLE);
        
        View linlayout = holder.root.findViewById(R.id.linearLayout);
        linlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewEntryActivity.class);
                intent.putExtra(context.getResources().getString(R.string.intentViewEntryList),
                        obj.getListname());
                intent.putExtra(context.getResources().getString(R.string.intentViewEntryKanji),
                        obj.getKanji());
                context.startActivity(intent);
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
            return;
        }
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

    @Override
    public void onItemDismiss(int position) {
        Toast.makeText(context, "Not yet implemented", Toast.LENGTH_LONG).show();
    }

    public void insertItem(Entry e) {
        int pos = getItemCount();
        entries.add(e);
        realContent.add(e);
        notifyItemInserted(pos);
    }

    public void deleteItemAtPosition(int position) {
        if (filterOn) {
            Toast.makeText(context, "Deletion disabled while filters active",
                    Toast.LENGTH_LONG).show();
            return;
        }
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

    public void filter(String s) {
        filter = s;
        if (filter == null) filter = "";
        ArrayList<Entry> filtered = new ArrayList<>(realContent.size());
        for (Entry e : realContent) {
            if (e.getKanji().contains(filter)) {
                filtered.add(e);
            }
            else if (e.getReading().contains(filter)) {
                filtered.add(e);
            }
        }
        setContent(filtered);
        filterOn = !filter.isEmpty();
    }
}

