<center>
<div style="text-align: center;">

<img src="https://cdn.modrinth.com/data/cached_images/5bb73026c406951eca5da3f4a008b9b6c2b3302c.png" alt="Circuit Core" width="432" style="image-rendering: pixelated;">

#

[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/mod/circuit-core)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/github_vector.svg)](https://github.com/AussieBox/Circuit-Core)
<br>
[![patreon-singular](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/patreon-singular_vector.svg)](https://patreon.com/aussiebox)
[![discord-singular](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_64h.png)](https://discord.gg/9m2HXggUHg)
</div></center>

#
### Core "library" used by many of AussieBox's mods!</center><br>

**Circuit Core** is a mod used by most of AussieBox's projects that supplies utility methods for **Client & Server-sided** mods. Currently, it provides:
- A PlayerAnimationLib wrapper (is that what you call it?)
- Item, Block, and ItemGroup registry util
    - Also adds registered items to ItemGroups automatically!
- Advancement granting/revoking util
- Devenv-exclusive utilities:
  - ```/circuit_core command```
    - Animation setting (for PAL wrapper)
    - Quick infinite effect toggle

The mod also supports four versions at the moment: ```1.21.1```, ```1.21.8```, ```1.21.10```, and ```1.21.11```.

## Depending on Circuit Core
You are free to depend on Circuit Core as you wish, and do not require credit to do so. However, keep in mind that the code itself is licensed under ARR (with conditions).

> [!IMPORTANT]
> If fetching the dependency fails, check [Jitpack](https://jitpack.io/#AussieBox/Circuit-Core) to ensure your version has finished building. If it's still in progress, wait for it to finish then try again.

**To depend on Circuit Core, add this to your ```build.gradle```:**
```gradle
// Inside your repositories block:
maven { url 'https://jitpack.io' }
maven {
  name = "RedlanceMinecraft"
  url = "https://repo.redlance.org/public"
}

// Inside your dependencies block:
modImplementation "com.github.AussieBox:Circuit-Core:${project.minecraft_version}~${project.circuit_core_version}"
```
**Then, define the mod's version in your ```gradle.properties```.**
```properties
minecraft_version={YOUR GAME VERSION (should already be here)}
circuit_core_version={SEE TABLE BELOW}
```
| Game Version | Latest Version | Supported |
|:------------:|:--------------:|:---------:|
|   1.21.11    |     0.1.10     |     ✅     |
|   1.21.10    |     0.1.10     |     ✅     |
|    1.21.8    |     0.1.10     |     ✅     |
|    1.21.1    |     0.1.10     |     ✅     |