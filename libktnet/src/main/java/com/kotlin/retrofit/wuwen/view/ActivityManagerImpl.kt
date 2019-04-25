package com.kotlin.retrofit.wuwen.view

import com.kotlin.retrofit.wuwen.base.BaseActivity
import java.util.ArrayList

/**
 * Activity manager implementaion
 *
 */
class ActivityManagerImpl : ActivityManager() {
    private val mActivityStack = ArrayList<BaseActivity>()
    private val ACTIVITY_MAX_NUMBER = 20

    override fun push(activity: BaseActivity) {
        if (mActivityStack.size >= ACTIVITY_MAX_NUMBER) {
            val act = mActivityStack.removeAt(mActivityStack.size - 1)
            if (!act.isFinishing) {
                act.finish()
            }
        }
        if (!mActivityStack.contains(activity)) {
            mActivityStack.add(0, activity)
        }
    }

    override fun pop(cls: Class<out BaseActivity>) {
        if (mActivityStack.size > 0) {
            for (activityInst in mActivityStack) {
                if (cls.name == activityInst.javaClass.name) {
                    if (!activityInst.isFinishing) {
                        activityInst.finish()
                    }
                    break
                }
            }
        }
    }

    override fun pop() {
        if (mActivityStack.size > 0) {
            val activity = mActivityStack.removeAt(0)
            if (!activity.isFinishing) activity.finish()
        }
    }

    override fun pop(vararg classes: Class<out BaseActivity>) {
        if (mActivityStack.size > 0 && classes.size > 0) {
            for (cls in classes) {
                for (activityInst in mActivityStack) {
                    if (activityInst.javaClass.name == cls.name) {
                        if (!activityInst.isFinishing) {
                            activityInst.finish()
                        }
                        break
                    }
                }
            }
        }
    }

    override fun popToActivity(cls: Class<out BaseActivity>) {
        val activityInst = instanceOf(cls)
        if (null != activityInst) {
            while (mActivityStack.size > 0) {
                val first = mActivityStack[0]
                if (first === activityInst) {
                    break
                } else {
                    mActivityStack.remove(first)
                }
            }
        }
    }

    private fun instanceOf(activity: Class<out BaseActivity>): BaseActivity? {
        if (mActivityStack.size > 0) {
            for (activityInst in mActivityStack) {
                if (activity.name == activityInst.javaClass.name) {
                    return activityInst
                }
            }
        }
        return null
    }

    override fun remove(activity: BaseActivity) {
        mActivityStack.remove(activity)
    }

    override fun removeAll(){
        for(activity in mActivityStack){
            activity.finish()
            System.gc()
        }
    }
}
