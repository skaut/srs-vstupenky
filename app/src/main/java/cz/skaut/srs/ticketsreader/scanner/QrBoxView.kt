package cz.skaut.srs.ticketsreader.scanner

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.graphics.toRectF

class QrBoxView(context: Context) : View(context) {
    private val paint = Paint()
    private var rectangle = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cornerRadius = 10f
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f

        canvas?.drawRoundRect(rectangle.toRectF(), cornerRadius, cornerRadius, paint)
    }

    fun setRect(rect: Rect) {
        rectangle = rect
        invalidate()
        requestLayout()
    }
}