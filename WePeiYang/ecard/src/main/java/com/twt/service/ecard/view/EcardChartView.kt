package com.twt.service.ecard.view

import android.content.Context
import android.graphics.*
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import com.twt.service.ecard.R
import com.twt.service.ecard.extansion.*
import org.jetbrains.anko.dip

class EcardChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val LINE_STROKE = dip(2)

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = LINE_STROKE.toFloat()
        color = Color.parseColor("#ffe043")
        isSubpixelText = true
        isAntiAlias = true
    }

    private val bottonLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = LINE_STROKE.toFloat()
        color = Color.parseColor("#555555")
        isSubpixelText = true
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val pointPaintWhite = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        textSize = DETAILS_TEXT_SIZE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.parseColor("#666666")
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
    }
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var lineColor
        get() = linePaint.color
        set(value) {
            linePaint.color = value
        }

    var fillColor
        get() = fillPaint.color
        set(value) {
            fillPaint.color = value
        }

    var pointColor
        get() = pointPaint.color
        set(value) {
            pointPaint.color = value
        }

    var distanceOfBegin: Double = 0.0
        set(value) {
            field = value
            invalidate()
        }

    data class DataWithDetail(val data: Double, val year: String)

    var dataWithDetail: MutableList<DataWithDetail> = mutableListOf()
        set(value) {
            field = value
            selectedIndex = selectedIndex // ha?
            invalidate()
        }

    var selectedIndex = 0
        set(value) {
            field = value.coerceIn(if (dataWithDetail.isNotEmpty()) dataWithDetail.indices else 0..0)
            invalidate()
        }

    private val linePath = Path()
    private val fillPath = Path()
    private val pointPath = Path()
    private val whitePointPath = Path()
    private val centerPointPath = Path()
    private val bottomLinePath = Path()
    private val points = mutableListOf<PointF>()

    private fun computePath() {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        val widthStep = contentWidth / 4

        val centerY = paddingTop + contentHeight / 2
        val startX = paddingLeft + distanceOfBegin * widthStep * (dataWithDetail.size - 5)
        val endX = widthStep * (dataWithDetail.size - 1) * (1 - distanceOfBegin)

        points.clear()
        if (dataWithDetail.isNotEmpty()) {
            val maxData = dataWithDetail.maxBy(DataWithDetail::data)?.data!!
            // not all the same
            val extension = 10f
            val minDataExtended = 0f
            val maxDataExtended = maxData + extension
            val dataSpanExtended = maxDataExtended - minDataExtended

            if (dataWithDetail.size == 1) {
                points.add(PointF(startX.toFloat(), extension * contentHeight / dataSpanExtended.toFloat()))
            } else {
                /*单个数据的时候不执行……蜜汁问题*/

                dataWithDetail.asSequence()
                        .map { (it.data - minDataExtended) / dataSpanExtended }
                        .mapIndexedTo(points) { index, ratio ->
                            PointF((startX + widthStep * (index)).toFloat(),
                                    (1 - ratio.toFloat()) * (contentHeight - dip(22)))
                        }
            }
        }

        linePath.reuse {
            if (points.isNotEmpty()) {
                moveTo(points.first())
                cubicThrough(points)

            } else {
                moveTo(startX.toFloat(), centerY)
                lineTo(pivotX - endX.toFloat(), centerY)
            }
        }

        bottomLinePath.reuse {
            moveTo(points.first().x, height.toFloat() - dip(22))
            lineTo(points.last().x, height.toFloat() - dip(22))
        }

        fillPath.reuse {
            addPath(linePath)
            lineTo(points.last().x, height.toFloat() - dip(22))
            lineTo(points.first().x, height.toFloat() - dip(22))
            close()
        }

        val firstPoint = points.removeAt(0)
        val lastPoint = points.removeAt(points.size - 1)
        pointPath.reuse {
            points.asSequence()
                    .forEach { (x, y) -> addCircle(x, y, POINT_RADIUS, Path.Direction.CCW) }
        }

        whitePointPath.reuse {
            points.asSequence()
                    .forEach { (x, y) -> addCircle(x, y, WHITE_POINT_RADIUS, Path.Direction.CCW) }
        }

        centerPointPath.reuse {
            points.asSequence()
                    .forEach { (x, y) -> addCircle(x, y, CENTER_POINT_RADIUS, Path.Direction.CCW) }
        }
        points.add(0, firstPoint)
        points.add(lastPoint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataWithDetail.size > 4) {
            computePath()
        }

        canvas.apply {
            if (points.size > 1) {
                drawPath(fillPath, fillPaint)
                drawPath(linePath, linePaint)
            }
            drawPath(pointPath, pointPaint)
            drawPath(whitePointPath, pointPaintWhite)
            drawPath(centerPointPath, pointPaint)
            drawPath(bottomLinePath, bottonLinePaint)

            points.asSequence().forEachIndexed { index, (x, y) ->
                drawText(dataWithDetail[index].year, x, height.toFloat(), textPaint)
            }
        }
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.EcardChartView, defStyleAttr, 0).apply {

            fillColor = getColor(R.styleable.EcardChartView_fillColor, EcardChartView.DEFAULT_FILL_COLOR)

            pointColor = getColor(R.styleable.EcardChartView_pointColor, EcardChartView.DEFAULT_POINT_COLOR)

            lineColor = getColor(R.styleable.EcardChartView_lineColor, EcardChartView.DEFAULT_LINE_COLOR)

            recycle()
        }
    }

    companion object {
        const val DEFAULT_LINE_COLOR = 0xFFFFE043.toInt()
        const val DEFAULT_FILL_COLOR = 0xFFFFF5C2.toInt()
        const val DEFAULT_POINT_COLOR = 0xFFFFE043.toInt()
        const val POINT_RADIUS = 17F
        const val WHITE_POINT_RADIUS = 14F
        const val CENTER_POINT_RADIUS = 8F
        const val DETAILS_TEXT_SIZE = 30F
    }
}