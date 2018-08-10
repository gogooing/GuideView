package com.sanqiu.loro.guideview.guide.component

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.sanqiu.loro.guideview.R
import com.sanqiu.loro.guideview.guide.Component


/**
 * Created by loro
 */
class SimpleHintComponent : Component {
    //为flase位置只能固定5个位置，通知用于文本展示
    override val hasHint: Boolean
        get() = true

    override val anchor: Int
        get() = Component.ANCHOR_TOP

    //hashint为true暂时用不到
    override val fitPosition: Int
        get() = Component.FIT_START

    override val xOffset: Int
        get() = -5

    override val yOffset: Int
        get() = 5

    private var ivGuideIcon: ImageView? = null
    private var tvGuideHint: TextView? = null

    override fun getView(inflater: LayoutInflater): View {

        val ll = inflater.inflate(R.layout.guide_hint_simple, null) as LinearLayout
        ivGuideIcon = ll.findViewById(R.id.ivGuideIcon)
        tvGuideHint = ll.findViewById(R.id.tvGuideHint)

        var mShowAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        mShowAction.duration = 500

//        var mHiddenAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                0.0f)
//        mHiddenAction.duration = 500
        ll?.post {
            ll?.startAnimation(mShowAction)
        }

        ll.setOnClickListener { view -> Toast.makeText(view.context, "引导层被点击了", Toast.LENGTH_SHORT).show() }
        return ll
    }
}
