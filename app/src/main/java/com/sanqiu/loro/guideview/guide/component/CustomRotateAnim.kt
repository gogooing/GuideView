package com.sanqiu.loro.guideview.guide.component

import android.view.animation.Animation
import android.view.animation.Transformation


open class CustomRotateAnim : Animation() {

    /** 控件宽  */
    private var mWidth: Int = 0

    /** 控件高  */
    private var mHeight: Int = 0

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        this.mWidth = width
        this.mHeight = height
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        // 左右摇摆
        t.matrix.setRotate((Math.sin(interpolatedTime.toDouble() * Math.PI * 2.0) * 50).toFloat(), mWidth / 2f, mHeight / 2f)
        super.applyTransformation(interpolatedTime, t)
    }

    companion object {

        /** 实例  */
        private var rotateAnim: CustomRotateAnim? = null

        /**
         * 获取动画实例
         * @return 实例
         */
        val customRotateAnim: CustomRotateAnim
            get() {
                if (null == rotateAnim) {
                    rotateAnim = CustomRotateAnim()
                }
                return rotateAnim!!
            }
    }
}