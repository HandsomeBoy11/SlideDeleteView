package com.xrd.slidedeleteview.weight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import static android.content.ContentValues.TAG;

/**
 * Created by WJ on 2019/3/13.
 */

public class SlideDeleteView extends FrameLayout {

    private static final int DEFAULT_TIME = 300;
    private final int mTouchSlop;
    private Scroller mScroller;
    private View mContentView;
    private View mMenuView;
    private int contentWidth;
    private int menuWidth;
    private int contentHeight;
    private int menuHeight;
    private int startX;
    private float startY;
    private int scrollOffset;

    public SlideDeleteView(@NonNull Context context) {
        this(context, null);
    }

    public SlideDeleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlideDeleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 2) {
            try {
                throw new Exception("SlideDeleteView 必须只能有2个子view");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mContentView = getChildAt(0);
        mMenuView = getChildAt(1);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        contentWidth = mContentView.getWidth();
        menuWidth = mMenuView.getWidth();
        contentHeight = mContentView.getHeight();
        menuHeight = mMenuView.getHeight();
        mContentView.layout(0, 0, contentWidth, contentHeight);
        mMenuView.layout(contentWidth, 0, contentWidth + menuWidth, menuHeight);
    }

    /**
     * 返回值为true 时：将执行onTouchEvent方法  返回值为false 时：将传递给子view
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        boolean isIntercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(changeStateListener != null){
                    changeStateListener.onTouch(this);
                }
                startX = (int) ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float dx = Math.abs(endX - startX);
                float dy = Math.abs(endY - startY);
                if (dx > mTouchSlop && dx > dy) {
                    isIntercept = true;
                }
                startX = (int) ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollX = getScrollX();
                int scrollY = getScrollY();
                int endX = (int) event.getX();
                float endY = event.getY();

                int dx = endX - startX;
                if(Math.abs(dx)>mTouchSlop){//水平滑动请求父容器不要拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                Log.e(TAG, "onTouchEvent: "+"getScrollX:  "+scrollX+"  dx: "+ dx);
                //边界处理
                int scrollto = scrollX - dx;
                if(scrollto<0){
                    scrollto=0;
                }else if(scrollto>menuWidth){
                    scrollto=menuWidth;
                }
                scrollTo(scrollto,scrollY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.i("TAG", "---------onTouchEvent ACTION_UP");
                scrollOffset = getScrollX();
                if(scrollOffset >= (menuWidth / 2.0)){
                    openSlid();
                } else {
                    closeSlid();
                }
                break;
        }
        return true;
    }

    private   void closeSlid() {
        if(changeStateListener != null){
            changeStateListener.slidClose(this);
        }
        int dx = 0 - scrollOffset;
        Log.i("TAG", "---------closeSlid_dx="+dx);
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, DEFAULT_TIME);
        invalidate();
    }
    /**
     * 防止出现关闭不上的效果出现
     */
    public void closeSlid2(){
        if(changeStateListener != null){
            changeStateListener.slidClose(this);
        }
        if(getScrollX() == 0){
            return;
        }
        int dx = 0 - menuWidth;
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, DEFAULT_TIME);
        invalidate();
    }

    private void openSlid() {
        if(changeStateListener != null){
            changeStateListener.slidOpen(this);
        }
        int dx = menuWidth - scrollOffset;
        Log.i("TAG", "---------openSlid_dx="+dx);
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, DEFAULT_TIME);
        invalidate();

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if( mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }

    }
    /**
     * 监听slideView的开关状态
     */
    private ISlideItemCallBack changeStateListener;

    public void setChangeStateListener(ISlideItemCallBack changeStateListener) {
        this.changeStateListener = changeStateListener;
    }
    public interface ISlideItemCallBack{
        void slidOpen(SlideDeleteView itemView);
        void slidClose(SlideDeleteView itemView);
        void onTouch(SlideDeleteView itemView);
    }
}
