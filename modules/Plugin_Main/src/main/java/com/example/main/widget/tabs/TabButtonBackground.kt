package com.example.main.widget.tabs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.asin


class TabButtonBackground @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private var isDraw: Boolean = false

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var ui: UiData = UiData()

    fun setActive(active: Boolean) {
        ui.isActive = active
        invalidate()
    }

    fun setStatus(active: Boolean, isFirst: Boolean, isEnd: Boolean) {
        ui.isActive = active
        ui.isFirst = isFirst
        ui.isEnd = isEnd
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDraw) return
        isDraw = true
        onDrawButton(canvas)
        isDraw = false
    }

    private fun onDrawButton(canvas: Canvas) {
        ui.setSize(width.toFloat(), height.toFloat())

        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 按钮外凹槽
        val curvePath = ui.getCurvePath()
        val curveClipPath = ui.getCurveClipPath()
        canvas.clipPath(curveClipPath)
        mPaint.apply {
            reset()
            color = Color.parseColor(Constant.SHADOW_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = Constant.getBorderWidth()
            maskFilter = BlurMaskFilter(Constant.getVerticalWidth(), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(curvePath, mPaint)
        // 连接线
        mPaint.apply {
            reset()
            color = Color.parseColor(Constant.BORDER_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = Constant.getBorderWidth()
        }
        canvas.drawPath(ui.getConnectPath(), mPaint)

        // 按钮背景
        val buttonPath = ui.getButtonPath()
        mPaint.apply {
            reset()
            color = Color.parseColor(ui.buttonColor())
            style = Paint.Style.FILL
        }
        canvas.drawPath(buttonPath, mPaint)

        // 按钮内边框阴影
        val buttonShadowPath = ui.getButtonShadowPath()
        canvas.clipPath(buttonPath)
        buttonShadowPath.apply {
            fillType = Path.FillType.INVERSE_WINDING
        }
        mPaint.apply {
            color = Color.parseColor(ui.innerShadow())
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(Constant.getBorderWidth(), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(buttonShadowPath, mPaint)

        // 按钮边框
        mPaint.apply {
            reset()
            color = Color.parseColor(Constant.BORDER_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = Constant.getBorderWidth() * 2
        }
        canvas.drawPath(buttonPath, mPaint)

        // 按钮渐变
        val innerPath = ui.getButtonInnerPath()
        val linearGradient = LinearGradient(
            0f, 0f, 0f, ui.getInnerHeight(), Color.parseColor(ui.gradientColor()), Color.TRANSPARENT, Shader.TileMode.CLAMP
        )
        mPaint.apply {
            reset()
            shader = linearGradient
        }
        canvas.drawPath(innerPath, mPaint)
    }

    inner class UiData {
        var isActive: Boolean = true
        var isFirst: Boolean = true
        var isEnd: Boolean = true
        private var boxWidth: Float = 0f
        private var boxHeight: Float = 0f

        fun setSize(width: Float, height: Float) {
            this.boxWidth = width
            this.boxHeight = height
        }

        fun buttonColor() = if (isActive) Constant.ACTIVE_BUTTON_COLOR else Constant.BUTTON_COLOR

        fun innerShadow() = if (isActive) Constant.ACTIVE_INNER_SHADOW else Constant.INNER_SHADOW

        fun gradientColor() = if (isActive) Constant.ACTIVE_GRADIENT_COLOR else Constant.GRADIENT_COLOR

        fun getInnerHeight(): Float {
            return boxHeight - Constant.getVerticalWidth() * 4 - Constant.getBorderWidth() * 2
        }

        fun getConnectPath(): Path {
            val rectF = RectF(
                Constant.getHorizontalWidth(),
                Constant.getVerticalWidth(),
                boxWidth - Constant.getHorizontalWidth(),
                boxHeight - Constant.getVerticalWidth(),
            )
            return getConnectLine(rectF.height() / 2f, rectF)
        }

        fun getCurvePath(): Path {
            val rect = RectF(0f, 0f, boxWidth, boxHeight)
            return getCurveNewPath(rect.height() / 2f, rect, true)
        }

        fun getCurveClipPath(): Path {
            val rect = RectF(0f, 0f, boxWidth, boxHeight)
            return getCurveNewPath(rect.height() / 2f, rect)
        }

        fun getButtonPath(): Path {
            return Path().apply {
                val rectF = RectF(
                    Constant.getHorizontalWidth(),
                    Constant.getVerticalWidth(),
                    boxWidth - Constant.getHorizontalWidth(),
                    boxHeight - Constant.getVerticalWidth(),
                )
                addRoundRect(rectF, rectF.height() / 2, rectF.height() / 2, Path.Direction.CW)
            }
        }

        fun getButtonShadowPath(): Path {
            return Path().apply {
                val rectF = RectF(
                    Constant.getHorizontalWidth() + Constant.getBorderWidth(),
                    Constant.getVerticalWidth() + 2 * Constant.getBorderWidth(),
                    boxWidth - Constant.getHorizontalWidth() - Constant.getBorderWidth(),
                    boxHeight - Constant.getVerticalWidth(),
                )
                addRoundRect(rectF, rectF.height() / 2, rectF.height() / 2, Path.Direction.CW)
            }
        }

        fun getButtonInnerPath(): Path {
            return Path().apply {
                val rectF = RectF(
                    Constant.getHorizontalWidth() * 2 + Constant.getBorderWidth(),
                    Constant.getVerticalWidth() * 2 + Constant.getBorderWidth(),
                    boxWidth - Constant.getHorizontalWidth() * 2 - Constant.getBorderWidth(),
                    boxHeight - Constant.getVerticalWidth() * 2 - Constant.getBorderWidth(),
                )
                addRoundRect(rectF, rectF.height() / 2, rectF.height() / 2, Path.Direction.CW)
            }
        }

        private fun getConnectLine(round: Float, rectF: RectF): Path {
            val path = Path()
            path.reset()
            if (!isFirst) {
                path.moveTo(-Constant.getConnectExtend(), rectF.top + rectF.height() / 2f)
                path.lineTo(rectF.left, rectF.top + rectF.height() / 2f)
            }
            if (!isEnd) {
                path.moveTo(rectF.right, rectF.top + rectF.height() / 2f)
                path.lineTo(rectF.right + rectF.left + Constant.getConnectExtend(), rectF.top + rectF.height() / 2f)
            }
            return path
        }

        private fun getCurveNewPath(round: Float, rectF: RectF, hasBreak: Boolean = false): Path {
            val degrees: Float = Math.toDegrees(asin(Constant.getConnectWidth() / round.toDouble())).toFloat() / 2f
            val leftDegrees = if (isFirst) 0f else degrees
            val rightDegrees = if (isEnd) 0f else degrees
            val path = Path()
            path.reset()
            if (isFirst) {
                path.moveTo(rectF.left, rectF.top + round)
            } else {
                path.moveTo(rectF.left - Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2)
                path.lineTo(rectF.left, (rectF.height() - Constant.getConnectWidth()) / 2)
            }
            if (round > 0f) {
                path.arcTo(RectF(rectF.left, rectF.top, rectF.left + round * 2f, rectF.top + round * 2f), 180f + leftDegrees, 90f - leftDegrees)
            }
            path.lineTo(rectF.right - round, rectF.top)
            if (round > 0f) {
                path.arcTo(RectF(rectF.right - round * 2f, rectF.top, rectF.right, rectF.top + round * 2f), 270f, 90f - rightDegrees)
            }
            if (isEnd) {
                path.lineTo(rectF.right, rectF.bottom - round)
            } else {
                path.lineTo(rectF.right + Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2)
                if (hasBreak) path.moveTo(rectF.right + Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2 + Constant.getConnectWidth())
                else path.lineTo(rectF.right + Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2 + Constant.getConnectWidth())
                path.lineTo(rectF.right, (rectF.height() - Constant.getConnectWidth()) / 2 + Constant.getConnectWidth())
            }
            if (round > 0f) {
                path.arcTo(RectF(rectF.right - round * 2f, rectF.bottom - round * 2f, rectF.right, rectF.bottom), 0f + rightDegrees, 90f - rightDegrees)
            }
            path.lineTo(rectF.left + round, rectF.bottom)
            if (round > 0f) {
                path.arcTo(RectF(rectF.left, rectF.bottom - round * 2f, rectF.left + round * 2f, rectF.bottom), 90f, 90f - leftDegrees)
            }
            if (isFirst) {
                path.lineTo(rectF.left, rectF.top + round)
            } else {
                path.lineTo(rectF.left - Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2 + Constant.getConnectWidth())
                if (hasBreak) path.moveTo(rectF.left - Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2)
                else path.lineTo(rectF.left - Constant.getConnectExtend(), (rectF.height() - Constant.getConnectWidth()) / 2)
            }
            return path
        }
    }
}