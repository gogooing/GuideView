package com.sanqiu.loro.guideview

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import com.sanqiu.loro.guideview.guide.Guide
import com.sanqiu.loro.guideview.guide.GuideBuilder
import com.sanqiu.loro.guideview.guide.component.SimpleComponent
import com.sanqiu.loro.guideview.guide.component.SimpleHintComponent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivIcon?.post {
            showGuideView()
        }
    }


    private var guide: Guide? = null

    private fun showGuideView() {
        if (null != guide) return
        val builder = GuideBuilder()
        builder.setTargetView(ivIcon)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(0)
                .setOverlayTarget(false)
                .setOutsideTouchable(false)

        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {

            }

            override fun onDismiss() {

            }
        })

        builder.addComponent(SimpleComponent())
        builder.addComponent(SimpleHintComponent())
        guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(this@MainActivity)
    }


}
