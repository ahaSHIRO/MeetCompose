package com.lsh.meetcompose.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

// TAG 放在文件顶层：编译期常量不需要挂在类上，Logcat 过滤时直接写 MyService3 即可
private const val TAG = "MyService3"

/**
 * 混合模式服务：先 startService 再 bindService。
 *
 * 两条调用路各跑各的回调：
 * - 只 start 不 bind：onCreate -> onStartCommand -> onDestroy
 * - 只 bind 不 start：onCreate -> onBind -> onUnbind -> onDestroy
 *
 * 两者都走，才是「既能常驻、又能被遥控」的完整形态。
 */
class MyService3 : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyService3 onCreate")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(TAG, "MyService3 onStartCommand")
        return START_STICKY // 被杀后由系统重建；重建时 intent 为 null，解引用前必须判空
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MyService3 onDestroy")
    }

    // 返回非 null 就等于宣告「我接受绑定」，绑定方才会收到 onServiceConnected
    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "MyService3 onBind")
        return ProxyBinder()
    }

    // 空 Binder 也合法：构造时已向内核登记，它本身就是一个可寻址的凭证。
    // 若要让绑定方调用服务内的方法，得改成 inner class，或改用 AIDL 跨进程。
    class ProxyBinder : Binder()
}
