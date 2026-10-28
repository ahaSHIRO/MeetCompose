package com.lsh.meetcompose.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 用 startService 拉起的服务，演示「服务常驻 + 内部自己开线程干活」的写法。
 *
 * 服务的生命周期跟谁启动它无关，启动方退出后它还能活着，直到有人 stopService 或系统回收。
 * 但 Service 本身跑在主线程上，它不带线程——如果把 while 循环直接摆在 onCreate 里，
 * 主线程会被永久占住，界面直接 ANR。所以这里 onCreate 只做启动工作，真正的循环丢进
 * [asyncTask] 里开的子线程中，每秒打一条日志模拟后台任务。
 *
 * onBind 返回 null 是刻意的：这个服务只接受 startService 这种「发命令」式调用，
 * 不提供让调用方远程调方法的 Binder，所以不需要被绑定。
 */
class MyService1 : Service() {

    companion object {
        const val TAG = "MyService1"
    }

    @Volatile // 主线程写、子线程读，不加可见性不保证，线程会停不下来
    private var isRun = true

    override fun onBind(intent: Intent): IBinder? {
        return null // 明确不支持绑定：bindService 拿到的 service 会是 null
    }

    override fun onCreate() {
        super.onCreate()
        isRun = true
        Log.d(TAG, "MyService1 created")
        asyncTask()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MyService1 started")
        return super.onStartCommand(intent, flags, startId)
    }

    // 用户在最近任务里划掉 App 时回调；国内厂商可能不触发，只能当兜底清理
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "MyService1 task removed")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.d(TAG, "MyService1 destroyed")
        super.onDestroy()
        isRun = false // 唯一的线程停止点，漏了就线程泄漏
    }

    private fun asyncTask() {
        Thread({
            while (isRun) {
                Log.d(TAG, "MyService1 的异步任务工作，执行中~~~~~~ ")
                Thread.sleep(1000)
            }
        }).start()
    }
}