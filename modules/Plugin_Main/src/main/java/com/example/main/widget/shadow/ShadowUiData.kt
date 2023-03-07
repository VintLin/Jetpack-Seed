package com.example.main.widget.shadow

import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max

class ShadowUiData private constructor() {
    // 视图宽度
    var boxWidth: Float = 0f

    // 视图高度
    var boxHeight: Float = 0f

    // 视图圆角
    private var round: Round = Round()

    // 视图颜色
    var color = Color.TRANSPARENT

    // 视图内阴影
    var outerShadow: Shadow? = null
        private set

    // 视图外阴影
    var innerShadow: Shadow? = null
        private set

    private var clipRect: RectF? = null

    private var outerShadowRect: RectF? = null

    private var innerShadowRect: RectF? = null

    private var clipPath: Path? = null

    private var outerShadowPath: Path? = null

    private var innerShadowPath: Path? = null

    private fun getClipRect(): RectF {
        if (clipRect == null) clipRect = RectF(
            0f, 0f, boxWidth, boxHeight
        )
        return clipRect!!
    }

    private fun getOuterShadowRect(): RectF? {
        if (outerShadow != null && outerShadowRect == null) {
            val shadow: Shadow = outerShadow!!
            outerShadowRect = RectF(
                shadow.x - shadow.expand,
                shadow.y - shadow.expand,
                boxWidth + shadow.x + shadow.expand,
                boxHeight + shadow.y + shadow.expand,
            )
        }
        return outerShadowRect
    }

    private fun getInnerShadowRect(): RectF? {
        if (innerShadow != null && innerShadowRect == null) {
            val shadow: Shadow = innerShadow!!
            innerShadowRect = RectF(
                shadow.x - shadow.expand,
                shadow.y - shadow.expand,
                boxWidth + shadow.x + shadow.expand,
                boxHeight + shadow.y + shadow.expand,
            )
        }
        return innerShadowRect
    }

    fun getClipPath(): Path {
        if (clipPath == null) clipPath = getOutLinePath(round, getClipRect(), null)
        return clipPath!!
    }

    fun getOuterShadowPath(): Path? {
        val rect = getOuterShadowRect()
        if (rect != null && outerShadowPath == null) outerShadowPath = getOutLinePath(round, rect, outerShadow?.side ?: Side(), true)
        return outerShadowPath
    }

    fun getInnerShadowPath(): Path? {
        val rect = getInnerShadowRect()
        if (rect != null && innerShadowPath == null) innerShadowPath = getOutLinePath(round, rect, innerShadow?.side ?: Side(), false)
        return innerShadowPath
    }

    private fun getOutLinePath(round: Round, rectF: RectF, side: Side? = Side(), isOuter: Boolean = true): Path {
        val path = Path()
        path.reset()
        path.moveTo(rectF.left, rectF.top + round.leftTop)
        if (round.leftTop > 0f) {
            path.arcTo(RectF(rectF.left, rectF.top, rectF.left + round.leftTop * 2f, rectF.top + round.leftTop * 2f), 180f, 90f)
        }
        if (side?.top == false) {
            path.lineTo(rectF.left + rectF.width() / 2, rectF.top + if (isOuter) rectF.height() / 2 else -rectF.height() / 2)
        }
        path.lineTo(rectF.right - round.rightTop, rectF.top)
        if (round.rightTop > 0f) {
            path.arcTo(RectF(rectF.right - round.rightTop * 2f, rectF.top, rectF.right, rectF.top + round.rightTop * 2f), 270f, 90f)
        }
        if (side?.right == false) {
            path.lineTo(rectF.right + if (isOuter) -rectF.width() / 2 else rectF.width() / 2, rectF.top + rectF.height() / 2)
        }
        path.lineTo(rectF.right, rectF.bottom - round.rightBottom)
        if (round.rightBottom > 0f) {
            path.arcTo(RectF(rectF.right - round.rightBottom * 2f, rectF.bottom - round.rightBottom * 2f, rectF.right, rectF.bottom), 0f, 90f)
        }
        if (side?.bottom == false) {
            path.lineTo(rectF.left + rectF.width() / 2, rectF.bottom + if (isOuter) -rectF.height() / 2 else rectF.height() / 2)
        }
        path.lineTo(rectF.left + round.leftBottom, rectF.bottom)
        if (round.leftBottom > 0f) {
            path.arcTo(RectF(rectF.left, rectF.bottom - round.leftBottom * 2f, rectF.left + round.leftBottom * 2f, rectF.bottom), 90f, 90f)
        }
        if (side?.left == false) {
            path.lineTo(rectF.left + if (isOuter) rectF.width() / 2 else -rectF.width() / 2, rectF.top + rectF.height() / 2)
        }
        path.lineTo(rectF.left, rectF.top + round.leftTop)
        return path
    }

    class Config {
        private val ui = ShadowUiData()

        /**
         * 设置视图大小
         */
        fun setSize(boxWidth: Float, boxHeight: Float): Config {
            ui.boxWidth = boxWidth
            ui.boxHeight = boxHeight
            return this
        }

        /**
         * 设置视图阴影
         */
        fun setShadow(outer: Shadow? = null, inner: Shadow? = null): Config {
            ui.innerShadow = if (inner?.isInit() == true) inner else null
            ui.outerShadow = if (outer?.isInit() == true) outer else null
            return this
        }

        /**
         * 设置视图圆角
         */
        fun setRound(round: Round): Config {
            ui.round = round
            return this
        }

        /**
         * 设置视图颜色
         */
        fun setColor(color: Int): Config {
            ui.color = color
            return this
        }

        /**
         * 获得[ShadowView]配置参数数据
         */
        fun getUiData(): ShadowUiData {
            return ui
        }
    }

    class Round(
        val leftTop: Float = 0f,
        val rightTop: Float = 0f,
        val rightBottom: Float = 0f,
        val leftBottom: Float = 0f,
    )

    class Side(
        val left: Boolean = false,
        val top: Boolean = false,
        val right: Boolean = false,
        val bottom: Boolean = false,
    )

    class Shadow(
        val x: Float = 0f, // x 偏移量
        val y: Float = 0f, // y 偏移量
        val blur: Float = 0f, // 模糊程度
        val expand: Float = 0f, // 阴影宽度
        var color: Int = Color.TRANSPARENT, val side: Side = Side()
    ) {
        fun getRadius(): Float = max(max(abs(x) + abs(expand), abs(y) + abs(expand)), abs(blur)) + 10f

        fun isInit(): Boolean = x != 0f || y != 0f || blur != 0f || expand != 0f
    }
}