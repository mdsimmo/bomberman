Bomberman
=========

A Bukkit plugin for Minecraft which adds Bomberman.

https://www.spigotmc.org/resources/bomberman.77616/

## Building

To build bomberman, run

```shell
# To build exactly as released
./gradlew minify

# To skip shading and proguard
# ./gradlew build
```


To have the built file automatically copied to a local testing minecraft server:

<code>gradle install -PserverLocation=/path/to/server</code>
