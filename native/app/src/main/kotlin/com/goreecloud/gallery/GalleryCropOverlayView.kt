package com.goreecloud.gallery

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.min

class GalleryCropOverlayView(context: Context) : View(context) {
    private enum class DragMode {
        NONE,
        MOVE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
    }

    private val density = resources.displayMetrics.density
    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0x99000000.toInt() }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x99ffffff.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f * density
    }
    private val handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var sourceWidth = 1
    private var sourceHeight = 1
    private var crop = GalleryNormalizedCrop.FULL
    private var dragMode = DragMode.NONE
    private var lastX = 0f
    private var lastY = 0f

    var onCropChanged: ((GalleryNormalizedCrop) -> Unit)? = null

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        contentDescription =
            "Crop area. Drag the white corner handles or drag inside the crop. Aspect preset buttons provide an accessible crop alternative."
    }

    fun setSourceSize(width: Int, height: Int) {
        require(width > 0 && height > 0)
        sourceWidth = width
        sourceHeight = height
        invalidate()
    }

    fun crop(): GalleryNormalizedCrop = crop

    fun setCrop(value: GalleryNormalizedCrop, notify: Boolean = true) {
        crop = value
        invalidate()
        if (notify) onCropChanged?.invoke(value)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val content = contentRect()
        if (content.width() <= 0f || content.height() <= 0f) return
        val cropRect = cropRect(content)

        canvas.drawRect(content.left, content.top, content.right, cropRect.top, dimPaint)
        canvas.drawRect(content.left, cropRect.bottom, content.right, content.bottom, dimPaint)
        canvas.drawRect(content.left, cropRect.top, cropRect.left, cropRect.bottom, dimPaint)
        canvas.drawRect(cropRect.right, cropRect.top, content.right, cropRect.bottom, dimPaint)

        canvas.drawRect(cropRect, borderPaint)
        val thirdWidth = cropRect.width() / 3f
        val thirdHeight = cropRect.height() / 3f
        canvas.drawLine(cropRect.left + thirdWidth, cropRect.top, cropRect.left + thirdWidth, cropRect.bottom, gridPaint)
        canvas.drawLine(cropRect.left + 2f * thirdWidth, cropRect.top, cropRect.left + 2f * thirdWidth, cropRect.bottom, gridPaint)
        canvas.drawLine(cropRect.left, cropRect.top + thirdHeight, cropRect.right, cropRect.top + thirdHeight, gridPaint)
        canvas.drawLine(cropRect.left, cropRect.top + 2f * thirdHeight, cropRect.right, cropRect.top + 2f * thirdHeight, gridPaint)

        val radius = 6f * density
        canvas.drawCircle(cropRect.left, cropRect.top, radius, handlePaint)
        canvas.drawCircle(cropRect.right, cropRect.top, radius, handlePaint)
        canvas.drawCircle(cropRect.left, cropRect.bottom, radius, handlePaint)
        canvas.drawCircle(cropRect.right, cropRect.bottom, radius, handlePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val content = contentRect()
        if (content.width() <= 0f || content.height() <= 0f) return false
        val cropRect = cropRect(content)

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dragMode = hitMode(event.x, event.y, cropRect)
                if (dragMode == DragMode.NONE) return false
                parent?.requestDisallowInterceptTouchEvent(true)
                lastX = event.x
                lastY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragMode == DragMode.NONE) return false
                val deltaX = (event.x - lastX) / content.width()
                val deltaY = (event.y - lastY) / content.height()
                crop = when (dragMode) {
                    DragMode.MOVE -> GalleryPhotoEditPolicy.moveCrop(crop, deltaX, deltaY)
                    DragMode.TOP_LEFT -> GalleryPhotoEditPolicy.resizeCrop(
                        crop,
                        GalleryCropHandle.TOP_LEFT,
                        deltaX,
                        deltaY,
                    )
                    DragMode.TOP_RIGHT -> GalleryPhotoEditPolicy.resizeCrop(
                        crop,
                        GalleryCropHandle.TOP_RIGHT,
                        deltaX,
                        deltaY,
                    )
                    DragMode.BOTTOM_LEFT -> GalleryPhotoEditPolicy.resizeCrop(
                        crop,
                        GalleryCropHandle.BOTTOM_LEFT,
                        deltaX,
                        deltaY,
                    )
                    DragMode.BOTTOM_RIGHT -> GalleryPhotoEditPolicy.resizeCrop(
                        crop,
                        GalleryCropHandle.BOTTOM_RIGHT,
                        deltaX,
                        deltaY,
                    )
                    DragMode.NONE -> crop
                }
                lastX = event.x
                lastY = event.y
                invalidate()
                onCropChanged?.invoke(crop)
                true
            }
            MotionEvent.ACTION_UP -> {
                if (dragMode == DragMode.NONE) return false
                dragMode = DragMode.NONE
                parent?.requestDisallowInterceptTouchEvent(false)
                performClick()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                dragMode = DragMode.NONE
                parent?.requestDisallowInterceptTouchEvent(false)
                true
            }
            else -> dragMode != DragMode.NONE
        }
    }

    override fun performClick(): Boolean = super.performClick()

    private fun hitMode(x: Float, y: Float, cropRect: RectF): DragMode {
        val hitRadius = 28f * density
        fun near(cx: Float, cy: Float): Boolean = hypot(x - cx, y - cy) <= hitRadius

        return when {
            near(cropRect.left, cropRect.top) -> DragMode.TOP_LEFT
            near(cropRect.right, cropRect.top) -> DragMode.TOP_RIGHT
            near(cropRect.left, cropRect.bottom) -> DragMode.BOTTOM_LEFT
            near(cropRect.right, cropRect.bottom) -> DragMode.BOTTOM_RIGHT
            cropRect.contains(x, y) -> DragMode.MOVE
            else -> DragMode.NONE
        }
    }

    private fun contentRect(): RectF {
        val availableWidth = width.toFloat().coerceAtLeast(0f)
        val availableHeight = height.toFloat().coerceAtLeast(0f)
        if (availableWidth == 0f || availableHeight == 0f) return RectF()

        val scale = min(availableWidth / sourceWidth.toFloat(), availableHeight / sourceHeight.toFloat())
        val renderedWidth = sourceWidth * scale
        val renderedHeight = sourceHeight * scale
        val left = (availableWidth - renderedWidth) / 2f
        val top = (availableHeight - renderedHeight) / 2f
        return RectF(left, top, left + renderedWidth, top + renderedHeight)
    }

    private fun cropRect(content: RectF): RectF = RectF(
        content.left + content.width() * crop.left,
        content.top + content.height() * crop.top,
        content.left + content.width() * crop.right,
        content.top + content.height() * crop.bottom,
    )
}
