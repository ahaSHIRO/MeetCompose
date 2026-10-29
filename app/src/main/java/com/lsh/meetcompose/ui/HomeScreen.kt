package com.lsh.meetcompose.ui

import com.lsh.meetcompose.ui.theme.MeetComposeTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 服务演示的首页，本身不持有任何状态，只把五个按钮的点击翻译成回调往外抛。
 *
 * 这样拆的原因是 Compose 里整页常被当作「纯展示层」：按钮点了以后是启动服务、停止服务还是
 * 绑定服务，全看外部传进来的 [onStartService] 等五个 lambda，页面本身不知道也不关心。
 * 好处是同一个页面既能塞进 MainActivity 走真逻辑，也能在 @Preview 里传一堆空 lambda 直接预览，
 * 不用为了看图而真把服务拉起来。
 *
 * 布局链路很短：Scaffold 铺骨架并算出系统栏与标题栏占掉的区域，Column 把五个按钮竖排，
 * 每个按钮都经 [ServiceButton] 渲染。注意五个参数都写成 `() -> Unit`，说明调用方只会被通知
 * 「这件事发生了」，拿不到任何回传数据——将来要追加回传值时，这里要改签名。
 */
@OptIn(ExperimentalMaterial3Api::class) // TopAppBar 属实验期 API，不写 OptIn 无法编译
@Composable
fun HomeScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onBindService: () -> Unit,
    onUnbindService: () -> Unit,
    onForegroundService: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("HomeScreen", fontSize = 20.sp) })
    }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 必须接收，否则内容顶进 TopAppBar 底下被遮挡
        ) {
            ServiceButton("启动服务", onStartService)
            ServiceButton("停止服务", onStopService)
            ServiceButton("绑定服务", onBindService)
            ServiceButton("解绑服务", onUnbindService)
            ServiceButton("启动前台服务", onForegroundService)
        }
    }
}

/**
 * HomeScreen 的预览入口。
 *
 * 必须另起一个无参函数：HomeScreen 的五个回调都没有默认值，预览器没法替调用方传参，
 * 所以在这里统一喂空 lambda，只渲染界面，不会真的把服务拉起来。
 * 外层包 MeetComposeTheme 是为了让配色与字体跟真机运行时保持一致。
 */
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MeetComposeTheme {
        HomeScreen(
            onStartService = {},
            onStopService = {},
            onBindService = {},
            onUnbindService = {},
            onForegroundService = {},
        )
    }
}

/**
 * 五个操作按钮长得一模一样，只有文案和点击行为不同，所以抽成一个私有组件反复使用。
 *
 * 它自己不保存任何状态，点击事件原样经 [onClick] 上抛，符合 Compose「无状态子组件」的写法；
 * 样式集中在这一个函数里，将来想把描边按钮换成填充按钮，只改这里一处，五个按钮同步生效。
 * 声明成 private 是刻意的：它只服务本文件，没必要暴露给 ui 包外去 import。
 */
@Composable
private fun ServiceButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text = text, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
    }
}
