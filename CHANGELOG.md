# Circuit Core 0.1.0
## Additions
- Added PALStackAnimations
  - These animations will stop playing if the player is holding an ItemStack that is not of the correct Item
  - Additionally, they can lock the ItemStack's slots, stopping the player from moving the item during the animation
    - Enabling this could restrict gameplay until the animation is stopped
- Fixed(?) PlayerAnimationLib having to be added as its own dependency by mods depending on this one
- Made ```/circuit_core````` exclusive to development environments
- Added quick infinite effect toggle to `````/circuit_core````` (```/circuit_core effect <effect>```)