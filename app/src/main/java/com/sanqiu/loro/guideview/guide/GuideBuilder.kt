package com.sanqiu.loro.guideview.guide

import android.view.View
import com.sanqiu.loro.guideview.guide.BuildException
import com.sanqiu.loro.guideview.guide.Component
import com.sanqiu.loro.guideview.guide.Guide

import java.util.ArrayList

/**
 * <h1>遮罩系统构建器
 *
 *
 * 本系统能够快速的为一个Activity里的任何一个View控件创建一个遮罩式的导航页。
 *
 * <h3>工作原理</h3>
 *
 *
 * 首先它需要一个目标View或者它的id,我们通过findViewById来得到这个View，计算它在屏幕上的区域targetRect,参见
 * [.setTargetViewId]与[.setTargetView]通过这个区域，
 * 开始绘制一个覆盖整个Activity的遮罩，可以定义蒙板的颜色[.setFullingColorId]和透明度
 * [.setAlpha]。然而目标View的区域不会被绘制从而实现高亮的效果。
 *
 *
 *
 * 接下来是在相对于这个targetRect的区域绘制一些图片或者文字。我们把这样一张图片或者文字抽象成一个Component接口
 * [com.domobile.pixelworld.ui.widget.guide.Component],设置文字或者图片等
 * [com.domobile.pixelworld.ui.widget.guide.Component.getView]
 * . 所有的图片文字都是相对于targetRect来定义的。可以设定额外的x，
 * [com.domobile.pixelworld.ui.widget.guide.Component.getXOffset] ;y偏移量,
 * [com.domobile.pixelworld.ui.widget.guide.Component.getYOffset]。
 *
 *
 *
 * 可以对遮罩系统设置可见状态的发生变化时的监听回调
 * [.setOnVisibilityChangedListener]
 *
 *
 *
 * 可以对遮罩系统设置开始和结束时的动画效果 [.setEnterAnimationId]
 * [.setExitAnimationId]
 *
 *
 *
 * 另外，我们可以不对整个Activity覆盖遮罩，而是对某一个View覆盖遮罩。 [.setFullingViewId]
 *
</h1> */
class GuideBuilder {

    private var mConfiguration: Configuration? = null

    /**
     * Builder被创建后，不允许在对它进行更改
     */
    private var mBuilt: Boolean = false

    private var mComponents: MutableList<Component>? = ArrayList()
    private var mOnVisibilityChangedListener: OnVisibilityChangedListener? = null

    /**
     * 构造函数
     */
    init {
        mConfiguration = Configuration()
    }

    /**
     * 设置蒙板透明度
     *
     * @param alpha [0-255] 0 表示完全透明，255表示不透明
     * @return GuideBuilder
     */
    fun setAlpha(alpha: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (alpha < 0 || alpha > 255) {
            throw BuildException("Illegal alpha value, should between [0-255]")
        }
        mConfiguration!!.mAlpha = alpha
        return this
    }

    /**
     * 设置目标view
     */
    fun setTargetView(v: View?): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (v == null) {
            throw BuildException("Illegal view.")
        }
        mConfiguration!!.mTargetView = v
        return this
    }

    /**
     * 设置目标View的id
     *
     * @param id 目标View的id
     * @return GuideBuilder
     */
    fun setTargetViewId(id: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (id <= 0) {
            throw BuildException("Illegal view id.")
        }
        mConfiguration!!.mTargetViewId = id
        return this
    }

    /**
     * 设置蒙板View的id
     *
     * @param id 蒙板View的id
     * @return GuideBuilder
     */
    fun setFullingViewId(id: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (id <= 0) {
            throw BuildException("Illegal view id.")
        }
        mConfiguration!!.mFullingViewId = id
        return this
    }

    /**
     * 设置高亮区域的圆角大小
     *
     * @return GuideBuilder
     */
    fun setHighTargetCorner(corner: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (corner < 0) {
            mConfiguration!!.mCorner = 0
        }
        mConfiguration!!.mCorner = corner
        return this
    }

    /**
     * 设置高亮区域的图形样式
     *
     * @return GuideBuilder
     */
    fun setHighTargetGraphStyle(style: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        }
        mConfiguration!!.mGraphStyle = style
        return this
    }

    /**
     * 设置蒙板颜色的资源id
     *
     * @param id 资源id
     * @return GuideBuilder
     */
    fun setFullingColorId(id: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (id <= 0) {
            throw BuildException("Illegal color resource id.")
        }
        mConfiguration!!.mFullingColorId = id
        return this
    }

    /**
     * 是否在点击的时候自动退出蒙板
     *
     * @param b true if needed
     * @return GuideBuilder
     */
    fun setAutoDismiss(b: Boolean): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created, rebuild a new one.")
        }
        mConfiguration!!.mAutoDismiss = b
        return this
    }

    /**
     * 是否覆盖目标
     *
     * @param b true 遮罩将会覆盖整个屏幕
     * @return GuideBuilder
     */
    fun setOverlayTarget(b: Boolean): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created, rebuild a new one.")
        }
        mConfiguration!!.mOverlayTarget = b
        return this
    }

    /**
     * 设置进入动画
     *
     * @param id 进入动画的id
     * @return GuideBuilder
     */
    fun setEnterAnimationId(id: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (id <= 0) {
            throw BuildException("Illegal animation resource id.")
        }
        mConfiguration!!.mEnterAnimationId = id
        return this
    }

    /**
     * 设置退出动画
     *
     * @param id 退出动画的id
     * @return GuideBuilder
     */
    fun setExitAnimationId(id: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (id <= 0) {
            throw BuildException("Illegal animation resource id.")
        }
        mConfiguration!!.mExitAnimationId = id
        return this
    }

    /**
     * 添加一个控件
     *
     * @param component 被添加的控件
     * @return GuideBuilder
     */
    fun addComponent(component: Component): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created, rebuild a new one.")
        }
        mComponents!!.add(component)
        return this
    }

    /**
     * 设置遮罩可见状态变化时的监听回调
     */
    fun setOnVisibilityChangedListener(
            onVisibilityChangedListener: OnVisibilityChangedListener): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created, rebuild a new one.")
        }
        mOnVisibilityChangedListener = onVisibilityChangedListener
        return this
    }

    /**
     * 设置遮罩系统是否可点击并处理点击事件
     *
     * @param touchable true 遮罩不可点击，处于不可点击状态 false 可点击，遮罩自己可以处理自身点击事件
     */
    fun setOutsideTouchable(touchable: Boolean): GuideBuilder {
        mConfiguration!!.mOutsideTouchable = touchable
        return this
    }

    /**
     * 设置高亮区域的padding
     *
     * @return GuideBuilder
     */
    fun setHighTargetPadding(padding: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (padding < 0) {
            mConfiguration!!.mPadding = 0
        }
        mConfiguration!!.mPadding = padding
        return this
    }

    /**
     * 设置高亮区域的左侧padding
     *
     * @return GuideBuilder
     */
    fun setHighTargetPaddingLeft(padding: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (padding < 0) {
            mConfiguration!!.mPaddingLeft = 0
        }
        mConfiguration!!.mPaddingLeft = padding
        return this
    }

    /**
     * 设置高亮区域的顶部padding
     *
     * @return GuideBuilder
     */
    fun setHighTargetPaddingTop(padding: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (padding < 0) {
            mConfiguration!!.mPaddingTop = 0
        }
        mConfiguration!!.mPaddingTop = padding
        return this
    }

    /**
     * 设置高亮区域的右侧padding
     *
     * @return GuideBuilder
     */
    fun setHighTargetPaddingRight(padding: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (padding < 0) {
            mConfiguration!!.mPaddingRight = 0
        }
        mConfiguration!!.mPaddingRight = padding
        return this
    }

    /**
     * 设置高亮区域的底部padding
     *
     * @return GuideBuilder
     */
    fun setHighTargetPaddingBottom(padding: Int): GuideBuilder {
        if (mBuilt) {
            throw BuildException("Already created. rebuild a new one.")
        } else if (padding < 0) {
            mConfiguration!!.mPaddingBottom = 0
        }
        mConfiguration!!.mPaddingBottom = padding
        return this
    }

    /**
     * 创建Guide，非Fragment版本
     *
     * @return Guide
     */
    fun createGuide(): Guide {
        val guide = Guide()
        guide.setComponents(mComponents!!.toTypedArray())
        guide.setConfiguration(mConfiguration)
        guide.setCallback(mOnVisibilityChangedListener)
        mComponents = null
        mConfiguration = null
        mOnVisibilityChangedListener = null
        mBuilt = true
        return guide
    }

    /**
     * 遮罩可见发生变化时的事件监听
     *
     * @author Simon
     */
    interface OnVisibilityChangedListener {

        fun onShown()

        fun onDismiss()
    }
}
