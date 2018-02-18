package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.data.Entry;

public class SharedKanjiListAdapter
    extends RecyclerView.Adapter<SharedKanjiListAdapter.ItemViewHolder> {
    
    private Context context;
    private ArrayList<KanjiWordPair> items;
    private OnItemClickListener listener;
    
    public static class KanjiWordPair {
        private String kanji;
        private Entry word;
        public KanjiWordPair(String kanji, Entry word) {
            this.kanji = kanji;
            this.word = word;
        }
        public String getKanji() {
            return this.kanji;
        }
        public Entry getWord() {
            return this.word;
        }
        @Override
        public String toString() {
            return this.kanji +  " - " + this.word.getKanji();
        }
    }
    
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView kanji;
        public TextView word;
        public TextView reading;
        public View root;
        public ItemViewHolder(View view) {
            super(view);
            root = view;
            kanji = view.findViewById(R.id.sharedKanjiItemKanji);
            word = view.findViewById(R.id.sharedKanjiItemWord);
            reading = view.findViewById(R.id.sharedKanjiItemReading);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ItemViewHolder holder, int position);
    }

    public SharedKanjiListAdapter(Context context, ArrayList<KanjiWordPair> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        KanjiWordPair pair = items.get(position);
        Entry e = pair.getWord();
        holder.kanji.setText(pair.getKanji());
        holder.word.setText(e.getKanji());
        holder.reading.setText(e.getReading());

        final ItemViewHolder h = holder;
        final int pos = position;
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(h, pos);
                }
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shared_kanji_item,
                parent, false);
        return new ItemViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
