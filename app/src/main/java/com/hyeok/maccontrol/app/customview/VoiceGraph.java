package com.hyeok.maccontrol.app.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 2014. 7. 10..
 */
public class VoiceGraph extends HorizontalScrollView {
    ArrayList<Float> graphdata = new ArrayList<Float>();
    private ImageView imageView;

    public VoiceGraph(Context context) {
        this(context, null);
    }

    public VoiceGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VoiceGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        imageView = new ImageView(context);
        addView(imageView);

    }

    public void resetGraphValue() {
        graphdata.clear();
    }

    public void addGraphValue(float value) {
        
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff315331);
        paint.setStrokeWidth(6);
        path.moveTo(0, getHeight() / 2);
        graphdata.add(value);
        for (int i = 0; i < graphdata.size(); i++) {
            path.lineTo((float) i * 10, getHeight() - ((graphdata.get(i)) + getHeight() / 2));
        }
        canvas.drawPath(path, paint);
        imageView.setImageBitmap(bitmap);
    }

}
