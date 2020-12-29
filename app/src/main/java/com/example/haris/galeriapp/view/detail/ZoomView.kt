package com.example.haris.galeriapp.view.detail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class ZoomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val imageBound = RectF()
    private val imageMatrixArray = FloatArray(9)
    private val scaleDetector: ScaleGestureDetector

    private var scale: Float = 1f
    private var scalePoint = PointF()
    private var translateX: Float = 0f
    private var translateY: Float = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastDownTouchX = 0f
    private var lastDownTouchY = 0f
    private var lastGestureX = 0f
    private var lastGestureY = 0f
    private var isScaling = false
    private var activePointerId = -1
    private var drawableHeight = -1.0
    private var drawableWidth = -1.0
    private var minBoxRectSide = 0

    init {

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector
        .SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                isScaling = true
                return super.onScaleBegin(detector)
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scale *= detector.scaleFactor
                scale = Math.min(scale, 25f)
                scale = Math.max(0.5f, scale)

                invalidate()
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                super.onScaleEnd(detector)
                isScaling = false
            }
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //Timber.d("Image on layout : layoutChanged = $changed")
        if (changed) {
            drawable?.let {
                resetBoxRect(it, left, right, top, bottom)
            }
        }
    }

    private fun resetBoxRect(it: Drawable, left: Int, right: Int, top: Int, bottom: Int) {
        //Timber.d("image coordinates $left, $top, $right, $bottom")

        drawableHeight = it.intrinsicHeight.toDouble()
        drawableWidth = it.intrinsicWidth.toDouble()

        imageMatrix.getValues(imageMatrixArray)
        imageBound.set(
            (left + right) / 2 - imageMatrixArray[Matrix.MSCALE_X] * it.intrinsicWidth / 2,
            (bottom - top) / 2 - imageMatrixArray[Matrix.MSCALE_Y] * it.intrinsicHeight / 2,
            (left + right) / 2 + imageMatrixArray[Matrix.MSCALE_X] * it.intrinsicWidth / 2,
            (bottom - top) / 2 + imageMatrixArray[Matrix.MSCALE_Y] * it.intrinsicHeight / 2
        )

        //Timber.d("Image bound $imageBound, and $boxRect")
        minBoxRectSide = (.01f * Math.max(
            imageBound.bottom - imageBound.top,
            imageBound.right - imageBound.left
        )).toInt()
        scale = 1f
        translateX = 0f
        translateY = 0f

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scalePoint.set(w / 2.toFloat(), h / 2.toFloat())
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        drawable?.let {
            if (imageMatrixArray != null) {
                resetBoxRect(it, left, right, top, bottom)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        //Timber.d("On touch start")
        scaleDetector.onTouchEvent(event)
        //Timber.d("On touch gesture sent")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_MOVE -> onMoveEvent(event)
            MotionEvent.ACTION_CANCEL -> activePointerId = -1
            MotionEvent.ACTION_UP -> {
                activePointerId = -1
            }
            MotionEvent.ACTION_POINTER_UP -> onActionUp(event)
        }
        return true
    }


    private fun onActionDown(event: MotionEvent) {
        val actionIndex = event.actionIndex
        lastDownTouchX = event.getX(actionIndex)
        lastDownTouchY = event.getY(actionIndex)

        lastTouchX = event.getX(actionIndex)
        lastTouchY = event.getY(actionIndex)
        lastGestureX = lastTouchX
        lastGestureY = lastTouchY
        activePointerId = event.getPointerId(0)

        invalidate()
    }

    private fun onMoveEvent(event: MotionEvent) {
        if (!isScaling) {
            val index = event.findPointerIndex(activePointerId)
            val dx = (event.getX(index) - lastTouchX) / scale
            val dy = (event.getY(index) - lastTouchY) / scale
            lastTouchX = event.getX(index)
            lastTouchY = event.getY(index)

            if (Math.abs(translateX + dx) < imageBound.right - imageBound.left)
                translateX += dx
            if (Math.abs(translateY + dy) < imageBound.bottom - imageBound.top)
                translateY += dy

            invalidate()

        }
    }

    private fun onActionUp(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            lastTouchX = event.getX(newPointerIndex)
            lastTouchY = event.getY(newPointerIndex)
            activePointerId = event.getPointerId(newPointerIndex)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        canvas.scale(scale, scale, scalePoint.x, scalePoint.y)
        canvas.translate(translateX, translateY)

        super.onDraw(canvas)

        canvas.restore()
    }
}