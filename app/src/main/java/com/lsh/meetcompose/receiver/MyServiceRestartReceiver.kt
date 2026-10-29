package com.lsh.meetcompose.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lsh.meetcompose.service.MyForegroundService

/**
 * 前台服务重启广播接收器：充当后台拉起前台服务的轻量跳板。
 *
 * 8.0+ 系统对后台直接启动服务有严格限制，但允许广播接收器在收到合法广播后拉起前台服务。
 * 本组件接收通知 Action 按钮（AAA/BBB）及系统广播，转发执行 startForegroundService。
 *
 * 运行在主线程且受 10 秒超时限制，此处仅做轻量转发，绝不能执行耗时任务。
 */
class MyServiceRestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MyServiceRestartReceiver", "onReceive, action=${intent?.action}")
        if (context != null) {
            val restartIntent = Intent(context, MyForegroundService::class.java)
            context.startForegroundService(restartIntent) // 必须用形参 context 发起启动，Receiver 本身不是 Context
        }
    }
}
