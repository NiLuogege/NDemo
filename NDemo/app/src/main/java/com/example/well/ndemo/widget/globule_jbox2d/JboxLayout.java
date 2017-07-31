package com.example.well.ndemo.widget.globule_jbox2d;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.well.ndemo.R;

/**
 * Created by ${LuoChen} on 2017/7/31 11:36.
 * email:luochen0519@foxmail.com
 */

public class JboxLayout extends RelativeLayout {

    private JboxImpl mJboxImpl;

    public JboxLayout(@NonNull Context context) {
        this(context, null);
    }

    public JboxLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JboxLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //强制ViewGroup执行onDrow
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取该控件的大小,并创建
        mJboxImpl = new JboxImpl(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mJboxImpl.newWrold(getContext().getResources().getDisplayMetrics().density);

        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                if (view != null && isGlobuleBody(view)) {
                    mJboxImpl.createGlobule(view);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mJboxImpl.startDraw();//开始绘制

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null && isGlobuleBody(view)) {
                view.setX(mJboxImpl.getX(view));
                view.setY(mJboxImpl.getY(view));
                view.setRotation(mJboxImpl.getRotation(view));
            }
        }

        invalidate();//让不断的运动
    }

    private boolean isGlobuleBody(View view) {
        MyLayoutParams layoutParams = (MyLayoutParams) view.getLayoutParams();
        return layoutParams.getIsGlobuleBody();
    }

    /**
     * 在这里 获得了 自View的属性
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyLayoutParams(getContext(), attrs);
    }

    public void onSensorChanged(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null && isGlobuleBody(view)) {
                mJboxImpl.onSensorChanged(view, x, y);
            }
        }

    }

    public class MyLayoutParams extends RelativeLayout.LayoutParams {


        private final boolean mIsGlobuleBody;

        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.Jbox_GlobuleBody);
            mIsGlobuleBody = typedArray.getBoolean(R.styleable.Jbox_GlobuleBody_globule_body, false);
            typedArray.recycle();
        }

        public boolean getIsGlobuleBody() {
            return mIsGlobuleBody;
        }
    }
}
