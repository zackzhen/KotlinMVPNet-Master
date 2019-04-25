package com.kotlin.retrofit.wuwen.view


interface BaseView {
    /**
     * 首次进入页面，页面需要进行初始化。比如：页面展示依赖服务器数据，在页面加载开始的时候，需要显示一个加载
     * 等待的页面，直到请求完成，页面才能正常渲染。这个时候，等待页面消失，页面正常显示。
     */
    fun onInitViewStarted() {}

    /**
     * 对应[onInitViewStarted]，首次进入页面，后台操作完成后调用
     */
    fun onInitViewCompleted() {}

    /**
     * 网络接口请求开始，用于在UI页面展示等待对话框等操作
     */
    fun onRequestStarted() {}

    /**
     * 网络接口请求完成，对应[onRequestStarted],在请求完成后调用
     */
    fun onRequestCompleted() {}
}