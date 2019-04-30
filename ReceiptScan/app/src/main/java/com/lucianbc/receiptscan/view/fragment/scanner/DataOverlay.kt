package com.lucianbc.receiptscan.view.fragment.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class DataOverlay(context: Context, attrs: AttributeSet): View(context, attrs) {
    var elements: Iterable<Element> = emptyList()
        set(value) {
            field = value
            postInvalidate()
        }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(this) {
            elements.forEach { it.drawOnto(canvas!!) }
        }
    }
}

interface Element {
    fun drawOnto(canvas: Canvas)
}

class BoxedData(
    private val box: Rect,
    private val data: String): Element {

    companion object {
        private val boxPaint = Paint()
        private val textPaint = Paint()
        init {
            boxPaint.color = Color.parseColor("#ff0000")
            boxPaint.style = Paint.Style.STROKE
            textPaint.color = boxPaint.color
            boxPaint.textSize = 20f
        }
    }

    override fun drawOnto(canvas: Canvas) {
        canvas.drawRect(box, boxPaint)
        canvas.drawText(data, box.left.toFloat(), box.bottom.toFloat(), textPaint)
    }
}