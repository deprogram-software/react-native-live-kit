package com.yuanyinguoji.livekit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ListView;

/**
 * 弹幕listview消息
 * Created by lgq on 2016/8/2.
 */
public class ListViewBullet extends ListView {
    public ListViewBullet(Context context) {
        super(context);
    }

    public ListViewBullet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewBullet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        setMeasuredDimension((int)(width*0.8f), (int)(height*0.3f));
    }
}
