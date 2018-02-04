package com.github.winterweird.jpractice.layout;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.MotionEvent;
import android.support.v4.view.ViewCompat;
import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;

import android.util.Log;

import com.github.winterweird.jpractice.R;

public class DragLayout extends RelativeLayout {
    private ViewDragHelper dragHelper;
    private View layout;
    private AppCompatImageButton deleteButton;
    
    public DragLayout(Context context) {
        super(context);
        dragHelper = ViewDragHelper.create(this,
                1.0f, new DragHelperCallback());
    }
    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        dragHelper = ViewDragHelper.create(this,
                1.0f, new DragHelperCallback());
    }
    public DragLayout(Context context, AttributeSet attrs, int defstyleattr) {
        super(context, attrs, defstyleattr);
        dragHelper = ViewDragHelper.create(this,
                1.0f, new DragHelperCallback());
    }
    public DragLayout(Context context, AttributeSet attrs, int defstyleattr, int defstyleres) {
        super(context, attrs, defstyleattr, defstyleres);
        dragHelper = ViewDragHelper.create(this,
                1.0f, new DragHelperCallback());
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == layout;
        }

        @Override
        public void onViewPositionChanged(View view, int left, int top, int dx, int dy) {
//            ViewGroup.LayoutParams lp = deleteButton.getLayoutParams();
//            lp.width = left;
//            deleteButton.setLayoutParams(lp);
        }
        
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d("DragLayout", "clampViewPositionHorizontal " + left + "," + dx);

            final int leftBound = child.getPaddingLeft();
            final int rightBound = child.getWidth();

            final int newLeft = Math.min(Math.max(left, 0), rightBound);

            return newLeft;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.d("DragLayout", "released view");
            
            int lpos = 0;
            if (releasedChild.getLeft() >= deleteButton.getWidth()) {
                lpos = deleteButton.getWidth();
            }
            if (dragHelper.settleCapturedViewAt(lpos, 0)) {
                DragLayout.this.postInvalidateOnAnimation();
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(dragHelper.continueSettling(true)) {
            Log.d("DragLayout", "invalidate...");
            this.postInvalidateOnAnimation();
        }
        else {
            DragLayout.this.getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
    
    @Override
    protected void onFinishInflate() {
        layout = findViewById(R.id.linearLayout);
        deleteButton = findViewById(R.id.buttonDelete);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            return false;
        }
        if (dragHelper.shouldInterceptTouchEvent(e)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return dragHelper.shouldInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        dragHelper.processTouchEvent(e);
        return true;
    }
}
