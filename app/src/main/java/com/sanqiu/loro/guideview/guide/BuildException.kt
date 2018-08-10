package com.sanqiu.loro.guideview.guide

/**
 * 遮罩系统运行异常的封装
 */
internal class BuildException : RuntimeException {
    private val mDetailMessage: String

    constructor() {
        mDetailMessage = "General error."
    }

    constructor(detailMessage: String) {
        mDetailMessage = detailMessage
    }

    override fun getLocalizedMessage(): String {
        return "Build GuideFragment failed: $mDetailMessage"
    }

    companion object {

        private val serialVersionUID = 6208777692136933357L
    }
}
