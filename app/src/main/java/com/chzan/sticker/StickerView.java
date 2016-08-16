package com.chzan.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzan on 2016/8/15.
 */
public class StickerView extends ImageView {

    private float oldDistance;
    private float oldRotation;
    private PointF centerPointF;

    private enum ActionMode {
        NONE,
        DRAG,
        ZOOM_WITH_ICON,
        ZOOM_WITH_TWO_FINGER,
        DELETE
    }

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Matrix mSizeMatrix;
    private Matrix mDownMatrix;
    private Matrix mMoveMatrix;
    private RectF mStickRect;

    private List<Sticker> stickers = new ArrayList<>();
    private Sticker handleringStiker;
    private Bitmap delIcon;
    private Bitmap zoomIcon;
    private BitmapSticker delIconbitmapStiker;
    private BitmapSticker zoombitmaoStiker;

    private float delIcon_radius = 30f;
    private float zoomIcon_radius = 30f;
    private float mIconExtraRadius = 2;

    private ActionMode currentActionMode = ActionMode.NONE;

    public StickerView(Context context) {
        this(context, null, 0);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setFilterBitmap(true);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setAlpha(160);

        mSizeMatrix = new Matrix();
        mDownMatrix = new Matrix();
        mMoveMatrix = new Matrix();

        mStickRect = new RectF();

        delIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_close_white_18dp);
        delIcon_radius = delIcon.getWidth();
        delIconbitmapStiker = new BitmapSticker(delIcon, new Matrix());
        zoomIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_scale_white_18dp);
        zoomIcon_radius = zoomIcon.getWidth();
        zoombitmaoStiker = new BitmapSticker(delIcon, new Matrix());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (sticker != null) {
                sticker.draw(canvas, mBitmapPaint);
            }
        }
        if (handleringStiker != null) {
            //得到四个点的坐标
            float[] stickerPoints = getStickerPoints(handleringStiker);
            //所有的坐标点
            float x1 = stickerPoints[0];
            float y1 = stickerPoints[1];
            float x2 = stickerPoints[2];
            float y2 = stickerPoints[3];
            float x3 = stickerPoints[4];
            float y3 = stickerPoints[5];
            float x4 = stickerPoints[6];
            float y4 = stickerPoints[7];

            canvas.drawLine(x1, y1, x2, y2, mBorderPaint);
            canvas.drawLine(x1, y1, x3, y3, mBorderPaint);
            canvas.drawLine(x2, y2, x4, y4, mBorderPaint);
            canvas.drawLine(x3, y3, x4, y4, mBorderPaint);

            float rotation = calculateRotation(x3, y3, x4, y4);

            delIconbitmapStiker.setX(x1);
            delIconbitmapStiker.setY(y1);

            zoombitmaoStiker.setX(x4);
            zoombitmaoStiker.setY(y4);

            canvas.drawCircle(x1, y1, delIcon_radius, mBorderPaint);
            Matrix delMatrix = delIconbitmapStiker.getmMatrix();
            delMatrix.reset();//reset很重要
            delMatrix.postRotate(rotation, delIcon.getWidth() / 2, delIcon.getHeight() / 2);
            delMatrix.postTranslate(x1 - delIcon.getWidth() / 2, y1 - delIcon.getHeight() / 2);
            canvas.drawBitmap(delIcon, delMatrix, mBitmapPaint);

            canvas.drawCircle(x4, y4, zoomIcon_radius, mBorderPaint);
            Matrix zoomMatrix = zoombitmaoStiker.getmMatrix();
            zoomMatrix.reset();
            zoomMatrix.postRotate(rotation, zoomIcon.getWidth() / 2, zoomIcon.getHeight() / 2);
            zoomMatrix.postTranslate(x4 - zoomIcon.getWidth() / 2, y4 - zoomIcon.getHeight() / 2);
            canvas.drawBitmap(zoomIcon, zoomMatrix, mBitmapPaint);
        }
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                currentActionMode = ActionMode.DRAG;
                if (checkTouchedDelIcon()) {
                    currentActionMode = ActionMode.DELETE;
                } else if (checkTouchedZoomIcon()) {
                    currentActionMode = ActionMode.ZOOM_WITH_ICON;
                    centerPointF = calculateCenterPoint();
                    oldDistance = calculateDistance(centerPointF.x, centerPointF.y, mDownX, mDownY);
                    oldRotation = calculateRotation(centerPointF.x, centerPointF.y, mDownX, mDownY);
                } else {
                    handleringStiker = assertHandleringSticker();
                }
                if (handleringStiker != null) {
                    mDownMatrix.set(handleringStiker.getMatrix());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);
                centerPointF = calculateCenterPoint(event);
                if (handleringStiker != null && isOnStickerArea(handleringStiker, event.getX(1), event.getY(1))
                        && !checkTouchedDelIcon()) {
                    currentActionMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (currentActionMode) {
                    case DRAG:
                        if (handleringStiker != null) {
                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postTranslate(event.getX() - mDownX, event.getY() - mDownY);//移动
                            handleringStiker.getmMatrix().set(mMoveMatrix);
                            invalidate();
                        }
                        break;
                    case ZOOM_WITH_ICON:
                        if (handleringStiker != null) {
                            float newDistance = calculateDistance(centerPointF.x, centerPointF.y,
                                    event.getX(), event.getY());
                            float newRotation = calculateRotation(centerPointF.x, centerPointF.y,
                                    event.getX(), event.getY());
                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, centerPointF.x, centerPointF.y);
                            mMoveMatrix.postRotate(newRotation - oldRotation, centerPointF.x, centerPointF.y);
                            handleringStiker.getmMatrix().reset();
                            handleringStiker.getmMatrix().set(mMoveMatrix);
                            invalidate();
                        }
                        break;
                    case ZOOM_WITH_TWO_FINGER:
                        if (handleringStiker != null) {
                            float newDistance = calculateDistance(event);
                            float newRotation = calculateRotation(event);
                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, centerPointF.x, centerPointF.y);
                            mMoveMatrix.postRotate(newRotation - oldRotation, centerPointF.x, centerPointF.y);
                            handleringStiker.getmMatrix().set(mMoveMatrix);
                            invalidate();
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (currentActionMode) {
                    case DELETE:
                        stickers.remove(handleringStiker);
                        handleringStiker.release();
                        handleringStiker = null;
                        invalidate();
                        break;
                }
                currentActionMode = ActionMode.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                currentActionMode = ActionMode.NONE;
                break;
        }
        return true;
    }

    private PointF calculateCenterPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private float calculateRotation(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        double radius = Math.atan2(y, x);
        return (float) Math.toDegrees(radius);
    }

    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float calculateDistance(float x, float y, float mDownX, float mDownY) {
        return (float) Math.sqrt((mDownX - x) * (mDownX - x) + (mDownY - y) * (mDownY - y));
    }

    private PointF calculateCenterPoint() {
        return handleringStiker.getMappedCenterPoint();
    }

    private Sticker assertHandleringSticker() {
        //倒叙判断最上层的
        for (int i = stickers.size() - 1; i >= 0; i--) {
            if (isOnStickerArea(stickers.get(i))) {
                return stickers.get(i);
            }
        }
        return null;
    }

    private boolean isOnStickerArea(Sticker sticker) {
        RectF mappedBoundPoints = sticker.getMapBound();
        return mappedBoundPoints.contains(mDownX, mDownY);
    }

    private boolean isOnStickerArea(Sticker sticker, float downX, float downY) {
        RectF dst = sticker.getMapBound();
        return dst.contains(downX, downY);
    }

    private boolean checkTouchedZoomIcon() {
        float distanceX = zoombitmaoStiker.getX() - mDownX;//按下点和 del图标的距离判断
        float distanceY = zoombitmaoStiker.getY() - mDownY;
        float distance_pow = distanceX * distanceX + distanceY * distanceY;
        return distance_pow <= (zoomIcon_radius + mIconExtraRadius) * (zoomIcon_radius + mIconExtraRadius);
    }

    private boolean checkTouchedDelIcon() {
        float distanceX = delIconbitmapStiker.getX() - mDownX;//按下点和 del图标的距离判断
        float distanceY = delIconbitmapStiker.getY() - mDownY;
        float distance_pow = distanceX * distanceX + distanceY * distanceY;
        return distance_pow <= (delIcon_radius + mIconExtraRadius) * (delIcon_radius + mIconExtraRadius);
    }

    private float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radius = Math.atan2(y, x);
        return (float) Math.toDegrees(radius);
    }

    public float[] getStickerPoints(Sticker sticker) {
        if (sticker == null) return new float[8];
        return sticker.getMappedBoundPoints();
    }

    public void addSticker(Bitmap stickerBitmap) {
        Matrix matrix = new Matrix();
        BitmapSticker bitmapStiker = new BitmapSticker(stickerBitmap, matrix);
        handleringStiker = bitmapStiker;
        stickers.add(bitmapStiker);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (sticker != null) {
                transformSticker(sticker);
            }
        }
    }

    private void transformSticker(Sticker sticker) {
        if (sticker == null) {
            return;
        }
        if (mSizeMatrix != null) {
            mSizeMatrix.reset();
        }
        //step 1
        float offsetX = (getWidth() - sticker.getWidth()) / 2;
        float offsetY = (getHeight() - sticker.getHeight()) / 2;

        mSizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / sticker.getWidth();
        } else {
            scaleFactor = (float) getHeight() / sticker.getHeight();
        }

        mSizeMatrix.postScale(scaleFactor / 2, scaleFactor / 2,
                getWidth() / 2, getHeight() / 2);

        sticker.getMatrix().reset();
        sticker.getMatrix().set(mSizeMatrix);

        invalidate();
    }
}
