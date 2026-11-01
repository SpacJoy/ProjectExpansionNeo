[ProjectExpansionNeo](README.md) › [English](README.md) / **简体中文**

# 等价升华:Neo（ProjectExpansionNeo）

**等价升华:Neo**（英文名 **ProjectExpansionNeo**）是面向 **Minecraft 1.21.1 / Fabric** 的 ProjectEF Neo 扩展，
由 Project Expansion 的 NeoForge 版本迁移而来，为 EMC 的生产、存储与自动化提供更多选择。

> **当前分支面向 Minecraft 26.3。**
> 由 [SpacJoy](https://github.com/SpacJoy/ProjectExpansionNeo) 从 [wchiway/ProjectExpansionNeo](https://github.com/wchiway/ProjectExpansionNeo) fork 后移植。
> 与 1.21.1 分支的差异见下方「本分支改动」。

## 本分支改动

### 一、移植到 Minecraft 26.3

底层约定与 ProjectEF 26.3 一致：

| 项目 | 1.21.1 | 26.3 |
| --- | --- | --- |
| 物品模型入口 | `assets/<ns>/models/item/*.json` | `assets/<ns>/items/*.json` |
| 饰品槽图标 | 任意图集 | 必须在 `textures/gui/sprites/` 下 |
| GUI 分层 | 按绘制顺序 | `GuiRenderState` 包围盒树，需要置顶时调 `nextStratum()` |
| 方块物品翻译键 | `block.<ns>.<path>` | `item.<ns>.<path>`，需调 `useBlockDescriptionPrefix()` 切回 |
| access widener | `*.accesswidener` | `*.classtweaker` |
| 构建 | Loom 1.10.x / JDK 21 | Loom 1.18.2 / JDK 25 |

配方 JSON 里引用物品改成字符串，不再是 `{"item": "..."}` 对象。

### 二、本分支独有的行为修复

- **奥术转换桌的数量文字**改在物品图标之后单独分层绘制，不再被图标盖住。
- **EMC 单位**按游戏语言显示（中文为 兆 / 京 / 垓 等中文数量级，不是 Quintillion 这类英文后缀），左上角 HUD 同步生效。
- **EMC 每秒速率**之前显示成实际值的两倍，现已改为两次采样取平均。
- **堆叠上限**改用 `getMaxStackSize()`，不再直接读 `MAX_STACK_SIZE`，兼容 Stackable 这类堆叠模组。
- **删除失效的 Curios 数据**：上游 1.21.1 树里仍留着 `data/curios/` 文件，但模组实际用 Trinkets，这些死文件已清除，项链和戒指标签改为直接列物品 ID。

### 三、未移植

- **EMI** 没有 26.3 版本，集成源码保留在树里但排除在编译之外。

## 主要内容

- **EMC 生产**：更多等级的 EMC 收集器、能量中继器和能量之花。
- **EMC 存储与自动化**：巨型之星、EMC 链接、转换接口和能量凝聚器 MK3。
- **收纳与实用工具**：高级炼金箱、炼金书、奥术转换终端和无限燃料。

## 安装

本分支使用 Minecraft 26.3、Java 25 和 Fabric Loader 0.19.5 或更高版本，
并在实例的 `mods/` 目录中放入：

- Fabric API 0.165.0+26.3。
- [ProjectEF 1.3.4 Release](https://github.com/SpacJoy/ProjectEF/releases/tag/1.3.4) 中的完整 Minecraft 26.3 JAR。
- `ProjectExpansionNeo-26.3-1.2.0.jar`。

选择普通的 ProjectEF JAR，例如 `ProjectEF-26.3-PE1.3.4.jar`，**不要**下载 `-api.jar` 或 `-sources.jar` 作为游戏模组安装。
ProjectEF Neo 内嵌提供 Forge Config API Port 和权限库，无需单独安装。

1.21.1 分支则需要 Minecraft 1.21.1、Java 21 和 Fabric Loader 0.16.9 或更高版本。

## 可选集成

支持 JEI、EMI、Jade、WTHIT 和 Trinkets。
Trinkets 提供专用转换终端槽位，替代原 Curios 集成；本 Fabric 版本不包含 TOP 集成。

## 配置与兼容性

服务器和客户端配置沿用 TOML 格式。模组 ID 保持为 `projectexpansion`。
旧版 NeoForge 存档跨加载器迁移尚未验证。

构建、开发环境、资源生成和测试说明见 [开发指南](DEV.md)。

## 项目来源

- [上游 Project Expansion](https://github.com/DonovanDMC/ProjectExpansion)
- [ProjectEF](https://github.com/wchiway/ProjectEF)

本项目保留上游代码、资源与作者署名，采用 [MIT 许可证](LICENSE)。
这是非官方扩展，不由 ProjectE 原项目提供支持。
