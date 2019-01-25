package com.github.winterweird.jpractice.layout;

import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;

public class SwipeToRevealButtonItemLayout extends RelativeLayout {
    private ViewDragHelper dragHelper;
    private View layout;
    private LinearLayout buttonsLayout;
    private int buttonSize;
    ArrayList<View> buttons = new ArrayList<>();
    
    public SwipeToRevealButtonItemLayout(Context context) {
        super(context);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }
    public SwipeToRevealButtonItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }
    public SwipeToRevealButtonItemLayout(Context context, AttributeSet attrs,
            int defstyleattr) {
        super(context, attrs, defstyleattr);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }
    public SwipeToRevealButtonItemLayout(Context context, AttributeSet attrs,
            int defstyleattr, int defstyleres) {
        super(context, attrs, defstyleattr, defstyleres);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == layout;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = child.getPaddingLeft();
            final int rightBound = child.getWidth();

            final int newLeft = Math.min(Math.max(left, 0), rightBound);

            return newLeft;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            
            int lpos = 0;
            if (releasedChild.getLeft() >= buttonSize*buttons.size()) {
                lpos = buttonSize*buttons.size();
            }
            if (dragHelper.settleCapturedViewAt(lpos, 0)) {
                SwipeToRevealButtonItemLayout.this.postInvalidateOnAnimation();
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
            this.postInvalidateOnAnimation();
        }
        else {
            // not sure if this is the best place to have this, but eh.
            SwipeToRevealButtonItemLayout.this.getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        layout = findViewById(R.id.linearLayout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                buttonSize = layout.getHeight();
            }
        });
        buttonsLayout = findViewById(R.id.buttonsLayout);
        buttonsLayout.post(new Runnable() {
            @Override
            public void run() {
                if (buttonSize == 0) {
                    buttonsLayout.postDelayed(this, 10);
                }
                else {
                    ViewGroup.LayoutParams lp = buttonsLayout.getLayoutParams();
                    lp.height = buttonSize;
                    buttonsLayout.setLayoutParams(lp);
                }
            }

        });
    }

    public void addButton(View button) {
        final View b = button;
        b.post(new Runnable() {
            @Override
            public void run() {
                if (buttonSize == 0) {
                    b.postDelayed(this, 10);
                }
                else {
                    ViewGroup.LayoutParams lp = b.getLayoutParams();
                    lp.width = buttonSize;
                    lp.height = buttonSize;
                    b.setLayoutParams(lp);
                    buttons.add(b);
                }
            }
        });
    }

    /**
     * Make layout intercept touch events.
     *
     * @param e the event to be intercepted
     * @return true if the drag helper should intercept touch events
     */
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

    /**
     * Handle intercepted touch events.
     *
     * @param e the event to be processed
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        dragHelper.processTouchEvent(e);
        return true;
    }
}
