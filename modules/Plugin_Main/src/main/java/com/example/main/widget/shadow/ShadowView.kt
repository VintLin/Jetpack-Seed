package com.example.main.widget.shadow

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.example.main.R

/**
 * 阴影绘制控件，支持：
 * - 内阴影绘制
 * - 外阴影绘制
 * - 圆角绘制
 *
 * 参考代码：
 * `
 *  val ui = ShadowUiData.Config()
 *      .setColor(Color.WHITE)
 *      .setShadow(
 *          outer = ShadowUiData.Shadow(
 *              0f, 0f, 20f, 10f, Color.parseColor("#999999")
 *              ),
 *          inner = ShadowUiData.Shadow(
 *              0f, 0f, 50f, -10f, Color.parseColor("#CCCCCC")
 *              ),
 *          )
 *       .setRound(
 *          ShadowUiData.Round(
 *              leftTop = 30f,
 *              rightTop = 40f,
 *              rightBottom = 50f,
 *              leftBottom = 60f,
 *          )
 *       ).getUiData()
 *  <ShadowView>.setData(ui)
 * `
 */
class ShadowView : RelativeLayout {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        setWillNotDraw(false)
        if (attributeSet != null) getAttr(attributeSet)
    }

    private var isDraw: Boolean = false

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var ui: ShadowUiData? = null

    private fun getAttr(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowView)
        val config = ShadowUiData.Config()
        config.setRound(
            ShadowUiData.Round(
                typedArray.getInteger(R.styleable.ShadowView_round_shadow_left_top, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_round_shadow_right_top, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_round_shadow_right_bottom, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_round_shadow_left_bottom, 0).toFloat(),
            )
        )
        config.setShadow(
            inner = ShadowUiData.Shadow(
                typedArray.getInteger(R.styleable.ShadowView_inner_shadow_x, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_inner_shadow_y, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_inner_shadow_blur, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_inner_shadow_expand, 0).toFloat(),
                typedArray.getColor(R.styleable.ShadowView_inner_shadow_color, Color.TRANSPARENT),
            ),
            outer = ShadowUiData.Shadow(
                typedArray.getInteger(R.styleable.ShadowView_outer_shadow_x, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_outer_shadow_y, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_outer_shadow_blur, 0).toFloat(),
                typedArray.getInteger(R.styleable.ShadowView_outer_shadow_expand, 0).toFloat(),
                typedArray.getColor(R.styleable.ShadowView_outer_shadow_color, Color.TRANSPARENT),
            ),
        )
        config.setColor(typedArray.getColor(R.styleable.ShadowView_view_color, Color.TRANSPARENT))
        ui = config.getUiData()
        typedArray.recycle()
    }


    fun getData(): ShadowUiData? = ui

    /**
     * 设置参数
     */
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
                mPaint.reset()
                mPaint.color = outerShadow!!.color
                mPaint.style = Paint.Style.FILL
                if (outerShadow?.blur != 0f) mPaint.maskFilter = BlurMaskFilter(outerShadow!!.blur, BlurMaskFilter.Blur.NORMAL)
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
                if (innerShadow?.blur != 0f) mPaint.maskFilter = BlurMaskFilter(innerShadow!!.blur, if (otherDraw) BlurMaskFilter.Blur.OUTER else BlurMaskFilter.Blur.NORMAL)
                canvas.drawPath(path, mPaint)
                if (otherDraw && innerShadow?.blur != 0f) {
                    mPaint.maskFilter = BlurMaskFilter(innerShadow!!.getRadius(), BlurMaskFilter.Blur.INNER)
                    canvas.drawPath(path, mPaint)
                }
            }
        }
    }

}