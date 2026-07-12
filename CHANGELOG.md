## Additions
- Added ```AnimationData```, which handles everything stored in the new version of the ```PALAnimation``` class.
  - The animation's behavior will differ depending on the data type you use.
  - Currently, ```AnimationData``` and ```StackAnimationData``` are the only available options.
- Added ```HandlerData```, which handles everything stored in ```PALControllerHandler``` instances.
  - The data type will differ depending on the animation type, and is handled automatically.
    - ```DefaultHandlerData``` is used for animations with ```AnimationData``` data.
    - ```StackHandlerData``` is used for animations with ```StackAnimationData``` data.
## Changes
- ```PALAnimation``` now works for all animation types, and has a generic ```AnimationData``` that holds special information.
- ```PALControllerHandler``` now holds a generic ```HandlerData```, which now handles stored information.
- ```PALController``` can now only hold one type of animation (unfortunately), which must be provided upon creation.
- Fixed animations not playing in 1.21.8+
  - Our solution may result in lag- if it does, report it on github, and we'll try to optimise it, promise!
- Various fixes related to slot locking for PAL animations of ```Stack``` type.
## Removals
- Removed ```PALStackAnimation``` in favor of new system.