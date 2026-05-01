package com.bradycorp.cortexdecoderkotlinsample

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

/**
 * An interactive UI component providing a dynamic Region of Interest (ROI) overlay
 * for the CortexDecoder SDK.
 *
 * This view enables users to intuitively define a scanning sub-region via corner-drag
 * resizing and center-drag translation. By narrowing the decoding area, application
 * performance is optimized and targeting accuracy is improved.
 *
 */
class ScannerOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Style for the ROI boundary.
    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.md_theme_light_surfaceTint)
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    // Style for the draggable corner handles.
    private val handlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    /**
     * ROI Rectangle in Screen Coordinates.
     * Note: CortexDecoder SDK's setRegionOfInterest handles the transformation
     * from these screen coordinates to camera image pixels automatically.
     */
    var roiRect = RectF(200f, 400f, 800f, 800f)
    private val handleRadius = 20f
    private var touchPoint = -1

    /**
     * This callback bridges the UI interaction to the main logic.
     * When the user releases the ROI box, we pass the updated coordinates.
     */
    var onRoiChanged: ((RectF) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the ROI bounding box
        canvas.drawRect(roiRect, paint)

        // Draw the corner handles to indicate interactivity
        canvas.drawCircle(roiRect.left, roiRect.top, handleRadius, handlePaint)
        canvas.drawCircle(roiRect.right, roiRect.top, handleRadius, handlePaint)
        canvas.drawCircle(roiRect.left, roiRect.bottom, handleRadius, handlePaint)
        canvas.drawCircle(roiRect.right, roiRect.bottom, handleRadius, handlePaint)
    }

    private var lastX = 0f
    private var lastY = 0f
    private var isMovingWholeRect = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint = getTouchedCorner(x, y)
                lastX = x
                lastY = y

                // Priority: Check corners first for resizing, then interior for moving.
                if (touchPoint == -1 && roiRect.contains(x, y)) {
                    isMovingWholeRect = true
                }
                return touchPoint != -1 || isMovingWholeRect
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX
                val dy = y - lastY

                if (touchPoint != -1) {
                    updateRect(x, y) // Handle Resizing
                } else if (isMovingWholeRect) {
                    // Handle Translation (moving the whole box)
                    val newLeft = roiRect.left + dx
                    val newTop = roiRect.top + dy
                    val newRight = roiRect.right + dx
                    val newBottom = roiRect.bottom + dy

                    // Boundary Check: Ensure ROI stays within the UI View
                    if (newLeft >= 0 && newRight <= width && newTop >= 0 && newBottom <= height) {
                        roiRect.offset(dx, dy)
                    }
                }

                lastX = x
                lastY = y
                invalidate() // Redraw overlay
            }

            MotionEvent.ACTION_UP -> {
                touchPoint = -1
                isMovingWholeRect = false

                /*
                 * When the user finishes moving the ROI, we invoke the listener.
                 * The Activity will then call:
                 * CDDecoder.shared.setRegionOfInterest(newRect, true, true)
                 */
                onRoiChanged?.invoke(roiRect)
            }
        }
        return true
    }

    private fun getTouchedCorner(x: Float, y: Float): Int {
        // Hit-area is doubled to allow for easier interaction with small corner circles
        val hitArea = handleRadius * 3
        if (dist(x, y, roiRect.left, roiRect.top) < hitArea) return 0
        if (dist(x, y, roiRect.right, roiRect.top) < hitArea) return 1
        if (dist(x, y, roiRect.left, roiRect.bottom) < hitArea) return 2
        if (dist(x, y, roiRect.right, roiRect.bottom) < hitArea) return 3
        return -1
    }

    private fun updateRect(x: Float, y: Float) {
        val minSize = 100f // Prevent the ROI from disappearing
        when (touchPoint) {
            0 -> { roiRect.left = x.coerceAtMost(roiRect.right - minSize); roiRect.top = y.coerceAtMost(roiRect.bottom - minSize) }
            1 -> { roiRect.right = x.coerceAtLeast(roiRect.left + minSize); roiRect.top = y.coerceAtMost(roiRect.bottom - minSize) }
            2 -> { roiRect.left = x.coerceAtMost(roiRect.right - minSize); roiRect.bottom = y.coerceAtLeast(roiRect.top + minSize) }
            3 -> { roiRect.right = x.coerceAtLeast(roiRect.left + minSize); roiRect.bottom = y.coerceAtLeast(roiRect.top + minSize) }
        }
    }

    private fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Double =
        Math.sqrt(Math.pow((x1 - x2).toDouble(), 2.0) + Math.pow((y1 - y2).toDouble(), 2.0))
}