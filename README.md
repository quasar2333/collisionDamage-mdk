# Collision Damage Mod

## Introduction

The Collision Damage Mod introduces a dynamic combat mechanic to Minecraft by adding a special potion effect. When an entity is under this effect and moves at high speeds, it can inflict significant damage upon colliding with other entities. This mod aims to make high-speed movement and impacts more consequential and exciting.

## Features

*   **Custom Potion Effect:** Adds a new potion effect called "Collision Damage".
*   **Speed-Based Damage:** Damage dealt to other entities upon collision is proportional to the current speed of the attacker and the potion's level.
*   **Knockback Effect:** Collisions also apply a configurable horizontal knockback to the target entity.
*   **Client-Side Speed Display:** Players with the "Collision Damage" effect will see their current speed displayed on the game HUD (Heads-Up Display).
*   **Server-Side Mechanics:** Speed calculation is primarily client-authoritative but synchronized to the server for damage and collision processing, ensuring multiplayer compatibility.

## How it Works (Mechanics)

1.  **Potion Effect (`CollisionDamageEffect`):**
    *   The core of the mod is the "collision\_damage" potion effect. Entities must have this effect active to trigger collision damage.
    *   The effect itself doesn't do anything per tick other than enabling the collision detection logic.

2.  **Speed Calculation & Display (Client-Side):**
    *   If a player has the "Collision Damage" effect active, their speed is calculated every tick on the client-side (`CollisionEventHandler.onRenderGameOverlay`).
    *   Speed is determined by measuring the distance traveled between ticks (`(posX - prevPosX)`, etc.) and converting it to meters per second.
    *   This calculated speed is displayed on the player's HUD.

3.  **Speed Synchronization (`SpeedSyncPacket`):**
    *   The client sends its calculated speed to the server using a custom network packet (`SpeedSyncPacket`).
    *   The server receives this packet (`PacketHandler` & `SpeedSyncPacket.Handler`) and stores the speed value in the entity's NBT data under the key `"collisiondamage.speed"`. This makes the speed accessible for server-side logic.

4.  **Collision Detection & Damage (Server-Side):**
    *   On the server, `CollisionEventHandler.onEntityTick` runs for every living entity.
    *   If an entity has the "Collision Damage" potion effect active:
        *   It retrieves the synchronized speed from its NBT data.
        *   If the speed is greater than a threshold (currently `10.0 m/s`), the mod checks for nearby entities within a small radius (`RADIUS = 1.0`).
        *   For each valid target entity (another `EntityLivingBase`):
            *   **Damage Calculation:** Damage is dealt using the formula: `damage = 0.1 * speed * level`, where `level` is the potion effect's amplifier + 1 (e.g., Potion Level I means amplifier 0, so level = 1). The damage source is attributed to the attacking entity (`DamageSource.causeMobDamage(entity)`).
            *   **Knockback:** A horizontal knockback is applied to the target, pushing it away from the attacker. The strength of this knockback is currently `0.5`.

## Core Components (Code Structure)

*   **`CollisionDamageMod.java`:**
    *   The main mod class, annotated with `@Mod`.
    *   Responsible for initializing the mod and registering event handlers during the `FMLPreInitializationEvent`.
    *   Registers `CollisionEventHandler` and `PotionRegistryHandler` to the Forge event bus.
    *   Initializes the `PacketHandler` for network messages.

*   **`effect/CollisionDamageEffect.java`:**
    *   Defines the custom `Potion` named "collision\_damage".
    *   Sets its properties (e.g., color, name). It's configured to be an "instant" effect in terms of its `isReady` check, but its logic is driven by the `CollisionEventHandler`.

*   **`handler/PotionRegistryHandler.java`:**
    *   Handles the registration of the `CollisionDamageEffect.INSTANCE` into the game's potion registry during the `RegistryEvent.Register<Potion>` event.

*   **`handler/CollisionEventHandler.java`:**
    *   **`onEntityTick(LivingEvent.LivingUpdateEvent event)`:** Server-side logic. Checks if an entity has the potion effect, reads its speed from NBT, and if speed is high enough, iterates through nearby entities to apply damage and knockback.
    *   **`calculateSpeed(EntityLivingBase entity)`:** Utility method (used on client) to calculate an entity's current speed based on position changes.
    *   **`onRenderGameOverlay(RenderGameOverlayEvent.Post event)`:** Client-side logic. If the player has the potion effect, it calculates their speed, renders it on the HUD, stores it in the player's client-side NBT, and sends a `SpeedSyncPacket` to the server.

*   **`network/PacketHandler.java`:**
    *   Sets up a `SimpleNetworkWrapper` channel for the mod's custom packets.
    *   Registers the `SpeedSyncPacket` and its handler, specifying that it's a server-bound message.

*   **`network/SpeedSyncPacket.java`:**
    *   Defines the custom packet used to send the player's speed from the client to the server.
    *   Includes serialization (`toBytes`) and deserialization (`fromBytes`) methods.
    *   The nested `Handler` class processes the packet on the server, updating the player's NBT data with the received speed.

## How to Use

1.  **Obtaining the Effect:**
    *   Currently, the primary way to get the "Collision Damage" effect is likely through commands, such as:
        `/effect <player_name> collisiondamage:collision_damage <duration_seconds> <amplifier>`
        (e.g., `/effect @p collisiondamage:collision_damage 60 0` for Level I for 60 seconds).
    *   The mod does not currently include a custom brewing recipe for this potion.

2.  **Triggering Collision Damage:**
    *   Once an entity has the effect, it needs to move at a speed greater than **10.0 m/s**.
    *   When moving above this speed, if the entity collides with (comes very close to) another living entity, damage and knockback will be applied to the target.
    *   The player's current speed will be displayed on the screen if they have the effect.

## For Developers (Setup)

This mod follows the standard Minecraft Forge modding practices.
(The original `README.txt` contained setup instructions for older Forge versions. For modern Forge development, please refer to the official Forge Documentation: [http://mcforge.readthedocs.io/en/latest/gettingstarted/](http://mcforge.readthedocs.io/en/latest/gettingstarted/))

To set up a development environment:
1.  Clone the repository.
2.  Open the project in your preferred IDE (IntelliJ IDEA or Eclipse).
3.  Ensure you have the Java Development Kit (JDK) installed.
4.  The project uses Gradle. Your IDE should automatically handle importing the project and downloading dependencies via the `build.gradle` file.
5.  Common Gradle tasks:
    *   `gradlew genIntellijRuns` (for IntelliJ)
    *   `gradlew genEclipseRuns` (for Eclipse)
    *   `gradlew build` (to build the mod JAR)
    *   `gradlew runClient` / `gradlew runServer` (to test the mod)

---

# 碰撞伤害 Mod

## 简介

碰撞伤害 Mod 通过添加一种特殊的药水效果，为 Minecraft 引入了一种动态的战斗机制。当一个实体处于此效果下并高速移动时，它可以在与其他实体碰撞时造成显著的伤害。此 Mod 旨在使高速移动和撞击更具影响力和刺激性。

## 特性

*   **自定义药水效果:** 添加名为“碰撞伤害”的新药水效果。
*   **基于速度的伤害:** 碰撞时对其他实体造成的伤害与攻击者的当前速度和药水等级成正比。
*   **击退效果:** 碰撞还会对目标实体施加可配置的水平击退效果。
*   **客户端速度显示:** 具有“碰撞伤害”效果的玩家将在游戏 HUD（抬头显示器）上看到其当前速度。
*   **服务端机制:** 速度计算主要由客户端授权，但会同步到服务器进行伤害和碰撞处理，确保多人游戏兼容性。

## 工作原理 (机制)

1.  **药水效果 (`CollisionDamageEffect`):**
    *   Mod 的核心是 "collision\_damage" 药水效果。实体必须激活此效果才能触发碰撞伤害。
    *   该效果本身每 tick 不执行任何操作，仅用于启用碰撞检测逻辑。

2.  **速度计算与显示 (客户端):**
    *   如果玩家激活了“碰撞伤害”效果，他们的速度会在客户端每 tick 计算一次 (`CollisionEventHandler.onRenderGameOverlay`)。
    *   速度通过测量 tick 之间的移动距离（`(posX - prevPosX)` 等）并将其转换为米/秒来确定。
    *   计算出的速度会显示在玩家的 HUD 上。

3.  **速度同步 (`SpeedSyncPacket`):**
    *   客户端使用自定义网络数据包 (`SpeedSyncPacket`) 将其计算出的速度发送到服务器。
    *   服务器接收此数据包 (`PacketHandler` & `SpeedSyncPacket.Handler`) 并将速度值存储在实体的 NBT 数据中，键为 `"collisiondamage.speed"`。这使得服务器端逻辑可以访问该速度。

4.  **碰撞检测与伤害 (服务端):**
    *   在服务器上，`CollisionEventHandler.onEntityTick` 会为每个生物实体运行。
    *   如果一个实体激活了“碰撞伤害”药水效果：
        *   它会从其 NBT 数据中检索同步的速度。
        *   如果速度大于阈值（当前为 `10.0 m/s`），Mod 会检查小半径（`RADIUS = 1.0`）内的附近实体。
        *   对于每个有效的目标实体（另一个 `EntityLivingBase`）：
            *   **伤害计算:** 使用公式 `伤害 = 0.1 * 速度 * 等级` 来造成伤害，其中 `等级` 是药水效果的放大系数 + 1 (例如，药水等级 I 表示放大系数 0，因此等级 = 1)。伤害来源归因于攻击实体 (`DamageSource.causeMobDamage(entity)`)。
            *   **击退:** 对目标施加水平击退，将其推离攻击者。此击退的强度当前为 `0.5`。

## 核心组件 (代码结构)

*   **`CollisionDamageMod.java`:**
    *   主 Mod 类，使用 `@Mod` 注解。
    *   负责在 `FMLPreInitializationEvent` 期间初始化 Mod 并注册事件处理器。
    *   将 `CollisionEventHandler` 和 `PotionRegistryHandler` 注册到 Forge 事件总线。
    *   初始化用于网络消息的 `PacketHandler`。

*   **`effect/CollisionDamageEffect.java`:**
    *   定义名为 "collision\_damage" 的自定义 `Potion`（药水）。
    *   设置其属性（例如，颜色、名称）。就其 `isReady` 检查而言，它被配置为“即时”效果，但其逻辑由 `CollisionEventHandler` 驱动。

*   **`handler/PotionRegistryHandler.java`:**
    *   在 `RegistryEvent.Register<Potion>` 事件期间，处理将 `CollisionDamageEffect.INSTANCE` 注册到游戏的药水注册表中的操作。

*   **`handler/CollisionEventHandler.java`:**
    *   **`onEntityTick(LivingEvent.LivingUpdateEvent event)`:** 服务端逻辑。检查实体是否具有药水效果，从 NBT 读取其速度，如果速度足够高，则遍历附近的实体以施加伤害和击退。
    *   **`calculateSpeed(EntityLivingBase entity)`:** 工具方法（在客户端使用），用于根据位置变化计算实体的当前速度。
    *   **`onRenderGameOverlay(RenderGameOverlayEvent.Post event)`:** 客户端逻辑。如果玩家具有药水效果，它会计算他们的速度，将其渲染在 HUD 上，存储在玩家的客户端 NBT 中，并向服务器发送 `SpeedSyncPacket`。

*   **`network/PacketHandler.java`:**
    *   为 Mod 的自定义数据包设置 `SimpleNetworkWrapper` 通道。
    *   注册 `SpeedSyncPacket` 及其处理器，指明它是发往服务器的消息。

*   **`network/SpeedSyncPacket.java`:**
    *   定义用于将玩家速度从客户端发送到服务器的自定义数据包。
    *   包括序列化 (`toBytes`) 和反序列化 (`fromBytes`) 方法。
    *   嵌套的 `Handler` 类在服务器上处理数据包，用接收到的速度更新玩家的 NBT 数据。

## 如何使用

1.  **获取效果:**
    *   目前，获取“碰撞伤害”效果的主要方式可能是通过指令，例如：
        `/effect <玩家名称> collisiondamage:collision_damage <持续时间秒数> <放大系数>`
        （例如，`/effect @p collisiondamage:collision_damage 60 0` 表示为玩家提供 60 秒的 I 级效果）。
    *   该 Mod 目前不包含此药水的自定义酿造配方。

2.  **触发碰撞伤害:**
    *   一旦实体获得该效果，它需要以大于 **10.0 m/s** 的速度移动。
    *   当移动速度超过此阈值时，如果实体与另一个生物实体碰撞（非常接近），则会对目标施加伤害和击退。
    *   如果玩家具有该效果，他们的当前速度将显示在屏幕上。

## 开发者指南 (设置)

此 Mod 遵循标准的 Minecraft Forge Mod 开发实践。
(原始 `README.txt` 包含旧版 Forge 的设置说明。对于现代 Forge 开发，请参阅官方 Forge 文档：[http://mcforge.readthedocs.io/en/latest/gettingstarted/](http://mcforge.readthedocs.io/en/latest/gettingstarted/))

设置开发环境：
1.  克隆仓库。
2.  在您偏好的 IDE（IntelliJ IDEA 或 Eclipse）中打开项目。
3.  确保已安装 Java 开发工具包 (JDK)。
4.  该项目使用 Gradle。您的 IDE 应能通过 `build.gradle` 文件自动处理项目导入和依赖项下载。
5.  常用的 Gradle 任务：
    *   `gradlew genIntellijRuns` (适用于 IntelliJ)
    *   `gradlew genEclipseRuns` (适用于 Eclipse)
    *   `gradlew build` (构建 Mod JAR 文件)
    *   `gradlew runClient` / `gradlew runServer` (测试 Mod)
