package com.sanqiu.loro.guideview.guide

import android.view.LayoutInflater
import android.view.View

/**
 * 遮罩系统中相对于目标区域而绘制一些图片或者文字等view需要实现的接口. <br></br>
 * <br></br>
 * [.getView] <br></br>
 * [.getAnchor] <br></br>
 * [.getFitPosition] <br></br>
 * [.getXOffset] <br></br>
 * [.getYOffset]
 * <br></br>
 * 具体创建遮罩的说明请参加[com.domobile.pixelworld.ui.widget.guide.GuideBuilder]
 *
 * @see com.domobile.pixelworld.ui.widget.guide.GuideBuilder
 */
interface Component {

    /**
     * 相对目标View的锚点
     *
     * @return could be [.ANCHOR_LEFT], [.ANCHOR_RIGHT],
     * [.ANCHOR_TOP], [.ANCHOR_BOTTOM], [.ANCHOR_OVER]
     */
    val anchor: Int

    /**
     * 相对目标View的对齐
     *
     * @return could be [.FIT_START], [.FIT_END],
     * [.FIT_CENTER]
     */
    val fitPosition: Int

    /**
     * 相对目标View的X轴位移，在计算锚点和对齐之后。
     *
     * @return X轴偏移量, 单位 dp
     */
    val xOffset: Int

    /**
     * 相对目标View的Y轴位移，在计算锚点和对齐之后。
     *
     * @return Y轴偏移量，单位 dp
     */
    val yOffset: Int

    /**
     * 是否是介绍文字
     */
    val hasHint: Boolean

    /**
     * 需要显示的view
     *
     * @param inflater use to inflate xml resource file
     * @return the component view
     */
    fun getView(inflater: LayoutInflater): View

    companion object {

        const val FIT_START = MaskView.LayoutParams.PARENT_START  //相对于高亮的位置

        const val FIT_END = MaskView.LayoutParams.PARENT_END //相对于高亮的位置

        const val FIT_CENTER = MaskView.LayoutParams.PARENT_CENTER //相对于高亮的位置

        const val ANCHOR_LEFT = MaskView.LayoutParams.ANCHOR_LEFT //如果hasHint为false，则为左边，ture则为左上角

        const val ANCHOR_RIGHT = MaskView.LayoutParams.ANCHOR_RIGHT //如果hasHint为false，则为右边，ture则为右下角

        const val ANCHOR_BOTTOM = MaskView.LayoutParams.ANCHOR_BOTTOM //如果hasHint为false，则为下边，ture则为左下角

        const val ANCHOR_TOP = MaskView.LayoutParams.ANCHOR_TOP //如果hasHint为false，则为上边，ture则为右上角

        const val ANCHOR_OVER = MaskView.LayoutParams.ANCHOR_OVER //中间位置

        /**
         * 圆角矩形&矩形
         */
        const val ROUNDRECT = 0

        /**
         * 圆形
         */
        const val CIRCLE = 1
    }
}
