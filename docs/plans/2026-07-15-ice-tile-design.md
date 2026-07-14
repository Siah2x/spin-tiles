# Ice Tile Design

## Behavior

- Add a full-block `cobblemon_spin_tiles:ice_tile` named **Ice Tile**.
- Entering the tile with meaningful horizontal motion locks the player to the dominant cardinal axis.
- While the player remains over consecutive Ice Tiles, the server applies exactly 0.5 blocks/tick on that axis and preserves vertical velocity.
- The slide ends when the block beneath the player's feet is not an Ice Tile or when the player's next bounding-box step would collide with a solid block.
- The client suppresses only horizontal movement input during the slide. Camera rotation remains fully player-controlled.

## Presentation

- Recreate the pale cyan diagonal ice-floor pattern seen in Pokemon FireRed's Icefall Cave as a crisp tileable 16x16 texture.
- Add the block to the existing Cobblemon Spin Tiles creative tab and make it obtainable with `/give`.
- No survival recipe is added in this version.

## Architecture

- `IceSlideRules` owns deterministic cardinal locking and stop decisions.
- `IceSlideController` owns server-authoritative per-player slide state and collision checks.
- A small S2C payload tells the local client when to suppress horizontal input.
- A client input mixin clears W/A/S/D movement values without changing yaw, pitch, camera, jump, or sneak state.

