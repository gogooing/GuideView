package com.sanqiu.loro.guideview.guide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.sanqiu.loro.guideview.guide.Component

/**
 * 蒙版view
 */
internal class MaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    private val mTargetRect = RectF()//镂空的
    private val mFullingRect = RectF()
    private val mChildTmpRect = RectF()//component 的 rectF
    private val mChildHintTmpRect = RectF() //hint 的 rectF
    private val mFullingPaint = Paint()

    private var mPadding = 0
    private var mPaddingLeft = 0
    private var mPaddingTop = 0
    private var mPaddingRight = 0
    private var mPaddingBottom = 0

    private var mCustomFullingRect: Boolean = false//是否绘制全屏
    private var mOverlayTarget: Boolean = false//是否覆盖

    private var mCorner = 0
    private var mStyle = Component.ROUNDRECT

    ////画板bitmap
    private val mEraser: Paint
    private var mEraserBitmap: Bitmap? = null
    private val mEraserCanvas: Canvas
    private val mPaint: Paint
    private val transparentPaint: Paint

    init {
        setWillNotDraw(false)
        val size = Point()
        size.x = resources.displayMetrics.widthPixels
        size.y = resources.displayMetrics.heightPixels

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888)
        mEraserCanvas = Canvas(mEraserBitmap!!)

        mPaint = Paint()
        mPaint.color = -0x34000000
        transparentPaint = Paint()
        transparentPaint.color = resources.getColor(android.R.color.transparent)
        transparentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        mEraser = Paint()
        mEraser.color = -0x1
        mEraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mEraser.flags = Paint.ANTI_ALIAS_FLAG
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            clearFocus()
            mEraserCanvas.setBitmap(null)
            mEraserBitmap?.recycleSafety()
            mEraserBitmap = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
        if (!mCustomFullingRect) {
            mFullingRect.set(0f, 0f, w.toFloat(), h.toFloat())
            resetOutPath()
        }

        val count = childCount
        var child: View?
        for (i in 0 until count) {
            child = getChildAt(i)
            if (child != null) {
                val lp = child.layoutParams as LayoutParams
                if (lp == null) {
                    child.layoutParams = lp
                }
                measureChild(child, w + View.MeasureSpec.AT_MOST, h + View.MeasureSpec.AT_MOST)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val density = resources.displayMetrics.density
        var child: View?
        for (i in 0 until count) {
            child = getChildAt(i)
            if (child == null) {
                continue
            }
            val lp = child.layoutParams as LayoutParams ?: continue
            if (lp.hasHint) {
                when (lp.targetAnchor) {
                    LayoutParams.ANCHOR_LEFT//左上角
                    -> {
                        mChildHintTmpRect.left = mFullingRect.left
                        mChildHintTmpRect.top = mFullingRect.top
                        mChildHintTmpRect.right = child.measuredWidth.toFloat()
                        mChildHintTmpRect.bottom = child.measuredHeight.toFloat()
                        //额外的xy偏移
                        mChildHintTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                                (density * lp.offsetY + 0.5f).toInt().toFloat())
                        child.layout(mChildHintTmpRect.left.toInt(), mChildHintTmpRect.top.toInt(), mChildHintTmpRect.right.toInt(),
                                mChildHintTmpRect.bottom.toInt())

//                        child.layout(mFullingRect.left.toInt(), mFullingRect.top.toInt(), child.measuredWidth, child.measuredHeight)
                    }
                    LayoutParams.ANCHOR_TOP//右上角
                    -> {
                        mChildHintTmpRect.left = (mFullingRect.right - child.measuredWidth)
                        mChildHintTmpRect.top = mFullingRect.top
                        mChildHintTmpRect.right = mFullingRect.right
                        mChildHintTmpRect.bottom = child.measuredHeight.toFloat()
                        //额外的xy偏移
                        mChildHintTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                                (density * lp.offsetY + 0.5f).toInt().toFloat())
                        child.layout(mChildHintTmpRect.left.toInt(), mChildHintTmpRect.top.toInt(), mChildHintTmpRect.right.toInt(),
                                mChildHintTmpRect.bottom.toInt())

//                        child.layout((mFullingRect.right - child.measuredWidth).toInt(), mFullingRect.top.toInt(), mFullingRect.right.toInt(), child.measuredHeight)
                    }
                    LayoutParams.ANCHOR_RIGHT//右下角
                    -> {
                        mChildHintTmpRect.left = (mFullingRect.right - child.measuredWidth)
                        mChildHintTmpRect.top = (mFullingRect.bottom - child.measuredHeight)
                        mChildHintTmpRect.right = mFullingRect.right
                        mChildHintTmpRect.bottom = mFullingRect.bottom
                        //额外的xy偏移
                        mChildHintTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                                (density * lp.offsetY + 0.5f).toInt().toFloat())
                        child.layout(mChildHintTmpRect.left.toInt(), mChildHintTmpRect.top.toInt(), mChildHintTmpRect.right.toInt(),
                                mChildHintTmpRect.bottom.toInt())

//                        child.layout((mFullingRect.right - child.measuredWidth).toInt(), (mFullingRect.bottom - child.measuredHeight).toInt(), mFullingRect.right.toInt(), mFullingRect.bottom.toInt())
                    }
                    LayoutParams.ANCHOR_BOTTOM//左下角
                    -> {
                        mChildHintTmpRect.left = mFullingRect.left
                        mChildHintTmpRect.top = (mFullingRect.bottom - child.measuredHeight)
                        mChildHintTmpRect.right = child.measuredWidth.toFloat()
                        mChildHintTmpRect.bottom = mFullingRect.bottom
                        //额外的xy偏移
                        mChildHintTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                                (density * lp.offsetY + 0.5f).toInt().toFloat())
                        child.layout(mChildHintTmpRect.left.toInt(), mChildHintTmpRect.top.toInt(), mChildHintTmpRect.right.toInt(),
                                mChildHintTmpRect.bottom.toInt())

//                        child.layout(mFullingRect.left.toInt(), (mFullingRect.bottom - child.measuredHeight).toInt(), child.measuredWidth, mFullingRect.bottom.toInt())
                    }
                    LayoutParams.ANCHOR_OVER//中心
                    -> {
                        mChildHintTmpRect.left = (mFullingRect.width().toInt() - child.measuredWidth shr 1).toFloat()
                        mChildHintTmpRect.top = (mFullingRect.height().toInt() - child.measuredHeight shr 1).toFloat()
                        mChildHintTmpRect.right = (mFullingRect.width().toInt() + child.measuredWidth shr 1).toFloat()
                        mChildHintTmpRect.bottom = (mFullingRect.height().toInt() + child.measuredHeight shr 1).toFloat()
                        mChildHintTmpRect.offset(mFullingRect.left, mFullingRect.top)
                        //额外的xy偏移
                        mChildHintTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                                (density * lp.offsetY + 0.5f).toInt().toFloat())
                        child.layout(mChildHintTmpRect.left.toInt(), mChildHintTmpRect.top.toInt(), mChildHintTmpRect.right.toInt(),
                                mChildHintTmpRect.bottom.toInt())
                    }
                }
            } else {
                when (lp.targetAnchor) {
                    LayoutParams.ANCHOR_LEFT//左
                    -> {
                        mChildTmpRect.right = mTargetRect.left
                        mChildTmpRect.left = mChildTmpRect.right - child.measuredWidth
                        verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition)
                    }
                    LayoutParams.ANCHOR_TOP//上
                    -> {
                        mChildTmpRect.bottom = mTargetRect.top
                        mChildTmpRect.top = mChildTmpRect.bottom - child.measuredHeight
                        horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition)
                    }
                    LayoutParams.ANCHOR_RIGHT//右
                    -> {
                        mChildTmpRect.left = mTargetRect.right
                        mChildTmpRect.right = mChildTmpRect.left + child.measuredWidth
                        verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition)
                    }
                    LayoutParams.ANCHOR_BOTTOM//下
                    -> {
                        mChildTmpRect.top = mTargetRect.bottom
                        mChildTmpRect.bottom = mChildTmpRect.top + child.measuredHeight
                        horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition)
                    }
                    LayoutParams.ANCHOR_OVER//中心
                    -> {
                        mChildTmpRect.left = (mTargetRect.width().toInt() - child.measuredWidth shr 1).toFloat()
                        mChildTmpRect.top = (mTargetRect.height().toInt() - child.measuredHeight shr 1).toFloat()
                        mChildTmpRect.right = (mTargetRect.width().toInt() + child.measuredWidth shr 1).toFloat()
                        mChildTmpRect.bottom = (mTargetRect.height().toInt() + child.measuredHeight shr 1).toFloat()
                        mChildTmpRect.offset(mTargetRect.left, mTargetRect.top)
                    }
                }
                //额外的xy偏移
                mChildTmpRect.offset((density * lp.offsetX + 0.5f).toInt().toFloat(),
                        (density * lp.offsetY + 0.5f).toInt().toFloat())
                child.layout(mChildTmpRect.left.toInt(), mChildTmpRect.top.toInt(), mChildTmpRect.right.toInt(),
                        mChildTmpRect.bottom.toInt())
            }
        }
    }

    private fun horizontalChildPositionLayout(child: View, rect: RectF, targetParentPosition: Int) {
        when (targetParentPosition) {
            LayoutParams.PARENT_START -> {
                rect.left = mTargetRect.left
                rect.right = rect.left + child.measuredWidth
            }
            LayoutParams.PARENT_CENTER -> {
                rect.left = (mTargetRect.width() - child.measuredWidth) / 2
                rect.right = (mTargetRect.width() + child.measuredWidth) / 2
                rect.offset(mTargetRect.left, 0f)
            }
            LayoutParams.PARENT_END -> {
                rect.right = mTargetRect.right
                rect.left = rect.right - child.measuredWidth
            }
        }
    }

    private fun verticalChildPositionLayout(child: View, rect: RectF, targetParentPosition: Int) {
        when (targetParentPosition) {
            LayoutParams.PARENT_START -> {
                rect.top = mTargetRect.top
                rect.bottom = rect.top + child.measuredHeight
            }
            LayoutParams.PARENT_CENTER -> {
                rect.top = (mTargetRect.width() - child.measuredHeight) / 2
                rect.bottom = (mTargetRect.width() + child.measuredHeight) / 2
                rect.offset(0f, mTargetRect.top)
            }
            LayoutParams.PARENT_END -> {
                rect.bottom = mTargetRect.bottom
                rect.top = mTargetRect.bottom - child.measuredHeight
            }
        }
    }

    private fun resetOutPath() {
        resetPadding()
    }

    /**
     * 设置padding
     */
    private fun resetPadding() {
        if (mPadding != 0 && mPaddingLeft == 0) {
            mTargetRect.left -= mPadding.toFloat()
        }
        if (mPadding != 0 && mPaddingTop == 0) {
            mTargetRect.top -= mPadding.toFloat()
        }
        if (mPadding != 0 && mPaddingRight == 0) {
            mTargetRect.right += mPadding.toFloat()
        }
        if (mPadding != 0 && mPaddingBottom == 0) {
            mTargetRect.bottom += mPadding.toFloat()
        }
        if (mPaddingLeft != 0) {
            mTargetRect.left -= mPaddingLeft.toFloat()
        }
        if (mPaddingTop != 0) {
            mTargetRect.top -= mPaddingTop.toFloat()
        }
        if (mPaddingRight != 0) {
            mTargetRect.right += mPaddingRight.toFloat()
        }
        if (mPaddingBottom != 0) {
            mTargetRect.bottom += mPaddingBottom.toFloat()
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val drawingTime = drawingTime
        try {
            var child: View
            for (i in 0 until childCount) {
                child = getChildAt(i)
                drawChild(canvas, child, drawingTime)
            }
        } catch (e: NullPointerException) {

        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mEraserBitmap!!.eraseColor(Color.TRANSPARENT)
        mEraserCanvas.drawColor(mFullingPaint.color)

        if (!mOverlayTarget) {
            when (mStyle) {
                Component.ROUNDRECT -> mEraserCanvas.drawRoundRect(mTargetRect, mCorner.toFloat(), mCorner.toFloat(), mEraser)
                Component.CIRCLE -> mEraserCanvas.drawCircle(mTargetRect.centerX(), mTargetRect.centerY(),
                        mTargetRect.width() / 2, mEraser)
                else -> mEraserCanvas.drawRoundRect(mTargetRect, mCorner.toFloat(), mCorner.toFloat(), mEraser)
            }
            canvas.drawBitmap(mEraserBitmap!!, 0f, 0f, null)
        }
    }

    fun setTargetRect(rect: Rect) {
        mTargetRect.set(rect)
        resetOutPath()
        invalidate()
    }

    fun setFullingRect(rect: Rect) {
        mFullingRect.set(rect)
        resetOutPath()
        mCustomFullingRect = true
        invalidate()
    }

    fun setFullingAlpha(alpha: Int) {
        mFullingPaint.alpha = alpha
        invalidate()
    }

    fun setFullingColor(color: Int) {
        mFullingPaint.color = color
        invalidate()
    }

    fun setHighTargetCorner(corner: Int) {
        this.mCorner = corner
    }

    fun setHighTargetGraphStyle(style: Int) {
        this.mStyle = style
    }

    fun setOverlayTarget(b: Boolean) {
        mOverlayTarget = b
    }

    fun setPadding(padding: Int) {
        this.mPadding = padding
    }

    fun setPaddingLeft(paddingLeft: Int) {
        this.mPaddingLeft = paddingLeft
    }

    fun setPaddingTop(paddingTop: Int) {
        this.mPaddingTop = paddingTop
    }

    fun setPaddingRight(paddingRight: Int) {
        this.mPaddingRight = paddingRight
    }

    fun setPaddingBottom(paddingBottom: Int) {
        this.mPaddingBottom = paddingBottom
    }

    internal class LayoutParams : ViewGroup.LayoutParams {

        var targetAnchor = ANCHOR_BOTTOM
        var targetParentPosition = PARENT_CENTER
        var offsetX = 0
        var offsetY = 0
        var hasHint: Boolean = false

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {}

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}

        companion object {

            //在什么位置
            const val ANCHOR_LEFT = 0x01
            const val ANCHOR_TOP = 0x02
            const val ANCHOR_RIGHT = 0x03
            const val ANCHOR_BOTTOM = 0x04
            const val ANCHOR_OVER = 0x05

            //相对位置
            const val PARENT_START = 0x10
            const val PARENT_CENTER = 0x20
            const val PARENT_END = 0x30
        }
    }

    companion object {
        private val TAG = "MaskView"
    }

    /**
     * 安全回收bitmap
     */
    fun Bitmap.recycleSafety() {
        if (!this.isRecycled)
            this.recycle()
    }

}
