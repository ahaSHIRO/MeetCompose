package com.lsh.meetcompose

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lsh.meetcompose.service.MyForegroundService
import com.lsh.meetcompose.service.MyService1
import com.lsh.meetcompose.service.MyService2
import com.lsh.meetcompose.ui.HomeScreen
import com.lsh.meetcompose.ui.theme.MeetComposeTheme

/**
 * 应用唯一的 Activity，既是 Compose 世界的入口，也是五个服务回调真正的落地处。
 *
 * 启动型走命令通道：startService / stopService 把 Intent 投递给 MyService1，
 * 从此不再关心它内部在干什么。绑定型走通话通道：bindService 要等系统回调
 * onServiceConnected，再从 IBinder 里把 MyService2 的 MyBinder 强转出来，
 * 之后就能像调本地方法一样调服务暴露的能力。
 *
 * 两条链路不能合成一条的原因是：startService 本身没有回传通道，而 bindService
 * 多给了一个 Binder 当回传通道，代价是服务的生死被绑定方数量牵着走。
 */
class MainActivity : ComponentActivity() {

    // 记录「当前有没有真的绑上服务」。unbindService 要求先绑定过，没绑就解绑会抛
    // IllegalArgumentException: Service not registered，用这个标志把关
    private var isBound = false

    /**
     * 绑定关系的凭证，同时是系统回调的入口。
     *
     * 它必须留作类成员而不是 onCreate 里的局部变量，因为 unbindService 要求传入
     * 绑定时用的同一个实例，换一个对象系统就认不出来，直接抛 "Service not registered"。
     * 放在 Activity 字段上还顺带保证了它的存活期与界面一致。
     */
    val bindConnection = object : ServiceConnection {
        // 绑定成功后回到主线程，service 就是 MyService2.onBind 返回的那个 MyBinder
        override fun onServiceConnected(
            name: ComponentName?, service: IBinder?
        ) {
            Log.d("MainActivity", "onServiceConnected")
            isBound = true
            val myBinder = service as MyService2.MyBinder // 强转失败会当场 ClassCastException
            // myBinder.startPlayMusic() // 拿到遥控器后就能远程调方法，本关先只验证强转
        }

        // 服务异常崩溃或进程被杀时触发；正常的 unbindService 不会走这里
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity", "onServiceDisconnected")
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetComposeTheme {
                HomeScreen(
                    onStartService = {
                        val intent = Intent(this@MainActivity, MyService1::class.java) // 用 Activity 自己的 context，服务要挂在这套系统的名下
                        startService(intent)
                    },
                    onStopService = {
                        val intent = Intent(this@MainActivity, MyService1::class.java)
                        stopService(intent)
                    },
                    onBindService = {
                        val intent = Intent(this@MainActivity, MyService2::class.java)
                        bindService(intent, bindConnection, BIND_AUTO_CREATE) // 不带 AUTO_CREATE 时服务未创建就直接绑定失败
                    },
                    onUnbindService = {
                        if (isBound) { // 没绑过就跳过；连点两次解绑也不会崩
                            isBound = false
                            unbindService(bindConnection)
                        }
                    },
                    onForegroundService = {
                        // 8.0+ 必须用 startForegroundService 拉起前台服务，
                        // 服务里再调 startForeground 把通知挂出来，否则系统判定违规
                        val intent = Intent(this@MainActivity, MyForegroundService::class.java)
                        startForegroundService(intent)
                    },
                )
            }
        }
    }
}