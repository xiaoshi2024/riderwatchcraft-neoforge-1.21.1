# Rider Watch Craft

**Rider Watch Craft** 是一个 Minecraft NeoForge 模组，作为 **[假面骑士武器大合集 (Kamen Rider Weapon Craft)](https://modrinth.com/mod/kamenriderweaponcraft)** 的扩展，添加了多种骑士表盘核心，可安装到平成剑上，提供独特的技能、模型和动画效果。

---

## 📖 目录

- [功能特性](#-功能特性)
- [依赖要求](#-依赖要求)
- [安装说明](#-安装说明)
- [当前内容](#-当前内容)
    - [骑士表盘](#骑士表盘)
    - [技能效果](#技能效果)
- [使用方法](#-使用方法)
- [配置说明](#-配置说明)
- [开发与构建](#-开发与构建)
- [文件结构](#-文件结构)
- [许可证](#-许可证)

---

## ✨ 功能特性

- **Alt+右键** 拆除/安装表盘（非 Shift+右键）
- 每个核心提供独立的：
    - 3D 模型与动画（基于 GeckoLib）
    - 攻击伤害与能量消耗
    - 粒子特效技能
    - 独特的音效
- 与假面骑士武器大合集核心机制无缝集成
- 支持叠层模式 (Scramble Mode)

---

## 📦 依赖要求

| 依赖 | 说明 |
|------|------|
| **Minecraft** | 1.21.1 / NeoForge 21.1+ |
| **GeckoLib** | 4.7+ (动画库) |
| **KamenRiderWeaponCraft** | 前置核心模组 |

---

## 🛠️ 安装说明

1. 安装 NeoForge 21.1+
2. 将以下模组放入 `mods` 文件夹：
    - `kamenriderweaponcraft-x.x.x.jar`
    - `geckolib-x.x.x.jar`
    - `riderwatchcraft-x.x.x.jar`
3. 启动游戏

---

## 🎮 当前内容

### 骑士表盘

| 表盘 | 注册名 | 核心ID |
|------|--------|--------|
| **Kuuga Mighty Core** | `kuuga_mighty_core` | `kuuga_mighty` |
| **ZZZ Dream Core** | `zzz_dream_core` | `zzz_dream` |

### 技能效果

#### 🔴 Kuuga Mighty Core
| 属性 | 数值 |
|------|------|
| 攻击伤害 | 35.0 |
| 效果范围 | 6.0 格 |
| 能量消耗 | 20.0 |
| 耐久加成 | +500 |
| 激活音效 | `entity.player.attack.strong` |
| 叠层模式 | ✅ 支持 (最大4层) |
| 技能特效 | 发光粒子 (GLOW) |

#### ⚪ ZZZ Dream Core
| 属性 | 数值 |
|------|------|
| 攻击伤害 | 48.0 |
| 效果范围 | 15.0 格 |
| 能量消耗 | 25.0 |
| 耐久加成 | +500 |
| 激活音效 | `zzz_dream_activate` |
| 叠层模式 | ❌ 不支持 |
| 技能特效 | 灵魂粒子 (SOUL) + 女巫粒子 (WITCH) |

---

## 🔧 使用方法

### 安装表盘
1. 手持 **平成剑 (Heiseisword)**
2. 副手持有 **表盘核心**
3. 按下 **Alt + 右键**

### 拆除表盘
1. 手持 **平成剑 (Heiseisword)**
2. 副手为空
3. 按下 **Alt + 右键**

> ⚠️ **注意**：Shift+右键不会触发拆除，这是本模组的特性修改。

### 激活技能
- 安装核心后，使用平成剑的技能键位（由前置模组定义）

---

## ⚙️ 配置说明

当前配置为预留接口 (`Config.java`)，可在后续版本中添加：

```java
// 示例配置项（尚未实现）
- 攻击倍率调整
- 能量消耗修改
- 技能冷却时间
```

---

## 🏗️ 开发与构建

### 前置模组修改

本模组需要修改前置模组的 `CoreInteractionHandler.java`：

**删除以下代码：**
```java
if (!player.isSecondaryUseActive()) {
    return;
}
```

### 构建模组

```bash
./gradlew build
```

构建产物位于 `build/libs/`

### Mixin 配置

本模组使用 Mixin 修改 `HeiseiswordModel` 的模型/纹理/动画加载：

```json5
// mixin.riderwatchcraft.json
{
  "mixins": ["HeiseiswordModelMixin"]
}
```

---

## 📁 文件结构

```
riderwatchcraft/
├── src/main/
│   ├── java/com/xiaoshi2022/riderwatchcraft/
│   │   ├── RiderWatchCraft.java          # 主类
│   │   ├── RiderWatchCraftClient.java    # 客户端入口
│   │   ├── Config.java                   # 配置
│   │   ├── mixin/
│   │   │   └── HeiseiswordModelMixin.java
│   │   └── rider/
│   │       ├── kuuga/                    # 空我表盘
│   │       │   ├── KuugaCoreItem.java
│   │       │   ├── KuugaCoreItemModel.java
│   │       │   ├── KuugaCoreItemRenderer.java
│   │       │   ├── KuugaCoreEffectProvider.java
│   │       │   └── KuugaRegistry.java
│   │       └── zzz/                      # ZZZ表盘
│   │           ├── ZZZCoreItem.java
│   │           ├── ZZZCoreItemModel.java
│   │           ├── ZZZCoreItemRenderer.java
│   │           ├── ZZZCoreEffectProvider.java
│   │           └── ZZZRegistry.java
│   └── resources/
│       ├── mixin.riderwatchcraft.json
│       ├── assets/riderwatchcraft/
│       │   ├── geo/item/
│       │   │   ├── heiseisword_kuuga.geo.json
│       │   │   ├── kuuga_core.geo.json
│       │   │   └── zzz_core.geo.json
│       │   ├── textures/item/
│       │   │   ├── kuuga_core.png
│       │   │   └── zzz_core.png
│       │   └── animations/item/
│       │       ├── heiseisword_kuuga.animation.json
│       │       └── kuuga_core.animation.json
│       └── sounds.json
└── build.gradle
```

---

## 📜 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

## 🙏 致谢

- [KamenRiderWeaponCraft](https://github.com/xiaoshi2024/MC-KamenRiderWeaponCraft-Mod) - 前置核心模组
- [GeckoLib](https://github.com/bernie-g/geckolib) - 动画库
- NeoForge 社区

---

## 🔗 相关链接

- [假面骑士武器大合集 源码](https://github.com/xiaoshi2024/MC-KamenRiderWeaponCraft-Mod)
- [GeckoLib 文档](https://github.com/bernie-g/geckolib/wiki)

---

*Made with 🐉 for Kamen Rider fans*