package com.example.main.widget.progress;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.example.base.app.App;
import com.example.main.R;

import me.jessyan.autosize.utils.AutoSizeUtils;

class ProgressUiData {

    //进度条的圆角
    float radius = 2f;

    //进度条描边的颜色
    int borderColor = Color.YELLOW;

    //进度条描边的大小
    float borderSize = 2f;

    //进度条进度的颜色
    int progressColor = Color.RED;

    //条纹的颜色
    int zebraColor = Color.YELLOW;

    //条纹的间隔
    float zebraSize = 50f;

    //条纹的间隔
    float zebraGap = 75f;

    //外边框颜色
    int boxBorderColor = Color.GREEN;

    //外边框描边颜色
    int boxStrokeColor = Color.GRAY;

    //外边框背景颜色
    int boxBgColor = Color.YELLOW;

    //外边框描边粗细
    float boxStrokeSize = 0f;

    //外边框粗细
    float boxBorderSize = 0f;

    //外边框与进度条之间的垂直边距
    float boxVerticalPadding = 0f;

    //外边框与进度条之间的水平边距
    float boxHorizontalPadding = 0f;

    //外边框描边
    RectF boxBorderBgRect;

    //外边框
    RectF boxBorderRect;

    // 外边框背景
    RectF boxRect;


    /**
     * 初始化默认数值
     *
     * @param context context
     */
    public void initDefaultValue(Context context) {
        boxBorderColor = ContextCompat.getColor(context, R.color.download_box_border);
        boxStrokeColor = ContextCompat.getColor(context, R.color.download_box_stroke);
        boxBgColor = ContextCompat.getColor(context, R.color.download_box_bg);

        boxStrokeSize = AutoSizeUtils.dp2px(App.get(), 2);
        boxBorderSize = AutoSizeUtils.dp2px(App.get(), 18);
        boxHorizontalPadding = AutoSizeUtils.dp2px(App.get(), 24);
        boxVerticalPadding = AutoSizeUtils.dp2px(App.get(), 22);
    }


    /**
     * 设置一般进度条颜色
     *
     * @param context context
     */
    public void setNormalProgress(Context context) {
        borderColor = ContextCompat.getColor(context, R.color.download_progress_error_border);
        progressColor = ContextCompat.getColor(context, R.color.download_progress_error_bg);
        zebraColor = ContextCompat.getColor(context, R.color.download_progress_error_zebra);
    }

    /**
     * 获取当前进度条条纹偏移量
     *
     * @param value 动画进度
     * @return 偏移量
     */
    public float getAnimateOffset(float value) {
        return (zebraSize + zebraGap) * value;
    }

    /**
     * 获取当前进度对应的进度条长度
     *
     * @param width   视图宽度
     * @param percent 进度条百分比（0.0～1.0）
     * @return 进度条宽度
     */
    public float getProgressWidth(float width, float percent) {
        return (width - 2 * boxHorizontalPadding) * percent;
    }

    /**
     * 获取进度条边界数据
     *
     * @param progressWidth 进度条宽度
     * @param boxHeight     视图高度
     * @param rectF         计算完成后将边界数据设置进此变量中
     * @return 边界数据
     */
    public RectF setProgressRect(float progressWidth, float boxHeight, RectF rectF) {
        final RectF rect = new RectF(boxHorizontalPadding, boxVerticalPadding, progressWidth + boxHorizontalPadding, boxHeight - boxVerticalPadding);
        rectF.set(rect);
        return rect;
    }


    /**
     * 进度条边框边界数据
     *
     * @param progressRect 进度条边界数据
     * @param rectF        计算完成后将边界数据设置进此变量中
     */
    public void setProgressBorderRect(RectF progressRect, RectF rectF) {
        rectF.set(progressRect.left + borderSize / 2, progressRect.top + borderSize / 2, progressRect.right - borderSize / 2, progressRect.bottom - borderSize / 2);
    }

    /**
     * 显示的条纹个数
     *
     * @param progressWidth 进度条宽度
     * @return 条纹个数
     */
    public int getZebraNum(float progressWidth) {
        return (int) (progressWidth / (zebraSize + zebraGap));
    }

    /**
     * 获取条纹左上角的X坐标值
     *
     * @param offset       偏移量
     * @param index        第几个条纹
     * @param progressRect 进度条边界数据
     * @return 进度条左上角的X坐标值
     */
    public float getZebraTopLeft(float offset, float index, RectF progressRect) {
        return offset + progressRect.left + index * (zebraSize + zebraGap);
    }

    /**
     * 当开始绘制进度条边框时调用此方法
     *
     * @param height 视图高度
     * @param width  视图宽度
     */
    public void whenDrawBox(float height, float width) {
        if (boxBorderBgRect != null && boxBorderRect != null && boxRect != null) return;
        boxBorderBgRect = new RectF((boxBorderSize + boxStrokeSize) / 2, (boxBorderSize + boxStrokeSize) / 2, width - (boxBorderSize + boxStrokeSize) / 2, height - (boxBorderSize + boxStrokeSize) / 2);
        boxBorderRect = new RectF(boxStrokeSize + (boxBorderSize - boxStrokeSize) / 2, boxStrokeSize + (boxBorderSize - boxStrokeSize) / 2, width - boxStrokeSize - (boxBorderSize - boxStrokeSize) / 2, height - boxStrokeSize - (boxBorderSize - boxStrokeSize) / 2);
        boxRect = new RectF(0, 0, width, height);
    }

    public void setBoxBorderBgRect(RectF rectF) {
        if (boxBorderBgRect != null) rectF.set(boxBorderBgRect);
    }

    public void setBoxBorderRect(RectF rectF) {
        if (boxBorderRect != null) rectF.set(boxBorderRect);
    }

    public void setBoxRect(RectF rectF) {
        if (boxRect != null) rectF.set(boxRect);
    }

    public float getBoxStrokeWidth() {
        return boxBorderSize + boxStrokeSize;
    }

    public float getBoxBorderSize() {
        return boxBorderSize - boxStrokeSize;
    }
}
