# Cobblemon Spin Tiles

Cobblemon Spin Tiles adds Pokemon-style movement puzzle blocks to Minecraft using Fabric 1.21.1.

## Requirements

- Minecraft 1.21.1

## Blocks

### Spin Tile

A thin directional floor tile that faces the same direction as the player who places it.

Walking onto the tile pushes the player in the arrow’s direction. Players can enter from the back or either side, but cannot enter from the front. The tile will not activate if a wall blocks it's exit.

Spin Tiles search forward for another Spin Tile or Spin Stop Tile. If no connecting tile is found, the player moves one block forward and stops.

Chaining tiles builds a combo. Each tile increases movement speed. Combos are limited to 50 activated tiles by default.

### Spin Stop Tile

Ends a Spin Tile route

### Ice Tile

Locks the player into their current movement direction while sliding across connected Ice Tiles. The player regains control after hitting a wall, leaving the ice, or falling off the path.

## Getting The Blocks

The blocks are available in the Cobblemon Spin Tiles creative tab and have simple recipies available.

```mcfunction
/give @s cobblemon_spin_tiles:spin_tile
/give @s cobblemon_spin_tiles:spin_stop_tile
/give @s cobblemon_spin_tiles:ice_tile
```

## Gamerules

Spin Tiles search up to 50 blocks for another tile by default.

```mcfunction
/gamerule spinTilesInfiniteRange true
```

Spin combos stop after 50 activated arrows by default.

```mcfunction
/gamerule spinTilesUnlimitedCombo true
```
