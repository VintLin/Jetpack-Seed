package com.example.main.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.main.R
import java.util.*

class ParticleView : View {

    private var particles: MutableList<Particle> = mutableListOf()
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var frameRate: Long = 16L // 60fps
    private var lastTime: Long = 0L
    private var bitmap: Bitmap? = null
    private var bitmapSize = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    private fun init() {
        paint.color = ContextCompat.getColor(context, android.R.color.white)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 4f
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        bitmapSize = 50
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (particle in particles) {
            val left = particle.x - bitmapSize / 2
            val top = particle.y - bitmapSize / 2
            val right = particle.x + bitmapSize / 2
            val bottom = particle.y + bitmapSize / 2
            val rect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            canvas.drawBitmap(bitmap!!, null, rect, paint)
        }

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetParticles()
    }

    private fun resetParticles() {
        particles.clear()
        for (i in 0..49) {
            val particle = Particle(
                Random().nextInt(width), -Random().nextInt(height), (Random().nextInt(3) + 1).toFloat(), (Random().nextInt(20) + 30).toFloat(), (Random().nextInt(20) - 10).toFloat() / 1000f, (Random().nextInt(20) - 10).toFloat() / 1000f
            )
            particles.add(particle)
        }
    }

    fun startAnimation() {
        lastTime = System.currentTimeMillis()
        postDelayed(updateRunnable, frameRate)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val deltaTime = now - lastTime
            lastTime = now

            for (particle in particles) {
                particle.x += (particle.vx * deltaTime).toInt()
                particle.y += (particle.vy * deltaTime).toInt()
                particle.vy += particle.ay * deltaTime
                particle.vx += particle.ax * deltaTime
                if (particle.y > height) {
                    particle.y = -Random().nextInt(height)
                    particle.x = Random().nextInt(width)
                    particle.vy = (Random().nextInt(20) + 30).toFloat()
                }
            }

            invalidate()
            postDelayed(this, frameRate)
        }
    }

    private class Particle(
        var x: Int,
        var y: Int,
        var vx: Float,
        var vy: Float,
        var ax: Float,
        var ay: Float,
    )
}
