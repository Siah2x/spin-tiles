# Air Exit and Model Spin Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let spin rides enter unsupported air and rotate only the rendered player model while the camera remains unchanged.

**Architecture:** Remove floor support from the server path-clearance decision while retaining foot/head obstruction checks. Synchronize ride start/stop through one S2C payload and apply a time-based Y rotation only inside a client-side player renderer mixin.

**Tech Stack:** Java 21, Fabric Loader 0.17.2, Fabric API 0.116.12+1.21.1, Yarn 1.21.1+build.3, JUnit 5, Sponge Mixin.

---

### Task 1: Unsupported Air Traversal

**Files:**
- Create: `src/main/java/dev/siah/spintiles/SpinTraversalRules.java`
- Modify: `src/main/java/dev/siah/spintiles/SpinRideController.java`
- Create: `src/test/java/dev/siah/spintiles/SpinTraversalRulesTest.java`

- [ ] Write `SpinTraversalRulesTest` with three cases: clear feet/head returns true, blocked feet returns false, and blocked head returns false.
- [ ] Run `gradle test --tests dev.siah.spintiles.SpinTraversalRulesTest` and confirm the missing rule fails compilation.
- [ ] Implement `SpinTraversalRules.canTraverse(boolean feetClear, boolean headClear)` as `feetClear && headClear`.
- [ ] Change `SpinRideController.isPassable` to calculate only foot and head clearance, call the new rule, and remove the floor-below collision check.
- [ ] Run the targeted test and `SpinPathRulesTest`; both must pass.

### Task 2: Ride State Networking

**Files:**
- Create: `src/main/java/dev/siah/spintiles/SpinRidePayload.java`
- Create: `src/main/java/dev/siah/spintiles/SpinRideNetworking.java`
- Modify: `src/main/java/dev/siah/spintiles/CobblemonSpinTiles.java`
- Modify: `src/main/java/dev/siah/spintiles/SpinRideController.java`

- [ ] Add `SpinRidePayload(int entityId, boolean active)` implementing `CustomPayload`, with `PacketCodec.tuple(PacketCodecs.VAR_INT, ..., PacketCodecs.BOOLEAN, ...)` and id `cobblemon_spin_tiles:spin_ride`.
- [ ] Register the payload through `PayloadTypeRegistry.playS2C()` during common initialization.
- [ ] Add a sender that sends only to clients for which `ServerPlayNetworking.canSend` is true, covering the rider and `PlayerLookup.tracking(rider)` without duplicates.
- [ ] Send active state after adding a ride and inactive state before removing a live rider's ride.
- [ ] Compile with `gradle compileJava` and correct any mapping-specific signature mismatch before proceeding.

### Task 3: Renderer-Only Model Spin

**Files:**
- Create: `src/main/java/dev/siah/spintiles/SpinRotationMath.java`
- Create: `src/test/java/dev/siah/spintiles/SpinRotationMathTest.java`
- Create: `src/client/java/dev/siah/spintiles/client/CobblemonSpinTilesClient.java`
- Create: `src/client/java/dev/siah/spintiles/client/SpinRideVisualState.java`
- Create: `src/client/java/dev/siah/spintiles/client/mixin/PlayerEntityRendererMixin.java`
- Create: `src/main/resources/cobblemon_spin_tiles.client.mixins.json`
- Modify: `src/main/resources/fabric.mod.json`
- Modify: `build.gradle`

- [ ] Write `SpinRotationMathTest` proving zero elapsed time is zero degrees, 75 ms is 180 degrees, 150 ms wraps to zero, and the API accepts no player/camera yaw.
- [ ] Run the targeted test and confirm it fails because `SpinRotationMath` does not exist.
- [ ] Implement a 150 ms rotation period using elapsed milliseconds modulo the period.
- [ ] Configure Loom split environment source sets and add a `client` entrypoint.
- [ ] Register a global S2C receiver that starts/stops entity ids in `SpinRideVisualState`, and clear the state on disconnect.
- [ ] Inject at the tail of the typed `PlayerEntityRenderer.setupTransforms(AbstractClientPlayerEntity, MatrixStack, float, float, float, float)` method and multiply its existing model matrix by `RotationAxis.POSITIVE_Y`. Do not call any yaw, pitch, head-yaw, camera, or entity rotation setter.
- [ ] Run the targeted math test and `gradle compileClientJava`.

### Task 4: Package and Install

**Files:**
- Modify: `gradle.properties`
- Install: `/Users/siahsneaka/Library/Application Support/ModrinthApp/profiles/Test Pack/mods/CobblemonSpinTiles-1.0.2-fabric-1.21.1.jar`

- [ ] Bump `mod_version` to `1.0.2`.
- [ ] Run `gradle clean build` and require a successful full test/build result.
- [ ] Inspect the jar for the payload, client initializer, mixin config, renderer mixin, and metadata version.
- [ ] Disable the prior active Spin Tiles jar, copy the new jar into Test Pack, and verify exactly one active copy.
- [ ] Compare SHA-256 hashes of the build and installed jars; they must match.

This directory is not a Git repository, so commit steps are intentionally omitted rather than fabricating commits.
