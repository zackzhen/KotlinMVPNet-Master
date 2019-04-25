package com.kotlin.retrofit.wuwen.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.http.OnHttpError
import com.kotlin.retrofit.wuwen.util.KLog
import com.kotlin.retrofit.wuwen.view.ActivityLifecycleDelegate
import com.kotlin.retrofit.wuwen.view.ActivityManager
import com.kotlin.retrofit.wuwen.view.bindDialog
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import java.io.Serializable
import io.reactivex.disposables.Disposable
import java.util.*


abstract class BaseActivity : RxAppCompatActivity(), OnHttpError {
    private var mCurrentFragmentTag: String? = null
    private var mContentView: LinearLayout? = null
    private var mLifecycleDelegates = ArrayList<ActivityLifecycleDelegate>()
    private val mDialog by bindDialog {
        positiveButton(visible = false)
        negativeButton(visible = false)
        setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)// 隐藏标题
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)// 设置全屏 亮屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        prepare()
        ActivityManager.newInstance().push(this)

        mLifecycleDelegates.forEach {
            it.onCreate(savedInstanceState)
        }
    }

    private var mActivityCount = 0


    private fun prepare() {
//        mToolbar = toolbar()

        mContentView = LinearLayout(this)
        mContentView!!.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mContentView!!.orientation = LinearLayout.VERTICAL
//        addToolbarToContentView()
        super.setContentView(mContentView)

//        fitSystemWindows(true)
//        fitStatusBar()
//        StatusBarHelper.setLightMode(window, true)

    }

    protected fun fitSystemWindows(fit: Boolean) {
        mContentView?.fitsSystemWindows = fit
    }

    /**
     * 开启浸透式状态栏
     *
     * @param enable true 开启 ，false 关闭
     */
    protected fun enableSoakedStatusBar(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (enable) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
    }

    private fun fitStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    protected fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val contentView = layoutInflater.inflate(layoutResID, mContentView, false)
        setContentView(contentView)
    }

    override fun setContentView(view: View) {
        setContentView(view, view.layoutParams)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        var lp = params
        if (null == lp) lp = generateDefaultLayoutParams()
        mContentView?.removeAllViews()
        mContentView?.addView(view, lp)
    }

    fun setContentViewSuper(@LayoutRes idRes: Int) {
        super.setContentView(idRes)
    }


    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    fun addLifecycleDelegate(lifecycleDelegate: ActivityLifecycleDelegate) {
        mLifecycleDelegates.add(lifecycleDelegate)
    }

/*
    fun push(@AnimRes enter: Int = R.anim.fragment_enter,
             @AnimRes exit: Int = R.anim.fragment_exit,
             @AnimRes popEnter: Int = R.anim.fragment_pop_enter,
             @AnimRes popExit: Int = R.anim.fragment_pop_exit,
             fragment: KClass<out BaseFragment>,
             vararg params: Pair<String, Any>) {
        try {
            if (fragment.qualifiedName == mCurrentFragmentTag) return

            KLog.e("push: current = $mCurrentFragmentTag")

            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.setCustomAnimations(enter, exit, popEnter, popExit)
            if (!TextUtils.isEmpty(mCurrentFragmentTag)) {
                val currentFragment = fragmentManager.findFragmentByTag(mCurrentFragmentTag)

                if (null != currentFragment) {
                    transaction.hide(currentFragment)
                    transaction.addToBackStack(mCurrentFragmentTag)
                }
            }

            var targetFragment = fragmentManager.findFragmentByTag(fragment.qualifiedName) as? BaseFragment
            if (null != targetFragment && targetFragment.isActive()) {
                targetFragment.arguments?.clear()
                targetFragment.arguments?.putAll(bundleOf(*params))
                transaction.show(targetFragment)
            } else {
                targetFragment = Fragment.instantiate(this, fragment.qualifiedName, bundleOf(*params)) as BaseFragment?
                if (null != targetFragment) {
                    transaction.add(containerId(), targetFragment, fragment.qualifiedName)
                }
            }
            transaction.commitAllowingStateLoss()

            mCurrentFragmentTag = fragment.qualifiedName
            KLog.e("push: target = $mCurrentFragmentTag")
        } catch (e: NoContainerIdException) {
            val intent = Intent(this, RouterActivity::class.java)
            intent.putExtra(RouterActivity.KEY_FRAGMENT_CLASS_NAME, fragment.qualifiedName)
            intent.putExtra(RouterActivity.KEY_DATA, bundleOf(*params))
            startActivity(intent)
        }
    }

    fun push(fragment: KClass<out BaseFragment>, vararg params: Pair<String, Any>) {
        push(enter = R.anim.fragment_enter,
                exit = R.anim.fragment_exit,
                popEnter = R.anim.fragment_pop_enter,
                popExit = R.anim.fragment_pop_exit,
                fragment = fragment,
                params = *params)
    }

    /**
     * 无缝切换到Fragment，不展示切换动画
     *
     * @param fragment 切换的目标Fragment
     * @param smooth true 平行切换Fragment，不加入回退栈。
     *               false 正确切换Fragment，并加入回退栈
     * @param params 传递到目标Fragment的数据 (eg："name" to " Smith")
     */
    fun switchTo(fragment: KClass<out BaseFragment>,
                 smooth: Boolean = false,
                 vararg params: Pair<String, Any>) {
        if (!smooth) {
            push(enter = 0,
                    exit = 0,
                    popEnter = 0,
                    popExit = 0,
                    fragment = fragment,
                    params = *params)
        } else {
            val success = SmoothFragmentManager.get(this)
                    .switchTo(containerId = containerId(),
                            fragment = fragment,
                            params = *params)
            if (success) {
                mCurrentFragmentTag = fragment.qualifiedName
            }
        }
    }

    /**
     * 平行切换Fragment，通常用于Tab页面切换Fragment
     *
     * @param fragment 目标Fragment
     * @param params 传递到目标Fragment的数据 (eg："name" to "Smith")
     */
    fun smoothSwitchTo(fragment: KClass<out BaseFragment>,
                       vararg params: Pair<String, Any>) {
        switchTo(fragment, true, *params)
    }
    */
    fun pop(): Boolean {
        val fragmentManager = supportFragmentManager

        var fragmentTag: String? = null
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentTag = fragmentManager
                    .getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
        }

        val lastFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                as? BaseFragment

        KLog.e("pop: before =  $mCurrentFragmentTag")
        if (fragmentManager.popBackStackImmediate()) {
            mCurrentFragmentTag = fragmentTag
            KLog.e("pop: after =  $mCurrentFragmentTag")
            (supportFragmentManager.findFragmentByTag(mCurrentFragmentTag) as? BaseFragment)
                    ?.onBackCompleted(lastFragment)
            return true
        }

        finish()
        return false
    }

    fun popForResult(requestCode: Int, resultCode: Int, data: Bundle?): Boolean {
        val fragmentManager = supportFragmentManager

        var fragmentTag: String? = null
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentTag = fragmentManager
                    .getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
        }

        val lastFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                as? BaseFragment

        if (fragmentManager.popBackStackImmediate()) {
            mCurrentFragmentTag = fragmentTag
            val targetFragment = supportFragmentManager.findFragmentByTag(mCurrentFragmentTag)
                    as? BaseFragment
            targetFragment?.onBackCompleted(lastFragment)
            targetFragment?.onFragmentResult(requestCode, resultCode, data)
            return true
        }

        val intent = Intent()
        fullDataIntoIntent(intent, data)
        setResult(resultCode)
        finish()
        return false
    }

    private fun fullDataIntoIntent(intent: Intent, data: Bundle?) {
        if (null != data) {
            val keySet = data.keySet()
            val iterator = keySet.iterator()

            while (iterator.hasNext()) {
                val key = iterator.next()
                val value = data.get(key)
                when (value) {
                    is Boolean -> intent.putExtra(key, value)
                    is Byte -> intent.putExtra(key, value)
                    is Char -> intent.putExtra(key, value)
                    is Short -> intent.putExtra(key, value)
                    is Int -> intent.putExtra(key, value)
                    is Long -> intent.putExtra(key, value)
                    is Float -> intent.putExtra(key, value)
                    is Double -> intent.putExtra(key, value)
                    is String -> intent.putExtra(key, value)
                    is CharSequence -> intent.putExtra(key, value)
                    is Parcelable -> intent.putExtra(key, value)
                    is Serializable -> intent.putExtra(key, value)
                    is BooleanArray -> intent.putExtra(key, value)
                    is ByteArray -> intent.putExtra(key, value)
                    is CharArray -> intent.putExtra(key, value)
                    is DoubleArray -> intent.putExtra(key, value)
                    is FloatArray -> intent.putExtra(key, value)
                    is IntArray -> intent.putExtra(key, value)
                    is LongArray -> intent.putExtra(key, value)
                    is Array<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        when {
                            value.isArrayOf<Parcelable>() -> intent.putExtra(key, value)
                            value.isArrayOf<CharSequence>() -> intent.putExtra(key, value)
                            value.isArrayOf<String>() -> intent.putExtra(key, value)
                            else -> throw RuntimeException("Unsupported bundle component (${value.javaClass})")
                        }
                    }
                    is ShortArray -> intent.putExtra(key, value)
                    is Bundle -> intent.putExtra(key, value)
                    else -> throw RuntimeException("Unsupported bundle component (${value.javaClass})")
                }
            }
        }
    }

    /**
     * 从Intent中获取数据
     *
     * @param init 初始化函数
     */
    fun intent(init: Intent.() -> Unit) {
        intent.init()
    }



    fun promptError(error: String?) {

        mDialog.message(error!!)
        mDialog.show()
    }

    fun promptError(@StringRes stringRes: Int) {

        promptError(getString(stringRes))
    }

    open fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onError(httpCode: HttpCode, code: String?, error: String?) {
        onBeforeError(httpCode, code, error)
//        ErrorMachine.create(this).onError(this, httpCode, code, error)
    }

    /**
     * 统一错误处理之前的操作
     */
    open fun onBeforeError(httpCode: HttpCode, code: String?, error: String?) {
    }


    override fun onStart() {
        super.onStart()
        mLifecycleDelegates.forEach { it.onStart() }
    }

    override fun onResume() {
        super.onResume()
        mLifecycleDelegates.forEach { it.onResume() }
        mActivityCount ++
        // 友盟统计
//        MobclickAgent.onResume(this)
//        TCAgent.onPageStart(this, showActivityName("${this.javaClass.name}"))
    }

    var disposable: Disposable? = null
    override fun onPause() {
        super.onPause()
        mLifecycleDelegates.forEach { it.onPause() }

        mActivityCount --

        if (disposable == null){
//            disposable = Observable.interval(3, TimeUnit.SECONDS)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object :Observer<Long>()
//                        )


        }
        if (mActivityCount == 0){
//            IFlyTTS.getInstance(this).playText("软件在后台运行，请打开软件")
        }
        // 友盟统计
//        MobclickAgent.onPause(this)
//        TCAgent.onPageEnd(this, showActivityName("${this.javaClass.name}"))
    }

    override fun onStop() {
        super.onStop()
        mLifecycleDelegates.forEach { it.onStop() }
    }

    /**
     * 回退事件处理逻辑：如果Fragment拦截了回退事件，则将事件交给Fragment处理；否则，Activity自行处理
     */
    override fun onBackPressed() {
            pop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    /*    if (Short.MAX_VALUE / 2 == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                if (this is OnPictureSelectListener) {
                    onPictureSelected(
                            data?.getSerializableExtra(PictureSelector.KEY_SELECTED_IMAGE) as? Image)
                }
            }

            if (Activity.RESULT_CANCELED == resultCode) {
                if (this is OnPictureSelectListener) {
                    onPictureSelectCanceled()
                }
            }
        }*/
        mLifecycleDelegates.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.newInstance().remove(this)
//        SmoothFragmentManager.recycle(this)
        mLifecycleDelegates.forEach { it.onDestroy() }
        mDialog.dismiss()

//         BaseApplication.getRefWatcher(this).watch(this)
    }

    fun onRequestCompleted(){

    }

}