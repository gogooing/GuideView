package com.sanqiu.loro.guideview.guide

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.sanqiu.loro.guideview.guide.Component

/**
 * 遮罩系统创建时配置参数的封装
 */
internal class Configuration : Parcelable {

    /**
     * 需要被找的View
     */
    var mTargetView: View? = null

    /**
     * 高亮区域的padding
     */
    var mPadding = 0
    /**
     * 高亮区域的左侧padding
     */
    var mPaddingLeft = 0
    /**
     * 高亮区域的顶部padding
     */
    var mPaddingTop = 0
    /**
     * 高亮区域的右侧padding
     */
    var mPaddingRight = 0
    /**
     * 高亮区域的底部padding
     */
    var mPaddingBottom = 0

    /**
     *
     */
    var mOutsideTouchable: Boolean = false

    /**
     * 遮罩透明度
     */
    var mAlpha = 255

    /**
     * 遮罩覆盖区域控件Id
     *
     *
     * 该控件的大小既该导航页面的大小
     */
    var mFullingViewId = -1

    /**
     * 目标控件Id
     */
    var mTargetViewId = -1

    /**
     * 高亮区域的圆角大小
     */
    var mCorner = 0

    /**
     * 高亮区域的图形样式，默认为矩形
     */
    var mGraphStyle = Component.ROUNDRECT

    /**
     * 遮罩背景颜色id
     */
    var mFullingColorId = android.R.color.black

    /**
     * 是否在点击的时候自动退出导航
     */
    var mAutoDismiss = true

    /**
     * 是否覆盖目标控件
     */
    var mOverlayTarget = false

    var mShowCloseButton = false

    var mEnterAnimationId = -1

    var mExitAnimationId = -1

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(mAlpha)
        dest.writeInt(mFullingViewId)
        dest.writeInt(mTargetViewId)
        dest.writeInt(mFullingColorId)
        dest.writeInt(mCorner)
        dest.writeInt(mPadding)
        dest.writeInt(mPaddingLeft)
        dest.writeInt(mPaddingTop)
        dest.writeInt(mPaddingRight)
        dest.writeInt(mPaddingBottom)
        dest.writeInt(mGraphStyle)
        dest.writeByte((if (mAutoDismiss) 1 else 0).toByte())
        dest.writeByte((if (mOverlayTarget) 1 else 0).toByte())
    }

    companion object {

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<Configuration> = object : Parcelable.Creator<Configuration> {
            override fun createFromParcel(source: Parcel): Configuration {
                val conf = Configuration()
                conf.mAlpha = source.readInt()
                conf.mFullingViewId = source.readInt()
                conf.mTargetViewId = source.readInt()
                conf.mFullingColorId = source.readInt()
                conf.mCorner = source.readInt()
                conf.mPadding = source.readInt()
                conf.mPaddingLeft = source.readInt()
                conf.mPaddingTop = source.readInt()
                conf.mPaddingRight = source.readInt()
                conf.mPaddingBottom = source.readInt()
                conf.mGraphStyle = source.readInt()
                conf.mAutoDismiss = source.readByte().toInt() == 1
                conf.mOverlayTarget = source.readByte().toInt() == 1
                return conf
            }

            override fun newArray(size: Int): Array<Configuration?> {
                return arrayOfNulls(size)
            }
        }
    }
}
