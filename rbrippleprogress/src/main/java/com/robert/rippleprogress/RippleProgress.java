package com.robert.rippleprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

import com.robert.rippleprogress.util.ViewUtil;

/**
 * Created by robert on 2017/9/9.
 */

public class RippleProgress extends View {
    private int waterWaveResid;
    private Bitmap waterWaveBitmap;
    private Paint paint;

    /**
     * 背景颜色
     */
    private int progressBackgroundColor= Color.WHITE;

    private int x;
    private int y;

    private int viewWidth;
    private int viewHeight;

    private float progress=0.0f;
    /**
     * 水波动画频率
     */
    private int waveDuration=3000;
    /**
     * 上升动画频率
     */
    private int upDuration=1000;

    /**
     *百分比文字颜色
     */
    private int progressColor= Color.BLACK;
    /**
     * 百分比文字大小
     */
    private int progressTextSize=30;

    /**
     * 是否显示百分比
     */
    private boolean showPercent=true;
    private Paint borderPaint;
    /**
     * 描边的宽度
     */
    private float borderWidth=2;
    /**
     * 描边的颜色
     */
    private int borderColor= Color.GRAY;

    private ValueAnimator animatorW;

    public void setUpDuration(int upDuration) {
        this.upDuration = upDuration;

        if (animatorH!=null){
            animatorH.setDuration(upDuration);
            animatorH.cancel();
            animatorH.start();
        }

    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setShowPercent(boolean showPercent) {
        this.showPercent = showPercent;
    }

    private ValueAnimator animatorH;

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setProgressTextSize(int progressTextSize) {
        this.progressTextSize = progressTextSize;
    }

    public void setWaveDuration(int waveDuration) {
        this.waveDuration = waveDuration;

        if (animatorW!=null){
            animatorW.setDuration(waveDuration);
            animatorW.cancel();
            animatorW.start();
        }

    }

    public void setProgressBackgroundColor(int progressBackgroundColor) {
        this.progressBackgroundColor = progressBackgroundColor;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (animatorH!=null) {
            animatorH.cancel();
            animatorH.start();
        }
    }

    public void setWaterWaveResid(int waterWaveResid) {
        this.waterWaveResid = waterWaveResid;
    }

    public RippleProgress(Context context) {
        super(context);
        initView();
    }

    public RippleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RippleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        waterWaveResid = R.mipmap.ripple;
        waterWaveBitmap = BitmapFactory.decodeResource(getResources(), waterWaveResid);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewWidth=getWidth();
                viewHeight=getHeight();

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                y=viewHeight;

                animatorW = ValueAnimator.ofInt(0, -(waterWaveBitmap.getWidth() - viewWidth));

                animatorW.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        x = (int) valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                });

                AccelerateInterpolator ll = new AccelerateInterpolator();
                animatorW.setInterpolator(ll);
                animatorW.setDuration(waveDuration);
                animatorW.setRepeatMode(ValueAnimator.REVERSE);
                animatorW.setRepeatCount(Animation.INFINITE);
                animatorW.start();


                animatorH = ValueAnimator.ofInt(0, viewHeight);
                animatorH.setInterpolator(ll);
                animatorH.setDuration(upDuration);
                animatorH.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int v = (int) valueAnimator.getAnimatedValue();
                        y=(int)(viewHeight- v * progress);
                        invalidate();
                    }
                });

                animatorH.setInterpolator(ll);
                animatorH.start();
            }
        });
    }


    public void setProgressAnimate(float progress){
        this.progress=progress;

        if (animatorH!=null) {
            animatorH.start();
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
        canvas.clipPath(path);
        paint.setColor(progressBackgroundColor);
        canvas.drawCircle(viewWidth/2,viewHeight/2,viewWidth/2,paint);
        Rect srcRect=new Rect();
        srcRect.left=0;
        srcRect.top=0;
        srcRect.right=waterWaveBitmap.getWidth();
        srcRect.bottom=waterWaveBitmap.getHeight();

        float scale=waterWaveBitmap.getHeight()/getHeight();

        RectF dstRect=new RectF();
        dstRect.left=x;
        dstRect.top=y;
        dstRect.right=waterWaveBitmap.getWidth()/scale;
        dstRect.bottom=waterWaveBitmap.getHeight()/scale;
        canvas.drawBitmap(waterWaveBitmap, srcRect, dstRect, paint);

        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth*2);
        canvas.drawCircle(viewWidth/2,viewHeight/2,viewWidth/2,borderPaint);

        if (showPercent) {
            paint.setColor(progressColor);
            paint.setTextSize(ViewUtil.sp2px(getContext(), progressTextSize));

            String progressStr = (int) (progress * 100) + "%";
            Rect rect = new Rect();
            paint.getTextBounds(progressStr, 0, progressStr.length(), rect);
            int w = rect.width();
            int h = rect.height();

            canvas.drawText(progressStr, viewWidth / 2 - w / 2, viewHeight / 2 + h / 2, paint);
        }

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility== View.GONE||visibility== View.INVISIBLE){
            if (waterWaveBitmap!=null) {
                if (!waterWaveBitmap.isRecycled()) {
                    waterWaveBitmap.recycle();
                }
            }

            if (animatorH!=null){
                animatorH.end();
            }

            if(animatorW!=null){
                animatorW.end();
            }

        }else{
            if (waterWaveBitmap.isRecycled()){
                waterWaveBitmap = BitmapFactory.decodeResource(getResources(), waterWaveResid);
            }
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility== View.GONE||visibility== View.INVISIBLE){
            if (waterWaveBitmap!=null) {
                if (!waterWaveBitmap.isRecycled()) {
                    waterWaveBitmap.recycle();
                }
            }

            if (animatorH!=null){
                animatorH.end();
            }

            if(animatorW!=null){
                animatorW.end();
            }
        }else{
            if (waterWaveBitmap.isRecycled()){
                waterWaveBitmap = BitmapFactory.decodeResource(getResources(), waterWaveResid);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
