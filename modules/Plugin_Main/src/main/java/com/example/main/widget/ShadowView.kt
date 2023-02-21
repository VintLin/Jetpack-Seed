package com.example.main.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class ShadowView : View {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    private var isDraw: Boolean = false

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var ui: ShadowUiData? = null

    fun setData(ui: ShadowUiData) {
        this.ui = ui
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            if (isDraw) return
            isDraw = true
            onDrawShadow(canvas)
            isDraw = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onDrawShadow(canvas: Canvas) {
        ui?.apply {
            if (boxHeight == 0f) boxHeight = height.toFloat()
            if (boxWidth == 0f) boxWidth = width.toFloat()
            canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            // 外阴影
            val outerPath = getOuterShadowPath()
            if (outerPath != null && outerShadow != null) {
                mPaint.color = outerShadow!!.color
                mPaint.style = Paint.Style.FILL
                mPaint.maskFilter = BlurMaskFilter(outerShadow!!.blur, BlurMaskFilter.Blur.NORMAL)
                canvas.drawPath(outerPath, mPaint)
            }

            // 视图背景
            mPaint.reset()
            mPaint.color = color
            mPaint.style = Paint.Style.FILL
            canvas.drawPath(getClipPath(), mPaint)

            // 内阴影
            val path = getInnerShadowPath()
            if (path != null && innerShadow != null) {
                // 内阴影绘制范围
                canvas.clipPath(getClipPath())
                path.fillType = Path.FillType.INVERSE_WINDING
                mPaint.color = innerShadow!!.color
                mPaint.style = Paint.Style.FILL
                val otherDraw: Boolean = innerShadow!!.blur < innerShadow!!.getRadius()
                mPaint.maskFilter = BlurMaskFilter(innerShadow!!.blur, if (otherDraw) BlurMaskFilter.Blur.OUTER else BlurMaskFilter.Blur.NORMAL)
                canvas.drawPath(path, mPaint)
                if (otherDraw) {
                    mPaint.maskFilter = BlurMaskFilter(innerShadow!!.getRadius(), BlurMaskFilter.Blur.INNER)
                    canvas.drawPath(path, mPaint)
                }
            }
        }
    }

}