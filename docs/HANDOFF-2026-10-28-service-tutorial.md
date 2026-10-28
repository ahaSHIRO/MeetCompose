# HANDOFF 2026-10-28 · Service 教学项目交接

> 给接手的 AI：读完这份就能接上进度，不需要回溯原会话。本版为当晚第二次更新（第 7 关进行中）。

## 一、当前状态速览

| 项目 | 状态 |
| :--- | :--- |
| 仓库 | https://github.com/ahaSHIRO/MeetCompose（Public，默认分支 main） |
| 最新代码提交 | `9066542` wip(service): 第 7 关推进至第 2 步，第 3 步 receiver 未建 |
| 本地路径 | Windows：`C:\Code\Android\MeetCompose`；Linux：`/home/deck/Documents/study/MeetCompose` |
| 当前关卡 | 第 7 关进行中：第 1 步完成，第 2 步代码就位，第 3 步未建 |
| 编译状态 | **失败**，`./gradlew :app:compileDebugKotlin` 唯一报错是 `Unresolved reference 'MyServiceRestartReceiver'`（用户明确选择保留手敲原样提交，下次补文件即恢复） |

## 二、教学背景与规则

用户目标：**纯手敲复刻**老师的 Android Service 演示项目，锻炼肌肉记忆，拒绝 AI 代写业务代码。

必须遵守的技能：`interactive-hands-on-learning`（仓库内已随附于 `skills/interactive-hands-on-learning/SKILL.md`）。

核心铁律摘要（细节以 SKILL.md 为准）：

1. 手敲阶段绝不代劳业务代码；
2. 参考代码严格置底，回复结构固定五段（目标 → 架构 → 原理 → 坑点 → 参考代码）；
3. 深度原理先行，用生活比喻讲透机制；
4. 提前预警常见坑；
5. 只读审查用户代码；
6. 审查通过后补两层注释（易错行行尾短注 + 关键函数/类顶部块注，不套固定模板文字）；
7. 下一关必须等用户主动开口（「开始下一关」「开始下一步」都算）；
8. 看板走 `dsh-ui` 围栏的 `panel:true`，不要用 `render_ui` 的 panel；
9. 命名准确优先，老师明显笔误要纠正并指出；
10. 禁止 ASCII 图与 Markdown 表格，一律 genui 组件，≥3 节点或含 table 的围栏先过 `validate_dsh_ui`。

## 三、已完成的关卡（1–6）

| 关卡 | 内容 | 产出文件 |
| :--- | :--- | :--- |
| 1 | HomeScreen 五按钮 UI | `app/src/main/java/com/lsh/meetcompose/ui/HomeScreen.kt` |
| 2 | MainActivity 接入 Compose | `app/src/main/java/com/lsh/meetcompose/MainActivity.kt` |
| 3 | Manifest 注册 service | `app/src/main/AndroidManifest.xml` |
| 4 | 两个 Service 类 | `app/src/main/java/com/lsh/meetcompose/service/MyService1.kt`、`MyService2.kt` |
| 5 | 五项服务回调落地 | `MainActivity.kt`（bindConnection、start/stop/bind/unbind） |
| 6 | 前台服务与通知 | `app/src/main/java/com/lsh/meetcompose/service/MyForegroundService.kt` |

关键设计与踩过的坑已写入代码注释，直接读文件即可，此处不重复。

## 四、第 7 关进度（保活闭环）

| 步骤 | 产出 | 状态 |
| :--- | :--- | :--- |
| 1 | `MyService3.kt` 混合模式 | **完成**：已审查、已补注释 |
| 2 | `MyForegroundService.kt` 改造 | 代码就位，**未经审查**（编译不通过，无法联调验证） |
| 3 | `receiver/MyServiceRestartReceiver.kt` | **未开始** —— 当前编译失败的根因 |
| 4 | `MyApplication.kt`（AlarmManager 61 秒心跳） | 未开始 |
| 5 | Manifest + HomeScreen/MainActivity 接线 | 部分：`.service.MyService3` 已注册（用户提前做了） |
| 6 | 编译与真机联调 | 未开始 |

第 1 步要点（已落地）：`onCreate → onStartCommand → onBind → onDestroy` 全链打日志；`onStartCommand` 返回 `START_STICKY`；`onBind` 返回非 inner 的空 `ProxyBinder()`。类头块注写了两条调用链的差异，三处行注分别钉在 START_STICKY、onBind、ProxyBinder 上。

第 2 步已敲入内容：`createNotification()` 增加 contentIntent（`getActivity`，请求码 22）与两个 `addAction`（`getBroadcast`，请求码 13 / 25）；`onDestroy()` 与 `onTaskRemoved()` 统一调用私有方法 `sendRestartSignal(from)`；`companion` 新增 `ACTION_RESTART` 常量，供接收端共用。

第 2 步遗留的小收尾：

- 顶部 `import com.lsh.meetcompose.receiver.MyServiceRestartReceiver` 已写，但类不存在 → 红；
- IDE 误加了一行 `import kotlin.jvm.java`，应删；
- `sendRestartSignal` 上方第二行注释缩进错位，`Ctrl+Alt+L` 可修。

## 五、老师源码（已核实，2026-10-28 夜）

老师仓库不在 GitHub（按仓库名、包名、类名搜均无结果，`ahaSHIRO` 名下也没有），实际在 Gitee：

| 项目 | 值 |
| :--- | :--- |
| 地址 | https://gitee.com/GG26078/service-demo-composeui |
| HEAD | `dc82704` feat(service): 增强前台服务保活机制 |
| 本机克隆 | `/tmp/teacher-svc`（临时目录，被清理后重新 `git clone` 即可；Windows 侧在 `C:\Code\Android\study\service-demo-composeui`） |
| 包名 | `com.guigu.servicedemo`（用户项目是 `com.lsh.meetcompose`，参考代码必须换） |

`dc82704` 本身只改了 4 个文件（Manifest、MyApplication、MyForegroundService 增量）；`MyService3.kt` 与 receiver 属于更早的提交。

已确认的、老师源码里的问题（第 5 步要一并处理）：

| 问题 | 说明 |
| :--- | :--- |
| 包名拼写 | 老师写作 `reciver`（应为 `receiver`），用户已用正确拼写 |
| 缺权限 | Manifest 注册了监听 `BOOT_COMPLETED` 的 receiver，却没有 `RECEIVE_BOOT_COMPLETED` 权限，真机开机收不到广播 |
| 停止按钮对不上 | 「启动服务」已改成 `startService(MyService3)`，「停止服务」仍是 `stopService(MyService1)`，对新服务无效 |
| Binder 空转 | 全项目没有任何 `bindService` 到 `MyService3`，`ProxyBinder` 实际从未被使用 |
| Action 文案 | 通知两个按钮是 "AAA"/"BBB"，指向同一个 receiver，纯占位，**不是**暂停/停止 |
| 隐式广播 | `onDestroy`/`onTaskRemoved` 用空 `Intent()` + action 发广播，必须 `setPackage` 才能被静态 receiver 收到（8.0+ 限制） |

用户相对老师的合理偏离（保留，别改回去）：用共用常量 `MyForegroundService.ACTION_RESTART` 代替三处硬编码字符串；把重复的广播发送抽成 `sendRestartSignal()` 私有方法。

## 六、下次开工第一件事：补第 3 步（最小可编译清单）

1. 新建 `app/src/main/java/com/lsh/meetcompose/receiver/MyServiceRestartReceiver.kt`，继承 `BroadcastReceiver`，`onReceive` 里 `context.startForegroundService(Intent(context, MyForegroundService::class.java))`，日志打出 `intent.action`。参考代码已在会话里给过，对应老师 `reciver/MyServiceRestartReceiver.kt` 换包名即可。
2. 删掉 `MyForegroundService.kt` 顶部的 `import kotlin.jvm.java`。
3. `Ctrl+Alt+L` 格式化。
4. `./gradlew :app:compileDebugKotlin` 应变绿。

注意：此时 receiver 还没在 Manifest 注册，编译通过但运行收不到广播，那是第 5 步的事。

## 七、环境与坑

| 事项 | 说明 |
| :--- | :--- |
| 目标 SDK | 用户 targetSdk 37，`foregroundServiceType` 与配套权限必须成对；通知渠道属性首次创建后不可改 |
| Git 身份 | Linux 侧此前未配置，本次已设 **仓库级** `user.name=29335`、`user.email=aha_shiro@qq.com`，与历史提交一致 |
| 网络 | GitHub 直连可用；本机 Clash 混合代理 `127.0.0.1:7890` 可用；旧文档里的 `65512` 端口**已不通**，别再用 `env -u` 那套绕过说法 |
| gh CLI | 已登录 `ahaSHIRO`（scopes: gist, read:org, repo, workflow） |
| 未跟踪目录 | `.idea/` 未纳入版本控制，提交时排除即可 |
| 已解决的历史故障 | 解绑闪退（`isBound` 标志）、Manifest 权限位置错误、Binder 未定义 TAG，均已修复在仓库代码里 |

## 八、异步学习模式提醒

教室时间有限，用户有时会要求「先消化知识」或「今天到此为止」。此时：

- 不要擅自开新关或下一步骤（铁律 7）；
- 可提供复习清单、自测题，或对着 Logcat 验证行为；
- 用户主动说「开第 7 关」「开始下一关」时才正式进入讲解；
- 收尾时按用户要求提交推送并更新本文档。

## 九、建议调用的技能

| 技能 | 用途 |
| :--- | :--- |
| `interactive-hands-on-learning` | 主技能，纯手敲教学模式全流程 |
| `genui` | 输出讲解图与进度看板时查阅组件规范，避免围栏降级为代码块 |

## 十、交接前状态快照

```
9066542 wip(service): 第 7 关推进至第 2 步，第 3 步 receiver 未建
        author: 29335 <aha_shiro@qq.com>
        files : MyService3.kt（新增）、MyForegroundService.kt、AndroidManifest.xml

branch: main -> origin/main 已同步（本条与文档提交一并推送）
build : :app:compileDebugKotlin 失败，待补 MyServiceRestartReceiver
```