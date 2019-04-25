package com.kotlin.retrofit.wuwen.util

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Fragment
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import java.io.File

/**
 * 检查当前网络是否连接
 *
 * @return true 连接状态， false 网络已断开
 */
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo?.isAvailable ?: false
}

/**
 * 打开当前应用设置页面
 */
fun Context.openSystemSetting() {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS ,
                Uri.parse("package:" + packageName))
        startActivity(intent)
    } catch (e: Exception) {}
}

/**
 * 调起系统拨号界面
 *
 * @param mobile 手机号
 */
fun Context.openDial(mobile: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile"))
        startActivity(intent)
    } catch (e: Exception) {}
}


/**
 * 获取NotificationManager
 */
fun Context.notificationManager(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

/**
 * 获取WindowManager
 */
fun Context.windowManager(): WindowManager {
    return getSystemService(Context.WINDOW_SERVICE) as WindowManager
}

/**
 * 获取AlarmManager
 */
fun Context.alarmManager(): AlarmManager {
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

/**
 * ActivityManager
 */
fun Context.activityManager(): ActivityManager {
    return getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
}

/**
 * 获取WifiManager
 */
fun Context.wifiManager(): WifiManager {
    return getSystemService(Context.WIFI_SERVICE) as WifiManager
}

/**
 * 获取ConnectivityManager
 */
fun Context.connectivityManager(): ConnectivityManager {
    return getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

/**
 * 获取Default SharedPreferences
 */
fun Context.defaultSharedPreferences(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

/**
 * 获取Version code
 *
 * @return version code
 */
fun Context.versionCode(): Int {
    return packageManager.getPackageInfo(packageName, 0).versionCode
}

/**
 * 获取Version name
 *
 * @return version name
 */
fun Context.versionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

/**
 * 获取像素密集度参数density
 *
 * @return density
 */
fun Context.density(): Float {
    return resources.displayMetrics.density
}

/**
 * 获取metaData
 *
 * @param key Meta data对应Key值
 */
fun Context.metaData(key: String): String? {
    val applicationInfo = packageManager.getApplicationInfo(packageName,
            PackageManager.GET_META_DATA)
    return applicationInfo.metaData[key] as? String
}

/**
 * 获取导航栏高度
 *
 * @return 导航栏高度 (px)
 */
fun Context.navigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

    var navigationBarHeight = 0
    if (resourceId > 0) {
        navigationBarHeight = resources.getDimensionPixelSize(resourceId)
    }

    return navigationBarHeight
}

/**
 * 获取屏幕宽度
 *
 * @return 屏幕宽度
 */
fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * 获取屏幕高度
 *
 * @return 屏幕高度
 */
fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 获取状态栏高度
 *
 * @return 状态栏高度（px）
 */
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    var statusBarHeight = 0
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }

    return statusBarHeight
}

@Suppress("UNCHECKED_CAST")
fun <V : View> Context.inflate(@LayoutRes layoutId: Int,
                               parent: ViewGroup? = null,
                               attachToRoot: Boolean = false): V {
    return LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot) as V
}

@Suppress("UNCHECKED_CAST")
fun <V : View> Fragment.inflate(@LayoutRes layoutId: Int,
                                parent: ViewGroup? = null,
                                attachToRoot: Boolean = false): V {
    return LayoutInflater.from(activity).inflate(layoutId, parent, attachToRoot) as V
}

@Suppress("UNCHECKED_CAST")
fun <V : View> android.support.v4.app.Fragment.inflate(@LayoutRes layoutId: Int,
                                                       parent: ViewGroup? = null,
                                                       attachToRoot: Boolean = false): V {
    return LayoutInflater.from(activity).inflate(layoutId, parent, attachToRoot) as V
}

/**
 * 返回指定权限是否已经被授予
 *
 * @param permission 权限字符串
 * @return true 已经被授权 false 没有被授权
 */
fun Context.isGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this , permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 安装指定外部路径下的应用，兼容Android N，使用FileProvider访问
 *
 * @param path      apk文件路径
 * @param authority FileProvider authority
 */
fun Context.installPkg(path: String ,
                       authority: String) {
    val pkgFile = File(path)

    if(pkgFile.isFile && pkgFile.extension == "apk") {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(FileProvider.getUriForFile(this , authority , pkgFile) ,
                "application/vnd.android.package-archive")
        startActivity(intent)
    }
}

/**
 * 卸载应用
 *
 * @param pkg 应用包名
 */
@Throws(Exception::class)
fun Context.unInstallPkg(pkg: String) {
    val uri = Uri.parse("package:$pkg")
    val intent = Intent(Intent.ACTION_DELETE , uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun  Context.getDeviceId():String{
   val manager =  getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return manager.simSerialNumber
}