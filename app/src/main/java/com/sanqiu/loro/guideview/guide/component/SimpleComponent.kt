package com.sanqiu.loro.guideview.guide.component

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

import android.widget.ImageView
import android.view.animation.LinearInterpolator
import com.sanqiu.loro.guideview.R
import com.sanqiu.loro.guideview.guide.Component


/**
 * Created by loro
 */
class SimpleComponent : Component {
    //为flase位置就跟随高亮走，通常用于高亮动效展示
    override val hasHint: Boolean
        get() = false

    override val anchor: Int
        get() = Component.ANCHOR_TOP

    override val fitPosition: Int
        get() = Component.FIT_START

    override val xOffset: Int
        get() = -30

    override val yOffset: Int
        get() = 30

    private var ivIcon: ImageView? = null

    override fun getView(inflater: LayoutInflater): View {

        val ll = inflater.inflate(R.layout.guide_layer_simple, null) as LinearLayout
        ivIcon = ll.findViewById(R.id.ivIcon)

        showRotateAnimation(ivIcon)

        ll.setOnClickListener { view -> Toast.makeText(view.context, "引导层被点击了", Toast.LENGTH_SHORT).show() }
        return ll
    }

    /**
     * 设置动画
     */
    private fun showRotateAnimation(view: View?) {
        // 获取自定义动画实例
        val rotateAnim = CustomRotateAnim.customRotateAnim
        // 一次动画执行1秒
        rotateAnim.duration = 1000
        // 设置为循环播放
        rotateAnim.repeatCount = -1
        // 设置为匀速
        rotateAnim.interpolator = LinearInterpolator()
        // 开始播放动画
        view?.post {
            view?.startAnimation(rotateAnim)
        }
    }
}
