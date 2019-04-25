package com.kotlin.retrofit.wuwen.base

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import com.trello.rxlifecycle2.components.support.RxFragment
import android.content.Intent
import android.support.annotation.MenuRes
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.*
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.http.OnHttpError
import com.kotlin.retrofit.wuwen.util.KLog
import com.kotlin.retrofit.wuwen.view.bindDialog
//import me.foji.smartui.util.Logger
//import org.jetbrains.anko.support.v4.toast


abstract  class BaseFragment: RxFragment(), OnHttpError {
    var REQUEST_CODE = 0x300

    private var mContentView: LinearLayout? = null
    private val mDialog by bindDialog {
        negativeButton(visible = false)
        positiveButton(visible = false)
        setCanceledOnTouchOutside(true)
    }

    private var mRequestCode = -1
    private var mResultCode = RESULT_CANCEL
    private var mResultData: Bundle? = null

    companion object {
        val RESULT_OK = Activity.RESULT_OK
        val RESULT_CANCEL = Activity.RESULT_CANCELED
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = LinearLayout(activity)
        mContentView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT)
        mContentView!!.orientation = LinearLayout.VERTICAL

        val bindView = onBindView(inflater, container, savedInstanceState)
        if (null != bindView) mContentView!!.addView(bindView)


        return mContentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        onInitView(savedInstanceState)
        initListener()
        loadData()
        super.onActivityCreated(savedInstanceState)
    }

    abstract fun loadData()

    abstract fun pushData()

    /**
     * 初始化控件的监听
     */
    abstract fun initListener()

    abstract fun onInitView(savedInstanceState: Bundle?)

    abstract fun onBindView(inflater: LayoutInflater? ,
                            container: ViewGroup? ,
                            savedInstanceState: Bundle?): View?


/*
    fun push(@AnimRes enter: Int = R.anim.fragment_enter,
             @AnimRes exit: Int = R.anim.fragment_exit,
             @AnimRes popEnter: Int = R.anim.fragment_pop_enter,
             @AnimRes popExit: Int = R.anim.fragment_pop_exit,
             fragment: KClass<out BaseFragment>,
             vararg params: Pair<String, Any>) {
        (activity as? BaseActivity)?.push(enter , exit , popEnter , popExit , fragment , *params)
    }

    fun push(fragment: KClass<out BaseFragment>, vararg params: Pair<String, Any>) {
        push(enter    = R.anim.fragment_enter,
                exit     = R.anim.fragment_exit,
                popEnter = R.anim.fragment_pop_enter,
                popExit  = R.anim.fragment_pop_exit,
                fragment = fragment ,
                params   = *params)
    }*/

    fun pop() {
        (activity as? BaseActivity)?.pop()
    }

//    fun switchTo(smooth: Boolean = false,
//                 fragment: KClass<out BaseFragment>,
//                 vararg params: Pair<String, Any>) {
//        (activity as? BaseActivity)?.switchTo(fragment, smooth, *params)
//    }

    /**
     * 获取LayoutInflater实例
     *
     * @return [LayoutInflater]实例
     */
    fun layoutInflater(): LayoutInflater = LayoutInflater.from(activity)

    override fun onError(httpCode: HttpCode, code: String?, error: String?) {
//        ErrorMachine.create(this).onError(activity, httpCode, code, error)
    }



    fun data(f: Bundle.()->Unit) {
        arguments?.f()
    }




    fun inflateMenu(@MenuRes menuResId: Int) {
    }

    open fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return false
    }

    fun promptError(error: String?) {
        mDialog.message(error!!)
        mDialog.show()
    }

    fun promptError(@StringRes stringRes: Int) {
        promptError(getString(stringRes))
    }

    open fun onBackPressed() {

    }

    /**
     * 回退到当前页面操作完成后会调用这个方法
     *
     * @param from 表示源头Fragment
     */
    open fun onBackCompleted(from: BaseFragment? = null) {
        KLog.e("onBackCompleted: ${from?.javaClass?.name}")
    }

    private fun recycleView() {

    }

    open fun onFragmentResult(requestCode: Int,
                              resultCode: Int,
                              data: Bundle?) {

    }

    fun setRequestCode(requestCode: Int) {
        mRequestCode = requestCode
    }


    fun setResult(resultCode: Int, resultData: Bundle? = null) {
        mResultCode = resultCode
        mResultData = resultData
    }

    fun finish() {
        (activity as? BaseActivity)?.popForResult(requestCode = mRequestCode,
                resultCode = mResultCode,
                data = mResultData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycleView()

    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
//        BaseApplication.getRefWatcher(context!!).watch(this)

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
//            KLog.d("fragment hide")
            mDialog.dismiss()
        }else{
//            KLog.d("fragment show")
        }
    }
//    private var mRefreshUi :UiRefreshAllListener?=null
//
//    interface UiRefreshAllListener {
//        fun fragmentUiRefresh()
//    }
//
//    fun setRefreshUI(uiRefresh: UiRefreshAllListener) {
//
//        this.mRefreshUi =uiRefresh
//    }


    val paraTime = "yyyy-MM-dd HH:mm:ss"

    fun getAlarmType(type: Int?): String {
        when (type) {
            1 -> return "火警"
            2 -> return "救援"
            3 -> return "救助"
        }
        return "火警"
    }

    fun getAlarmLevel(type: Int?): String {
        when (type) {
            1 -> return "一级"
            2 -> return "二级"
            3 -> return "三级"
        }
        return "一级"
    }


}