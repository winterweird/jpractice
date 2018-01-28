package com.github.winterweird.jpractice.adapters;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

public class ViewListItemTouchHelperCallback extends ItemTouchHelper.Callback {
    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    
    private ItemTouchHelperAdapter adapter;
    public ViewListItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
    
    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
    
    @Override
    public boolean isLongPressDragEnabled() {
            return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
