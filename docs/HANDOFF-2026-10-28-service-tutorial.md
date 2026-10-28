# HANDOFF 2026-10-28 · Service 教学项目交接

> 给宿舍 AI 的交接文档。读完这份就能接上教室里的进度，不需要回溯原会话。

## 一、当前状态速览

| 项目 | 状态 |
| :--- | :--- |
| 仓库 | https://github.com/ahaSHIRO/MeetCompose（Public，默认分支 main） |
| 最新提交（交接前） | `569a483` feat(service): 完成 Service 全部教学关卡 |
| 本地路径 | `C:\Code\Android\MeetCompose` |
| 当前关卡 | 第 6 关已完成并推送；第 7 关未开始 |
| 编译状态 | `./gradlew :app:assembleDebug` 通过 |

## 二、教学背景与规则

用户目标：**纯手敲复刻**老师的 Android Service 演示项目，锻炼肌肉记忆，拒绝 AI 代写业务代码。

必须遵守的技能：`interactive-hands-on-learning`（仓库内已随附于 `skills/interactive-hands-on-learning/SKILL.md`）。

核心铁律摘要（细节以 SKILL.md 为准）：

1. 手敲阶段绝不代劳业务代码；
2. 参考代码严格置底，回复结构固定五段；
3. 深度原理先行，用生活比喻讲透机制；
4. 提前预警常见坑；
5. 只读审查用户代码；
6. 审查通过后补两层注释（易错行行尾短注 + 关键函数/类顶部块注，不套固定模板文字）；
7. 下一关必须等用户主动开口；
8. 看板走 `dsh-ui` 围栏的 `panel:true`；
9. 命名准确优先，老师明显笔误要纠正并指出；
10. 禁止 ASCII 图与 Markdown 表格，一律 genui 组件，复杂围栏先过 `validate_dsh_ui`。

## 三、已完成的关卡

| 关卡 | 内容 | 产出文件 |
| :--- | :--- | :--- |
| 1 | HomeScreen 五按钮 UI | `app/src/main/java/com/lsh/meetcompose/ui/HomeScreen.kt` |
| 2 | MainActivity 接入 Compose | `app/src/main/java/com/lsh/meetcompose/MainActivity.kt` |
| 3 | Manifest 注册 service | `app/src/main/AndroidManifest.xml` |
| 4 | 两个 Service 类 | `app/src/main/java/com/lsh/meetcompose/service/MyService1.kt`、`MyService2.kt` |
| 5 | 五项服务回调落地 | `MainActivity.kt`（bindConnection、start/stop/bind/unbind） |
| 6 | 前台服务与通知 | `app/src/main/java/com/lsh/meetcompose/service/MyForegroundService.kt` |

关键设计与踩过的坑，已写入代码注释，直接读文件即可，无需在此重复。

## 四、下一关（第 7 关）待办

老师仓库最新提交 `dc82704` 已实现，用户尚未手敲：

| 文件 | 内容 |
| :--- | :--- |
| `MyService3.kt` | 先 startService 再 bindService 的混合模式，onBind 返回 ProxyBinder |
| `MyServiceRestartReceiver.kt` | 广播接收器，收到 RESTART 或开机广播后 `startForegroundService` |
| `MyApplication.kt` | 自定义 Application，AlarmManager 每 61 秒心跳拉起前台服务 |
| `MyForegroundService.kt` 增量 | 通知加点击跳转与两个 Action 按钮；onTaskRemoved 发重启广播 |
| `AndroidManifest.xml` 增量 | `android:name=".MyApplication"`、MainActivity 加 `launchMode="singleTask"`、floating receiver 注册 |

老师源码位置：`C:\Code\Android\study\service-demo-composeui`（已克隆，本地 main 分支与 origin 同步在 `dc82704`）。

注意：老师自己注明这套保活「非必要不建议使用，部分市场及安全软件会限制」，讲解时要如实告知风险。

## 五、环境与坑

| 事项 | 说明 |
| :--- | :--- |
| 包名 | 用户项目是 `com.lsh.meetcompose`，老师是 `com.guigu.servicedemo`，所有参考代码要换包名 |
| 命名修正 | 用户已采用更规范的命名：`onForegroundService`（老师是小写 f）、`MyBinder`（老师曾写 `Mybinder`）、`channelId1/2`（老师曾写 `asdfsdf`） |
| 目标 SDK | 用户 targetSdk 37，`foregroundServiceType` 与配套权限必须成对 |
| GitHub 访问 | 当前网络 shell 里 `https_proxy=127.0.0.1:65512` 可能失效；git/gh 命令前加 `env -u http_proxy -u https_proxy -u HTTP_PROXY -u HTTPS_PROXY` 可绕过 |
| 已解决的历史故障 | 解绑闪退（加 `isBound` 标志）、Manifest 权限位置错误、Binder 未定义 TAG 等，均已修复在仓库代码里 |

## 六、异步学习模式提醒

教室时间有限，用户有时会要求「先消化知识」而不是继续推进。此时：

- 不要擅自开新关卡（铁律 7）；
- 可提供复习清单、自测题、或对着 Logcat 验证行为；
- 用户主动说「开第 7 关」时才正式进入讲解。

## 七、建议调用的技能

| 技能 | 用途 |
| :--- | :--- |
| `interactive-hands-on-learning` | 主技能，纯手敲教学模式全流程 |
| `genui` | 输出讲解图与进度看板时查阅组件规范，避免围栏降级为代码块 |

## 八、交接前状态快照

```
commit 569a483
Author: 29335 <aha_shiro@qq.com>
Message: feat(service): 完成 Service 全部教学关卡
Files: 43 changed (含 skills/ 与 docs/)
Branch: main -> origin/main 已同步
```