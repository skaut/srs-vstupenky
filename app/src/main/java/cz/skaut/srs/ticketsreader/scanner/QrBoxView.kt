package cz.skaut.srs.ticketsreader.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.toRectF

class QrBoxView(context: Context) : View(context) {
    companion object {
        private const val CORNER_RADIUS = 10f
        private const val STROKE_WIDTH = 5f
    }

    private val paint = Paint()
    private var rectangle = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = STROKE_WIDTH

        canvas.drawRoundRect(rectangle.toRectF(), CORNER_RADIUS, CORNER_RADIUS, paint)
    }

    fun setRect(rect: Rect) {
        rectangle = rect
        invalidate()
        requestLayout()
    }
}
