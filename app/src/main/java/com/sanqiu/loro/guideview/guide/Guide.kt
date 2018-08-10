package com.sanqiu.loro.guideview.guide

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * 遮罩系统的封装 <br></br>
 * 外部需要调用[com.domobile.pixelworld.ui.widget.guide.GuideBuilder]来创建该实例，实例创建后调用
 * [.show] 控制显示； 调用 [.dismiss]让遮罩系统消失。 <br></br>
 *
 */
class Guide
/**
 * Cannot initialize out of package <font color=red>包内才可见，外部使用时必须调用GuideBuilder来创建.</font>
 *
 * @see com.domobile.pixelworld.ui.widget.guide.GuideBuilder
 */
internal constructor() : View.OnKeyListener, View.OnClickListener {

    private var mConfiguration: Configuration? = null
    private var mMaskView: MaskView? = null
    private var mComponents: Array<Component>? = null
    // 根据locInwindow定位后，是否需要判断loc值非0
    private var mShouldCheckLocInWindow = true
    private var mOnVisibilityChangedListener: GuideBuilder.OnVisibilityChangedListener? = null

    internal fun setConfiguration(configuration: Configuration?) {
        mConfiguration = configuration
    }

    internal fun setComponents(components: Array<Component>) {
        mComponents = components
    }

    internal fun setCallback(listener: GuideBuilder.OnVisibilityChangedListener?) {
        mOnVisibilityChangedListener = listener
    }

    /**
     * 显示该遮罩, <br></br>
     * 外部借助[com.domobile.pixelworld.ui.widget.guide.GuideBuilder]
     * 创建好一个Guide实例后，使用该实例调用本函数遮罩才会显示
     *
     * @param activity 目标Activity
     */
    fun show(activity: Activity) {
        if (mMaskView == null) {
            mMaskView = onCreateView(activity)
        }
        val content = activity.findViewById<View>(android.R.id.content) as ViewGroup
        if (mMaskView!!.parent == null) {
            content.addView(mMaskView)
            if (mConfiguration!!.mEnterAnimationId != -1) {
                val anim = AnimationUtils.loadAnimation(activity, mConfiguration!!.mEnterAnimationId)!!
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        if (mOnVisibilityChangedListener != null) {
                            mOnVisibilityChangedListener!!.onShown()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                mMaskView!!.startAnimation(anim)
            } else {
                if (mOnVisibilityChangedListener != null) {
                    mOnVisibilityChangedListener!!.onShown()
                }
            }
        }
    }

    /**
     * 隐藏该遮罩并回收资源相关
     */
    fun dismiss() {
        if (mMaskView == null) {
            return
        }
        val vp = mMaskView!!.parent as ViewGroup ?: return
        if (mConfiguration!!.mExitAnimationId != -1) {
            // mMaskView may leak if context is null
            val context = mMaskView!!.context!!

            val anim = AnimationUtils.loadAnimation(context, mConfiguration!!.mExitAnimationId)!!
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    vp.removeView(mMaskView)
                    if (mOnVisibilityChangedListener != null) {
                        mOnVisibilityChangedListener!!.onDismiss()
                    }
                    onDestroy()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            mMaskView!!.startAnimation(anim)
        } else {
            vp.removeView(mMaskView)
            if (mOnVisibilityChangedListener != null) {
                mOnVisibilityChangedListener!!.onDismiss()
            }
            onDestroy()
        }
    }

    /**
     * 根据locInwindow定位后，是否需要判断loc值非0
     */
    fun setShouldCheckLocInWindow(set: Boolean) {
        mShouldCheckLocInWindow = set
    }

    private fun onCreateView(activity: Activity): MaskView {
        val content = activity.findViewById<View>(android.R.id.content) as ViewGroup
        // ViewGroup content = (ViewGroup) activity.getWindow().getDecorView();
        val maskView = MaskView(activity)
        maskView.setFullingColor(activity.resources.getColor(mConfiguration!!.mFullingColorId))
        maskView.setFullingAlpha(mConfiguration!!.mAlpha)
        maskView.setHighTargetCorner(mConfiguration!!.mCorner)
        maskView.setPadding(mConfiguration!!.mPadding)
        maskView.paddingLeft = mConfiguration!!.mPaddingLeft
        maskView.paddingTop = mConfiguration!!.mPaddingTop
        maskView.paddingRight = mConfiguration!!.mPaddingRight
        maskView.paddingBottom = mConfiguration!!.mPaddingBottom
        maskView.setHighTargetGraphStyle(mConfiguration!!.mGraphStyle)
        maskView.setOverlayTarget(mConfiguration!!.mOverlayTarget)
        maskView.setOnKeyListener(this)

        // For removing the height of status bar we need the root content view's
        // location on screen
        //全屏的情况下不需要下面这段
        val parentX = 0
        var parentY = 0
        val loc = IntArray(2)
        content.getLocationInWindow(loc)
        parentY = loc[1]//通知栏的高度
        if (mShouldCheckLocInWindow && parentY == 0) {
            val localClass: Class<*>
            try {
                localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
                parentY = activity.resources.getDimensionPixelSize(i5)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

        }
        if (mConfiguration!!.mTargetView != null) {
            maskView.setTargetRect(Common.getViewAbsRect(mConfiguration!!.mTargetView!!, parentX, parentY))
        } else {
            // Gets the target view's abs rect
            val target = activity.findViewById<View>(mConfiguration!!.mTargetViewId)
            if (target != null) {
                maskView.setTargetRect(Common.getViewAbsRect(target, parentX, parentY))
            }
        }

        // Gets the fulling view's abs rect
        val fulling = activity.findViewById<View>(mConfiguration!!.mFullingViewId)
        if (fulling != null) {
            maskView.setFullingRect(Common.getViewAbsRect(fulling, parentX, parentY))
        }

        if (mConfiguration!!.mOutsideTouchable) {
            maskView.isClickable = false
        } else {
            maskView.setOnClickListener(this)
        }

        // Adds the components to the mask view.
        for (c in mComponents!!) {
            maskView.addView(Common.componentToView(activity.layoutInflater, c))
        }

        return maskView
    }

    private fun onDestroy() {
        mConfiguration = null
        mComponents = null
        mOnVisibilityChangedListener = null
        mMaskView!!.removeAllViews()
        mMaskView = null
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (mConfiguration != null && mConfiguration!!.mAutoDismiss) {
                dismiss()
                return true
            } else {
                return false
            }
        }
        return false
    }

    override fun onClick(v: View) {
        if (mConfiguration != null && mConfiguration!!.mAutoDismiss) {
            dismiss()
        }
    }
}
