package com.example.main.widget

import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max

class ShadowUiData private constructor() {
    var boxWidth: Float = 0f

    var boxHeight: Float = 0f

    private var round: Round = Round()

    var color = Color.TRANSPARENT
        private set

    var outerShadow: Shadow? = null
        private set

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
        if (clipPath == null) clipPath = getOutLinePath(round.leftTop, round.rightTop, round.rightBottom, round.leftBottom, getClipRect())
        return clipPath!!
    }

    fun getOuterShadowPath(): Path? {
        val rect = getOuterShadowRect()
        if (rect != null && outerShadowPath == null) outerShadowPath = getOutLinePath(round.leftTop, round.rightTop, round.rightBottom, round.leftBottom, rect)
        return outerShadowPath
    }

    fun getInnerShadowPath(): Path? {
        val rect = getInnerShadowRect()
        if (rect != null && innerShadowPath == null) innerShadowPath = getOutLinePath(round.leftTop, round.rightTop, round.rightBottom, round.leftBottom, rect)
        return innerShadowPath
    }

    private fun getOutLinePath(leftTopRound: Float, rightTopRound: Float, rightBottomRound: Float, leftBottomRound: Float, rectF: RectF): Path {
        val path = Path()
        path.reset()
        path.moveTo(rectF.left, rectF.top + leftTopRound)
        if (leftTopRound > 0f) {
            path.arcTo(RectF(rectF.left, rectF.top, rectF.left + leftTopRound * 2f, rectF.top + leftTopRound * 2f), 180f, 90f)
        }
        path.lineTo(rectF.right - rightTopRound, rectF.top)
        if (rightTopRound > 0f) {
            path.arcTo(RectF(rectF.right - rightTopRound * 2f, rectF.top, rectF.right, rectF.top + rightTopRound * 2f), 270f, 90f)
        }
        path.lineTo(rectF.right, rectF.bottom - rightBottomRound)
        if (rightBottomRound > 0f) {
            path.arcTo(RectF(rectF.right - rightBottomRound * 2f, rectF.bottom - rightBottomRound * 2f, rectF.right, rectF.bottom), 0f, 90f)
        }
        path.lineTo(rectF.left + leftBottomRound, rectF.bottom)
        if (leftBottomRound > 0f) {
            path.arcTo(RectF(rectF.left, rectF.bottom - leftBottomRound * 2f, rectF.left + leftBottomRound * 2f, rectF.bottom), 90f, 90f)
        }
        path.lineTo(rectF.left, rectF.top + leftTopRound)
        return path
    }

    class Config {
        private val ui = ShadowUiData()

        fun setSize(boxWidth: Float, boxHeight: Float): Config {
            ui.boxWidth = boxWidth
            ui.boxHeight = boxHeight
            return this
        }

        fun setShadow(outer: Shadow? = null, inner: Shadow? = null): Config {
            ui.innerShadow = inner
            ui.outerShadow = outer
            return this
        }

        fun setRound(round: Round): Config {
            ui.round = round
            return this
        }

        fun setColor(color: Int): Config {
            ui.color = color
            return this
        }

        fun getUiData(): ShadowUiData {
            return ui
        }
    }

    class Round(
        val leftTop: Float = 0f,
        val rightTop: Float = 0f,
        val leftBottom: Float = 0f,
        val rightBottom: Float = 0f,
    )

    class Shadow(
        val x: Float = 0f,
        val y: Float = 0f,
        val blur: Float = 0f,
        val expand: Float = 0f,
        val color: Int = Color.TRANSPARENT,
    ) {
        fun getRadius(): Float = max(max(abs(x) + abs(expand), abs(y) + abs(expand)), abs(blur)) + 10f
    }
}