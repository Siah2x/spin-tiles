# Air Exit and Model Spin Design

## Goal

Update Cobblemon Spin Tiles so a lone arrow always ejects its rider one block forward even when that block has no floor, and visibly spins the rider's player model without changing the player's camera direction.

## Movement

- Empty air at foot and head height is traversable even when no supporting block exists below it.
- Solid blocks at foot or head height remain obstructions.
- With no later arrow or stop tile, the destination remains exactly one block beyond the current arrow.
- If that destination has no floor, the ride ends there and normal gravity takes over.
- Existing same-height chaining to a later arrow or stop tile remains unchanged.

## Model Animation

- The server remains authoritative for ride start and stop.
- Ride state is sent to clients with a small Fabric custom payload containing the player's entity id and whether the ride started or stopped.
- Clients track active riders and rotate only the player renderer's matrix around the model's vertical center.
- The spin angle advances continuously from elapsed render time and is not derived from player yaw.
- Camera yaw, head yaw, movement direction, and server rotation are never modified.
- Start and stop events are shared with the rider and players tracking that rider so third-person and nearby observers see the same effect.
- Client ride state is cleared on disconnect and when a stop event is received.

## Approaches Considered

1. Renderer-only rotation with synchronized ride state: selected because it preserves camera control and works for observers.
2. Mutating player body yaw: rejected because vanilla movement can overwrite it and it can bleed into orientation behavior.
3. Detecting tiles only on each client: rejected because it can disagree with server ride timing and does not reliably cover other players.

## Verification

- Unit tests prove unsupported air is traversable while solid foot/head obstructions remain blocked.
- Unit tests prove spin angle is time-based and independent of camera yaw.
- Source/package checks prove the client entrypoint, payload, and renderer mixin are included.
- A clean Gradle build must pass before installing version 1.0.2 in Test Pack.
