package com.example.main.widget.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.example.base.app.App;
import com.example.main.R;

import me.jessyan.autosize.utils.AutoSizeUtils;


public class ProgressView extends View {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF mRectF = new RectF();

    private final Path mPath = new Path();

    private final Path mZebraPath = new Path();

    private boolean isDraw = false;

    private float offset = 0f;

    private Context context;

    // 监听进度动画
    private Listener listener;

    // 动画
    private ValueAnimator animator;

    // 进度条样式数值
    private final ProgressUiData data = new ProgressUiData();

    //最大进度
    private int max = 100;

    //进度条
    private float progress = 0f;

    //动画进度条
    private float animateProgress = 0f;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);
            max = typeArray.getInt(R.styleable.ProgressView_max, 100);

            progress = typeArray.getFloat(R.styleable.ProgressView_progress, 0);

            data.radius = AutoSizeUtils.dp2px(App.get(), typeArray.getFloat(R.styleable.ProgressView_progressRadius, 0f));

            data.borderSize = AutoSizeUtils.dp2px(App.get(), typeArray.getFloat(R.styleable.ProgressView_borderSize, 1f));

            data.zebraSize = AutoSizeUtils.dp2px(App.get(), typeArray.getFloat(R.styleable.ProgressView_zebraSize, 10f));

            data.zebraGap = AutoSizeUtils.dp2px(App.get(), typeArray.getFloat(R.styleable.ProgressView_zebraGap, 10f));
            // 初始化默认值
            data.initDefaultValue(context);
            // 初始化进度条条纹颜色
            data.setNormalProgress(context);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        try {
            listener = null;
            context = null;
            clearAnimation();
            animator.cancel();
            animator = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听进度条
     *
     * @param listener 监听
     */
    public void setOnUpdateListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setProgress(float progress) {
        if (progress < max) this.progress = progress;
        else this.progress = max;
        if (progress == max || progress == 0) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (isDraw) return;
            isDraw = true;
            onDrawBox(canvas);
//            onDrawProgress(canvas);
            isDraw = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 画进度条外边框
     *
     * @param canvas canvas
     */
    private void onDrawBox(Canvas canvas) {

        int colorShadow = Color.parseColor("#6EE7FF");
        int colorBorder = Color.WHITE;
        int colorBackground = Color.parseColor("#6ED1FF");

        float shadowRadius = AutoSizeUtils.dp2px(context, 37);
        float shadowHeight = AutoSizeUtils.dp2px(context, 86);
        float borderMargin = AutoSizeUtils.dp2px(context, 12);
        float borderExpand = AutoSizeUtils.dp2px(context, 4);

        float backgroundMarginV = AutoSizeUtils.dp2px(context, 27);
        float backgroundMarginH = AutoSizeUtils.dp2px(context, 15);


        RectF rect;
        Path path;
        // 画阴影
        mPaint.reset();
        mPaint.setColor(colorShadow);
        mPaint.setStyle(Paint.Style.FILL);
        path = new Path();
        rect = new RectF(0f, 0f, getWidth(), shadowHeight);
        path.addRoundRect(rect, shadowRadius, shadowRadius, Path.Direction.CCW);
        canvas.drawPath(path, mPaint);

        // 画外边框
        canvas.clipPath(path);
        rect = new RectF(0f, borderMargin, getWidth(), shadowHeight + borderMargin);
        path = new Path();
        path.addRoundRect(rect, shadowRadius, shadowRadius, Path.Direction.CCW);
        mPaint.reset();
        mPaint.setColor(colorBorder);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setMaskFilter(new BlurMaskFilter(borderExpand, BlurMaskFilter.Blur.NORMAL));
        canvas.drawPath(path, mPaint);

        // 画边框背景
        mPaint.reset();
        mPaint.setColor(colorBackground);
        mPaint.setStyle(Paint.Style.FILL);
        rect = new RectF(backgroundMarginH, backgroundMarginV, getWidth() - backgroundMarginH, getHeight() - backgroundMarginV);
        canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, mPaint);

        // 进度条宽度
        final float progressWidth = rect.width() * animateProgress / max;
        final RectF progressRect = new RectF(backgroundMarginH, backgroundMarginV, backgroundMarginH + progressWidth, getHeight() - backgroundMarginV);
        // 进度条颜色
        final int zebraColor = Color.parseColor("#66FFEC94");

        //裁剪进度的形状
        mPath.reset();
        mPath.addRoundRect(progressRect, progressRect.height() / 2, progressRect.height() / 2, Path.Direction.CCW);
        canvas.save();
        canvas.clipPath(mPath);

        //画进度
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FFEF95"));
        canvas.drawRoundRect(progressRect, data.radius, data.radius, mPaint);
        LinearGradient linearGradient = new LinearGradient(0f, 0f, (float) progressRect.width(), 0f, Color.parseColor("#FFD964"), Color.parseColor("#FFAF30"), Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        rect = new RectF(backgroundMarginH + borderExpand, backgroundMarginV + borderExpand, backgroundMarginH + progressWidth - borderExpand, getHeight() - backgroundMarginV - borderExpand);
        mPaint.setMaskFilter(new BlurMaskFilter(rect.height(), BlurMaskFilter.Blur.NORMAL));
        canvas.drawRoundRect(rect, data.radius, data.radius, mPaint);

        //画条纹
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(zebraColor);
        int zebraNum = data.getZebraNum(progressWidth);

        float top = progressRect.top;
        float topLeft;
        float topRight;

        float bottom = progressRect.bottom;
        float bottomLeft;
        float bottomRight;

        mZebraPath.reset();

        for (int n = -2; n <= zebraNum + 2; n++) {
            topLeft = data.getZebraTopLeft(offset, n, progressRect);
            topRight = topLeft + data.zebraSize;
            bottomLeft = topLeft - data.zebraSize / 2;
            bottomRight = topLeft + data.zebraSize / 2;

            mZebraPath.rewind();
            mZebraPath.moveTo(topLeft, top);
            mZebraPath.lineTo(topRight, top);
            mZebraPath.lineTo(bottomRight, bottom);
            mZebraPath.lineTo(bottomLeft, bottom);
            mZebraPath.close();
            canvas.drawPath(mZebraPath, mPaint);
        }

        canvas.restore();

        if (listener != null) listener.onProgressUpdate(animateProgress, progressWidth);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                final float value = (float) animation.getAnimatedValue();
                offset = data.getAnimateOffset(value);
                // 当动画进度和实际进度一致时就进行刷新
                if (animateProgress == this.progress) return;
                animateProgress = animateProgress + (this.progress - animateProgress) * value;
                invalidate();
            });
        }
        animator.start();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }


    public abstract static class Listener {
        public abstract void onProgressUpdate(float progress, float offset);
    }
}
