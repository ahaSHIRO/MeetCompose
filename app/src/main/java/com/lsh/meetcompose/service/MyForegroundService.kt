package com.lsh.meetcompose.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.lsh.meetcompose.MainActivity
import com.lsh.meetcompose.R
import kotlin.jvm.java

/**
 * 前台服务：用一条常驻通知换取后台不被系统杀死的资格。
 *
 * 8.0 起后台服务被严格限制，想长时间驻留就必须走前台服务这条路。代价是通知栏一直挂着
 * 一条通知，用户随时看得见——这正是系统允许你继续运行的前提。
 *
 * 启动顺序不能乱：onCreate 里先建渠道、再构造 Notification；onStartCommand 里立刻
 * startForeground，五秒内没调用就会被系统判定违规并抛异常。
 * Manifest 里必须同时有前台服务权限、通知权限、dataSync 类型权限，且
 * foregroundServiceType="dataSync" 与 FOREGROUND_SERVICE_DATA_SYNC 成对出现。
 */
class MyForegroundService : Service() {
    companion object {
        // const val 走编译期常量，日志过滤器能直接用作标签
        const val TAG = "MyForegroundService"
        const val ACTION_RESTART = "com.lsh.meetcompose.service.MyForegroundService.RESTART"
    }

    @Volatile // 主线程写、子线程读，不标可见性线程可能读不到 false 而停不下来
    private var isRun = true
    private lateinit var notification: Notification
    private val channelId1 = "simple" // 普通通知渠道
    private val channelId2 = "silent" // 静默渠道，前台服务常驻通知用它避免每次响铃
    override fun onBind(intent: Intent): IBinder? {
        return null // 不支持绑定，只接受 startForegroundService 这条命令通道
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyForegroundService 创建了")
        createChannel()
        createNotification()
        isRun = true
        asyncTask()
    }

    private fun asyncTask() {
        Thread({
            while (isRun) {
                Log.d(TAG, "MyForegroundService 的异步任务工作，执行中~~~~~~ ")
                Thread.sleep(1000)
            }
        }).start()
    }

    private fun createNotification() {
        // 点通知本体：回到界面
        val contentIntent = PendingIntent.getActivity(
            this, 22,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // 两个按钮走广播：后台直接拉服务会被拦，广播接收器可以
        // 请求码刻意不同：相同则 PendingIntent 被复用，两个按钮会变成同一个
        val actionA = PendingIntent.getBroadcast(
            this, 13,
            Intent(this, MyServiceRestartReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val actionB = PendingIntent.getBroadcast(
            this, 25,
            Intent(this, MyServiceRestartReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        notification = Notification.Builder(this, channelId2)
            .setContentTitle("我的通知")
            .setContentText("你有条新的消息")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(contentIntent) // 点通知时执行
            .addAction(R.drawable.ic_launcher_foreground, "AAA", actionA)
            .addAction(R.drawable.ic_launcher_foreground, "BBB", actionB)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MyForegroundService 启动了")
        startForeground(1, notification) // 五秒内必须调用，否则系统抛 RemoteServiceException
        return START_STICKY // 被杀后由系统尝试重建，intent 为空
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MyForegroundService 销毁了")
        isRun = false
        sendRestartSignal("onDestroy")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "MyForegroundService 任务被移除了")
        sendRestartSignal("onTaskRemoved")
    }

    // 空 Intent + action + setPackage：属于“只限本包”的显式广播
// 8.0+ 只有显式广播才允许被静态注册的 receiver 收到；不设 package 等于石沉大海
    private fun sendRestartSignal(from: String) {
        Log.d(TAG, "发送重启信号，来源：$from")
        val intent = Intent().apply {
            action = ACTION_RESTART
            `package` = "com.lsh.meetcompose"
        }
        sendBroadcast(intent)
    }

    /**
     * 创建两个通知渠道：普通渠道用于响铃通知，静默渠道用于前台服务的常驻通知。
     *
     * 8.0 起通知必须归属某个渠道才能显示。渠道属性只能首次创建时生效，之后代码再改
     * 也覆盖不了，所以这里先查一下有没有，没有才创建。
     */
    private fun createChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(channelId1) == null) {
            val channel =
                NotificationChannel(channelId1, "普通通知", NotificationManager.IMPORTANCE_DEFAULT)
            channel.apply {
                description = "这是一个普通通知"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(500, 1000, 500, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }
        if (notificationManager.getNotificationChannel(channelId2) == null) {
            val channel = NotificationChannel(
                channelId2,
                "静默通知",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}