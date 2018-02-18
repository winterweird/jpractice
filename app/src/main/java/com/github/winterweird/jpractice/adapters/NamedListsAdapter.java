package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.content.Context;

import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.data.List;

public class NamedListsAdapter extends RecyclerView.Adapter<NamedListsAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<List> lists;
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

    public NamedListsAdapter(Context context, ArrayList<List> lists, OnItemClickListener listener) {
        this.context = context;
        this.lists = lists;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.listname.setText(lists.get(position).toString());
        
        final ItemViewHolder h = holder;
        final int pos = position;
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(h, pos);
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lists_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    public void insertItem(List l) {
        int pos = getItemCount();
        lists.add(l);
        notifyItemInserted(pos);
    }

    public void setContent(ArrayList<List> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }
}
