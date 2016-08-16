package com.chzan.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.regex.Matcher;

/**
 * Created by chenzan on 2016/8/15.
 */
public abstract class Sticker {
    protected Matrix mMatrix;
    private Matcher matrix;

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public float[] getMappedBoundPoints() {//映射点坐标
        float[] dst = new float[8];
        mMatrix.mapPoints(dst, getBoundPoints());//得到变化之后的映射点
        return dst;
    }

    public RectF getMapBound() {
        RectF rectF = new RectF();
        mMatrix.mapRect(rectF, getBound());//原RectF map处理后RectF
        return rectF;
    }

    private RectF getBound() {
        return new RectF(0, 0, getWidth(), getHeight());
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract int getWidth();

    public abstract int getHeight();

    public float[] getBoundPoints() {
        return new float[]{//四个点的值
                0, 0,
                getWidth(), 0,
                0, getHeight(),
                getWidth(), getHeight()
        };
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public PointF getCenterPoint() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    public PointF getMappedCenterPoint() {
        float[] mapFloat = new float[2];//set arrays size
        PointF mapPoint = new PointF();
        PointF centerPoint = getCenterPoint();
        mMatrix.mapPoints(mapFloat, new float[]{centerPoint.x, centerPoint.y});
        mapPoint.x = mapFloat[0];
        mapPoint.y = mapFloat[1];
        return mapPoint;
    }

    public void release() {
        if (mMatrix != null) {
            mMatrix.reset();
            mMatrix = null;
        }
    }
}
