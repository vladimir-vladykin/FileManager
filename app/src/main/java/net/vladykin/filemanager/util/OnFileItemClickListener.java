package net.vladykin.filemanager.util;

import android.content.Context;
import android.gesture.Gesture;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Владимир on 14.02.2015.
 */
public class OnFileItemClickListener implements RecyclerView.OnItemTouchListener {

    public interface OnFileClickListener {
         void onItemClick(View view, int position);
         void onItemLongClick(View view, int position);
    }

    private OnFileClickListener mListener;
    private GestureDetector mDetector;

    public OnFileItemClickListener(Context context, final RecyclerView recyclerView, OnFileClickListener listener) {
        mListener = listener;

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(childView != null && mListener != null)
                {
                    mListener.onItemLongClick(childView, recyclerView.getChildPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        if(childView != null && mListener != null && mDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //do nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
