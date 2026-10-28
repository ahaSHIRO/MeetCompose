package com.lsh.meetcompose.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * 用 bindService 拉起的服务，重点演示「绑定之后，Activity 能直接调服务里的方法」。
 *
 * 它和 MyService1 的差别在通信方式：MyService1 只能靠 Intent 发命令，服务内部做了什么
 * 调用方一概不知；这里 onBind 把 [MyBinder] 递出去，Activity 在 onServiceConnected
 * 里拿到、强转，就能像调本地对象一样调 startPlayMusic / stopPlayMusic。
 *
 * 代价是生命周期被绑定方牵着走：最后一个绑定方执行 unbindService 之后，系统就会销毁本服务。
 * 所以它不适合做「关掉界面还要继续跑」的事，那种场景该用 startService 配合前台通知。
 */
class MyService2 : Service() {

    companion object {
        const val TAG = "MyService2"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyService2 onCreate")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "MyService2 onBind")
        return MyBinder()
    }

    /**
     * 递给绑定方的「遥控器」，上面只挂了两个按钮：开始播、停止播。
     *
     * Activity 在 onServiceConnected 里拿到的静态类型只是 IBinder，光看它什么方法都调不到，
     * 必须先 `as MyService2.MyBinder` 强转回这个具体类型，两个播放方法才露出来。
     * 继承 Binder 是进程内通信最省事的做法；如果哪天服务真挪进了别的进程，就得换成 AIDL
     * 生成 Stub，这套直接强转的写法会当场抛 ClassCastException。
     *
     * 方法体现在是空的：这一关只验证「绑定链路通不通、强转对不对」，真实播放逻辑留待扩展。
     */
    class MyBinder : Binder() {

        fun startPlayMusic() {}
        fun stopPlayMusic() {}
    }

    override fun onDestroy() {
        Log.d(TAG, "MyService2 onDestroy")
        super.onDestroy()
    }
}